package gash.router.server.dao;

import gash.router.server.entity.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import java.util.List;

public interface UserDAO extends DAO<User, ObjectId> {

    void createUser(User user);

    User getUserByUsername(String username);

    void deleteUserByUsername(String username);

    List<User> getAllUsers();

}