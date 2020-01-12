package com.rev.configuration;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

public class GuiceInjectorProvider {
    private static Injector guiceInjector = null;

    public static Injector getGuiceInjector() {
        if (guiceInjector == null) {
            guiceInjector = Guice.createInjector(Stage.PRODUCTION,  new  ConfigurationModule());
        }
        return guiceInjector;
    }

}
