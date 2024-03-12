package com.georgev22.library.utilities.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Column {

    /**
     * Returns the name of the column.
     *
     * @return the name of the column
     */
    String name();

    /**
     * Returns the type of the column.
     * Example VARCHAR(255)
     *
     * @return the type of the column
     */
    String type();

    /**
     * Returns if the column is unique.
     *
     * @return if the column is unique
     */
    boolean unique() default false;

    /**
     * Returns the default value of the column.
     *
     * @return the default value of the column
     */
    String defaultValue() default "";

    /**
     * Returns the class of the default value of the column.
     *
     * @return the class of the default value of the column
     */
    Class<?> defaultValueClass() default String.class;

}
