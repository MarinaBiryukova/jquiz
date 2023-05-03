import core.DatabaseManager;
import core.QuestionManager;
import core.exceptions.QuestionAlreadyExistsException;
import core.exceptions.UserAlreadyExistsException;
import model.Question;
import model.Result;
import model.User;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

public class MapDatabaseTest {
    private DatabaseManager databaseManager;
    private QuestionManager questionManager;
    private Result result;

    @Before
    public void before() throws IOException {
        databaseManager = new DatabaseManager("db_test");
        databaseManager.createDbDirectory();
        questionManager = new QuestionManager("db_test");
    }

    @Test
    public void test() throws QuestionAlreadyExistsException, UserAlreadyExistsException {
        addOneUser();
        addManyUsers();
        addOneQuestion();
        addManyQuestions();
        addOneResult();
        addManyResults();

        findExistingUser();
        findNotExistingUser();
        findExistingQuestion();
        findNotExistingQuestion();
        findExistingResult();
        findNotExistingResult();

        updateUser();
        updateQuestion();
        updateResult();

        deleteUser();
        deleteQuestion();
        deleteResult();

        countOfRandomQuestions();
        isDifferentRandomQuestions();
    }

    private void addOneUser() throws UserAlreadyExistsException {
        long size = databaseManager.getUsersSize();
        databaseManager.addUser(new User("vanya", "qwerty"));
        assertEquals(size + 1, databaseManager.getUsersSize());
    }

    private void addManyUsers() throws UserAlreadyExistsException {
        long size = databaseManager.getUsersSize();

        databaseManager.addUser(new User("petya", "1234"));
        databaseManager.addUser(new User("masha", "hrGS9SbWze"));

        assertEquals(size + 2, databaseManager.getUsersSize());
    }

    private void addOneQuestion() throws QuestionAlreadyExistsException {
        long size = databaseManager.getQuestionsSize();
        databaseManager.addQuestion(new Question("How many?", new String[]{"one", "two", "three", "four"}, 2));
        assertEquals(size + 1, databaseManager.getQuestionsSize());
    }

    private void addManyQuestions() throws QuestionAlreadyExistsException {
        long size = databaseManager.getQuestionsSize();

        databaseManager.addQuestion(new Question("Question1?", new String[]{"one", "two", "three", "four"}, 2));
        databaseManager.addQuestion(new Question("Question2?", new String[]{"one", "two", "three", "four"}, 1));
        databaseManager.addQuestion(new Question("Question3?", new String[]{"one", "two", "three", "four"}, 4));
        databaseManager.addQuestion(new Question("Question4?", new String[]{"one", "two", "three", "four"}, 1));

        assertEquals(size + 4, databaseManager.getQuestionsSize());
    }

    private void addOneResult() {
        long size = databaseManager.getResultsSize();
        result = new Result("vanya", new Date(), 70);
        databaseManager.addResult(result);
        assertEquals(size + 1, databaseManager.getResultsSize());
    }

    private void addManyResults() {
        long size = databaseManager.getResultsSize();

        databaseManager.addResult(new Result("vanya", new Date(), 80));
        databaseManager.addResult(new Result("petya", new Date(), 100));
        databaseManager.addResult(new Result("masha", new Date(), 120));

        assertEquals(size + 3, databaseManager.getResultsSize());
    }

    private void findExistingUser() {
        String requiredUsername = "vanya";
        assertTrue(databaseManager.isExistUser(requiredUsername));
    }

    private void findNotExistingUser() {
        String requiredUsername = "kolya";
        assertFalse(databaseManager.isExistUser(requiredUsername));
    }

    private void findExistingQuestion() {
        String requiredQuestionText = "How many?";
        assertTrue(databaseManager.isExistQuestion(requiredQuestionText));
    }

    private void findNotExistingQuestion() {
        String requiredQuestionText = "QuestionDoesNotExist?";
        assertFalse(databaseManager.isExistQuestion(requiredQuestionText));
    }

    private void findExistingResult() {
        assertTrue(databaseManager.isExistResult(result));
    }

    private void findNotExistingResult() {
        Result requiredResult = new Result("kolya", new Date(), 20);
        assertFalse(databaseManager.isExistResult(requiredResult));
    }

    private void updateUser() {
        String requiredLogin = "vanya";
        User newUser = new User("vanya", "1234");
        databaseManager.updateUser(requiredLogin, newUser);
        assertEquals("1234", databaseManager.findUser("vanya").getPassword());
    }

    private void updateQuestion() {
        String requiredQuestionText = "How many?";
        Question newQuestion = new Question("How many?", new String[]{"one", "two", "three", "four"}, 3);
        databaseManager.updateQuestion(requiredQuestionText, newQuestion);
        assertEquals(3, databaseManager.findQuestion(requiredQuestionText).getCorrectAnswer());
    }

    private void updateResult() {
        Result newResult = new Result("vanya", new Date(), 50);
        databaseManager.updateResult(result, newResult);
        assertTrue(databaseManager.isExistResult(newResult));
        assertFalse(databaseManager.isExistResult(result));
        result = newResult;
    }

    private void deleteUser() {
        long size = databaseManager.getUsersSize();
        String requiredLogin = "vanya";
        databaseManager.deleteUser(requiredLogin);
        assertEquals(size - 1, databaseManager.getUsersSize());
    }

    private void deleteQuestion() {
        long size = databaseManager.getQuestionsSize();
        String requiredQuestionText = "How many?";
        databaseManager.deleteQuestion(requiredQuestionText);
        assertEquals(size - 1, databaseManager.getQuestionsSize());
    }

    private void deleteResult() {
        long size = databaseManager.getResultsSize();
        databaseManager.deleteResult(result);
        assertEquals(size - 1, databaseManager.getResultsSize());
    }

    private void countOfRandomQuestions() {
        Question[] allQuestions = questionManager.getAllQuestions();
        Question[] randomQuestions = questionManager.getTestQuestions(allQuestions.length - 1);
        assertEquals(randomQuestions.length, allQuestions.length - 1);
        randomQuestions = questionManager.getTestQuestions(allQuestions.length);
        assertEquals(randomQuestions.length, allQuestions.length);
        randomQuestions = questionManager.getTestQuestions(allQuestions.length + 1);
        assertEquals(randomQuestions.length, allQuestions.length);
    }

    private void isDifferentRandomQuestions() {
        Question[] allQuestions = questionManager.getAllQuestions();
        Question[] randomQuestions = questionManager.getTestQuestions(allQuestions.length / 2);
        for (int i = 0; i < randomQuestions.length - 1; i++) {
            for (int j = i + 1; j < randomQuestions.length; j++) {
                assertNotEquals(randomQuestions[i], randomQuestions[j]);
            }
        }
    }
}
