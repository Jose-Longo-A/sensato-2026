package jln.microservicos.p1.musica;

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
import jln.microservicos.p1.common.MusicaNaoEncontradaException;

@RestController
@RequestMapping("/musicas")
public class MusicaController {

    private final MusicaRepository repo;

    public MusicaController(MusicaRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public ResponseEntity<Iterable<Musica>> listar() {
        return new ResponseEntity<>(repo.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Musica> buscar(@PathVariable Integer id) {
        return repo.findById(id)
                .map(musica -> new ResponseEntity<>(musica, HttpStatus.OK))
                .orElseThrow(MusicaNaoEncontradaException::new);
    }

    @PostMapping
    public ResponseEntity<Musica> criar(@Valid @RequestBody MusicaRequest request) {
        Musica musica = paraEntidade(null, request);
        repo.save(musica);
        return new ResponseEntity<>(musica, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Musica> atualizar(@PathVariable Integer id, @Valid @RequestBody MusicaRequest request) {
        if (!repo.existsById(id)) {
            throw new MusicaNaoEncontradaException();
        }

        Musica musica = paraEntidade(id, request);
        repo.save(musica);
        return new ResponseEntity<>(musica, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        if (!repo.existsById(id)) {
            throw new MusicaNaoEncontradaException();
        }

        repo.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Musica paraEntidade(Integer id, MusicaRequest request) {
        Musica musica = new Musica();
        musica.id = id;
        musica.titulo = request.titulo;
        musica.artista = request.artista;
        musica.album = request.album;
        musica.duracao = request.duracao;
        musica.genero = request.genero;
        return musica;
    }

}
