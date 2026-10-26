package aula.microservicos.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;

// A senha nunca é devolvida
@Schema(name = "Usuario", description = "Usuário cadastrado e sua situação de bloqueio")
public record UsuarioResponse(
        @Schema(description = "Nome do usuário", example = "joao") String username,
        @Schema(description = "Indica se o usuário está bloqueado", example = "false") boolean bloqueado,
        @Schema(description = "Falhas de login consecutivas", example = "0") int tentativasInvalidas) {
}
