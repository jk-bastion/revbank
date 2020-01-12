package com.rev.controller.handler;

import com.rev.common.exception.InvalidCurrencyException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

public class InvalidCurrencyExceptionHandler implements ExceptionMapper<InvalidCurrencyException> {

    @Override
    public Response toResponse(InvalidCurrencyException exception) {
        return Response.status(BAD_REQUEST)
                .entity(exception.getMessage())
                .type(MediaType.TEXT_XML)
                .build();
    }
}
