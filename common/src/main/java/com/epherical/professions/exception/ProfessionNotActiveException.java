package com.epherical.professions.exception;

public class ProfessionNotActiveException extends Exception {

    public ProfessionNotActiveException(String message) {
        super(message);
    }

    public ProfessionNotActiveException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProfessionNotActiveException(Throwable cause) {
        super(cause);
    }
}
