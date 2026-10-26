package aula.microservicos.login;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

// Sobe a aplicação numa porta aleatória e chama os endpoints via HTTP.
// Cada teste cria os próprios usuários, pois o banco H2 é compartilhado entre os testes.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoginApplicationTests {

	record Resposta(int status, String body) {
	}

	@Value("${local.server.port}")
	private int porta;

	private RestClient client;

	@BeforeEach
	void configurarCliente() {
		client = RestClient.builder()
				.baseUrl("http://localhost:" + porta)
				.requestFactory(new JdkClientHttpRequestFactory())
				.build();
	}

	@Test
	void loginComCredenciaisValidasRetorna200() {
		criarUsuario("teste1", "s1");
		assertEquals(200, login("teste1", "s1").status());
	}

	@Test
	void loginComUsuarioInexistenteRetorna401() {
		assertEquals(401, login("naoexiste", "x").status());
	}

	@Test
	void segundaFalhaBloqueiaOUsuario() {
		criarUsuario("teste2", "s2");

		assertEquals(401, login("teste2", "errada").status());

		Resposta segunda = login("teste2", "errada");
		assertEquals(401, segunda.status());
		assertTrue(segunda.body().contains("bloqueado"));

		// bloqueado: nem a senha correta funciona
		assertEquals(401, login("teste2", "s2").status());
		assertTrue(chamar(HttpMethod.GET, "/", null, "manutencao", "manut123").body()
				.contains("{\"username\":\"teste2\",\"bloqueado\":true,\"tentativasInvalidas\":2}"));
	}

	@Test
	void loginValidoZeraAsFalhas() {
		criarUsuario("teste3", "s3");
		assertEquals(401, login("teste3", "errada").status());
		assertEquals(200, login("teste3", "s3").status());
		// se as falhas não fossem zeradas, esta seria a 2a e bloquearia
		assertEquals(401, login("teste3", "errada").status());
		assertEquals(200, login("teste3", "s3").status());
	}

	@Test
	void desbloqueioSomenteParaRoleAdmin() {
		criarUsuario("teste4", "s4");
		login("teste4", "errada");
		login("teste4", "errada");

		assertEquals(401, chamar(HttpMethod.PUT, "/unlock/teste4", null, null, null).status());
		assertEquals(403, chamar(HttpMethod.PUT, "/unlock/teste4", null, "manutencao", "manut123").status());
		assertEquals(200, chamar(HttpMethod.PUT, "/unlock/teste4", null, "admin", "admin123").status());
		assertEquals(200, login("teste4", "s4").status());

		assertEquals(404, chamar(HttpMethod.PUT, "/unlock/naoexiste", null, "admin", "admin123").status());
	}

	@Test
	void cadastroSomenteParaRoleManutencao() {
		String json = "{\"username\":\"teste5\",\"senha\":\"s5\"}";

		assertEquals(401, chamar(HttpMethod.POST, "/", json, null, null).status());
		assertEquals(401, chamar(HttpMethod.POST, "/", json, "manutencao", "senhaerrada").status());
		assertEquals(403, chamar(HttpMethod.POST, "/", json, "admin", "admin123").status());
		assertEquals(201, chamar(HttpMethod.POST, "/", json, "manutencao", "manut123").status());
		assertEquals(409, chamar(HttpMethod.POST, "/", json, "manutencao", "manut123").status());

		String senhaLonga = "{\"username\":\"teste6\",\"senha\":\"maisde10caracteres\"}";
		assertEquals(400, chamar(HttpMethod.POST, "/", senhaLonga, "manutencao", "manut123").status());
	}

	@Test
	void listagemSomenteParaRoleManutencaoESemSenha() {
		Resposta lista = chamar(HttpMethod.GET, "/", null, "manutencao", "manut123");
		assertEquals(200, lista.status());
		assertTrue(lista.body().contains("\"username\":\"joao\""));
		assertFalse(lista.body().contains("senha"));

		assertEquals(401, chamar(HttpMethod.GET, "/", null, null, null).status());
		assertEquals(403, chamar(HttpMethod.GET, "/", null, "admin", "admin123").status());
	}

	@Test
	void trocaDeSenhaPublicaConfereASenhaAtual() {
		criarUsuario("teste7", "s7");

		assertEquals(401, chamar(HttpMethod.PUT, "/trocarsenha/teste7",
				"{\"atual\":\"errada\",\"nova\":\"n7\"}", null, null).status());
		assertEquals(200, chamar(HttpMethod.PUT, "/trocarsenha/teste7",
				"{\"atual\":\"s7\",\"nova\":\"n7\"}", null, null).status());

		assertEquals(401, login("teste7", "s7").status());
		assertEquals(200, login("teste7", "n7").status());

		assertEquals(404, chamar(HttpMethod.PUT, "/trocarsenha/naoexiste",
				"{\"atual\":\"x\",\"nova\":\"y\"}", null, null).status());
	}

	@Test
	void metricasExpostasParaOPrometheus() {
		login("joao", "joao123");
		login("naoexiste", "x");

		Resposta metricas = chamar(HttpMethod.GET, "/actuator/prometheus", null, null, null);
		assertEquals(200, metricas.status());
		assertTrue(metricas.body().contains("login_validos_total"));
		assertTrue(metricas.body().contains("login_invalidos_total"));
		assertTrue(metricas.body().contains("usuarios_bloqueados"));
	}

	@Test
	void documentacaoOpenApiPublica() {
		Resposta docs = chamar(HttpMethod.GET, "/v3/api-docs", null, null, null);
		assertEquals(200, docs.status());
		assertTrue(docs.body().contains("/trocarsenha/{userid}"));
		assertTrue(docs.body().contains("basicAuth"));

		assertEquals(200, chamar(HttpMethod.GET, "/swagger-ui/index.html", null, null, null).status());
	}

	private void criarUsuario(String username, String senha) {
		String json = "{\"username\":\"" + username + "\",\"senha\":\"" + senha + "\"}";
		assertEquals(201, chamar(HttpMethod.POST, "/", json, "manutencao", "manut123").status());
	}

	private Resposta login(String username, String senha) {
		String json = "{\"username\":\"" + username + "\",\"senha\":\"" + senha + "\"}";
		return chamar(HttpMethod.POST, "/login", json, null, null);
	}

	private Resposta chamar(HttpMethod metodo, String path, String json, String usuario, String senha) {
		RestClient.RequestBodySpec requisicao = client.method(metodo).uri(path);
		if (usuario != null) {
			requisicao.headers(headers -> headers.setBasicAuth(usuario, senha));
		}
		if (json != null) {
			requisicao.contentType(MediaType.APPLICATION_JSON).body(json);
		}
		return requisicao.exchange((request, response) -> new Resposta(response.getStatusCode().value(),
				new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8)));
	}

}
