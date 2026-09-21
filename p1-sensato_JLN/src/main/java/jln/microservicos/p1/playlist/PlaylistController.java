package jln.microservicos.p1.playlist;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jln.microservicos.p1.common.MusicaNaoEstaNaPlaylistException;
import jln.microservicos.p1.common.PlaylistNaoEncontradaException;

@RestController
@RequestMapping("/playlists")
public class PlaylistController {

    private final PlaylistRepository repo;
    private final PlaylistMusicaRepository playlistMusicaRepo;

    public PlaylistController(PlaylistRepository repo, PlaylistMusicaRepository playlistMusicaRepo) {
        this.repo = repo;
        this.playlistMusicaRepo = playlistMusicaRepo;
    }

    @PostMapping
    public ResponseEntity<Playlist> criar(@Valid @RequestBody PlaylistRequest request) {
        Playlist playlist = paraEntidade(null, request);
        repo.save(playlist);
        return new ResponseEntity<>(playlist, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Iterable<Playlist>> listar() {
        return new ResponseEntity<>(repo.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{playlistid}")
    public ResponseEntity<Playlist> buscar(@PathVariable Integer playlistid) {
        return repo.findById(playlistid)
                .map(playlist -> new ResponseEntity<>(playlist, HttpStatus.OK))
                .orElseThrow(PlaylistNaoEncontradaException::new);
    }

    @PutMapping("/{playlistid}")
    public ResponseEntity<Playlist> atualizar(@PathVariable Integer playlistid, @Valid @RequestBody PlaylistRequest request) {
        if (!repo.existsById(playlistid)) {
            throw new PlaylistNaoEncontradaException();
        }

        Playlist playlist = paraEntidade(playlistid, request);
        repo.save(playlist);
        return new ResponseEntity<>(playlist, HttpStatus.OK);
    }

    @DeleteMapping("/{playlistid}")
    public ResponseEntity<Void> excluir(@PathVariable Integer playlistid) {
        if (!repo.existsById(playlistid)) {
            throw new PlaylistNaoEncontradaException();
        }

        playlistMusicaRepo.deleteAll(playlistMusicaRepo.findByPlaylistId(playlistid));
        repo.deleteById(playlistid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{playlistid}/musicas/{musicaId}")
    public ResponseEntity<Void> adicionarMusica(@PathVariable Integer playlistid, @PathVariable Integer musicaId) {
        if (!repo.existsById(playlistid)) {
            throw new PlaylistNaoEncontradaException();
        }

        PlaylistMusica playlistMusica = new PlaylistMusica();
        playlistMusica.playlistId = playlistid;
        playlistMusica.musicaId = musicaId;
        playlistMusicaRepo.save(playlistMusica);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{playlistid}/musicas/{musicaId}")
    public ResponseEntity<Void> removerMusica(@PathVariable Integer playlistid, @PathVariable Integer musicaId) {
        if (!repo.existsById(playlistid)) {
            throw new PlaylistNaoEncontradaException();
        }

        PlaylistMusica playlistMusica = playlistMusicaRepo.findByPlaylistIdAndMusicaId(playlistid, musicaId)
                .orElseThrow(MusicaNaoEstaNaPlaylistException::new);

        playlistMusicaRepo.delete(playlistMusica);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{playlistid}/musicas")
    public ResponseEntity<List<Integer>> listarMusicas(@PathVariable Integer playlistid) {
        if (!repo.existsById(playlistid)) {
            throw new PlaylistNaoEncontradaException();
        }

        List<Integer> musicaIds = new ArrayList<>();
        playlistMusicaRepo.findByPlaylistId(playlistid).forEach(pm -> musicaIds.add(pm.musicaId));

        return new ResponseEntity<>(musicaIds, HttpStatus.OK);
    }

    private Playlist paraEntidade(Integer id, PlaylistRequest request) {
        Playlist playlist = new Playlist();
        playlist.id = id;
        playlist.nome = request.nome;
        playlist.descricao = request.descricao;
        return playlist;
    }

}
