package gash.router.server.dao.impl;

import gash.router.server.dao.MessageDAO;
import gash.router.server.dao.UserDAO;
import gash.router.server.entity.Message;
import gash.router.server.entity.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;

import java.util.ArrayList;
import java.util.List;

public class MessageDAOImpl extends BasicDAO<Message, ObjectId> implements MessageDAO {

    public MessageDAOImpl(Class<Message> entityClass, Datastore ds) {
        super(entityClass, ds);
    }

    public void postMessage(Message message) {
        save(message);
    }

    public List<Message> getAllMessagesByUser(String username, List<String> groupNames) {

        List<Message> allMessages = new ArrayList<Message>();
        Query<Message> userQuery = createQuery().
                field("type").equal("SINGLE").
                field("to").equal(username);

        allMessages.addAll(userQuery.asList());

        for (String groupName : groupNames) {
            Query<Message> groupQuery = createQuery().
                    field("type").equal("GROUP").
                    field("to").equal(groupName);

            allMessages.addAll(groupQuery.asList());
        }

        return allMessages;
    }

}