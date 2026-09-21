package jln.microservicos.p1.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jln.microservicos.p1.reproducao.Reproducao;
import jln.microservicos.p1.reproducao.ReproducaoRequest;

@FeignClient(name = "reproducao-client", url = "${app.base-url}")
public interface ReproducaoClient {

    @PostMapping("/reproducao")
    Reproducao executar(@RequestBody ReproducaoRequest request);

}
