package jln.microservicos.p1.playlist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PlaylistRequest {

    @NotBlank(message = "Nome é obrigatório")
    public String nome;

    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    public String descricao;

}
