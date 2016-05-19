package ee.valja7.gate.ui.admin;

import ee.valja7.gate.persistence.UserEntity;
import ee.valja7.gate.persistence.UserService;
import ee.valja7.gate.ui.View;

import javax.inject.Inject;
import java.io.IOException;

public class EditUser extends View {
    @Inject
    UserService userService;

    @Override
    public void execute() throws IOException {
        String username = request.getParameter("username");
        UserEntity userEntity;
        if (username != null)
            userEntity = userService.findByUsername(username);
        else
            userEntity = new UserEntity();
        put("user", userEntity);
    }
}
