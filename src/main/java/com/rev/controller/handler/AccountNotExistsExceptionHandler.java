package com.rev.controller.handler;

import com.rev.common.exception.AccountNotExistsException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

public class AccountNotExistsExceptionHandler implements ExceptionMapper<AccountNotExistsException> {

    @Override
    public Response toResponse(AccountNotExistsException exception) {
        return Response.status(NOT_FOUND)
                .entity(exception.getMessage())
                .type(MediaType.TEXT_XML)
                .build();
    }
}
