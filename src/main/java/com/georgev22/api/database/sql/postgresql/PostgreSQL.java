package com.georgev22.api.database.sql.postgresql;

import com.georgev22.api.database.Database;
import com.georgev22.api.maven.LibraryLoader;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgreSQL extends Database {

    private final String user, password, database, hostname;
    private final int port;

    public PostgreSQL(String hostname, int port, String username, String password) {
        this(hostname, port, null, username, password);
    }

    public PostgreSQL(String hostname, int port, String database, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.user = username;
        this.password = password;
    }

    @Override
    public Connection openConnection() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (isConnectionValid()) {
            if (!isClosed())
                return connection;
        }
        Driver d = (Driver) Class.forName("org.postgresql.Driver", true, LibraryLoader.getURLClassLoaderAccess() == null ? this.getClass().getClassLoader() : LibraryLoader.getURLClassLoaderAccess().getClassLoader()).newInstance();
        DriverManager.registerDriver(new DriverShim(d));
        final Properties prop = new Properties();
        prop.setProperty("user", this.user);
        prop.setProperty("password", this.password);
        prop.setProperty("connectTimeout", String.valueOf(Integer.MAX_VALUE));
        prop.setProperty("autosave", "always");
        return connection = DriverManager.getConnection("jdbc:postgresql://" + this.hostname + ":" + this.port + "/" + this.database, prop);
    }
}