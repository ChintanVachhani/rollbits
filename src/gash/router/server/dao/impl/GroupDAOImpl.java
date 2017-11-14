package gash.router.server.dao.impl;

import gash.router.server.dao.GroupDAO;
import gash.router.server.entity.Group;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;

import java.util.List;

public class GroupDAOImpl extends BasicDAO<Group, ObjectId> implements GroupDAO {

    public GroupDAOImpl(Class<Group> entityClass, Datastore ds) {
        super(entityClass, ds);
    }

    public void createGroup(Group group) {
        save(group);
    }

    public Group getGroupByName(String name) {
        Query<Group> query = createQuery().
                field("name").equal(name);

        return query.get();
    }

    public void deleteGroupByName(String name) {
        Query<Group> query = createQuery().
                field("name").equal(name);

        deleteByQuery(query);
    }

    public List<Group> getAllGroups() {

        Query<Group> query = createQuery();

        return query.asList();
    }

}