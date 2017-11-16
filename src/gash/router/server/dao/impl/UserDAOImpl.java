package gash.router.server.dao.impl;

import gash.router.server.dao.UserDAO;
import gash.router.server.entity.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;

public class UserDAOImpl extends BasicDAO<User, ObjectId> implements UserDAO {

    public UserDAOImpl(Class<User> entityClass, Datastore ds) {
        super(entityClass, ds);
    }

    public void createUser(User user) {
        save(user);
    }

    public User getUserByUsername(String username) {
        Query<User> query = createQuery().
                field("username").equal(username);

        return query.get();
    }

    public void deleteUserByUsername(String username) {
        Query<User> query = createQuery().
                field("username").equal(username);

        deleteByQuery(query);
    }

    public void addGroupToUser(long groupId, String username) {
        Query<User> updateQuery = createQuery().
                field("username").equal(username);

        UpdateOperations<User> ops = createUpdateOperations()
                .addToSet("groupIds", groupId);

        update(updateQuery, ops);
    }

    public void removeGroupFromUser(long groupId, String username) {
        Query<User> updateQuery = createQuery().
                field("username").equal(username);

        UpdateOperations<User> ops = createUpdateOperations()
                .removeAll("groupIds", groupId);

        update(updateQuery, ops);
    }

    public List<User> getAllUsers() {

        Query<User> query = createQuery();

        return query.asList();
    }

}