package jln.microservicos.p1.musica;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class MusicaRequest {

    @NotBlank(message = "Título é obrigatório")
    public String titulo;

    @NotBlank(message = "Artista é obrigatório")
    public String artista;

    @Size(max = 150, message = "Álbum deve ter no máximo 150 caracteres")
    public String album;

    @NotNull(message = "Duração é obrigatória")
    @Positive(message = "Duração deve ser maior que zero")
    public Integer duracao;

    @Size(max = 50, message = "Gênero deve ter no máximo 50 caracteres")
    public String genero;

}
