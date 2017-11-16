package gash.router.server.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("user")
public class User {

    @Id
    private ObjectId objectId;

    private String username;

    private String email;

    private String password;

    private String recentActiveTime;

    public User() {
    }

    public User(String username) {
        super();
        this.username = username;
    }

    public User(String email, String recentActiveTime) {
        super();
        this.email = email;
        this.recentActiveTime = recentActiveTime;
    }

    public User(String username, String email, String password, String recentActiveTime) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;
        this.recentActiveTime = recentActiveTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRecentActiveTime() {
        return recentActiveTime;
    }

    public void setRecentActiveTime(String recentActiveTime) {
        this.recentActiveTime = recentActiveTime;
    }

}