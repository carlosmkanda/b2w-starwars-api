package b2w.starwarsapi.exception;

public class PlanetNotFoundException extends RuntimeException{

    public PlanetNotFoundException(String message) {
        super(message);
    }
}
