package com.rev.controller.handler;

import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

@Slf4j
public class GeneralExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        log.info("{}", exception.getMessage());
        return Response.status(INTERNAL_SERVER_ERROR)
                .entity("Something bad happened.")
                .type(MediaType.TEXT_XML)
                .build();
    }
}
