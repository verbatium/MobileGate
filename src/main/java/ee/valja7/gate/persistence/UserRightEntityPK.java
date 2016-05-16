package ee.valja7.gate.persistence;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class UserRightEntityPK implements Serializable {
    private String userName;
    private String right;

    @Column(name = "userName")
    @Id
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "right")
    @Id
    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserRightEntityPK that = (UserRightEntityPK) o;

        if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;
        return right != null ? right.equals(that.right) : that.right == null;

    }

    @Override
    public int hashCode() {
        int result = userName != null ? userName.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }
}
