package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
    final String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

}
