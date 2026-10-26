package aula.microservicos.login;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import aula.microservicos.login.dto.Mensagem;

@RestControllerAdvice
public class TratamentoErros {

    @ExceptionHandler(UsuarioException.class)
    public ResponseEntity<Mensagem> handleUsuario(UsuarioException e) {
        return ResponseEntity.status(e.getStatus()).body(new Mensagem(e.getMessage()));
    }

    // Especifico para validação de campos
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<String, String>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> erros.put(error.getField(), error.getDefaultMessage()));
        return erros;
    }

}
