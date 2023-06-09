package com.georgev22.library.database;

import com.georgev22.library.database.sql.sqlite.SQLite;
import com.georgev22.library.maps.ObjectMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.georgev22.library.utilities.Utils.Assertions.notNull;

public abstract class Database {

    protected Connection connection;

    protected Database() {
        this.connection = null;
    }

    /**
     * Attempts to establish a connection to the given database URL.
     * The <code>DriverManager</code> attempts to select an appropriate driver from
     * the set of registered JDBC drivers.
     * <p>
     * <B>Note:</B> If a property is specified as part of the {@code url} and
     * is also specified in the {@code Properties} object, it is
     * implementation-defined as to which value will take precedence.
     * For maximum portability, an application should only specify a
     * property once.
     *
     * @return a Connection to the URL
     * @throws SQLException           if a database access error occurs or the url is
     *                                {@code null}
     * @throws SQLTimeoutException    when the driver has determined that the
     *                                timeout value specified by the {@code setLoginTimeout} method
     *                                has been exceeded and has at least tried to cancel the
     *                                current database connection attempt
     * @throws ClassNotFoundException if the driver class does not exist
     */
    public abstract Connection openConnection() throws SQLException, ClassNotFoundException;

    /**
     * Checks if the connection is valid
     *
     * @return if the connection is valid
     */
    public boolean isConnectionValid() {
        return connection != null;
    }

    /**
     * Retrieves whether this <code>Connection</code> object has been
     * closed.  A connection is closed if the method <code>close</code>
     * has been called on it or if certain fatal errors have occurred.
     * This method is guaranteed to return <code>true</code> only when
     * it is called after the method <code>Connection.close</code> has
     * been called.
     * <p>
     * This method generally cannot be called to determine whether a
     * connection to a database is valid or invalid.  A typical client
     * can determine that a connection is invalid by catching any
     * exceptions that might be thrown when an operation is attempted.
     *
     * @return <code>true</code> if this <code>Connection</code> object
     * is closed; <code>false</code> if it is still open
     * @throws SQLException if a database access error occurs
     */
    public boolean isClosed() throws SQLException {
        return connection.isClosed();
    }

    /**
     * Returns the connection to the database
     * <p>
     * If the connection is invalid it returns null
     *
     * @return the <code>Connection</code> to the database or null
     * if the connection is not valid.
     */
    @Nullable
    public Connection getConnection() {
        return connection;
    }

    /**
     * Disconnects from the database.
     *
     * @throws SQLException if a database access error occurs
     */
    public boolean closeConnection() throws SQLException {
        if (connection == null) {
            return false;
        }
        connection.close();
        return true;
    }

    /**
     * Executes a SQL query and returns the result set.
     *
     * @param query the SQL query to execute
     * @return the result set generated by the query
     * @throws SQLException if a database access error occurs or the query is invalid
     */
    public ResultSet querySQL(String query) throws SQLException, ClassNotFoundException {
        if (!isClosed()) {
            openConnection();
        }

        return connection.createStatement().executeQuery(query);
    }

    /**
     * Executes a SQL update query and returns the number of affected rows.
     *
     * @param query the SQL update query to execute
     * @return the number of affected rows
     * @throws SQLException if a database access error occurs or the query is invalid
     */
    public int updateSQL(String query) throws SQLException, ClassNotFoundException {
        if (!isClosed()) {
            openConnection();
        }

        return connection.createStatement().executeUpdate(query);
    }

