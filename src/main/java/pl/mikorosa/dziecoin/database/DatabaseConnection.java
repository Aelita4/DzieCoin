package pl.mikorosa.dziecoin.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseConnection {
    private Connection connection = null;
    private Statement statement = null;
    private DatabaseBlocks blocks;
    private DatabaseTransactions transactions;
    private DatabaseNFTs nfts;
    public DatabaseConnection() {
        System.out.println("Connecting to database...");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://helium.mikorosa.pl:3306/dziecoin", "dziecoin", "Y6seMx#HwgYU");
            System.out.println("Connected");

            blocks = new DatabaseBlocks();
            transactions = new DatabaseTransactions();
            nfts = new DatabaseNFTs();
        } catch(ClassNotFoundException e) {
            System.out.println("Error while setting up JDBC driver: " + e.getMessage());
            e.printStackTrace();
        } catch(SQLException e) {
            System.out.println("Error while connecting to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public DatabaseBlocks getBlocksTable() {
        return blocks;
    }

    public DatabaseTransactions getTransactionsTable() {
        return transactions;
    }

    public DatabaseNFTs getNFTsTable() {
        return nfts;
    }

    public List<Map<String, Object>> query(String sql) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            System.out.println("Executing query " + sql);
            statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            ResultSetMetaData metaData = result.getMetaData();
            int columnCount = metaData.getColumnCount();

            while(result.next()) {
                Map<String, Object> record = new HashMap<>();
                for(int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    int columnType = metaData.getColumnType(i);
                    Object value = null;
                    switch(columnType) {
                        case Types.INTEGER:
                            value = result.getInt(i);
                            break;
                        case Types.VARCHAR:
                        case Types.NVARCHAR:
                            value = result.getString(i);
                            break;
                        default:
                            value = result.getObject(i);
                            break;
                    }
                    record.put(columnName, value);
                }
                list.add(record);
            }
        } catch(SQLException e) {
            System.out.println("Failed to execute query (" + sql + "): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public boolean update(String sql) {
        try {
            System.out.println("Executing query " + sql);
            statement = connection.createStatement();
            int modifiedRows = statement.executeUpdate(sql);

            if(modifiedRows > 0) return true;
        } catch(SQLException e) {
            System.out.println("Failed to execute query (" + sql + "): " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public void resetCounter(String tableName) {
        try {
            System.out.println("Executing query ALTER TABLE " + tableName + " AUTO_INCREMENT=1");
            statement = connection.createStatement();
            statement.execute("ALTER TABLE " + tableName + " AUTO_INCREMENT=1");
        } catch(SQLException e) {
            System.out.println("Failed to execute query (ALTER TABLE " + tableName + " AUTO_INCREMENT=1): " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteAllRecords(String tableName) {
        try {
            System.out.println("Executing query DELETE FROM " + tableName);
            statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM " + tableName);
        } catch(SQLException e) {
            System.out.println("Failed to execute query (DELETE FROM " + tableName + "): " + e.getMessage());
            e.printStackTrace();
        }
    }
}
