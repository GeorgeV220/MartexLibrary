package com.georgev22.library.database.sql.sqlite;

import com.georgev22.library.database.Database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;

public class SQLite extends Database {

    private final String fileName;
    private final File path;

    /**
     * @param path     The full path to the database file.
     * @param fileName The file name of the database file.
     */
    public SQLite(final File path, final String fileName) {
        this.fileName = fileName;
        this.path = path;
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
    @Override
    public Connection openConnection() throws SQLException, ClassNotFoundException {
        if (isConnectionValid()) {
            if (!isClosed())
                return connection;
        }
        Class.forName("org.sqlite.JDBC");
        String connectionURL = "jdbc:sqlite:" + path.getPath() + "/" + this.fileName + ".db";
        connection = DriverManager.getConnection(connectionURL);
        connection.createStatement().setQueryTimeout(Integer.MAX_VALUE);
        return connection;
    }

    @Override
    public String toString() {
        return "SQLite{" +
                "fileName='" + fileName + '\'' +
                ", path=" + path +
                '}';
    }
}
