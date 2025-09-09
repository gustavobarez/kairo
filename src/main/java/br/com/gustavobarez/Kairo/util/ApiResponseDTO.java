package br.com.gustavobarez.Kairo.util;

import lombok.Getter;

@Getter
public class ApiResponseDTO<T> {
    
    private T data;
    private String message;

    public ApiResponseDTO(T data, String operation) {
        this.data = data;
        this.message = buildMessage(operation);
    }

    private String buildMessage(String operation) {
        return "Operation from handler: " + operation + " successfully";
    }

}
