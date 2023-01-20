package pl.mikorosa.dziecoin.database;

import pl.mikorosa.dziecoin.Main;

import java.util.List;
import java.util.Map;

public class DatabaseNFTs extends DatabaseFetchedData {
    public DatabaseNFTs() {
        tableName = "nfts";
    }

    public List<Map<String, Object>> select() {
        return Main.db.query("SELECT * FROM nfts");
    }

    public void insert(String contract, String owner, String data, int blockHeight) {
        Main.db.update("INSERT INTO %s VALUES (null, '%s', '%s', '%s', %d)".formatted(tableName, contract, owner, data, blockHeight));
    }
}
