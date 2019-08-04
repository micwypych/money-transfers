package com.revolut.transfers.account.config;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class EntityManagerProvider {

    public EntityManager session() {
        EntityManager session = sessions.get();
        if (session == null) {
            session = factory.createEntityManager();
            sessions.set(session);
        }
        return session;
    }

    public EntityManagerProvider() {
        factory = Persistence.createEntityManagerFactory("test", properties());
        sessions = new ThreadLocal<>();
    }

    private Map<String, String> properties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("javax.persistence.jdbc.driver", "org.h2.Driver");
        properties.put("javax.persistence.jdbc.url", "jdbc:h2:mem:test");
        properties.put("javax.persistence.jdbc.user", "sa");
        properties.put("javax.persistence.jdbc.password", "");
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("show_sql", "true");
        properties.put("org.jadira.usertype.dateandtime.joda.PersistentDateTime", "dateTime");
        return properties;
    }

    private EntityManagerFactory factory;
    private ThreadLocal<EntityManager> sessions;
}
