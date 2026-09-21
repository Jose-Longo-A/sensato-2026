package jln.microservicos.p1.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GerenciamentoException {

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Música não localizada!")
    @ExceptionHandler(MusicaNaoEncontradaException.class)
    public void handleMusicaNaoEncontrada(MusicaNaoEncontradaException e) {
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Playlist não localizada!")
    @ExceptionHandler(PlaylistNaoEncontradaException.class)
    public void handlePlaylistNaoEncontrada(PlaylistNaoEncontradaException e) {
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Música não está na playlist!")
    @ExceptionHandler(MusicaNaoEstaNaPlaylistException.class)
    public void handleMusicaNaoEstaNaPlaylist(MusicaNaoEstaNaPlaylistException e) {
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return errors;
    }

}
