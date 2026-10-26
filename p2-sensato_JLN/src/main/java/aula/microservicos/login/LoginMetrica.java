package aula.microservicos.login;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

// Indicadores expostos em /actuator/metrics e /actuator/prometheus:
// - login.validos      -> login_validos_total
// - login.invalidos    -> login_invalidos_total
// - usuarios.bloqueados -> usuarios_bloqueados
@Component
public class LoginMetrica {

    private final Counter loginsValidos;
    private final Counter loginsInvalidos;

    public LoginMetrica(MeterRegistry registry, LoginAttemptRepository tentativas) {
        loginsValidos = Counter.builder("login.validos")
                .description("Quantidade de logins válidos")
                .register(registry);

        loginsInvalidos = Counter.builder("login.invalidos")
                .description("Quantidade de logins inválidos")
                .register(registry);

        // o gauge consulta o banco a cada coleta, então reflete bloqueios e desbloqueios
        Gauge.builder("usuarios.bloqueados", tentativas, LoginAttemptRepository::countByLockedTrue)
                .description("Total de usuários bloqueados")
                .register(registry);
    }

    public void loginValido() {
        loginsValidos.increment();
    }

    public void loginInvalido() {
        loginsInvalidos.increment();
    }

}
