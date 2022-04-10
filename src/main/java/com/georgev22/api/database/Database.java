package com.georgev22.api.database;

import com.georgev22.api.database.sql.sqlite.SQLite;
import com.georgev22.api.maps.HashObjectMap;
import com.georgev22.api.maps.ObjectMap;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import static com.georgev22.api.utilities.Utils.Assertions.notNull;

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
     * Releases this <code>Connection</code> object's database and JDBC resources
     * immediately instead of waiting for them to be automatically released.
     * <p>
     * Calling the method <code>close</code> on a <code>Connection</code>
     * object that is already closed is a no-op.
     * <p>
     * It is <b>strongly recommended</b> that an application explicitly
     * commits or rolls back an active transaction prior to calling the
     * <code>close</code> method.  If the <code>close</code> method is called
     * and there is an active transaction, the results are implementation-defined.
     * <p>
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
     * Executes the SQL query in this <code>PreparedStatement</code> object
     * and returns the <code>ResultSet</code> object generated by the query.
     *
     * @return a <code>ResultSet</code> object that contains the data produced by the
     * query; never <code>null</code>
     * @throws SQLException           if a database access error occurs;
     *                                this method is called on a closed  <code>PreparedStatement</code> or the SQL
     *                                statement does not return a <code>ResultSet</code> object
     * @throws SQLTimeoutException    when the driver has determined that the
     *                                timeout value that was specified by the {@code setQueryTimeout}
     *                                method has been exceeded and has at least attempted to cancel
     *                                the currently running {@code Statement}
     * @throws ClassNotFoundException if the driver class does not exist
     */
    public ResultSet queryPreparedSQL(String query) throws SQLException, ClassNotFoundException {
        if (!isClosed()) {
            openConnection();
        }

        return connection.prepareStatement(query).executeQuery();
    }

    /**
     * Executes the SQL statement in this <code>PreparedStatement</code> object,
     * which must be an SQL Data Manipulation Language (DML) statement, such as <code>INSERT</code>, <code>UPDATE</code> or
     * <code>DELETE</code>; or an SQL statement that returns nothing,
     * such as a DDL statement.
     *
     * @return either (1) the row count for SQL Data Manipulation Language (DML) statements
     * or (2) 0 for SQL statements that return nothing
     * @throws SQLException           if a database access error occurs;
     *                                this method is called on a closed  <code>PreparedStatement</code>
     *                                or the SQL statement returns a <code>ResultSet</code> object
     * @throws SQLTimeoutException    when the driver has determined that the
     *                                timeout value that was specified by the {@code setQueryTimeout}
     *                                method has been exceeded and has at least attempted to cancel
     *                                the currently running {@code Statement}
     * @throws ClassNotFoundException if the driver class does not exist
     */
    public int updatePreparedSQL(String query) throws SQLException, ClassNotFoundException {
        if (!isClosed()) {
            openConnection();
        }

        return connection.prepareStatement(query).executeUpdate();
    }

    /**
     * Executes the given SQL statement, which returns a single
     * <code>ResultSet</code> object.
     * <p>
     * <strong>Note:</strong>This method cannot be called on a
     * <code>PreparedStatement</code> or <code>CallableStatement</code>.
     *
     * @param query an SQL statement to be sent to the database, typically a
     *              static SQL <code>SELECT</code> statement
     * @return a <code>ResultSet</code> object that contains the data produced
     * by the given query; never <code>null</code>
     * @throws SQLException           if a database access error occurs,
     *                                this method is called on a closed <code>Statement</code>, the given
     *                                SQL statement produces anything other than a single
     *                                <code>ResultSet</code> object, the method is called on a
     *                                <code>PreparedStatement</code> or <code>CallableStatement</code>
     * @throws SQLTimeoutException    when the driver has determined that the
     *                                timeout value that was specified by the {@code setQueryTimeout}
     *                                method has been exceeded and has at least attempted to cancel
     *                                the currently running {@code Statement}
     * @throws ClassNotFoundException if the driver class does not exist
     */
    public ResultSet querySQL(String query) throws SQLException, ClassNotFoundException {
        if (!isClosed()) {
            openConnection();
        }

        return connection.createStatement().executeQuery(query);
    }

    /**
     * Executes the given SQL statement, which may be an <code>INSERT</code>,
     * <code>UPDATE</code>, or <code>DELETE</code> statement or an
     * SQL statement that returns nothing, such as an SQL DDL statement.
     * <p>
     * <strong>Note:</strong>This method cannot be called on a
     * <code>PreparedStatement</code> or <code>CallableStatement</code>.
     *
     * @param query an SQL Data Manipulation Language (DML) statement, such as <code>INSERT</code>, <code>UPDATE</code> or
     *              <code>DELETE</code>; or an SQL statement that returns nothing,
     *              such as a DDL statement.
     * @return either (1) the row count for SQL Data Manipulation Language (DML) statements
     * or (2) 0 for SQL statements that return nothing
     * @throws SQLException           if a database access error occurs,
     *                                this method is called on a closed <code>Statement</code>, the given
     *                                SQL statement produces a <code>ResultSet</code> object, the method is called on a
     *                                <code>PreparedStatement</code> or <code>CallableStatement</code>
     * @throws SQLTimeoutException    when the driver has determined that the
     *                                timeout value that was specified by the {@code setQueryTimeout}
     *                                method has been exceeded and has at least attempted to cancel
     *                                the currently running {@code Statement}
     * @throws ClassNotFoundException if the driver class does not exist
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
        ResultSet resultSet = querySQL("SELECT * FROM `" + notNull("tableName", tableName) + "`;");
        ResultSetMetaData metaData = resultSet.getMetaData();
        int rowCount = metaData.getColumnCount();

        boolean isMyColumnPresent = false;
        boolean isMyColumnTypeCorrect = false;
        for (int i = 1; i <= rowCount; i++) {
            if (notNull("column", column).equals(metaData.getColumnName(i))) {
                isMyColumnPresent = true;
            }
            if (StringUtils.split(notNull("column", column), "(")[0].equals(metaData.getColumnTypeName(i))) {
                isMyColumnTypeCorrect = true;
            }
        }

        if (!isMyColumnPresent) {
            updateSQL("ALTER TABLE `" + notNull("tableName", tableName) + "` ADD `" + column + "` " + notNull("type", type) + ";");
        } else {
            if (!isMyColumnTypeCorrect) {
                updateSQL("ALTER TABLE `" + notNull("tableName", tableName) + "` MODIFY COLUMN `" + column + "` " + notNull("type", type) + ";");
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
     * Create a table.
     *
     * @param tableName  The name of the table
     * @param columnsMap The map that contains the columns with their type
     * @throws SQLException           if a database access error occurs
     * @throws ClassNotFoundException if the driver class does not exist
     */
    public void createTable(@NotNull String tableName, @NotNull ObjectMap<String, ObjectMap.Pair<String, String>> columnsMap) throws SQLException, ClassNotFoundException {
        StringBuilder stringBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS `" + notNull("tableName", tableName) + "` (\n ");
        ObjectMap<String, String> tableMap = new HashObjectMap<>();
        Iterator<Map.Entry<String, ObjectMap.Pair<String, String>>> columnIterator = notNull("columnsMap", columnsMap).entrySet().iterator();
        while (columnIterator.hasNext()) {
            Map.Entry<String, ObjectMap.Pair<String, String>> entry = columnIterator.next();
            stringBuilder.append("`").append(entry.getKey()).append("` ").append(entry.getValue().getKey()).append(" DEFAULT ").append(entry.getValue().getValue());
            tableMap.append(entry.getKey(), entry.getValue().getKey() + " DEFAULT " + entry.getValue().getValue());
            if (columnIterator.hasNext()) {
                stringBuilder.append(",\n");
            }
        }
        stringBuilder.append("\n)");
        updateSQL(stringBuilder.toString());

        tableMap.forEach((columnName, type) -> {
            try {
                if (!(this instanceof SQLite))
                    checkColumn(notNull("tableName", tableName), columnName, type);
            } catch (SQLException | ClassNotFoundException exception) {
                exception.printStackTrace();
            }
        });
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