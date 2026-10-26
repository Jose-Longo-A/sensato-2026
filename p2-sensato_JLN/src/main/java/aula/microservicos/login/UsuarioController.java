package aula.microservicos.login;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import aula.microservicos.login.dto.LoginRequest;
import aula.microservicos.login.dto.Mensagem;
import aula.microservicos.login.dto.NovoUsuarioRequest;
import aula.microservicos.login.dto.TrocaSenhaRequest;
import aula.microservicos.login.dto.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    // POST - http://localhost:8080/login
    @Operation(summary = "Efetua login", tags = { "autenticacao" },
            description = "Valida usuário e senha na tabela usuarios. "
                    + "Após falhas consecutivas (login.tentativas-para-bloqueio, padrão 2) o usuário é bloqueado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário e senha válidos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mensagem.class))),
            @ApiResponse(responseCode = "401", description = "Usuário e senha inválidos ou usuário bloqueado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mensagem.class))) })
    @PostMapping("/login")
    public ResponseEntity<Mensagem> login(@RequestBody LoginRequest login) {

        ResultadoLogin resultado = service.login(login.username(), login.senha());
        HttpStatus status = resultado == ResultadoLogin.SUCESSO ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status).body(new Mensagem(resultado.getMensagem()));
    }

    // PUT - http://localhost:8080/trocarsenha/joao
    @Operation(summary = "Troca a senha de um usuário", tags = { "autenticacao" },
            description = "Confere a senha atual com a base de dados e grava a nova senha. "
                    + "Senha atual incorreta conta como tentativa inválida para o bloqueio.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Senha alterada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mensagem.class))),
            @ApiResponse(responseCode = "400", description = "Nova senha vazia ou com mais de 10 caracteres", content = @Content),
            @ApiResponse(responseCode = "401", description = "Senha atual não confere",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mensagem.class))),
            @ApiResponse(responseCode = "403", description = "Usuário bloqueado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mensagem.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mensagem.class))) })
    @PutMapping("/trocarsenha/{userid}")
    public ResponseEntity<Mensagem> trocarSenha(
            @Parameter(description = "username do usuário", example = "joao") @PathVariable String userid,
            @Valid @RequestBody TrocaSenhaRequest troca) {

        service.trocarSenha(userid, troca.atual(), troca.nova());
        return ResponseEntity.ok(new Mensagem("Senha alterada com sucesso"));
    }

    // POST - http://localhost:8080/
    @Operation(summary = "Cadastra um novo usuário", tags = { "usuarios" },
            description = "Somente operadores com a role ROLE_MANUTENCAO",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponse.class))),
            @ApiResponse(responseCode = "400", description = "username ou senha inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Operador sem a role ROLE_MANUTENCAO", content = @Content),
            @ApiResponse(responseCode = "409", description = "Usuário já cadastrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mensagem.class))) })
    @PostMapping("/")
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody NovoUsuarioRequest usuario) {

        return ResponseEntity.status(HttpStatus.CREATED).body(service.cadastrar(usuario.username(), usuario.senha()));
    }

    // PUT - http://localhost:8080/unlock/joao
    @Operation(summary = "Desbloqueia um usuário", tags = { "administracao" },
            description = "Somente operadores com a role ROLE_ADMIN. Zera as tentativas inválidas do usuário.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário desbloqueado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mensagem.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Operador sem a role ROLE_ADMIN", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Mensagem.class))) })
    @PutMapping("/unlock/{userid}")
    public ResponseEntity<Mensagem> desbloquear(
            @Parameter(description = "username do usuário", example = "joao") @PathVariable String userid) {

        boolean estavaBloqueado = service.desbloquear(userid);
        String mensagem = estavaBloqueado ? "Usuário " + userid + " desbloqueado"
                : "Usuário " + userid + " não estava bloqueado";
        return ResponseEntity.ok(new Mensagem(mensagem));
    }

    // GET - http://localhost:8080/
    @Operation(summary = "Lista os usuários cadastrados", tags = { "usuarios" },
            description = "Somente operadores com a role ROLE_MANUTENCAO. A senha não é retornada.",
            security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UsuarioResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Operador sem a role ROLE_MANUTENCAO", content = @Content) })
    @GetMapping("/")
    public ResponseEntity<List<UsuarioResponse>> listar() {

        return ResponseEntity.ok(service.listar());
    }

}
