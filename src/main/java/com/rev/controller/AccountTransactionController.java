package com.rev.controller;

import com.rev.common.exception.AccountBalanceUpdateException;
import com.rev.common.exception.AccountCreationException;
import com.rev.common.exception.AccountNotExistsException;
import com.rev.dto.AccountDto;
import com.rev.dto.TransactionDto;
import com.rev.server.AccountServer;
import com.rev.server.TransactionServer;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.CREATED;

@Path("")
public class AccountTransactionController {

    private final AccountServer accountServer;
    private final TransactionServer transactionServer;

    @Inject
    public AccountTransactionController(AccountServer accountServer, TransactionServer transactionServer) {
        this.accountServer = accountServer;
        this.transactionServer = transactionServer;
    }

    @POST
    @Path("/account")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount(AccountDto accountDto) throws AccountCreationException {
        return Response.status(CREATED)
                        .entity(accountServer.createAccount(accountDto))
                        .build();
    }
    @PUT
    @Path("/account")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAccount(AccountDto accountDto) throws AccountNotExistsException, AccountBalanceUpdateException {
        accountServer.updateAccountBalance(accountDto);
        return Response.noContent()
                .build();
    }

    @GET
    @Path("/account/{accountId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccount(@PathParam("accountId") String accountId) throws AccountNotExistsException {
        return Response.ok()
                        .entity(accountServer.findAccountById(Long.parseLong(accountId)))
                        .build();
    }

    @GET
    @Path("/account")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAccounts() {
        return Response.ok()
                        .entity(accountServer.getAllAccounts())
                        .build();
    }

    @DELETE
    @Path("/account/{accountId}")
    public Response deleteAccount(@PathParam("accountId") String accountId) throws AccountNotExistsException {
         accountServer.deleteAccount(Long.parseLong(accountId));
         return Response.noContent().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/account/transaction")
    public Response addTransaction(TransactionDto transactionDto) throws Exception {
        transactionServer.addTransaction(transactionDto);
        return Response.status(CREATED).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/account/{accountId}/transaction")
    public Response getTransactionsForAccount(@PathParam("accountId") String accountId) throws AccountNotExistsException {
        return Response.ok()
                .entity(transactionServer.getTransactionsForAccount(Long.parseLong(accountId)))
                .build();
    }
}
