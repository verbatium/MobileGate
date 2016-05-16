package ee.valja7.gate.persistence;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "Users", schema = "", catalog = "")
public class UsersEntity {
    private String userName;
    private String password;
    private String email;
    private String displayName;
    private String lastLogin;
    private Collection<PhoneEntity> phonesByUserName;
    private Collection<UserRightEntity> userRightsesByUserName;

    @Id
    @Column(name = "userName")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Basic
    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "displayName")
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Basic
    @Column(name = "lastLogin")
    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UsersEntity that = (UsersEntity) o;

        if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) return false;
        return lastLogin != null ? lastLogin.equals(that.lastLogin) : that.lastLogin == null;

    }

    @Override
    public int hashCode() {
        int result = userName != null ? userName.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (lastLogin != null ? lastLogin.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "usersByUserName")
    public Collection<PhoneEntity> getPhonesByUserName() {
        return phonesByUserName;
    }

    public void setPhonesByUserName(Collection<PhoneEntity> phonesByUserName) {
        this.phonesByUserName = phonesByUserName;
    }

    @OneToMany(mappedBy = "usersByUserName")
    public Collection<UserRightEntity> getUserRightsesByUserName() {
        return userRightsesByUserName;
    }

    public void setUserRightsesByUserName(Collection<UserRightEntity> userRightsesByUserName) {
        this.userRightsesByUserName = userRightsesByUserName;
    }
}
