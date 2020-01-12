package com.rev.controller.handler;

import com.rev.common.exception.AccountCreationException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

public class AccountCreationExceptionHandler implements ExceptionMapper<AccountCreationException> {

    @Override
    public Response toResponse(AccountCreationException exception) {
        return Response.status(BAD_REQUEST)
                .type(MediaType.TEXT_XML)
                .entity(exception.getMessage())
                .build();
    }
}
