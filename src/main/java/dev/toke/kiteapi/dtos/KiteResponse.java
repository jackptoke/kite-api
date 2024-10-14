package dev.toke.kiteapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class KiteResponse<T> {
    private T data;
    private int statusCode;
    private String message;
}
