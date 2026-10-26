package aula.microservicos.login;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aula.microservicos.login.dto.UsuarioResponse;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarios;
    private final LoginAttemptRepository tentativas;
    private final LoginMetrica metrica;
    private final int tentativasParaBloqueio;

    public UsuarioService(UsuarioRepository usuarios, LoginAttemptRepository tentativas, LoginMetrica metrica,
            @Value("${login.tentativas-para-bloqueio}") int tentativasParaBloqueio) {
        this.usuarios = usuarios;
        this.tentativas = tentativas;
        this.metrica = metrica;
        this.tentativasParaBloqueio = tentativasParaBloqueio;
    }

    @Transactional
    public ResultadoLogin login(String username, String senha) {

        Optional<Usuario> usuario = buscar(username);
        if (usuario.isEmpty()) {
            log.info("Login inválido: usuário {} inexistente", username);
            metrica.loginInvalido();
            return ResultadoLogin.CREDENCIAIS_INVALIDAS;
        }

        LoginAttempt tentativa = tentativaDe(username);
        if (tentativa.isLocked()) {
            log.info("Login inválido: usuário {} bloqueado", username);
            metrica.loginInvalido();
            return ResultadoLogin.USUARIO_BLOQUEADO;
        }

        if (!senhaConfere(usuario.get(), senha)) {
            boolean bloqueou = registrarFalha(tentativa);
            metrica.loginInvalido();
            return bloqueou ? ResultadoLogin.USUARIO_BLOQUEADO : ResultadoLogin.CREDENCIAIS_INVALIDAS;
        }

        tentativa.zerar();
        tentativas.save(tentativa);
        metrica.loginValido();
        log.info("Login válido: {}", username);
        return ResultadoLogin.SUCESSO;
    }

    // noRollbackFor: a falha registrada em login_attempts precisa ser gravada mesmo com a exceção
    @Transactional(noRollbackFor = UsuarioException.class)
    public void trocarSenha(String username, String atual, String nova) {

        Usuario usuario = buscar(username).orElseThrow(() -> UsuarioException.naoEncontrado(username));

        LoginAttempt tentativa = tentativaDe(username);
        if (tentativa.isLocked()) {
            throw new UsuarioException(HttpStatus.FORBIDDEN, ResultadoLogin.USUARIO_BLOQUEADO.getMensagem());
        }

        // a senha atual errada conta como tentativa inválida; sem isso este endpoint público
        // permitiria testar senhas sem nunca bloquear o usuário
        if (!senhaConfere(usuario, atual)) {
            boolean bloqueou = registrarFalha(tentativa);
            throw new UsuarioException(HttpStatus.UNAUTHORIZED,
                    bloqueou ? "Senha atual inválida. " + ResultadoLogin.USUARIO_BLOQUEADO.getMensagem()
                            : "Senha atual inválida");
        }

        usuario.setSenha(nova);
        usuarios.save(usuario);
        // a senha atual conferiu, então as falhas anteriores são zeradas (como no login)
        tentativa.zerar();
        tentativas.save(tentativa);
        log.info("Senha alterada: {}", username);
    }

    @Transactional
    public UsuarioResponse cadastrar(String username, String senha) {

        if (usuarios.existsById(username)) {
            throw new UsuarioException(HttpStatus.CONFLICT, "Usuário " + username + " já cadastrado");
        }

        usuarios.save(new Usuario(username, senha));
        log.info("Usuário cadastrado: {}", username);
        return new UsuarioResponse(username, false, 0);
    }

    // retorna true se o usuário estava bloqueado
    @Transactional
    public boolean desbloquear(String username) {

        if (!usuarios.existsById(username)) {
            throw UsuarioException.naoEncontrado(username);
        }

        LoginAttempt tentativa = tentativaDe(username);
        boolean estavaBloqueado = tentativa.isLocked();
        tentativa.zerar();
        tentativas.save(tentativa);
        log.info("Usuário desbloqueado: {}", username);
        return estavaBloqueado;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {

        Map<String, LoginAttempt> tentativasPorUsuario = StreamSupport.stream(tentativas.findAll().spliterator(), false)
                .collect(Collectors.toMap(LoginAttempt::getUsername, Function.identity()));

        return StreamSupport.stream(usuarios.findAll().spliterator(), false)
                .sorted(Comparator.comparing(Usuario::getUsername))
                .map(usuario -> {
                    LoginAttempt tentativa = tentativasPorUsuario.get(usuario.getUsername());
                    return tentativa == null
                            ? new UsuarioResponse(usuario.getUsername(), false, 0)
                            : new UsuarioResponse(usuario.getUsername(), tentativa.isLocked(),
                                    tentativa.getFailedAttempts());
                })
                .toList();
    }

    private Optional<Usuario> buscar(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        return usuarios.findById(username);
    }

    private LoginAttempt tentativaDe(String username) {
        return tentativas.findById(username).orElseGet(() -> new LoginAttempt(username));
    }

    private boolean senhaConfere(Usuario usuario, String senha) {
        return senha != null && senha.equals(usuario.getSenha());
    }

    // retorna true se esta falha bloqueou o usuário
    private boolean registrarFalha(LoginAttempt tentativa) {
        tentativa.registrarFalha(tentativasParaBloqueio);
        tentativas.save(tentativa);
        if (tentativa.isLocked()) {
            log.warn("Usuário {} bloqueado após {} tentativas inválidas", tentativa.getUsername(),
                    tentativa.getFailedAttempts());
        } else {
            log.info("Senha inválida para {} ({} tentativa(s))", tentativa.getUsername(),
                    tentativa.getFailedAttempts());
        }
        return tentativa.isLocked();
    }

}
