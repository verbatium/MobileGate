package ee.valja7.gate.persistance;

import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity(name = "Users")
public class User {
    @Id
    String userName;
    String password;
    String email;
    String displayName;
    Timestamp lastLogin;

    public boolean isPasswordCorect(String password) {
        return BCrypt.checkpw(password, this.password);
    }
}
