package ee.valja7.gate.ui.admin;

import ee.valja7.gate.persistence.UserEntity;
import ee.valja7.gate.persistence.UserService;
import ee.valja7.gate.ui.Action;

import javax.inject.Inject;

public class SaveUser extends Action {
    @Inject
    UserService userService;

    @Override
    public String execute() {
        String username = request.getParameter("username");
        UserEntity userEntity = null;
        if (username != null)
            userEntity = userService.findByUsername(username);
        if (userEntity == null)
            userEntity = new UserEntity(username);

        String password1 = request.getParameter("password1");
        String password2 = request.getParameter("password2");

        if (password1.equals(password2)) {
            userEntity.setPassword(password1);
        }
        String email = request.getParameter("email");
        userEntity.setEmail(email);
        String displayName = request.getParameter("displayName");
        userEntity.setDisplayName(displayName);
        userService.save(userEntity);
        return "users";
    }
}
