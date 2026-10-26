package aula.microservicos.login;

public enum ResultadoLogin {

    SUCESSO("Login efetuado com sucesso"),
    CREDENCIAIS_INVALIDAS("Usuário ou senha inválidos"),
    USUARIO_BLOQUEADO("Usuário bloqueado por excesso de tentativas. Solicite o desbloqueio a um administrador");

    private final String mensagem;

    ResultadoLogin(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }

}
