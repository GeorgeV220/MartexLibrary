package com.georgev22.api.database.sql.mysql;

import com.georgev22.api.database.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.Properties;

public class MySQL extends Database {

    private final String user, password, database, hostname;
    private final int port;

    public MySQL(String hostname, int port, String username, String password) {
        this(hostname, port, null, username, password);
    }

    public MySQL(String hostname, int port, String database, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.user = username;
        this.password = password;
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
        Class.forName("com.mysql.jdbc.Driver");
        final Properties prop = new Properties();
        prop.setProperty("user", user);
        prop.setProperty("password", password);
        prop.setProperty("useSSL", "false");
        prop.setProperty("autoReconnect", "true");
        prop.setProperty("connectTimeout", String.valueOf(Integer.MAX_VALUE));
        return connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database, prop);
    }
}