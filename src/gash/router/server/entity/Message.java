package gash.router.server.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import routing.Pipe;

@Entity("message")
public class Message {

    @Id
    private ObjectId objectId;

    private Pipe.Message.Type type;

    private String from;

    private String payload;

    private String to;

    private String timestamp;

    private Pipe.Message.Status status;

    public Message() {
    }

    public Message(Pipe.Message.Type type, String from, String payload, String to, String timestamp, Pipe.Message.Status status) {
        super();
        this.type = type;
        this.from = from;
        this.payload = payload;
        this.to = to;
        this.timestamp = timestamp;
        this.status = status;
    }

    public ObjectId getObjectId() {
        return objectId;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
    }

    public Pipe.Message.Type getType() {
        return type;
    }

    public void setType(Pipe.Message.Type type) {
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

    public Pipe.Message.Status getStatus() {
        return status;
    }

    public void setStatus(Pipe.Message.Status status) {
        this.status = status;
    }
}