package aula.microservicos.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;

// Sem Bean Validation: qualquer credencial incorreta (inclusive vazia) deve responder 401
@Schema(name = "LoginRequest", description = "Credenciais para efetuar login")
public record LoginRequest(
        @Schema(description = "Nome do usuário", example = "joao", requiredMode = Schema.RequiredMode.REQUIRED) String username,
        @Schema(description = "Senha do usuário", example = "joao123", requiredMode = Schema.RequiredMode.REQUIRED) String senha) {
}
