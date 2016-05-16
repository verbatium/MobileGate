package ee.valja7.gate;

import ee.valja7.gate.persistence.UserService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LoginService {

    @Inject
    UserService userService;

    public Principal loginWithPassword(String username, String password) {
        HibernateContext.openSession();
        Principal principal = userService.findByUsername(username);
        HibernateContext.closeSession();
        return principal == null ? null : principal.checkPassword(password) ? principal : null;
    }
}
