package gash.router.server.dao;

import gash.router.server.entity.Group;
import org.bson.types.ObjectId;
import org.mongodb.morphia.dao.DAO;

import java.util.List;

public interface GroupDAO extends DAO<Group, ObjectId> {

    void createGroup(Group group);

    Group getGroupByName(String name);

    void deleteGroupByName(String name);

    List<Group> getAllGroups();

}