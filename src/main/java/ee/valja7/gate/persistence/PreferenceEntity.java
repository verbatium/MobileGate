package ee.valja7.gate.persistence;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Preferences")
public class PreferenceEntity implements Serializable {
    @Id
    @Enumerated(value = EnumType.STRING)
    private Categories category;
    @Id
    private String name;
    private String value;
    private String description;

    public PreferenceEntity() {
    }

    public PreferenceEntity(Categories category, String name, PreferenceTypes type, String value) {
        this.category = category;
        this.name = name;
        this.value = value;
    }

    public Categories getCategory() {
        return category;
    }

    public void setCategory(Categories category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
