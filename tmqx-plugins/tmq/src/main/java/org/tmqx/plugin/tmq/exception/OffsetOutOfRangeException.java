package org.tmqx.plugin.tmq.exception;

public class OffsetOutOfRangeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OffsetOutOfRangeException() {
        super();
    }

    public OffsetOutOfRangeException(String message) {
        super(message);
    }

}
