package ee.valja7.gate.ui.admin;

import ee.valja7.gate.LoginService;
import ee.valja7.gate.Principal;
import ee.valja7.gate.ui.Action;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import javax.inject.Inject;

public class Login extends Action {
    private static final Logger LOG = Logger.getLogger(Login.class);
    @Inject
    LoginService loginService;

    @Override
    public String execute() {
        try {
            Principal principal = null;
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            principal = loginService.loginWithPassword(username, password);
            if (principal != null) {
                MDC.put("username", "[" + principal.getUserName() + "] ");

                try {
                    request.getSession().setAttribute("principal", principal);
                } finally {
                    MDC.remove("username");
                }
            } else {
                LOG.warn("Login failure for user: " + principal);
            }

        } catch (Exception e) {
            LOG.warn("Login failure", e);
        }

        return "Dashboard";
    }
}
