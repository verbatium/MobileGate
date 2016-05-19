package ee.valja7.gate.persistence;

import ee.valja7.gate.Principal;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Entity(name = "Users")
public class UserEntity implements Principal {

    @Id
    String userName;
    String password;
    String email;
    String displayName;
    Long lastLogin;

    public UserEntity(String userName) {

        this.userName = userName;
    }

    public UserEntity() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getLastLogin() {
        return lastLogin == null ? null : new Timestamp(lastLogin);
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin.getTime();
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
        boolean passwordCorrect = BCrypt.checkpw(password, this.password);
        if (passwordCorrect)
            setLastLogin(new Timestamp(new Date().getTime()));
        return passwordCorrect;
    }
}
