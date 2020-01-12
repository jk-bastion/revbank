package com.rev.common.exception;

public class AccountNotExistsException extends Exception {

    private static final long serialVersionUID = 1L;

    public AccountNotExistsException(String message) {
        super(message);
    }

}
