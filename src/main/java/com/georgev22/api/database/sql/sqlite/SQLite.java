package com.georgev22.api.database.sql.sqlite;

import com.georgev22.api.database.Database;
import com.georgev22.api.maven.LibraryLoader;

import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite extends Database {

    private final String fileName;
    private final File path;

    public SQLite(final File path, final String fileName) {
        this.fileName = fileName;
        this.path = path;
    }

    @Override
    public Connection openConnection() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (isConnectionValid()) {
            if (!isClosed())
                return connection;
        }
        Driver d = (Driver) Class.forName("org.sqlite.JDBC", true, LibraryLoader.getURLClassLoaderAccess() == null ? this.getClass().getClassLoader() : LibraryLoader.getURLClassLoaderAccess().getClassLoader()).newInstance();
        DriverManager.registerDriver(new DriverShim(d));
        String connectionURL = "jdbc:sqlite:" + path.getPath() + "/" + this.fileName + ".db";
        connection = DriverManager.getConnection(connectionURL);
        connection.createStatement().setQueryTimeout(Integer.MAX_VALUE);
        return connection;
    }
}
