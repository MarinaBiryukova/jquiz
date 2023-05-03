package core;

import core.exceptions.*;
import model.User;

public class LogInManager {

    private final DatabaseManager databaseManager;

    public LogInManager(String dbFolderName) {
        this.databaseManager = new DatabaseManager(dbFolderName);
    }

    public void logIn(
            String username,
            String password
    ) throws NullFieldsException, NoSuchUserException, WrongPasswordException, EmptyUsernameException, EmptyPasswordException {
        if (username == null || password == null) {
            throw new NullFieldsException("Имя пользователя или пароль принимают значение null");
        }

        checkCredentials(username, password);

        User user = databaseManager.findUser(username);

        if (user == null) {
            throw new NoSuchUserException("Пользователь с таким именем не существует!");
        }

        if (!user.getPassword().equals(password)) {
            throw new WrongPasswordException("Неверный пароль!");
        }
    }

    public void register(String username, String password) throws EmptyUsernameException, EmptyPasswordException, UserAlreadyExistsException {
        checkCredentials(username, password);
        databaseManager.addUser(new User(username, password));
    }

    public void registerAdmin(String username, String password) throws EmptyUsernameException, EmptyPasswordException, UserAlreadyExistsException {
        register(username, password);
    }

    public boolean isAdmin(String username, String password) {
        if (!username.equals("admin")) {
            return false;
        }
        User admin = databaseManager.findUser(username);
        return password.equals(admin.getPassword());
    }

    public void changePassword(String username, String password) throws EmptyUsernameException, EmptyPasswordException {
        checkCredentials(username, password);
        databaseManager.updateUser(username, new User(username, password));
    }

    public User getAdmin() {
        return databaseManager.findUser("admin");
    }

    private void checkCredentials(String username, String password) throws EmptyUsernameException, EmptyPasswordException {
        if (username.isEmpty()) {
            throw new EmptyUsernameException("Введите имя пользователя!");
        }

        if (password.isEmpty()) {
            throw new EmptyPasswordException("Введите пароль!");
        }
    }
}
