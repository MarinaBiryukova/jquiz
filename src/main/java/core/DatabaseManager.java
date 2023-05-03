package core;

import core.exceptions.QuestionAlreadyExistsException;
import core.exceptions.UserAlreadyExistsException;
import db.QuestionDao;
import db.ResultDao;
import db.UserDao;
import model.Question;
import model.Result;
import model.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DatabaseManager {
    private static final String PATH_TO_DBS = System.getProperty("user.home") + "/JQuiz/";

    private final String dbFolderName;
    private final UserDao userDao;
    private final QuestionDao questionDao;
    private final ResultDao resultDao;

    public DatabaseManager(String dbFolderName) {
        this.dbFolderName = dbFolderName;
        userDao = new UserDao(PATH_TO_DBS + dbFolderName);
        questionDao = new QuestionDao(PATH_TO_DBS + dbFolderName);
        resultDao = new ResultDao(PATH_TO_DBS + dbFolderName);
    }

    public void createDbDirectory() throws IOException {
        Files.createDirectories(Paths.get(PATH_TO_DBS + dbFolderName));
    }

    public boolean isExistUser(String username) {
        return userDao.contains(username);
    }

    public void addUser(User newUser) throws UserAlreadyExistsException {
        if (userDao.contains(newUser.getName())) {
            throw new UserAlreadyExistsException("Пользователь с таким именем уже существует!");
        }
        userDao.addUser(newUser);
    }

    public User findUser(String name) {
        return userDao.findUser(name);
    }

    public void updateUser(String name, User updatedUser) {
        userDao.updateUser(name, updatedUser);
    }

    public void deleteUser(String name) {
        userDao.deleteUser(name);
    }

    public long getUsersSize() {
        return userDao.getSize();
    }


    public boolean isExistQuestion(String question) {
        return questionDao.contains(question);
    }

    public void addQuestion(Question newQuestion) throws QuestionAlreadyExistsException {
        if (questionDao.contains(newQuestion.getQuestion())) {
            throw new QuestionAlreadyExistsException("Такой вопрос уже существует!");
        }
        questionDao.addQuestion(newQuestion);
    }

    public Question findQuestion(String question) {
        return questionDao.findQuestion(question);
    }

    public void updateQuestion(String question, Question updatedQuestion) {
        questionDao.updateQuestion(question, updatedQuestion);
    }

    public void deleteQuestion(String question) {
        questionDao.deleteQuestion(question);
    }

    public long getQuestionsSize() {
        return questionDao.getSize();
    }

    public Question[] getAllQuestions() {
        return questionDao.getAll();
    }


    public boolean isExistResult(Result result) {
        return resultDao.isExistResult(result);
    }

    public void addResult(Result newResult) {
        if (!resultDao.contains(newResult)) {
            resultDao.addResult(newResult);
        }
    }

    public void updateResult(Result oldResult, Result updatedResult) {
        resultDao.updateResult(oldResult, updatedResult);
    }

    public void deleteResult(Result result) {
        resultDao.deleteResult(result);
    }

    public long getResultsSize() {
        return resultDao.getSize();
    }

    public Result[] getAllResults() {
        return resultDao.getAll();
    }
}
