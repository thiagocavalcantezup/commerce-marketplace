package br.com.zup.edu.commercemarketplace.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ExceptionHandlers {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlers.class);

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
