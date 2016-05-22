package ee.valja7.gate.persistence;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "Logs")
public class LogEntity {
    @Id
    int id;
    String UserName;
    String Date;
    String Logger;
    String Level;
    String Message;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getLogger() {
        return Logger;
    }

    public void setLogger(String logger) {
        Logger = logger;
    }

    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level = level;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
