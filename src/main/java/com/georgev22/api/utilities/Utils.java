package com.georgev22.api.utilities;

import com.georgev22.api.maps.HashObjectMap;
import com.georgev22.api.minecraft.colors.Color;
import com.georgev22.api.maps.ObjectMap;
import com.georgev22.api.maps.TreeObjectMap;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
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

    /**
     * Checks if the input is of type long
     *
     * @param input input to check if it is long
     * @return <code>true</code> if the input is long,
     * <code>false</code> if it is not
     */
    public static boolean isLong(final String input) {
        return Longs.tryParse(StringUtils.deleteWhitespace(input)) != null;
    }

    /**
     * Checks if the input is of type double
     *
     * @param input input to check if it is double
     * @return <code>true</code> if the input is double,
     * <code>false</code> if it is not
     */
    public static boolean isDouble(final String input) {
        return Doubles.tryParse(StringUtils.deleteWhitespace(input)) != null;
    }

    /**
     * Checks if the input is of type int
     *
     * @param input input to check if it is int
     * @return <code>true</code> if the input is int,
     * <code>false</code> if it is not
     */
    public static boolean isInt(final String input) {
        return Ints.tryParse(StringUtils.deleteWhitespace(input)) != null;
    }

    /**
     * Checks if the input is a List
     *
     * @param input input to check if it is a List
     * @return <code>true</code> if the input is a List,
     * <code>false</code> if it is not
     */
    public static boolean isList(final Object input) {
        return input instanceof List;
    }

    /**
     * Translates all the placeholders of the string from the map
     *
     * @param str        the input string to translate the placeholders on
     * @param map        the map that contains all the placeholders with the replacement
     * @param ignoreCase if it is <code>true</code> all the placeholders will be replaced
     *                   in ignore case
     * @return the new string with the placeholders replaced
     */
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

    /**
     * Replaces a string, without distinguishing between lowercase and uppercase
     *
     * @param text         the input string
     * @param searchString the string to be replaced
     * @param replacement  the replacement of the string
     * @return the new string with the replacement
     */
    public static String replaceIgnoreCase(final String text, String searchString, final String replacement) {

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
        final int replacementLength = searchString.length();
        int increase = replacement.length() - replacementLength;
        increase = Math.max(increase, 0);
        increase *= 16;

        final StringBuilder buf = new StringBuilder(text.length() + increase);
        while (end != -1) {
            buf.append(text, start, end).append(replacement);
            start = end + replacementLength;
            if (--max == 0) {
                break;
            }
            end = searchText.indexOf(searchString, start);
        }
        return buf.append(text, start, text.length()).toString();
    }

    /**
     * Translates all the placeholders of the string from the map
     *
     * @param array      the input array of string to translate the placeholders on
     * @param map        the map that contains all the placeholders with the replacement
     * @param ignoreCase if it is <code>true</code> all the placeholders will be replaced
     *                   in ignore case
     * @return the new string array with the placeholders replaced
     */
    public static String @NotNull [] placeHolder(final String[] array, final Map<String, String> map, final boolean ignoreCase) {
        Validate.notNull(array, "The string array can't be null!");
        Validate.noNullElements(array, "The string array can't have null elements!");
        final String[] newArray = Arrays.copyOf(array, array.length);
        if (map == null) {
            return newArray;
        }
        for (int i = 0; i < newArray.length; i++) {
            newArray[i] = placeHolder(newArray[i], map, ignoreCase);
        }
        return newArray;
    }

    /**
     * Translates all the placeholders of the string from the map
     *
     * @param coll       the input string list to translate the placeholders on
     * @param map        the map that contains all the placeholders with the replacement
     * @param ignoreCase if it is <code>true</code> all the placeholders will be replaced
     *                   in ignore case
     * @return the new string list with the placeholders replaced
     */
    public static List<String> placeHolder(final List<String> coll, final Map<String, String> map,
                                           final boolean ignoreCase) {
        Validate.notNull(coll, "The string collection can't be null!");
        Validate.noNullElements(coll, "The string collection can't have null elements!");
        return map == null ? coll
                : coll.stream().map(str -> placeHolder(str, map, ignoreCase)).collect(Collectors.toList());
    }

    /**
     * Formats a number to a specific Locale
     *
     * @param lang  the desired locale
     * @param input the input number
     * @return the formatted String
     */
    public static String formatNumber(Locale lang, double input) {
        Validate.notNull(lang);
        return NumberFormat.getInstance(lang).format(input);
    }

    /**
     * Formats a number to the US Locale
     *
     * @param input the input number
     * @return the formatted String
     */
    public static String formatNumber(double input) {
        return formatNumber(Locale.US, input);
    }

    /**
     * Finds and retrieves the greatest values of a map.
     *
     * @param map The map to get the greatest values
     * @param n   The number of values you want to get
     * @param <K> The map key
     * @param <V> The map value
     * @return Map
     */
    public static <K, V extends Comparable<? super V>> @NotNull List<Entry<K, V>> findGreatest(@NotNull Map<K, V> map, int n) {
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

    public static @NotNull String getArgumentsToString(String @NotNull [] args, int num) {
        StringBuilder sb = new StringBuilder();
        for (int i = num; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        return sb.toString().trim();
    }

    public static String @NotNull [] getArgumentsToArray(String @NotNull [] args, int num) {
        if (args.length == 0) {
            return args;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = num; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        return sb.toString().trim().split(" ");
    }

    public static String @NotNull [] reverse(String[] a) {
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
     * @param string serialized string
     * @param <T>    the original object type (eg: {@code deserialize(stringToDeserialize, new TypeToken<ObjectMap<String, Integer>>(){}.getType())})
     * @return the deserialized object
     * @since v5.0.1
     */
    public static <T> T deserialize(@NotNull String string, @NotNull Type type) {
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

    /**
     * Converts a string list to string.
     *
     * @param stringList The String List to convert.
     * @return a String that contains the String List contents.
     */
    public static @NotNull String stringListToString(@NotNull List<String> stringList) {
        return stringList.toString().replace("[", "").replace("]", "").replace(" ", "");
    }

    /**
     * Convert a string to string list.
     *
     * @param string The string to convert.
     * @return a String List that contains the String contents.
     */
    public static @NotNull List<String> stringToStringList(@NotNull String string) {
        return string.replace(" ", "").isEmpty() ? Lists.newArrayList() : new ArrayList<>(Arrays.asList(string.split(",")));
    }

    /**
     * Converts a map to string list.
     *
     * @param objectMap The {@link ObjectMap} to convert.
     * @return a String List with the {@link ObjectMap} contents.
     */
    public static <K, V> @NotNull List<String> objectMapToStringList(@NotNull ObjectMap<K, V> objectMap) {
        return mapToStringList(objectMap);
    }

    /**
     * Converts a map to string list.
     *
     * @param map The {@link Map} to convert.
     * @return a String List with the {@link Map} contents.
     */
    public static <K, V> @NotNull List<String> mapToStringList(@NotNull Map<K, V> map) {
        List<String> stringList = Lists.newArrayList();
        for (Entry<K, V> entry : map.entrySet()) {
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
     * @return a {@link ObjectMap#newHashObjectMap(Map)} with the String List contents.
     */
    public static <K, V, T> @NotNull ObjectMap<K, V> stringListToObjectMap(List<String> stringList, final Class<T> clazz) {
        return new HashObjectMap<>(stringListToHashMap(stringList, clazz));
    }

    /**
     * Converts a String List to {@link HashMap}.
     *
     * @param stringList the String List to convert.
     * @param clazz      The class type of the value.
     * @param <T>        The class type.
     * @param <K>        Type of the key.
     * @param <V>        Type of the value.
     * @return a {@link HashMap} with the String List contents.
     */
    public static <K, V, T> @NotNull Map<K, V> stringListToMap(List<String> stringList, final Class<T> clazz) {
        return new HashMap<>(stringListToHashMap(stringList, clazz));
    }

    /**
     * Converts a String List to {@link Map}.
     *
     * @param stringList the String List to convert.
     * @param clazz      The class type of the value.
     * @param <T>        The class type.
     * @param <K>        Type of the key.
     * @param <V>        Type of the value.
     * @return a {@link HashMap} with the String List contents.
     */
    public static <K, V, T> @NotNull Map<K, V> stringListToHashMap(@NotNull List<String> stringList, final Class<T> clazz) {
        Map<K, V> map = new HashMap<>();

        if (stringList.isEmpty()) {
            return map;
        }

        for (String string : stringList) {
            if (!string.contains("=")) continue;
            String[] arguments = string.split("=");
            if (clazz != null) {
                Method method;
                try {
                    method = Reflection.fetchMethod(clazz, "valueOf", String.class);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    continue;
                }

                try {
                    map.put((K) arguments[0], (V) method.invoke(null, arguments[1]));
                } catch (InvocationTargetException | IllegalAccessException ex) {
                    ex.printStackTrace();
                    System.out.println("Failure: " + arguments[1] + " is not of type " + clazz.getName());
                }
            } else {
                map.put((K) arguments[0], (V) arguments[1]);
            }

        }

        return map;
    }

    /**
     * Converts a String List that contains color codes to Color List
     *
     * @param list the String List that contains the color codes
     * @return the new Color List with the colors of the input Color String List
     */
    public static @NotNull List<Color> colorsStringListToColorList(@NotNull List<String> list) {
        return colorsStringListToColorList(list.toArray(new String[0]));
    }

    /**
     * Converts a String Array that contains color codes to Color List
     *
     * @param array the String Array that contains the color codes
     * @return the new Color List with the colors of the input Color String Array
     */
    public static @NotNull List<Color> colorsStringListToColorList(String @NotNull ... array) {
        List<Color> colorList = Lists.newArrayList();
        for (String str : array) {
            colorList.add(Color.from(str));
        }
        return colorList;
    }

    /**
     * Generates a specific amount of random HEX colors
     *
     * @param amount the amount of the colors to be generated
     * @return a String List that contains the generated color codes
     */
    public static @NotNull List<String> randomColors(int amount) {
        List<String> randomColorList = Lists.newArrayList();
        for (int i = 0; i < amount; i++) {
            randomColorList.add(String.format("#%06x", new Random().nextInt(0xffffff + 1)));
        }
        return randomColorList;
    }

    /**
     * Converts a number to Roman Number format
     *
     * @param number number to convert
     * @return a string of the number at Roman Number format
     */
    public static String toRoman(int number) {
        if (number <= 0) {
            return String.valueOf(number);
        }
        TreeObjectMap<Integer, String> map = ObjectMap.newTreeObjectMap();
        map
                .append(1000, "M")
                .append(900, "CM")
                .append(500, "D")
                .append(400, "CD")
                .append(100, "C")
                .append(90, "XC")
                .append(50, "L")
                .append(40, "XL")
                .append(10, "X")
                .append(9, "IX")
                .append(5, "V")
                .append(4, "IV")
                .append(1, "I")
        ;
        int l = map.floorKey(number);
        if (number == l) {
            return map.get(number);
        }
        return map.get(l) + toRoman(number - l);
    }

    /**
     * Returns element closest to target in arr[]
     */
    public static int findClosest(@NotNull List<Integer> list, int target) {
        Integer[] arr = list.toArray(new Integer[0]);
        int n = arr.length;
        if (target <= arr[0])
            return arr[0];
        if (target >= arr[n - 1])
            return arr[n - 1];
        int i = 0, j = n, mid = 0;
        while (i < j) {
            mid = (i + j) / 2;
            if (arr[mid] == target)
                return arr[mid];
            if (target < arr[mid]) {
                if (mid > 0 && target > arr[mid - 1])
                    return getClosest(arr[mid - 1],
                            arr[mid], target);
                j = mid;
            } else {
                if (mid < n - 1 && target < arr[mid + 1])
                    return getClosest(arr[mid],
                            arr[mid + 1], target);
                i = mid + 1;
            }
        }

        return arr[mid];
    }

    /**
     * Method to compare which one is the more close
     */
    public static int getClosest(int val1, int val2,
                                 int target) {
        if (target - val1 >= val2 - target)
            return val2;
        else
            return val1;
    }

    /**
     * Generate a random alphanumeric string
     *
     * @param n string length
     * @return a random alphanumeric string
     */
    public static @NotNull String generateAlphaNumericString(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
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
        @Contract(value = "_, null -> fail; _, !null -> param2", pure = true)
        public static <T> @NotNull T notNull(final String name, final T value) {
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
        public static <T> @NotNull T convertToType(final @NotNull Class<T> clazz, final @NotNull Object value, final String errorMessage) {
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

        /**
         * Returns the {@link Class} object associated with the class or
         * interface with the given string name, using the given class loader.
         * Given the fully qualified name for a class or interface (in the same
         * format returned by {@link Class#getName}) this method attempts to
         * locate, load, and link the class or interface.  The specified class
         * loader is used to load the class or interface.  If the parameter
         * {@code loader} is null, the class is loaded through the bootstrap
         * class loader.  The class is initialized only if it has
         * not been initialized earlier.
         *
         * @param name   fully qualified name of the desired class
         * @param loader class loader from which the class must be loaded
         * @return class object representing the desired class
         * @throws LinkageError                if the linkage fails
         * @throws ExceptionInInitializerError if the initialization provoked
         *                                     by this method fails
         * @throws ClassNotFoundException      if the class cannot be located by
         *                                     the specified class loader
         * @see java.lang.Class#forName(String)
         * @see java.lang.ClassLoader
         */
        public static @NotNull Class<?> getClass(String name, ClassLoader loader) throws ClassNotFoundException {
            return Class.forName(name, true, loader);
        }

        /**
         * Returns the {@link Class} object associated with the class or
         * interface with the given string name, using the given class loader.
         * Given the fully qualified name for a class or interface (in the same
         * format returned by {@link Class#getName}) this method attempts to
         * locate, load, and link the class or interface.  The specified class
         * loader is used to load the class or interface.  If the parameter
         * {@code loader} is null, the class is loaded through the bootstrap
         * class loader.  The class is initialized only if it has
         * not been initialized earlier.
         *
         * @param name   fully qualified name of the desired class
         * @param loader class loader from which the class must be loaded
         * @return class object representing the desired class,
         * If a {@link ClassNotFoundException} occurs the return value is {@link Optional#empty()}
         * @see java.lang.Class#forName(String)
         * @see java.lang.ClassLoader
         */
        public static Optional<Class<?>> optionalClass(String name, ClassLoader loader) {
            try {
                return Optional.of(Class.forName(name, true, loader));
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

        /**
         * Returns the inner-{@link Class} object associated with the class or
         * interface with the given parent class, using the given class predicate.
         *
         * @param parentClass    the parent class
         * @param classPredicate the class predicate to test for the inner class
         * @return class object representing the desired inner-class,
         * @throws ClassNotFoundException if the inner-class does not exist
         */
        static Class<?> innerClass(@NotNull Class<?> parentClass, Predicate<Class<?>> classPredicate) throws ClassNotFoundException {
            for (Class<?> innerClass : parentClass.getDeclaredClasses()) {
                if (classPredicate.test(innerClass)) {
                    return innerClass;
                }
            }
            throw new ClassNotFoundException("No class in " + parentClass.getCanonicalName() + " matches the predicate.");
        }

        public static @NotNull Constructor findConstructor(Class<?> clazz, MethodHandles.@NotNull Lookup lookup) throws NoSuchMethodException, IllegalAccessException {
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

        /**
         * Returns the value of the field represented by this {@code Field}, on
         * the specified object. The value is automatically wrapped in an
         * object if it has a primitive type.
         *
         * <p>The underlying field's value is obtained as follows:
         *
         * <p>If the underlying field is a static field, the {@code object} argument
         * is ignored; it may be null.
         *
         * <p>Otherwise, the underlying field is an instance field.  If the
         * specified {@code object} argument is null, the method throws a
         * {@code NullPointerException}. If the specified object is not an
         * instance of the class or interface declaring the underlying
         * field, the method throws an {@code IllegalArgumentException}.
         *
         * <p>If this {@code Field} object is enforcing Java language access control, and
         * the underlying field is inaccessible, the method throws an
         * {@code IllegalAccessException}.
         * If the underlying field is static, the class that declared the
         * field is initialized if it has not already been initialized.
         *
         * <p>Otherwise, the value is retrieved from the underlying instance
         * or static field.  If the field has a primitive type, the value
         * is wrapped in an object before being returned, otherwise it is
         * returned as is.
         *
         * <p>If the field is hidden in the type of {@code object},
         * the field's value is obtained according to the preceding rules.
         *
         * @param clazz  class that contains the field
         * @param object object from which the represented field's value is
         *               to be extracted
         * @param name   field name
         * @return the value of the represented field in object
         * {@code object}; primitive values are wrapped in an appropriate
         * object before being returned
         * @throws IllegalAccessException      if this {@code Field} object
         *                                     is enforcing Java language access control and the underlying
         *                                     field is inaccessible.
         * @throws IllegalArgumentException    if the specified object is not an
         *                                     instance of the class or interface declaring the underlying
         *                                     field (or a subclass or implementor thereof).
         * @throws InvocationTargetException   if the underlying method
         *                                     throws an exception.
         * @throws NullPointerException        if the specified object is null
         *                                     and the field is an instance field.
         * @throws ExceptionInInitializerError if the initialization provoked
         *                                     by this method fails.
         */
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

        /**
         * Sets the field represented by this {@code Field} object on the
         * specified object argument to the specified new value. The new
         * value is automatically unwrapped if the underlying field has a
         * primitive type.
         *
         * <p>The operation proceeds as follows:
         *
         * <p>If the underlying field is static, the {@code object} argument is
         * ignored; it may be null.
         *
         * <p>Otherwise the underlying field is an instance field.  If the
         * specified object argument is null, the method throws a
         * {@code NullPointerException}.  If the specified object argument is not
         * an instance of the class or interface declaring the underlying
         * field, the method throws an {@code IllegalArgumentException}.
         *
         * <p>If this {@code Field} object is enforcing Java language access control, and
         * the underlying field is inaccessible, the method throws an
         * {@code IllegalAccessException}.
         *
         * <p>If the underlying field is final, the method throws an
         * {@code IllegalAccessException} unless {@code setAccessible(true)}
         * has succeeded for this {@code Field} object
         * and the field is non-static. Setting a final field in this way
         * is meaningful only during deserialization or reconstruction of
         * instances of classes with blank final fields, before they are
         * made available for access by other parts of a program. Use in
         * any other context may have unpredictable effects, including cases
         * in which other parts of a program continue to use the original
         * value of this field.
         *
         * <p>If the underlying field is of a primitive type, an unwrapping
         * conversion is attempted to convert the new value to a value of
         * a primitive type.  If this attempt fails, the method throws an
         * {@code IllegalArgumentException}.
         *
         * <p>If, after possible unwrapping, the new value cannot be
         * converted to the type of the underlying field by an identity or
         * widening conversion, the method throws an
         * {@code IllegalArgumentException}.
         *
         * <p>If the underlying field is static, the class that declared the
         * field is initialized if it has not already been initialized.
         *
         * <p>The field is set to the possibly unwrapped and widened new value.
         *
         * <p>If the field is hidden in the type of {@code object},
         * the field's value is set according to the preceding rules.
         *
         * @param clazz  class that contains the specific field
         * @param object object whose field should be modified
         * @param name   field name
         * @param value  new value for the field of {@code object}
         *               being modified
         * @throws IllegalAccessException      if this {@code Field} object
         *                                     is enforcing Java language access control and the underlying
         *                                     field is either inaccessible or final.
         * @throws IllegalArgumentException    if the specified object is not an
         *                                     instance of the class or interface declaring the underlying
         *                                     field (or a subclass or implementor thereof),
         *                                     or if an unwrapping conversion fails.
         * @throws InvocationTargetException   if the underlying method
         *                                     throws an exception.
         * @throws NullPointerException        if the specified object is null
         *                                     and the field is an instance field.
         * @throws ExceptionInInitializerError if the initialization provoked
         *                                     by this method fails.
         */
        public static void setFieldValue(final Class<?> clazz, final Object object, final String name, Object value) throws NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
            if (isUnsafeAvailable()) {
                Field field = clazz.getDeclaredField(name);
                long offset = (long) fetchMethodAndInvoke(theUnsafe.getClass(), "objectFieldOffset", theUnsafe, new Object[]{field}, new Class[]{Field.class});
                fetchMethodAndInvoke(theUnsafe.getClass(), "putObject", theUnsafe, new Object[]{object, offset, value}, new Class[]{Object.class, long.class, Object.class});
            }

            try {
                Field field = clazz.getDeclaredField(name);
                field.setAccessible(true);
                field.set(object, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new UnsupportedOperationException();
            }
        }

        /**
         * Returns a {@code Method} object that reflects the specified
         * declared method of the class or interface represented by this
         * {@code Class} object. The {@code name} parameter is a
         * {@code String} that specifies the simple name of the desired
         * method, and the {@code parameterTypes} parameter is an array of
         * {@code Class} objects that identify the method's formal parameter
         * types, in declared order.  If more than one method with the same
         * parameter types is declared in a class, and one of these methods has a
         * return type that is more specific than any of the others, that method is
         * returned; otherwise one of the methods is chosen arbitrarily.  If the
         * name is "&lt;init&gt;"or "&lt;clinit&gt;" a {@code NoSuchMethodException}
         * is raised.
         *
         * <p> If this {@code Class} object represents an array type, then this
         * method does not find the {@code clone()} method.
         *
         * @param clazz          the class that contains the method
         * @param name           the name of the method
         * @param parameterTypes the parameter array
         * @return the {@code Method} object for the method of this class
         * matching the specified name and parameters
         * @throws NoSuchMethodException if a matching method is not found.
         * @throws NullPointerException  if {@code name} is {@code null}
         * @throws SecurityException     If a security manager, <i>s</i>, is present and any of the
         *                               following conditions is met:
         *
         *                               <ul>
         *
         *                               <li> the caller's class loader is not the same as the
         *                               class loader of this class and invocation of
         *                               {@link SecurityManager#checkPermission
         *                               s.checkPermission} method with
         *                               {@code RuntimePermission("accessDeclaredMembers")}
         *                               denies access to the declared method
         *
         *                               <li> the caller's class loader is not the same as or an
         *                               ancestor of the class loader for the current class and
         *                               invocation of {@link SecurityManager#checkPackageAccess
         *                               s.checkPackageAccess()} denies access to the package
         *                               of this class
         *
         *                               </ul>
         */
        public static @NotNull Method fetchMethod(final @NotNull Class<?> clazz, final String name, Class<?>... parameterTypes) throws NoSuchMethodException {
            Method method = clazz.getDeclaredMethod(name, parameterTypes);
            if (!isUnsafeAvailable()) {
                method.setAccessible(true);
            }
            return method;
        }

        /**
         * Check {@link Reflection#fetchMethod(Class, String, Class...)} for
         * the fetch method.
         * Invokes the underlying method represented by this {@code Method}
         * object, on the specified object with the specified parameters.
         * Individual parameters are automatically unwrapped to match
         * primitive formal parameters, and both primitive and reference
         * parameters are subject to method invocation conversions as
         * necessary.
         *
         * <p>If the underlying method is static, then the specified {@code obj}
         * argument is ignored. It may be null.
         *
         * <p>If the number of formal parameters required by the underlying method is
         * 0, the supplied {@code args} array may be of length 0 or null.
         *
         * <p>If the underlying method is an instance method, it is invoked
         * using dynamic method lookup as documented in The Java Language
         * Specification, Second Edition, section 15.12.4.4; in particular,
         * overriding based on the runtime type of the target object will occur.
         *
         * <p>If the underlying method is static, the class that declared
         * the method is initialized if it has not already been initialized.
         *
         * <p>If the method completes normally, the value it returns is
         * returned to the caller of invoke; if the value has a primitive
         * type, it is first appropriately wrapped in an object. However,
         * if the value has the type of array of a primitive type, the
         * elements of the array are <i>not</i> wrapped in objects; in
         * other words, an array of primitive type is returned.  If the
         * underlying method return type is void, the invocation returns
         * null.
         *
         * @param clazz          the class that contains the method
         * @param name           the name of the method
         * @param parameterTypes the parameter array
         * @param obj            the object the underlying method is invoked from
         * @param arguments      the arguments used for the method call
         * @return the result of dispatching the method represented by
         * this object on {@code obj} with parameters
         * {@code args}
         * @throws IllegalAccessException      if this {@code Method} object
         *                                     is enforcing Java language access control and the underlying
         *                                     method is inaccessible.
         * @throws IllegalArgumentException    if the method is an
         *                                     instance method and the specified object argument
         *                                     is not an instance of the class or interface
         *                                     declaring the underlying method (or of a subclass
         *                                     or implementor thereof); if the number of actual
         *                                     and formal parameters differ; if an unwrapping
         *                                     conversion for primitive arguments fails; or if,
         *                                     after possible unwrapping, a parameter value
         *                                     cannot be converted to the corresponding formal
         *                                     parameter type by a method invocation conversion.
         * @throws InvocationTargetException   if the underlying method
         *                                     throws an exception.
         * @throws NullPointerException        if the specified object is null
         *                                     and the method is an instance method.
         * @throws ExceptionInInitializerError if the initialization
         *                                     provoked by this method fails.
         */
        public static Object fetchMethodAndInvoke(final Class<?> clazz, final String name, Object obj, Object[] arguments, Class<?>[] parameterTypes) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
            return fetchMethod(clazz, name, parameterTypes).invoke(obj, arguments);
        }

        @FunctionalInterface
        interface Constructor {
            Object invoke() throws Throwable;
        }
    }

    public static class Cooldown {
        private static final ObjectMap<String, Cooldown> cooldownManagerObjectMap = ObjectMap.newHashObjectMap();
        private long start;
        private final int timeInSeconds;
        private final UUID id;
        private final String cooldownName;

        public Cooldown(UUID id, String cooldownName, int timeInSeconds) {
            this.id = id;
            this.cooldownName = cooldownName;
            this.timeInSeconds = timeInSeconds;
        }

        public static boolean isInCooldown(UUID id, String cooldownName) {
            if (Cooldown.getTimeLeft(id, cooldownName) >= 1) {
                return true;
            }
            Cooldown.stop(id, cooldownName);
            return false;
        }

        private static void stop(UUID id, String cooldownName) {
            cooldownManagerObjectMap.remove(id + cooldownName);
        }

        private static Cooldown getCooldown(@NotNull UUID id, String cooldownName) {
            return cooldownManagerObjectMap.get(id + cooldownName);
        }

        public static int getTimeLeft(UUID id, String cooldownName) {
            Cooldown cooldown = Cooldown.getCooldown(id, cooldownName);
            int f = -1;
            if (cooldown != null) {
                long now = System.currentTimeMillis();
                long cooldownTime = cooldown.start;
                int r = (int) (now - cooldownTime) / 1000;
                f = (r - cooldown.timeInSeconds) * -1;
            }
            return f;
        }

        public void start() {
            this.start = System.currentTimeMillis();
            cooldownManagerObjectMap.put(this.id.toString() + this.cooldownName, this);
        }

        public static @NotNull String getTimeLeft(int secondTime) {
            TimeZone tz = Calendar.getInstance().getTimeZone();
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            df.setTimeZone(tz);
            return df.format(new Date(secondTime * 1000L));
        }

        public static ObjectMap<String, Cooldown> getCooldowns() {
            return cooldownManagerObjectMap;
        }

        public static ObjectMap<String, Cooldown> appendToCooldowns(ObjectMap<String, Cooldown> cooldownObjectMap) {
            return cooldownManagerObjectMap.append(cooldownObjectMap);
        }

    }

    public static abstract class Callback {

        public abstract void onSuccess();

        public abstract void onFailure(Throwable throwable);
    }
}
