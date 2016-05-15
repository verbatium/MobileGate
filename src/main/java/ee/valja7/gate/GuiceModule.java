package ee.valja7.gate;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.apache.velocity.app.VelocityEngine;

import javax.inject.Singleton;

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
