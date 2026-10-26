package aula.microservicos.login;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Usuário do sistema genérico (tabela usuarios do enunciado)
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    private String username;

    private String senha;

    protected Usuario() {
    }

    public Usuario(String username, String senha) {
        this.username = username;
        this.senha = senha;
    }

    public String getUsername() {
        return username;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

}
