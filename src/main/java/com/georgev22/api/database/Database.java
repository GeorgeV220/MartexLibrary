package com.georgev22.api.database;

import com.georgev22.api.maps.ObjectMap;

import java.sql.*;
import java.util.Iterator;
import java.util.Map;

public abstract class Database {

    protected Connection connection;

    protected Database() {
        this.connection = null;
    }

    public abstract Connection openConnection() throws SQLException, ClassNotFoundException;

    public boolean isConnectionValid() {
        return connection != null;
    }

    public boolean isClosed() throws SQLException {
        return connection.isClosed();
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean closeConnection() throws SQLException {
        if (connection == null) {
            return false;
        }
        connection.close();
        return true;
    }

    public ResultSet queryPreparedSQL(String query) throws SQLException, ClassNotFoundException {
        if (!isClosed()) {
            openConnection();
        }

        return connection.prepareStatement(query).executeQuery();
    }

    public int updatePreparedSQL(String query) throws SQLException, ClassNotFoundException {
        if (!isClosed()) {
            openConnection();
        }

        return connection.prepareStatement(query).executeUpdate();
    }

    public ResultSet querySQL(String query) throws SQLException, ClassNotFoundException {
        if (!isClosed()) {
            openConnection();
        }

        return connection.createStatement().executeQuery(query);
    }

    public int updateSQL(String query) throws SQLException, ClassNotFoundException {
        if (!isClosed()) {
            openConnection();
        }

        return connection.createStatement().executeUpdate(query);
    }

    public void checkColumn(String tableName, String column, String type) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM `" + tableName + "`;");
        ResultSetMetaData metaData = resultSet.getMetaData();
        int rowCount = metaData.getColumnCount();

        boolean isMyColumnPresent = false;
        for (int i = 1; i <= rowCount; i++) {
            if (column.equals(metaData.getColumnName(i))) {
                isMyColumnPresent = true;
            }
        }

        if (!isMyColumnPresent) {
            statement.executeUpdate("ALTER TABLE `" + tableName + "` ADD `" + column + "` " + type + ";");
        }
    }

    /**
     * Create the user table
     *
     * @throws SQLException           When something goes wrong
     * @throws ClassNotFoundException When class is not found
     */
    public void createTable(String tableName, ObjectMap<String, ObjectMap.Pair<String, String>> objectMap) throws SQLException, ClassNotFoundException {
        StringBuilder stringBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS `" + tableName + "` (\n ");
        ObjectMap<String, String> tableMap = ObjectMap.newHashObjectMap();
        Iterator<Map.Entry<String, ObjectMap.Pair<String, String>>> iterator = objectMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ObjectMap.Pair<String, String>> entry = iterator.next();
            stringBuilder.append("`").append(entry.getKey()).append("` ").append(entry.getValue().getKey()).append(" DEFAULT ").append(entry.getValue().getValue());
            tableMap.append(entry.getKey(), entry.getValue().getKey() + " DEFAULT " + entry.getValue().getValue());
            if (iterator.hasNext()) {
                stringBuilder.append(",\n");
            }
        }
        stringBuilder.append("\n)");
        updateSQL(stringBuilder.toString());

        tableMap.forEach((columnName, type) -> {
            try {
                checkColumn(tableName, columnName, type);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }
}