package gash.router.server.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("message")
public class Message {

    @Id
    private ObjectId objectId;

    private String type;

    private String from;

    private String payload;

    private String to;

    private String timestamp;

    private boolean read;

    public Message() {
    }

    public Message(String type, String from, String payload, String to, String timestamp, boolean read) {
        super();
        this.type = type;
        this.from = from;
        this.payload = payload;
        this.to = to;
        this.timestamp = timestamp;
        this.read = read;
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}