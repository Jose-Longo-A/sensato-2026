package jln.microservicos.p1.reproducao;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reproducao")
public class ReproducaoController {

    private final ReproducaoRepository repo;

    public ReproducaoController(ReproducaoRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public ResponseEntity<Reproducao> registrar(@Valid @RequestBody ReproducaoRequest request) {
        Reproducao reproducao = new Reproducao();
        reproducao.playlistId = request.playlistId;
        reproducao.datahora = LocalDateTime.now();

        repo.save(reproducao);
        return new ResponseEntity<>(reproducao, HttpStatus.OK);
    }

    @GetMapping("/{playlistid}")
    public ResponseEntity<Iterable<Reproducao>> listar(@PathVariable Integer playlistid) {
        return new ResponseEntity<>(repo.findByPlaylistId(playlistid), HttpStatus.OK);
    }

    @GetMapping("/total/{playlistid}")
    public ResponseEntity<Long> total(@PathVariable Integer playlistid) {
        return new ResponseEntity<>(repo.countByPlaylistId(playlistid), HttpStatus.OK);
    }

}
