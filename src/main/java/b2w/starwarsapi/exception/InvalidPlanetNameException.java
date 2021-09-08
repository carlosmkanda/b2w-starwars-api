package b2w.starwarsapi.exception;

public class InvalidPlanetNameException extends RuntimeException {

    public InvalidPlanetNameException(String message) {
        super(message);
    }
}
