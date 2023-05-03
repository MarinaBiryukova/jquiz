package core;

import core.exceptions.QuestionAlreadyExistsException;
import model.Question;

public class QuestionManager {

    private final DatabaseManager databaseManager;

    public QuestionManager(String dbFolderName) {
        this.databaseManager = new DatabaseManager(dbFolderName);
    }

    public Question[] getAllQuestions() {
        return databaseManager.getAllQuestions();
    }

    public Question[] getTestQuestions(int count) {
        Question[] result = new Question[count];
        Question[] allQuestions = getAllQuestions();
        if (allQuestions.length <= count) {
            return allQuestions;
        }
        int[] indexes = getRandomIndexes(count, allQuestions.length);
        for (int i = 0; i < count; i++) {
            result[i] = allQuestions[indexes[i]];
        }
        return result;
    }

    public void deleteQuestion(Question question) {
        databaseManager.deleteQuestion(question.getQuestion());
    }

    public void addQuestion(Question question) throws QuestionAlreadyExistsException {
        databaseManager.addQuestion(question);
    }

    public void updateQuestion(Question oldQuestion, Question newQuestion) {
        databaseManager.updateQuestion(oldQuestion.getQuestion(), newQuestion);
    }

    private int[] getRandomIndexes(int count, int max) {
        int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            boolean flag = false;
            while (!flag) {
                int temp = (int)(Math.random() * max);
                int j = 0;
                for (; j < i; j++) {
                    if (result[j] == temp) {
                        break;
                    }
                }
                if (j == i) {
                    flag = true;
                    result[i] = temp;
                }
            }
        }
        return result;
    }
}
