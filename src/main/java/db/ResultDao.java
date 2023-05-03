package db;

import model.Result;
import org.mapdb.*;

import java.io.IOException;
import java.io.Serializable;

public class ResultDao extends BaseDao<Result> {
    private static final String DB_NAME = "/results.db";

    public ResultDao(String pathToDb) {
        super(pathToDb, DB_NAME, "resultsList", new ResultSerializer());
    }

    public void addResult(Result newResult) {
        try {
            open();
            entities.add(newResult);
        } finally {
            close();
        }
    }

    public boolean isExistResult(Result result) {
        return findResultIndex(result) > -1;
    }

    public void updateResult(Result oldResult, Result updatedResult) {
        try {
            int i = findResultIndex(oldResult);
            open();
            entities.set(i, updatedResult);
        } finally {
            close();
        }
    }

    public void deleteResult(Result result) {
        try {
            int i = findResultIndex(result);
            open();
            entities.remove(i);
        } finally {
            close();
        }
    }

    public Result[] getAll() {
        try {
            open();
            Result[] result = new Result[0];
            result = entities.toArray(result);
            return result;
        } finally {
            close();
        }
    }

    public boolean contains(Result result) {
        try {
            open();
            return entities.contains(result);
        } finally {
            close();
        }
    }

    private int findResultIndex(Result result) {
        try {
            open();
            return entities.indexOf(result);
        } finally {
            close();
        }
    }

    public static class ResultSerializer implements Serializer<Result>, Serializable {
        @Override
        public void serialize(DataOutput2 out, Result result) throws IOException {
            out.writeUTF(result.getName());
            DATE.serialize(out, result.getDate());
            INTEGER.serialize(out, result.getScore());
        }

        @Override
        public Result deserialize(DataInput2 in, int i) throws IOException {
            return new Result(in.readUTF(), DATE.deserialize(in, i), INTEGER.deserialize(in, i));
        }
    }
}
