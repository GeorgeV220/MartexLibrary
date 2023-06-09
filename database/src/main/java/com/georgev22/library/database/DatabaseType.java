package com.georgev22.library.database;

public enum DatabaseType {

    MYSQL("MySQL"),
    SQLITE("SQLite"),
    POSTGRESQL("PostgreSQL"),
    MONGO("MongoDB");

    private final String name;

    DatabaseType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
