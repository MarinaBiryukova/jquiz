package db;

import model.User;
import org.mapdb.*;

import java.io.IOException;
import java.io.Serializable;

public class UserDao extends BaseDao<User> {
    private static final String DB_NAME = "/users.db";

    public UserDao(String pathToDb) {
        super(pathToDb, DB_NAME, "usersList", new UserSerializer());
    }

    public void addUser(User newUser) {
        try {
            open();
            entities.add(newUser);
        } finally {
            close();
        }
    }

    public User findUser(String name) {
        try {
            open();
            User user = null;
            for (User u : entities) {
                if (u.getName().equals(name)) {
                    user = u;
                    break;
                }
            }
            return user;
        } finally {
            close();
        }
    }

    public void updateUser(String name, User updatedUser) {
        try {
            int i = findUserIndex(findUser(name));
            open();
            entities.set(i, updatedUser);
        } finally {
            close();
        }
    }

    public void deleteUser(String name) {
        try {
            int i = findUserIndex(findUser(name));
            open();
            entities.remove(i);
        } finally {
            close();
        }
    }

    public boolean contains(String username) {
        try {
            open();
            boolean contains = false;
            for (User u : entities) {
                if (u.getName().equals(username)) {
                    contains = true;
                    break;
                }
            }
            return contains;
        } finally {
            close();
        }
    }

    private int findUserIndex(User user) {
        try {
            open();
            return entities.indexOf(user);
        } finally {
            close();
        }
    }

    public static class UserSerializer implements Serializer<User>, Serializable {
        @Override
        public void serialize(DataOutput2 out, User user) throws IOException {
            out.writeUTF(user.getName());
            out.writeUTF(user.getPassword());
        }

        @Override
        public User deserialize(DataInput2 in, int i) throws IOException {
            return new User(in.readUTF(), in.readUTF());
        }
    }
}
