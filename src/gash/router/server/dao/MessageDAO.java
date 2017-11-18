package gash.router.server.dao;

import gash.router.server.entity.Group;
import gash.router.server.entity.Message;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import java.util.List;

public interface MessageDAO extends DAO<Message, ObjectId> {

    void postMessage(Message message);

    List<Message> getAllMessagesByUser(String username, List<Long> groupIds);

}