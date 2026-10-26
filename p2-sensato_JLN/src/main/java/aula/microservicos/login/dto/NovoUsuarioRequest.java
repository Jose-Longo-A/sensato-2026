package aula.microservicos.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "NovoUsuarioRequest", description = "Dados para cadastrar um novo usuário")
public record NovoUsuarioRequest(
        @Schema(description = "Nome do usuário", example = "ana", maxLength = 100)
        @NotBlank(message = "O username é obrigatório")
        @Size(max = 100, message = "O username deve ter no máximo 100 caracteres")
        String username,

        @Schema(description = "Senha do usuário", example = "ana123", maxLength = 10)
        @NotBlank(message = "A senha é obrigatória")
        @Size(max = 10, message = "A senha deve ter no máximo 10 caracteres")
        String senha) {
}
