package db;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.util.List;

public abstract class BaseDao<T> {
    private final String pathToDb;
    private final String dbName;
    private final String treeName;
    private final Serializer<T> serializer;
    private DB db;
    protected List<T> entities;

    public BaseDao(String pathToDb, String dbName, String treeName, Serializer<T> serializer) {
        this.pathToDb = pathToDb;
        this.dbName = dbName;
        this.treeName = treeName;
        this.serializer = serializer;
    }

    public long getSize() {
        try {
            open();
            return entities.size();
        } finally {
            close();
        }
    }

    public String getDbName() {
        return dbName;
    }

    protected void open() {
        db = DBMaker.fileDB(pathToDb + dbName)
                .fileLockDisable()
                .fileMmapEnable()
                .checksumHeaderBypass()
                .closeOnJvmShutdown()
                .fileDeleteAfterClose()
                .make();
        entities = db.indexTreeList(treeName, serializer).createOrOpen();
    }

    protected void close() {
        if (db != null) {
            db.close();
        }
    }
}
