package aula.microservicos.login;

import org.springframework.http.HttpStatus;

// Erro de negócio já com o status HTTP que deve ser devolvido (tratado em TratamentoErros)
public class UsuarioException extends RuntimeException {

    private final HttpStatus status;

    public UsuarioException(HttpStatus status, String mensagem) {
        super(mensagem);
        this.status = status;
    }

    public static UsuarioException naoEncontrado(String username) {
        return new UsuarioException(HttpStatus.NOT_FOUND, "Usuário " + username + " não encontrado");
    }

    public HttpStatus getStatus() {
        return status;
    }

}
