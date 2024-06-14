package com.georgev22.library.utilities;

import com.georgev22.library.utilities.annotations.Column;
import org.jetbrains.annotations.NotNull;

public class GenericEntity extends Entity {

    @Column(name = "name", type = "VARCHAR(255)")
    public String name;

    public GenericEntity(String _id) {
        super(_id);
    }

    @Override
    public void setValue(@NotNull String key, Object value) {
        if (key.equals("name")) {
            this.name = (String) value;
        } else {
            super.setValue(key, value);
        }
    }

    public String getName() {
        return name;
    }
}
