package com.rev;

import com.rev.configuration.jersey.JerseyBootstrapper;

public class MainApplication {

private static JerseyBootstrapper jerseyBootstrapper;

    public static void main(String... args) {
        jerseyBootstrapper = new JerseyBootstrapper();
        jerseyBootstrapper.setupServer();

        try {
            jerseyBootstrapper.startServer();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jerseyBootstrapper.destroyServer();
        }
    }
}