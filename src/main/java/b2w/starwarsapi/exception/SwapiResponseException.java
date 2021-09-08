package b2w.starwarsapi.exception;

public class SwapiResponseException extends RuntimeException{

    public SwapiResponseException(String message) {
        super(message);
    }
}
