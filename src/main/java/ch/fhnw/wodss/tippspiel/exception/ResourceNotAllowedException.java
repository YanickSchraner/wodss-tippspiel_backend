package ch.fhnw.wodss.tippspiel.exception;

public class ResourceNotAllowedException extends RuntimeException {
    public ResourceNotAllowedException(String message) {
        super(message);
    }
}
