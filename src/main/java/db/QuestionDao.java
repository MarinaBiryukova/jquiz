package db;

import model.Question;
import org.mapdb.*;
import org.mapdb.serializer.SerializerArray;

import java.io.IOException;
import java.io.Serializable;

public class QuestionDao extends BaseDao<Question> {
    private static final String DB_NAME = "/questions.db";

    public QuestionDao(String pathToDb) {
        super(pathToDb, DB_NAME, "questionsList", new QuestionSerializer());
    }

    public void addQuestion(Question newQuestion) {
        try{
            open();
            entities.add(newQuestion);
        } finally {
            close();
        }
    }

    public Question findQuestion(String question) {
        try {
            open();
            Question q = null;
            for (Question question1 : entities) {
                if (question1.getQuestion().equals(question)) {
                    q = question1;
                    break;
                }
            }
            return q;
        } finally {
            close();
        }
    }

    public void updateQuestion(String question, Question updatedQuestion) {
        try {
            int i = findQuestionIndex(findQuestion(question));
            open();
            entities.set(i, updatedQuestion);
        } finally {
            close();
        }
    }

    public void deleteQuestion(String question) {
        try {
            int i = findQuestionIndex(findQuestion(question));
            open();
            entities.remove(i);
        } finally {
            close();
        }
    }

    public Question[] getAll() {
        try {
            open();
            Question[] result = new Question[0];
            result = entities.toArray(result);
            return result;
        } finally {
            close();
        }
    }

    public boolean contains(String question) {
        try {
            open();
            boolean contains = false;
            for (Question q : entities) {
                if (q.getQuestion().equals(question)) {
                    contains = true;
                    break;
                }
            }
            return contains;
        } finally {
            close();
        }
    }

    private int findQuestionIndex(Question question) {
        try {
            open();
            int index = -1;
            for (int i = 0; i < entities.size(); i++) {
                if (entities.get(i).getQuestion().equals(question.getQuestion())) {
                    index = i;
                    break;
                }
            }
            return index;
        } finally {
            close();
        }
    }

    private static class QuestionSerializer implements Serializer<Question>, Serializable {
        @Override
        public void serialize(DataOutput2 out, Question question) throws IOException {
            out.writeUTF(question.getQuestion());
            SerializerArray<String> stringSerializerArray = new SerializerArray<>(Serializer.STRING, String.class);
            stringSerializerArray.serialize(out, question.getAnswers());
            out.writeInt(question.getCorrectAnswer());
        }

        @Override
        public Question deserialize(DataInput2 in, int i) throws IOException {
            String question = in.readUTF();
            SerializerArray<String> stringSerializerArray = new SerializerArray<>(Serializer.STRING, String.class);
            String[] variants = stringSerializerArray.deserialize(in, i);
            int index = in.readInt();
            return new Question(question, variants, index);
        }
    }
}
