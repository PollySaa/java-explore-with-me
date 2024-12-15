package ru.practicum.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IncorrectParameterException extends RuntimeException {
    public IncorrectParameterException(String message) {
        super(message);
        log.error(message);
    }
}
