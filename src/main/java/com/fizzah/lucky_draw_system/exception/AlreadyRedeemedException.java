package com.fizzah.lucky_draw_system.exception;

public class AlreadyRedeemedException extends RuntimeException {
    public AlreadyRedeemedException(String message) {
        super(message);
    }
}