    /**
     * Checks if the column exists and if it is the right type
     *
     * @param tableName The name of the table to check the column
     * @param column    The column name
     * @param type      The column type
     * @throws SQLException if a database access error occurs
     */
    public void checkColumn(@NotNull String tableName, @NotNull String column, @NotNull String type) throws SQLException, ClassNotFoundException {
        ResultSet set = querySQL("SELECT * FROM `" + notNull("tableName", tableName) + "`;");
        ResultSetMetaData metaData = set.getMetaData();
        int rowCount = metaData.getColumnCount();

        boolean isMyColumnPresent = false;
        boolean isMyColumnTypeCorrect = false;
        for (int i = 1; i <= rowCount; i++) {
            if (notNull("column", column).equals(metaData.getColumnName(i))) {
                isMyColumnPresent = true;
            }
            if (type.replaceAll("\\(.*\\)", "").equals(metaData.getColumnTypeName(i))) {
                isMyColumnTypeCorrect = true;
            }
        }

        if (!isMyColumnPresent) {
            updateSQL("ALTER TABLE `" + notNull("tableName", tableName) + "` ADD COLUMN `" + column + "` " + notNull("type", type) + ";");
        } else {
            if (!isMyColumnTypeCorrect) {
                if (this instanceof SQLite) {
                    String getTableQuery = "SELECT sql FROM sqlite_master WHERE type='table' AND name='" + tableName + "'";
                    String tableCreationQuery;
                    try (ResultSet resultSet = querySQL(getTableQuery)) {
                        tableCreationQuery = resultSet.getString("sql");
                    }

                    String oldColumnType = null;
                    Pattern pattern = Pattern.compile(column + "\\s+(\\w+)");
                    Matcher matcher = pattern.matcher(tableCreationQuery);
                    if (matcher.find()) {
                        oldColumnType = matcher.group(1);
                    }

                    if (oldColumnType != null) {
                        String modifiedTableCreationQuery = tableCreationQuery.replace(column + " " + oldColumnType, column + " " + type);

                        String renameTableQuery = "ALTER TABLE " + tableName + " RENAME TO temp_" + tableName;
                        updateSQL(renameTableQuery);

                        updateSQL(modifiedTableCreationQuery);

                        String migrateDataQuery = "INSERT INTO " + tableName + " SELECT * FROM temp_" + tableName;
                        updateSQL(migrateDataQuery);

                        String dropTempTableQuery = "DROP TABLE temp_" + tableName;
                        updateSQL(dropTempTableQuery);
                    }
                } else {
                    updateSQL("ALTER TABLE `" + notNull("tableName", tableName) + "` MODIFY COLUMN `" + column + "` " + notNull("type", type) + ";");
                }
            }
        }
    }

    /**
     * Renames a table.
     *
     * @param oldTableName The old name of the table
     * @param newTableName The new name of the table
     * @throws SQLException           if a database access error occurs
     * @throws ClassNotFoundException if the driver class does not exist
     */
    public boolean renameTable(@NotNull String oldTableName, @NotNull String newTableName) throws SQLException, ClassNotFoundException {
        return updateSQL("ALTER TABLE " + oldTableName + " RENAME TO " + newTableName + ";") == 1;
    }

