package ee.valja7.gate;

import ee.valja7.gate.persistence.UserEntity;
import ee.valja7.gate.persistence.UserService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LoginService {

    @Inject
    UserService userService;

    public Principal loginWithPassword(String username, String password) {
        HibernateContext.getSession();
        UserEntity principal = userService.findByUsername(username);
        boolean passwordIsCorrect = principal.checkPassword(password);
        if (passwordIsCorrect)
            userService.save((principal));
        return principal == null ? null : passwordIsCorrect ? principal : null;
    }
}
