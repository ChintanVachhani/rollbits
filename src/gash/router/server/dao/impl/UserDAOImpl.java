package gash.router.server.dao.impl;

import gash.router.server.dao.UserDAO;
import gash.router.server.entity.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;

import java.util.List;

public class UserDAOImpl extends BasicDAO<User, ObjectId> implements UserDAO {

    public UserDAOImpl(Class<User> entityClass, Datastore ds) {
        super(entityClass, ds);
    }

    public void createUser(User user) {
        save(user);
    }

    public User getUserByEmail(String email) {
        Query<User> query = createQuery().
                field("email").equal(email);

        return query.get();
    }

    public void deleteUserByEmail(String email) {
        Query<User> query = createQuery().
                field("email").equal(email);

        deleteByQuery(query);
    }

    public List<User> getAllUsers() {

        Query<User> query = createQuery();

        return query.asList();
    }

}