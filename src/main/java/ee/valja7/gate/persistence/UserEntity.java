package ee.valja7.gate.persistence;

import ee.valja7.gate.Principal;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.Set;

@Entity(name = "Users")
public class UserEntity implements Principal {

    @Id
    String userName;
    String password;
    String email;
    String displayName;
    Timestamp lastLogin;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public Set<String> getRoles() {
        return null;
    }

    @Override
    public boolean checkPassword(String password) {
        return BCrypt.checkpw(password, this.password);
    }
}