    /**
     * Creates a table in the database with the specified name and columns.
     *
     * @param tableName  the name of the table to create
     * @param columnsMap a map containing the column names and their types with optional default values
     * @throws SQLException           if a database access error occurs
     * @throws ClassNotFoundException if the specified database driver class cannot be found
     */
    public void createTable(@NotNull String tableName, @NotNull ObjectMap<String, ObjectMap.Pair<String, String>> columnsMap) throws SQLException, ClassNotFoundException {
        StringBuilder queryBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS `" + tableName + "` (");

        for (Map.Entry<String, ObjectMap.Pair<String, String>> entry : columnsMap.entrySet()) {
            String columnName = entry.getKey();
            ObjectMap.Pair<String, String> columnDetails = entry.getValue();
            String columnType = columnDetails.key();
            String defaultValue = columnDetails.value();

            queryBuilder.append("`").append(columnName).append("` ").append(columnType);

            if (defaultValue != null && !defaultValue.isEmpty()) {
                queryBuilder.append(" DEFAULT ").append(defaultValue);
            }

            queryBuilder.append(", ");
        }

        queryBuilder.setLength(queryBuilder.length() - 2);
        queryBuilder.append(");");

        updateSQL(queryBuilder.toString());

        columnsMap.forEach((columnName, columnDetails) -> {
            try {
                checkColumn(tableName, columnName, columnDetails.key());
            } catch (SQLException | ClassNotFoundException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Builds an SQL INSERT statement for the specified table name and column values.
     *
     * @param tableName    the name of the table
     * @param columnValues a map containing column names as keys and corresponding values
     * @return the SQL INSERT statement
     */
    public String buildInsertStatement(String tableName, Map<String, Object> columnValues) {
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder columnsBuilder = new StringBuilder();
        StringBuilder valuesBuilder = new StringBuilder();

        sqlBuilder.append("INSERT INTO ").append(tableName);

        for (String column : columnValues.keySet()) {
            columnsBuilder.append(column).append(", ");
            valuesBuilder.append("?, ");
        }

        columnsBuilder.setLength(columnsBuilder.length() - 2);
        valuesBuilder.setLength(valuesBuilder.length() - 2);

        sqlBuilder.append(" (").append(columnsBuilder).append(")");
        sqlBuilder.append(" VALUES (").append(valuesBuilder).append(")").append(";");
        return sqlBuilder.toString();
    }

    /**
     * Builds an SQL UPDATE statement for the specified table name, column values, and condition.
     *
     * @param tableName    the name of the table
     * @param columnValues a map containing column names as keys and corresponding values
     * @param condition    the condition to apply for updating the rows
     * @return the SQL UPDATE statement
     */
    public String buildUpdateStatement(String tableName, Map<String, Object> columnValues, String condition) {
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder setBuilder = new StringBuilder();

        sqlBuilder.append("UPDATE ").append(tableName).append(" SET ");

        for (String column : columnValues.keySet()) {
            setBuilder.append(column).append(" = ?, ");
        }

        setBuilder.setLength(setBuilder.length() - 2);

        sqlBuilder.append(setBuilder);

        sqlBuilder.append(" WHERE ").append(condition).append(";");
        return sqlBuilder.toString();
    }

    /**
     * Builds an SQL DELETE statement for the specified table name, column values, and condition.
     *
     * @param tableName    the name of the table
     * @param condition    the condition to apply for deleting the rows
     * @return the SQL DELETE statement
     */
    public String buildDeleteStatement(String tableName, String condition) {
        return "DELETE FROM " +
                tableName +
                " WHERE " +
                condition;
    }

    public int getColumnDataType(String tableName, String columnName, @NotNull Connection connection) throws SQLException {
        if (this instanceof SQLite) {
            try (PreparedStatement statement = connection.prepareStatement("PRAGMA table_info(" + tableName + ")")) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String name = resultSet.getString("name");
                        String type = resultSet.getString("type");
                        if (name.equalsIgnoreCase(columnName)) {
                            return mapSQLiteDataTypeToJDBCType(type);
                        }
                    }
                }
            }
        } else {
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet resultSet = metaData.getColumns(null, null, tableName, columnName)) {
                if (resultSet.next()) {
                    return resultSet.getInt("DATA_TYPE");
                }
            }
        }
        return Types.NULL;
    }

    private int mapSQLiteDataTypeToJDBCType(@NotNull String type) {
        // Map SQLite data type to JDBC Types
        if (type.equalsIgnoreCase("INTEGER") || type.equalsIgnoreCase("INT")) {
            return Types.INTEGER;
        } else if (type.equalsIgnoreCase("REAL")) {
            return Types.REAL;
        } else if (type.equalsIgnoreCase("TEXT")) {
            return Types.VARCHAR;
        } else if (type.equalsIgnoreCase("BLOB")) {
            return Types.BLOB;
        } else {
            return Types.NULL;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Database database)) return false;
        return Objects.equals(connection, database.connection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connection);
    }

    @Override
    public String toString() {
        return "Database{" +
                "connection=" + connection +
                '}';
    }

}