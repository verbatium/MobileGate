package ee.valja7.gate.ui;

import ee.valja7.gate.HibernateContext;
import ee.valja7.gate.Principal;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.annotation.Retention;
import java.util.Date;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

public abstract class View extends RequestHandler {
    public static final String PRINCIPAL = "ee.valja7.gate.principal";
    @Inject
    VelocityEngine velocity;
    private String contentType = "text/html";
    private VelocityContext velocityContext = new VelocityContext();

    protected void put(String name, Object value) {
        velocityContext.put(name, value);
    }

    public abstract void execute() throws IOException;

    @Override
    public void doExecute() throws Exception {
        HibernateContext.openSession();
        String requestURI = request.getRequestURI();
        Labels.setContext(requestURI.substring(1).replace('/', '.'));
        put("Labels", Labels.class);
        put("logoutURL", "/admin/logout");
        Principal principal = getPrincipal();
        put("principal", principal);
        put("now", new Date());
        execute();
        if (this.getClass().getAnnotation(NoTemplate.class) != null || isJsonRedirect()) {
            return;
        }
        Template template = velocity.getTemplate("/templates" + requestURI + ".vm", "UTF-8");
        response.setContentType(contentType);
        response.setCharacterEncoding("UTF-8");

        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", -1);

        OutputStreamWriter out = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
        template.merge(velocityContext, out);
        out.close();
        HibernateContext.closeSession();
    }

    private Principal getPrincipal() {
        return (Principal) request.getAttribute(PRINCIPAL);
    }

    protected boolean isJsonRedirect() {
        return false;
    }

    @Retention(RUNTIME)
    public @interface NoTemplate {
    }
}
