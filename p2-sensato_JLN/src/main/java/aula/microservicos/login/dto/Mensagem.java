package aula.microservicos.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Mensagem", description = "Resposta simples com uma mensagem")
public record Mensagem(
        @Schema(description = "Texto da mensagem", example = "Login efetuado com sucesso") String mensagem) {
}
