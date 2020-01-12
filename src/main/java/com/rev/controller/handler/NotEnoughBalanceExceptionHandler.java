package com.rev.controller.handler;

import com.rev.common.exception.NotEnoughBalanceException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

public class NotEnoughBalanceExceptionHandler implements ExceptionMapper<NotEnoughBalanceException> {

    @Override
    public Response toResponse(NotEnoughBalanceException exception) {
        return Response.status(BAD_REQUEST)
                .entity(exception.getMessage())
                .type(MediaType.TEXT_XML)
                .build();
    }
}
