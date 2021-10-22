package com.georgev22.api.utilities;

import com.georgev22.api.maps.ObjectMap;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class Utils {

    public static String convertSeconds(long input, String secondInput, String secondsInput, String minuteInput,
                                        String minutesInput, String hourInput, String hoursInput, String dayInput, String daysInput,
                                        String invalidInput) {
        if (input < 0) {
            System.out.println(
                    "An attempt to convert a negative number was made for: " + input + ", making the number absolute.");
            input = Math.abs(input);
        }

        final StringBuilder builder = new StringBuilder();

        boolean comma = false;

        /* Days */
        final long days = TimeUnit.SECONDS.toDays(input);
        if (days > 0) {
            builder.append(days).append(" ").append(days == 1 ? dayInput : daysInput);
            comma = true;
        }

        /* Hours */
        final long hours = (TimeUnit.SECONDS.toHours(input) - TimeUnit.DAYS.toHours(days));
        if (hours > 0) {
            if (comma) {
                builder.append(", ");
            }
            builder.append(hours).append(" ").append(hours == 1 ? hourInput : hoursInput);
            comma = true;
        }

        /* Minutes */
        final long minutes = (TimeUnit.SECONDS.toMinutes(input) - TimeUnit.HOURS.toMinutes(hours)
                - TimeUnit.DAYS.toMinutes(days));
        if (minutes > 0) {
            if (comma) {
                builder.append(", ");
            }
            builder.append(minutes).append(" ").append(minutes == 1 ? minuteInput : minutesInput);
            comma = true;
        }

        /* Seconds */
        final long seconds = (TimeUnit.SECONDS.toSeconds(input) - TimeUnit.MINUTES.toSeconds(minutes)
                - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.DAYS.toSeconds(days));
        if (seconds > 0) {
            if (comma) {
                builder.append(", ");
            }
            builder.append(seconds).append(" ").append(seconds == 1 ? secondInput : secondsInput);
        }

        /* Result */
        final String result = builder.toString();
        return result.equals("") ? invalidInput : result;
    }

    public static String convertSeconds(long input) {
        return convertSeconds(input, "second", "seconds", "minute", "minutes",
                "hour", "hours", "day", "days",
                "invalid time");
    }

    /* ----------------------------------------------------------------- */

    //

    /* ----------------------------------------------------------------- */
    public static boolean isLong(final String input) {
        return Longs.tryParse(StringUtils.deleteWhitespace(input)) != null;
    }

    public static boolean isDouble(final String input) {
        return Doubles.tryParse(StringUtils.deleteWhitespace(input)) != null;
    }

    public static boolean isInt(final String input) {
        return Ints.tryParse(StringUtils.deleteWhitespace(input)) != null;
    }

    public static boolean isList(final Object obj) {
        return obj instanceof List;
    }

    public static String placeHolder(String str, final Map<String, String> map, final boolean ignoreCase) {
        Validate.notNull(str, "The string can't be null!");
        if (map == null) {
            return str;
        }
        for (final Entry<String, String> entry : map.entrySet()) {
            str = ignoreCase ? replaceIgnoreCase(str, entry.getKey(), entry.getValue())
                    : str.replace(entry.getKey(), entry.getValue());
        }
        return str;
    }

    private static String replaceIgnoreCase(final String text, String searchString, final String replacement) {

        if (text == null || text.length() == 0) {
            return text;
        }
        if (searchString == null || searchString.length() == 0) {
            return text;
        }
        if (replacement == null) {
            return text;
        }

        int max = -1;

        final String searchText = text.toLowerCase();
        searchString = searchString.toLowerCase();
        int start = 0;
        int end = searchText.indexOf(searchString, start);
        if (end == -1) {
            return text;
        }
        final int replLength = searchString.length();
        int increase = replacement.length() - replLength;
        increase = Math.max(increase, 0);
        increase *= 16;

        final StringBuilder buf = new StringBuilder(text.length() + increase);
        while (end != -1) {
            buf.append(text, start, end).append(replacement);
            start = end + replLength;
            if (--max == 0) {
                break;
            }
            end = searchText.indexOf(searchString, start);
        }
        return buf.append(text, start, text.length()).toString();
    }

    public static String[] placeHolder(final String[] array, final Map<String, String> map, final boolean ignoreCase) {
        Validate.notNull(array, "The string array can't be null!");
        Validate.noNullElements(array, "The string array can't have null elements!");
        final String[] newarr = Arrays.copyOf(array, array.length);
        if (map == null) {
            return newarr;
        }
        for (int i = 0; i < newarr.length; i++) {
            newarr[i] = placeHolder(newarr[i], map, ignoreCase);
        }
        return newarr;
    }

    public static List<String> placeHolder(final List<String> coll, final Map<String, String> map,
                                           final boolean ignoreCase) {
        Validate.notNull(coll, "The string collection can't be null!");
        Validate.noNullElements(coll, "The string collection can't have null elements!");
        return map == null ? coll
                : coll.stream().map(str -> placeHolder(str, map, ignoreCase)).collect(Collectors.toList());
    }

    private static String formatNumber(Locale lang, double input) {
        Validate.notNull(lang);
        return NumberFormat.getInstance(lang).format(input);
    }

    public static String formatNumber(double input) {
        return formatNumber(Locale.US, input);
    }

    /**
     * Get the greatest values in a map
     *
     * @param map The map to get the greatest values
     * @param n   The number of values you want to get
     * @param <K> The map key
     * @param <V> The map value
     * @return Map
     */
    public static <K, V extends Comparable<? super V>> List<Entry<K, V>> findGreatest(Map<K, V> map, int n) {
        Comparator<? super Entry<K, V>> comparator = (Comparator<Entry<K, V>>) (e0, e1) -> {
            V v0 = e0.getValue();
            V v1 = e1.getValue();
            return v1.compareTo(v0);
        };
        PriorityQueue<Entry<K, V>> highest = new PriorityQueue<>(n, comparator);
        for (Entry<K, V> entry : map.entrySet()) {
            highest.offer(entry);
            while (highest.size() > n) {
                highest.poll();
            }
        }

        List<Entry<K, V>> result = new ArrayList<>();
        while (highest.size() > 0) {
            result.add(highest.poll());
        }
        return result;
    }

    public static String getArgumentsToString(String[] args, int num) {
        StringBuilder sb = new StringBuilder();
        for (int i = num; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        return sb.toString().trim();
    }

    public static String[] getArgumentsToArray(String[] args, int num) {
        StringBuilder sb = new StringBuilder();
        for (int i = num; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        return sb.toString().trim().split(" ");
    }

    public static String[] reverse(String[] a) {
        List<String> list = Arrays.asList(a);
        Collections.reverse(list);
        return list.toArray(new String[0]);
    }

    /**
     * Serialize Object to string using google Gson
     *
     * @param object object to serialize
     * @return string output of the serialized object
     * @since v5.0.1
     */
    public static <T> String serialize(Object object) {
        ByteArrayOutputStream byteaOut = new ByteArrayOutputStream();
        GZIPOutputStream gzipOut = null;
        try {
            gzipOut = new GZIPOutputStream(new Base64OutputStream(byteaOut));
            gzipOut.write(new Gson().toJson(object).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gzipOut != null) try {
                gzipOut.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return byteaOut.toString();
    }

    /**
     * Deserialize a string back to object
     * see {@link #serialize(Object)}
     *
     * @param string serialized string before the serialization
     * @param <T>    the original object type (eg: {@code deserialize(stringToDeserialize, new TypeToken<ObjectMap<String, Integer>>(){}.getType());})
     * @return the deserialized object
     * @since v5.0.1
     */
    public static <T> T deserialize(String string, Type type) {
        ByteArrayOutputStream byteaOut = new ByteArrayOutputStream();
        GZIPInputStream gzipIn = null;
        try {
            gzipIn = new GZIPInputStream(new Base64InputStream(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8))));
            for (int data; (data = gzipIn.read()) > -1; ) {
                byteaOut.write(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gzipIn != null) try {
                gzipIn.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        return new Gson().fromJson(byteaOut.toString(), type);
    }

    //====================

    /**
     * Converts a string list to string.
     *
     * @param stringList The String List to convert.
     * @return a String that contains the String List contents.
     */
    public static String stringListToString(List<String> stringList) {
        return stringList.toString().replace("[", "").replace("]", "").replace(" ", "");
    }

    /**
     * Convert a string to string list.
     *
     * @param string The string to convert.
     * @return a String List that contains the String contents.
     */
    public static List<String> stringToStringList(String string) {
        return string.replace(" ", "").isEmpty() ? Lists.newArrayList() : new ArrayList<>(Arrays.asList(string.split(",")));
    }

    /**
     * Converts a map to string list.
     *
     * @param objectMap The {@link ObjectMap} to convert.
     * @return a String List with the {@link ObjectMap} contents.
     */
    public static <K, V> List<String> mapToStringList(ObjectMap<K, V> objectMap) {
        List<String> stringList = Lists.newArrayList();
        for (Entry<K, V> entry : objectMap.entrySet()) {
            stringList.add(entry.getKey() + "=" + entry.getValue());
        }
        return stringList;
    }

    /**
     * Converts a String List to {@link ObjectMap}.
     *
     * @param stringList the String List to convert.
     * @param clazz      The class type of the value.
     * @param <T>        The class type.
     * @param <K>        Type of the key.
     * @param <V>        Type of the value.
     * @return a {@link ObjectMap} with the String List contents.
     */
    public static <K, V, T> @NotNull ObjectMap<K, V> stringListToObjectMap(List<String> stringList, final Class<T> clazz) {
        ObjectMap<K, V> objectMap = ObjectMap.newHashObjectMap();

        if (stringList == null || stringList.isEmpty()) {
            return objectMap;
        }

        for (String string : stringList) {
            String[] entry = string.split("=");
            if (clazz != null) {
                Method method;
                try {
                    method = clazz.getDeclaredMethod("valueOf", String.class);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    continue;
                }

                if (method != null) {
                    try {
                        objectMap.append((K) entry[0], (V) method.invoke(null, entry[1]));
                    } catch (InvocationTargetException | IllegalAccessException ex) {
                        ex.printStackTrace();
                        System.out.println("Failure: " + entry[1] + " is not of type " + clazz.getName());
                    }
                }
            } else {
                objectMap.append((K) entry[0], (V) entry[1]);
            }

        }

        return objectMap;
    }

    public static final class Assertions {

        /**
         * Throw IllegalArgumentException if the value is null.
         *
         * @param name  the parameter name
         * @param value the value that should not be null
         * @param <T>   the value type
         * @return the value
         * @throws IllegalArgumentException if value is null
         */
        public static <T> T notNull(final String name, final T value) {
            if (value == null) {
                throw new IllegalArgumentException(name + " can not be null");
            }
            return value;
        }

        /**
         * Throw IllegalStateException if the condition if false.
         *
         * @param name      the name of the state that is being checked
         * @param condition the condition about the parameter to check
         * @throws IllegalStateException if the condition is false
         */
        public static void isTrue(final String name, final boolean condition) {
            if (!condition) {
                throw new IllegalStateException("state should be: " + name);
            }
        }

        /**
         * Throw IllegalArgumentException if the condition if false.
         *
         * @param name      the name of the state that is being checked
         * @param condition the condition about the parameter to check
         * @throws IllegalArgumentException if the condition is false
         */
        public static void isTrueArgument(final String name, final boolean condition) {
            if (!condition) {
                throw new IllegalArgumentException("state should be: " + name);
            }
        }

        /**
         * Throw IllegalArgumentException if the condition if false, otherwise return the value.  This is useful when arguments must be checked
         * within an expression, as when using {@code this} to call another constructor, which must be the first line of the calling
         * constructor.
         *
         * @param <T>       the value type
         * @param name      the name of the state that is being checked
         * @param value     the value of the argument
         * @param condition the condition about the parameter to check
         * @return the value
         * @throws IllegalArgumentException if the condition is false
         */
        public static <T> T isTrueArgument(final String name, final T value, final boolean condition) {
            if (!condition) {
                throw new IllegalArgumentException("state should be: " + name);
            }
            return value;
        }

        /**
         * Cast an object to the given class and return it, or throw IllegalArgumentException if it's not assignable to that class.
         *
         * @param clazz        the class to cast to
         * @param value        the value to cast
         * @param errorMessage the error message to include in the exception
         * @param <T>          the Class type
         * @return value cast to clazz
         * @throws IllegalArgumentException if value is not assignable to clazz
         */
        public static <T> T convertToType(final Class<T> clazz, final Object value, final String errorMessage) {
            if (!clazz.isAssignableFrom(value.getClass())) {
                throw new IllegalArgumentException(errorMessage);
            }
            return (T) value;
        }

        private Assertions() {
        }
    }

    public static class Reflection {

        private static volatile Object theUnsafe;

        static {
            try {
                synchronized (Reflection.class) {
                    if (theUnsafe == null) {
                        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                        Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
                        theUnsafeField.setAccessible(true);
                        theUnsafe = theUnsafeField.get(null);
                    }
                }
            } catch (Exception e) {
                theUnsafe = null;
            }
        }

        public static boolean isUnsafeAvailable() {
            return theUnsafe != null;
        }

        public static @NotNull Class<?> getClass(String className, ClassLoader classLoader) throws ClassNotFoundException {
            return Class.forName(className, true, classLoader);
        }

        public static Optional<Class<?>> getOptionalClass(String className, ClassLoader classLoader) {
            return optionalClass(className, classLoader);
        }

        public static Optional<Class<?>> optionalClass(String className, ClassLoader classLoader) {
            try {
                return Optional.of(Class.forName(className, true, classLoader));
            } catch (ClassNotFoundException e) {
                return Optional.empty();
            }
        }

        public static @NotNull Object enumValueOf(@NotNull Class<?> enumClass, String enumName) {
            return Enum.valueOf(enumClass.asSubclass(Enum.class), enumName);
        }

        public static Object enumValueOf(Class<?> enumClass, String enumName, int fallbackOrdinal) {
            try {
                return enumValueOf(enumClass, enumName);
            } catch (IllegalArgumentException e) {
                Object[] constants = enumClass.getEnumConstants();
                if (constants.length > fallbackOrdinal) {
                    return constants[fallbackOrdinal];
                }
                throw e;
            }
        }

        static Class<?> innerClass(@NotNull Class<?> parentClass, Predicate<Class<?>> classPredicate) throws ClassNotFoundException {
            for (Class<?> innerClass : parentClass.getDeclaredClasses()) {
                if (classPredicate.test(innerClass)) {
                    return innerClass;
                }
            }
            throw new ClassNotFoundException("No class in " + parentClass.getCanonicalName() + " matches the predicate.");
        }

        public static @NotNull Constructor findConstructor(Class<?> clazz, MethodHandles.@NotNull Lookup lookup) throws Exception {
            if (isUnsafeAvailable()) {
                MethodType allocateMethodType = MethodType.methodType(Object.class, Class.class);
                MethodHandle allocateMethod = lookup.findVirtual(theUnsafe.getClass(), "allocateInstance", allocateMethodType);
                return () -> allocateMethod.invoke(theUnsafe, clazz);
            }
            try {
                MethodHandle constructor = lookup.findConstructor(clazz, MethodType.methodType(void.class));
                return constructor::invoke;
            } catch (NoSuchMethodException | IllegalAccessException e) {
                throw new UnsupportedOperationException();
            }
        }

        public static Object fetchField(final Class<?> clazz, final Object object, final String name) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
            if (isUnsafeAvailable()) {
                Field field = clazz.getDeclaredField(name);
                long offset = (long) fetchMethodAndInvoke(theUnsafe.getClass(), "objectFieldOffset", theUnsafe, new Object[]{field}, new Class[]{Field.class});
                return fetchMethodAndInvoke(theUnsafe.getClass(), "getObject", theUnsafe, new Object[]{object, offset}, new Class[]{Object.class, long.class});
            }
            try {
                Field field = clazz.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(object);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new UnsupportedOperationException();
            }
        }

        public static Method fetchMethod(final @NotNull Class<?> clazz, final String name, Class<?>... parameterTypes) throws NoSuchMethodException {
            Method method = clazz.getDeclaredMethod(name, parameterTypes);
            if (!isUnsafeAvailable()) {
                method.setAccessible(true);
            }
            return method;
        }

        public static Object fetchMethodAndInvoke(final Class<?> clazz, final String name, Object obj, Object[] arguments, Class<?>[] parameterTypes) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            return fetchMethod(clazz, name, parameterTypes).invoke(obj, arguments);
        }

        @FunctionalInterface
        interface Constructor {
            Object invoke() throws Throwable;
        }
    }
}
