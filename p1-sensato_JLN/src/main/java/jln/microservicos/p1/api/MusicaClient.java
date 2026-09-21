package jln.microservicos.p1.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jln.microservicos.p1.musica.Musica;

@FeignClient(name = "musica-client", url = "${app.base-url}")
public interface MusicaClient {

    @GetMapping("/musicas/{id}")
    Musica buscar(@PathVariable("id") Integer id);

}
