package br.com.gustavobarez.Kairo.exceptions;

public class ResourceAlreadyExistsException extends RuntimeException {
    private final String field;

    public ResourceAlreadyExistsException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}