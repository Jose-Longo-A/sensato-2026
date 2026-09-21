package jln.microservicos.p1.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import feign.FeignException;
import jln.microservicos.p1.common.MusicaNaoEncontradaException;
import jln.microservicos.p1.common.PlaylistNaoEncontradaException;
import jln.microservicos.p1.musica.Musica;
import jln.microservicos.p1.playlist.Playlist;
import jln.microservicos.p1.reproducao.Reproducao;
import jln.microservicos.p1.reproducao.ReproducaoRequest;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final MusicaClient musicaClient;
    private final PlaylistClient playlistClient;
    private final ReproducaoClient reproducaoClient;

    public ApiController(MusicaClient musicaClient, PlaylistClient playlistClient, ReproducaoClient reproducaoClient) {
        this.musicaClient = musicaClient;
        this.playlistClient = playlistClient;
        this.reproducaoClient = reproducaoClient;
    }

    @PostMapping("/adicionar/{playlistId}/musicas/{musicaId}")
    public ResponseEntity<String> adicionar(@PathVariable Integer playlistId, @PathVariable Integer musicaId) {

        Playlist playlist = buscarPlaylist(playlistId);
        Musica musica = buscarMusica(musicaId);

        playlistClient.adicionarMusica(playlistId, musicaId);

        String mensagem = "Música " + musica.titulo + " adicionada com sucesso à playlist " + playlist.nome;
        return new ResponseEntity<>(mensagem, HttpStatus.OK);
    }

    @PutMapping("/executar/{playlistId}")
    public ResponseEntity<Reproducao> executar(@PathVariable Integer playlistId) {

        buscarPlaylist(playlistId);

        ReproducaoRequest request = new ReproducaoRequest();
        request.playlistId = playlistId;

        Reproducao reproducao = reproducaoClient.executar(request);
        return new ResponseEntity<>(reproducao, HttpStatus.OK);
    }

    private Playlist buscarPlaylist(Integer playlistId) {
        try {
            return playlistClient.buscar(playlistId);
        } catch (FeignException.NotFound e) {
            throw new PlaylistNaoEncontradaException();
        }
    }

    private Musica buscarMusica(Integer musicaId) {
        try {
            return musicaClient.buscar(musicaId);
        } catch (FeignException.NotFound e) {
            throw new MusicaNaoEncontradaException();
        }
    }

}
