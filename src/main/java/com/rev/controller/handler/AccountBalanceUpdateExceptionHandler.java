package com.rev.controller.handler;

import com.rev.common.exception.AccountBalanceUpdateException;
import com.rev.common.exception.AccountCreationException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

public class AccountBalanceUpdateExceptionHandler implements ExceptionMapper<AccountBalanceUpdateException> {

    @Override
    public Response toResponse(AccountBalanceUpdateException exception) {
        return Response.status(BAD_REQUEST)
                .type(MediaType.TEXT_XML)
                .entity(exception.getMessage())
                .build();
    }
}
