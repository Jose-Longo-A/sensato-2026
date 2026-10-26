package aula.microservicos.login;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;

// Documentação disponível em:
// - JSON: http://localhost:8080/v3/api-docs
// - YAML: http://localhost:8080/v3/api-docs.yaml
// - Swagger UI: http://localhost:8080/swagger-ui/index.html
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "MS-AV-02 - Login, Segurança e Observabilidade",
                version = "1.0.0",
                description = "Login de usuários com bloqueio após falhas consecutivas. "
                        + "Endpoints de manutenção protegidos por roles (JdbcUserDetailsManager + HTTP Basic). "
                        + "Operadores de teste: admin/admin123 (ROLE_ADMIN) e manutencao/manut123 (ROLE_MANUTENCAO)."),
        tags = {
                @Tag(name = "autenticacao", description = "Login e troca de senha (acesso público)"),
                @Tag(name = "usuarios", description = "Cadastro e consulta de usuários (ROLE_MANUTENCAO)"),
                @Tag(name = "administracao", description = "Desbloqueio de usuários (ROLE_ADMIN)") })
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic",
        description = "Usuário e senha de um operador cadastrado nas tabelas users / authorities")
public class OpenApiConfig {
}
