package ee.valja7.gate.persistence;

import javax.persistence.*;

@Entity
@Table(name = "Rights")
public class RightEntity {
    private String right;
    private String description;

    @Id
    @Column(name = "right")
    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RightEntity that = (RightEntity) o;

        if (right != null ? !right.equals(that.right) : that.right != null) return false;
        return description != null ? description.equals(that.description) : that.description == null;

    }

    @Override
    public int hashCode() {
        int result = right != null ? right.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }
}
