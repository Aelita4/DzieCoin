package pl.mikorosa.dziecoin.database;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    @Test
    void select() {
        DatabaseConnection db = new DatabaseConnection();
        List<Map<String, Object>> rows = db.query("SELECT * FROM test");
        int numRows = rows.size();

        assertEquals(0, numRows);
    }

    @Test
    void insertAndDelete() {
        DatabaseConnection db = new DatabaseConnection();

        List<Map<String, Object>> before = db.query("SELECT * FROM test");
        int numRowsBefore = before.size();

        db.update("INSERT INTO test VALUES (null, 'aaa')");

        List<Map<String, Object>> rowsAfter = db.query("SELECT * FROM test");
        int numRowsAfter = rowsAfter.size();

        assertEquals(numRowsBefore + 1, numRowsAfter);

        db.update("DELETE FROM test");

        List<Map<String, Object>> rowsDeleted = db.query("SELECT * FROM test");
        int numRowsDeleted = rowsDeleted.size();

        assertEquals(numRowsAfter - 1, numRowsDeleted);
    }
}