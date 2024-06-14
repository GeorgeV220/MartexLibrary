package com.georgev22.library.database.sql.postgresql;

import com.georgev22.library.database.sql.Database;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.Optional;
import java.util.Properties;

public class PostgreSQL extends Database {

    private final String user, password, database, hostname;
    private final int port;

    @Deprecated(forRemoval = true)
    public PostgreSQL(String hostname, int port, String username, String password) {
        this(hostname, port, username, password, Optional.empty());
    }

    /**
     * @param hostname PostgreSQL hostname.
     * @param port     PostgreSQL port.
     * @param username PostgreSQL username.
     * @param password PostgreSQL password.
     * @param database PostgreSQL database name.
     */
    public PostgreSQL(String hostname, int port, String username, String password, @NotNull Optional<String> database) {
        this.hostname = hostname;
        this.port = port;
        this.database = database.orElse(null);
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
        Class.forName("org.postgresql.Driver");
        final Properties prop = new Properties();
        prop.setProperty("user", this.user);
        prop.setProperty("password", this.password);
        prop.setProperty("connectTimeout", String.valueOf(Integer.MAX_VALUE));
        prop.setProperty("autosave", "always");
        return connection = database != null ? DriverManager.getConnection("jdbc:postgresql://" + this.hostname + ":" + this.port + "/" + this.database, prop) : DriverManager.getConnection("jdbc:postgresql://" + this.hostname + ":" + this.port + "/", prop);
    }

    @Override
    public String toString() {
        return "PostgreSQL{" +
                "user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", database='" + database + '\'' +
                ", hostname='" + hostname + '\'' +
                ", port=" + port +
                '}';
    }
}
