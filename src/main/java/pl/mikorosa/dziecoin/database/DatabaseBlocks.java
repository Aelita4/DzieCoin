package pl.mikorosa.dziecoin.database;

import pl.mikorosa.dziecoin.Main;

import java.util.List;
import java.util.Map;

public class DatabaseBlocks extends DatabaseFetchedData {
    public DatabaseBlocks() {
        tableName = "blocks";
    }

    public List<Map<String, Object>> select() {
        return Main.db.query("SELECT * FROM blocks");
    }

    public void insert(int height, String hash, String prevHash, int nonce) {
        Main.db.update("INSERT INTO %s VALUES (%d, '%s', '%s', %d)".formatted(tableName, height, hash, prevHash, nonce));
    }
}
