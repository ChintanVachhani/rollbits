package gash.router.server.dao.impl;

import gash.router.server.dao.MessageDAO;
import gash.router.server.entity.Message;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;

import java.util.List;

public class MessageDAOImpl extends BasicDAO<Message, ObjectId> implements MessageDAO {

    public MessageDAOImpl(Class<Message> entityClass, Datastore ds) {
        super(entityClass, ds);
    }

    public void postMessage(Message message) {
        save(message);
    }

    public List<Message> getAllMessagesByUser(String id) {

        Query<Message> query = createQuery().
                field("type").equal("user").
                field("to").equal(id);

        return query.asList();
    }

    public List<Message> getAllMessagesByGroup(String id) {

        Query<Message> query = createQuery().
                field("type").equal("group").
                field("to").equal(id);

        return query.asList();
    }

}