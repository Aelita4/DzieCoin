package pl.mikorosa.dziecoin.database;

import pl.mikorosa.dziecoin.Main;

import java.util.List;
import java.util.Map;

public class DatabaseTransactions extends DatabaseFetchedData {
    public DatabaseTransactions() {
        tableName = "transactions";
    }

    public List<Map<String, Object>> select() {
        return Main.db.query("SELECT * FROM transactions");
    }

    public void insert(String sender, String recipient, int amount, int blockHeight) {
        Main.db.update("INSERT INTO %s VALUES (null, '%s', '%s', %d, %d)".formatted(tableName, sender, recipient, amount, blockHeight));
    }
}
