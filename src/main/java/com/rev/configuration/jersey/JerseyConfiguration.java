package com.rev.configuration.jersey;

import com.rev.controller.AccountTransactionController;
import com.rev.controller.handler.*;
import com.rev.repository.AccountRepositoryImpl;
import com.rev.repository.TransactionRepositoryImpl;
import com.rev.server.AccountServer;
import com.rev.server.TransactionServer;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;
import com.rev.configuration.GuiceInjectorProvider;

import javax.inject.Inject;

class JerseyConfiguration extends ResourceConfig {

    @Inject
    public JerseyConfiguration(ServiceLocator serviceLocator) {

        // Guice injection bridge
        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
        GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
        guiceBridge.bridgeGuiceInjector(GuiceInjectorProvider.getGuiceInjector());

        // controllers
        register(AccountTransactionController.class);

        //services
        register(AccountServer.class);
        register(TransactionServer.class);

        //repositories
        register(AccountRepositoryImpl.class);
        register(TransactionRepositoryImpl.class);

        //handlers
        register(AccountCreationExceptionHandler.class);
        register(AccountBalanceUpdateExceptionHandler.class);
        register(AccountNotExistsExceptionHandler.class);
        register(InvalidCurrencyExceptionHandler.class);
        register(NotEnoughBalanceExceptionHandler.class);
        register(GeneralExceptionHandler.class);
    }
}