package com.rev.common;

public enum ErrorsCode {

    ACCOUNT_NOT_EXISTS("account does not exist"),
    SRC_ACCOUNT_NOT_EXISTS("source account does not exist"),
    DES_ACCOUNT_NOT_EXISTS("destination account does not exist"),
    INVALID_CURRENCY("not compatible currency"),
    ACCOUNT_CREATION_FAILED("account creation failed"),
    NOT_ENOUGH_BALANCE("not enough balance"),
    ACCOUNT_UPDATE_BALANCE_FAILED("account update balance failed"),
    UNEXPECTED_ERROR("unexpected error");

    private String message;

    ErrorsCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
