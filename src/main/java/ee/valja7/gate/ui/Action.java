package ee.valja7.gate.ui;

import com.google.gson.Gson;
import ee.valja7.gate.HibernateContext;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Callable;

import static org.apache.commons.codec.binary.Base64.encodeBase64;

public abstract class Action extends RequestHandler implements Callable<String> {
    private static final Logger LOG = Logger.getLogger(Action.class);
    @Inject
    protected Gson gson;

    public abstract String execute();

    public final void doExecute() throws Exception {
        String nextView = HibernateContext.doInTransaction(this);
        if (nextView != null)
            response.sendRedirect(nextView);
    }

    @Override
    public String call() throws Exception {
        return execute();
    }

    protected void error(String message) {
        addCookie("error", message);
    }

    protected void success(String message) {
        addCookie("success", message);
    }

    private void addCookie(String name, String message) {
        try {
            response.addCookie(new Cookie(name, new String(encodeBase64(message.getBytes("UTF8")))));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    protected void json(Object result) {
        response.setContentType("application/json;charset=UTF-8");
        try {
            response.getOutputStream().write(gson.toJson(result).getBytes("utf-8"));
        } catch (IOException e) {
            LOG.warn("Unexpected exception in json output", e);
        }
    }
}
