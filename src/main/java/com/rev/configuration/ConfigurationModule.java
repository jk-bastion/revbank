package com.rev.configuration;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class ConfigurationModule extends AbstractModule {

    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("rev-db");
    private static final ThreadLocal<EntityManager> ENTITY_MANAGER = ThreadLocal.withInitial(() -> entityManagerFactory.createEntityManager());

        @Provides
        public EntityManager createEntityManager() {
           return ENTITY_MANAGER.get();
        }
    }

