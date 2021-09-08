package b2w.starwarsapi.service.handler;

import b2w.starwarsapi.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(PlanetRegistrationException.class)
    public ResponseEntity<String> handle(PlanetRegistrationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PlanetNotFoundException.class)
    public ResponseEntity<String> handle(PlanetNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(PlanetDeletionException.class)
    public ResponseEntity<String> handle(PlanetDeletionException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SwapiResponseException.class)
    public ResponseEntity<String> handle(SwapiResponseException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public void handle() {
    }

    @ExceptionHandler(InvalidPlanetNameException.class)
    public ResponseEntity<String> handle(InvalidPlanetNameException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FilmsAppearanceUpdateException.class)
    public ResponseEntity<String> handle(FilmsAppearanceUpdateException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
