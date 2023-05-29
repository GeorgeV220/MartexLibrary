package com.georgev22.library.database;

public enum DatabaseType {

    MYSQL("MySQL"),
    SQLITE("SQLite"),
    POSTGRESQL("PostgreSQL"),
    MONGO("MongoDB");

    private final String name;
    private final Object[] data;

    DatabaseType(String name, Object... data) {
        this.name = name;
        this.data = data;
    }

    public Object[] getData() {
        return data;
    }

    public String getName() {
        return name;
    }
}
