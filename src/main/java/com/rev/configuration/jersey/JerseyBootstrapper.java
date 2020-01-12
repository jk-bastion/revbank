package com.rev.configuration.jersey;

import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.servlet.ServletContainer;
import com.rev.configuration.GuiceInjectorProvider;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

public class JerseyBootstrapper {

    private static final int PORT = 8086;
    private Server jettyServer;

    public JerseyBootstrapper() {
        GuiceInjectorProvider.getGuiceInjector().injectMembers(this);
    }

    public void setupServer() {
        jettyServer = new Server(PORT);

        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setServer(jettyServer);
        webAppContext.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));

        ServletHolder holder = new ServletHolder(ServletContainer.class);
        holder.setInitParameter("javax.ws.rs.Application", JerseyConfiguration.class.getCanonicalName());

        webAppContext.addServlet(holder, "/*");
        webAppContext.setResourceBase("/");
        webAppContext.setContextPath("/rev");

        HandlerCollection handlerCollection = new HandlerCollection();
        handlerCollection.addHandler(webAppContext);

        jettyServer.setHandler(handlerCollection);
    }

    public void startServer() throws Exception {
        jettyServer.start();
        jettyServer.join();
    }

    public void startServerForTest() throws Exception {
        jettyServer.start();
    }

    public void destroyServer() {
        jettyServer.destroy();
    }

    public void stopServer() {
        try {
            jettyServer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
