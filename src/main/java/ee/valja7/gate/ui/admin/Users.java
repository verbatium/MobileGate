package ee.valja7.gate.ui.admin;

import ee.valja7.gate.persistence.UserService;
import ee.valja7.gate.persistence.UsersEntity;
import ee.valja7.gate.ui.View;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

public class Users extends View {
    private static final Logger LOG = Logger.getLogger(Users.class);

    @Inject
    UserService userService;

    @Override
    public void execute() throws IOException {
        List<UsersEntity> users = userService.getAll();
        put("usersCount", users.size());
        put("users", users);
    }
}
