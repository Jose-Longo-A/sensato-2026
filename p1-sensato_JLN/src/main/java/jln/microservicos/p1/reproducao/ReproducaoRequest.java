package jln.microservicos.p1.reproducao;

import jakarta.validation.constraints.NotNull;

public class ReproducaoRequest {

    @NotNull(message = "playlistId é obrigatório")
    public Integer playlistId;

}
