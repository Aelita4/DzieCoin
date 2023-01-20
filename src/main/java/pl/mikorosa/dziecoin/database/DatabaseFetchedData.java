package pl.mikorosa.dziecoin.database;

import pl.mikorosa.dziecoin.Main;

import java.util.List;
import java.util.Map;

public abstract class DatabaseFetchedData {
    protected String tableName;

    public List<Map<String, Object>> select() {
        return null;
    }

    public void cleanTable() {
        Main.db.deleteAllRecords(tableName);
        Main.db.resetCounter(tableName);
    }
}
