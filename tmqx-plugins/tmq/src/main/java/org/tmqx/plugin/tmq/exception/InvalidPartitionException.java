package org.tmqx.plugin.tmq.exception;

public class InvalidPartitionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidPartitionException() {
        super();
    }

    public InvalidPartitionException(String message) {
        super(message);
    }

}
