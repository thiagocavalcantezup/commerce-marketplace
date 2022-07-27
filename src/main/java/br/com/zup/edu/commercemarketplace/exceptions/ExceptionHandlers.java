package br.com.zup.edu.commercemarketplace.exceptions;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ExceptionHandlers {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlers.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                          WebRequest webRequest) {
        HttpStatus status = BAD_REQUEST;
        ErroPadronizado erroPadronizado = getErroPadronizado(status, webRequest);

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        fieldErrors.forEach(erroPadronizado::adicionar);

        LOGGER.error("MethodArgumentNotValidException: " + ex.getLocalizedMessage(), ex);
        return ResponseEntity.status(status).body(erroPadronizado);
    }

    public ResponseEntity<?> handleResponseStatus(ResponseStatusException ex,
                                                  WebRequest webRequest) {
        HttpStatus status = ex.getStatus();
        ErroPadronizado erroPadronizado = getErroPadronizado(status, webRequest);

        erroPadronizado.adicionar(ex.getReason());

        LOGGER.error("ResponseStatusException: " + ex.getReason(), ex);
        return ResponseEntity.status(status).body(erroPadronizado);
    }

    private ErroPadronizado getErroPadronizado(HttpStatus status, WebRequest webRequest) {
        Integer codigoHttp = status.value();
        String mensagemHttp = status.getReasonPhrase();
        String caminho = webRequest.getDescription(false).replace("uri=", "");

        return new ErroPadronizado(codigoHttp, mensagemHttp, caminho);
    }

}
