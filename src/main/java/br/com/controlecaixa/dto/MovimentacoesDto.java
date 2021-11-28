package br.com.controlecaixa.dto;

public class MovimentacoesDto {

    private String key;
    private Object object;

    public MovimentacoesDto(String key, Object value) {
        this.key = key;
        this.object = value;
    }
}
