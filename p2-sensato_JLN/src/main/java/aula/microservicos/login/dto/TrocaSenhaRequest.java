package aula.microservicos.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "TrocaSenhaRequest", description = "Senha atual e nova senha do usuário")
public record TrocaSenhaRequest(
        @Schema(description = "Senha atual", example = "joao123")
        @NotBlank(message = "A senha atual é obrigatória")
        String atual,

        @Schema(description = "Nova senha", example = "novo123", maxLength = 10)
        @NotBlank(message = "A nova senha é obrigatória")
        @Size(max = 10, message = "A nova senha deve ter no máximo 10 caracteres")
        String nova) {
}
