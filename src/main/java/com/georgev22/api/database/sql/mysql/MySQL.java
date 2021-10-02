package com.georgev22.api.database.sql.mysql;

import com.georgev22.api.database.Database;
import com.georgev22.api.maven.LibraryLoader;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
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

    @Override
    public Connection openConnection() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (isConnectionValid()) {
            if (!isClosed())
                return connection;
        }
        Driver d = (Driver) Class.forName("com.mysql.jdbc.Driver", true, LibraryLoader.getURLClassLoaderAccess() == null ? this.getClass().getClassLoader() : LibraryLoader.getURLClassLoaderAccess().getClassLoader()).newInstance();
        DriverManager.registerDriver(new DriverShim(d));
        final Properties prop = new Properties();
        prop.setProperty("user", user);
        prop.setProperty("password", password);
        prop.setProperty("useSSL", "false");
        prop.setProperty("autoReconnect", "true");
        prop.setProperty("connectTimeout", String.valueOf(Integer.MAX_VALUE));
        return connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database, prop);
    }
}