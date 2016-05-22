package ee.valja7.gate;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import ee.valja7.gate.persistence.LogEntity;
import ee.valja7.gate.persistence.PreferenceEntity;
import ee.valja7.gate.persistence.UserEntity;
import org.apache.velocity.app.VelocityEngine;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import javax.inject.Singleton;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.velocity.runtime.RuntimeConstants.*;
import static org.apache.velocity.runtime.log.Log4JLogChute.RUNTIME_LOG_LOG4J_LOGGER;

abstract class GuiceModule extends AbstractModule {
    @Override
    protected void configure() {

    }

    private String getEnvironmentName() {
        return this.getClass().getSimpleName().replace("GuiceModule", "");
    }

    private boolean isDevelopment() {
        return "Development".equals(getEnvironmentName());
    }

    @Provides
    @Singleton
    SessionFactory buildHibernateSessionFactory() {
        Configuration configuration = new Configuration();

        List<Class<?>> entities = asList(
                UserEntity.class,
                PreferenceEntity.class,
                LogEntity.class
        );

        for (Class<?> entity : entities) {
            configuration.addAnnotatedClass(entity);
        }
        configuration.configure();
        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
                configuration.getProperties()).build();

        return configuration.buildSessionFactory(serviceRegistry);
    }

    @Provides
    @Singleton
    VelocityEngine createVelocityEngine() throws Exception {
        VelocityEngine velocity = new VelocityEngine();
        velocity.setProperty(RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute");
        velocity.setProperty(RUNTIME_LOG_LOG4J_LOGGER, "Velocity");
        velocity.setProperty(RESOURCE_LOADER, "file");
        velocity.setProperty(FILE_RESOURCE_LOADER_PATH, "webapp/WEB-INF");
        velocity.setProperty(FILE_RESOURCE_LOADER_CACHE, isDevelopment() ? "false" : "true");
        velocity.setProperty(VM_LIBRARY, "/templates/macros.vm");
        velocity.setProperty(VM_LIBRARY_AUTORELOAD, "true");
        velocity.setProperty(INPUT_ENCODING, "UTF-8");
        velocity.init();
        return velocity;
    }

    protected abstract String getModemPortName();
}
