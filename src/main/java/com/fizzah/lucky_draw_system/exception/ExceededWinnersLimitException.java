package com.fizzah.lucky_draw_system.exception;

public class ExceededWinnersLimitException extends RuntimeException {
    public ExceededWinnersLimitException(String message) {
        super(message);
    }
}
