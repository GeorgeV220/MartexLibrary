package com.georgev22.api.extensions;


import com.georgev22.api.exceptions.InvalidDescriptionException;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class ExtensionDescriptionFile {
    private static final Pattern VALID_NAME = Pattern.compile("^[A-Za-z0-9 _.-]+$");
    private static final ThreadLocal<Yaml> YAML = ThreadLocal.withInitial(() -> new Yaml(new SafeConstructor() {
        {
            yamlConstructors.put(null, new AbstractConstruct() {
                @NotNull
                @Override
                public Object construct(@NotNull final Node node) {
                    if (!node.getTag().startsWith("!@")) {
                        return SafeConstructor.undefinedConstructor.construct(node);
                    }
                    return new ExtensionAwareness() {
                        @Override
                        public String toString() {
                            return node.toString();
                        }
                    };
                }
            });
            for (final ExtensionAwareness.Flags flag : ExtensionAwareness.Flags.values()) {
                yamlConstructors.put(new Tag("!@" + flag.name()), new AbstractConstruct() {
                    @NotNull
                    @Override
                    public ExtensionAwareness.Flags construct(@NotNull final Node node) {
                        return flag;
                    }
                });
            }
        }
    }));
    String rawName = null;
    private String name = null;
    private List<String> provides = ImmutableList.of();
    private String main = null;

    private String pkg = null;
    private String classLoaderOf = null;
    private List<String> depend = ImmutableList.of();
    private List<String> softDepend = ImmutableList.of();
    private List<String> loadBefore = ImmutableList.of();
    private String version = null;
    private String description = null;
    private List<String> authors = null;
    private List<String> contributors = null;
    private String website = null;
    private String prefix = null;
    private List<String> libraries = ImmutableList.of();

    public ExtensionDescriptionFile(@NotNull final InputStream stream) throws InvalidDescriptionException {
        loadMap(asMap(YAML.get().load(stream)));
    }

    /**
     * Loads a ExtensionDescriptionFile from the specified reader
     *
     * @param reader The reader
     * @throws InvalidDescriptionException If the ExtensionDescriptionFile is
     *                                     invalid
     */
    public ExtensionDescriptionFile(@NotNull final Reader reader) throws InvalidDescriptionException {
        loadMap(asMap(YAML.get().load(reader)));
    }

    /**
     * Creates a new ExtensionDescriptionFile with the given detailed
     *
     * @param extensionName    Name of this extension
     * @param extensionVersion Version of this extension
     * @param mainClass        Full location of the main class of this extension
     */
    public ExtensionDescriptionFile(@NotNull final String extensionName, @NotNull final String extensionVersion, @NotNull final String mainClass) {
        name = rawName = extensionName;

        if (!VALID_NAME.matcher(name).matches()) {
            throw new IllegalArgumentException("name '" + name + "' contains invalid characters.");
        }
        name = name.replace(' ', '_');
        version = extensionVersion;
        main = mainClass;
    }

    /**
     * Gives the name of the extension. This name is a unique identifier for
     * extensions.
     * <ul>
     * <li>Must consist of all alphanumeric characters, underscores, hyphon,
     *     and period (a-z,A-Z,0-9, _.-). Any other character will cause the
     *     extension.yml to fail loading.
     * <li>Used to determine the name of the extension's data folder. Data
     *     folders are placed in the ./extensions/ directory by default, but this
     *     behavior should not be relied on. {@link Extension#getDataFolder()}
     *     should be used to reference the data folder.
     * <li>It is good practice to name your jar the same as this, for example
     *     'MyExtension.jar'.
     * <li>Case sensitive.
     * <li>The is the token referenced in {@link #getDepend()}, {@link
     *     #getSoftDepend()}, and {@link #getLoadBefore()}.
     * <li>Using spaces in the extension's name is deprecated.
     * </ul>
     * <p>
     * In the extension.yml, this entry is named <code>name</code>.
     * <p>
     * Example:<blockquote><pre>name: MyExtension</pre></blockquote>
     *
     * @return the name of the extension
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Gives the list of other extension APIs which this extension provides.
     * These are usable for other extensions to depend on.
     * <ul>
     * <li>Must consist of all alphanumeric characters, underscores, hyphon,
     *     and period (a-z,A-Z,0-9, _.-). Any other character will cause the
     *     extension.yml to fail loading.
     * <li>A different extension providing the same one or using it as their name
     *     will not result in the extension to fail loading.
     * <li>Case sensitive.
     * <li>An entry of this list can be referenced in {@link #getDepend()},
     *    {@link #getSoftDepend()}, and {@link #getLoadBefore()}.
     * <li><code>provides</code> must be in <a
     *     href="http://en.wikipedia.org/wiki/YAML#Lists">YAML list
     *     format</a>.
     * </ul>
     * <p>
     * In the extension.yml, this entry is named <code>provides</code>.
     * <p>
     * Example:
     * <blockquote><pre>provides:
     * - OtherExtensionName
     * - OldExtensionName</pre></blockquote>
     *
     * @return immutable list of the extension APIs which this extension provides
     */
    @NotNull
    public List<String> getProvides() {
        return provides;
    }

    /**
     * Gives the version of the extension.
     * <ul>
     * <li>Version is an arbitrary string, however the most common format is
     *     MajorRelease.MinorRelease.Build (eg: 1.4.1).
     * <li>Typically you will increment this every time you release a new
     *     feature or bug fix.
     * <li>Displayed when a user types <code>/version ExtensionName</code>
     * </ul>
     * <p>
     * In the extension.yml, this entry is named <code>version</code>.
     * <p>
     * Example:<blockquote><pre>version: 1.4.1</pre></blockquote>
     *
     * @return the version of the extension
     */
    @NotNull
    public String getVersion() {
        return version;
    }

    /**
     * Gives the fully qualified name of the main class for a extension. The
     * format should follow the {@link ClassLoader#loadClass(String)} syntax
     * to successfully be resolved at runtime. For most extensions, this is the
     * class that extends {@link Extension}.
     * <ul>
     * <li>This must contain the full namespace including the class file
     *     itself.
     * <li>If your namespace is <code>org.bukkit.extension</code>, and your class
     *     file is called <code>MyExtension</code> then this must be
     *     <code>org.bukkit.extension.MyExtension</code>
     * <li>No extension can use <code>org.bukkit.</code> as a base package for
     *     <b>any class</b>, including the main class.
     * </ul>
     * <p>
     * In the extension.yml, this entry is named <code>main</code>.
     * <p>
     * Example:
     * <blockquote><pre>main: org.bukkit.extension.MyExtension</pre></blockquote>
     *
     * @return the fully qualified main class for the extension
     */
    @NotNull
    public String getMain() {
        return main;
    }

    /**
     * Gives the fully qualified name of the package for an extension.
     * <ul>
     * <li>This must contain the full namespace
     * <li>No extension can use <code>org.bukkit.</code>
     * </ul>
     * <p>
     * In the extension.yml, this entry is named <code>package</code>.
     * <p>
     * Example:
     * <blockquote><pre>package: org.bukkit.extension</pre></blockquote>
     *
     * @return the fully qualified package for the extension
     */
    public String getPackage() {
        return pkg;
    }

    /**
     * Gives a human-friendly description of the functionality the extension
     * provides.
     * <ul>
     * <li>The description can have multiple lines.
     * <li>Displayed when a user types <code>/version ExtensionName</code>
     * </ul>
     * <p>
     * In the extension.yml, this entry is named <code>description</code>.
     * <p>
     * Example:
     * <blockquote><pre>description: This extension is so 31337. You can set yourself on fire.</pre></blockquote>
     *
     * @return description of this extension, or null if not specified
     */
    @Nullable
    public String getDescription() {
        return description;
    }

    /**
     * Gives the list of authors for the extension.
     * <ul>
     * <li>Gives credit to the developer.
     * <li>Used in some server error messages to provide helpful feedback on
     *     who to contact when an error occurs.
     * <li>A SpigotMC forum handle or email address is recommended.
     * <li>Is displayed when a user types <code>/version ExtensionName</code>
     * <li><code>authors</code> must be in <a
     *     href="http://en.wikipedia.org/wiki/YAML#Lists">YAML list
     *     format</a>.
     * </ul>
     * <p>
     * In the extension.yml, this has two entries, <code>author</code> and
     * <code>authors</code>.
     * <p>
     * Single author example:
     * <blockquote><pre>author: CaptainInflamo</pre></blockquote>
     * Multiple author example:
     * <blockquote><pre>authors: [Cogito, verrier, EvilSeph]</pre></blockquote>
     * When both are specified, author will be the first entry in the list, so
     * this example:
     * <blockquote><pre>author: Grum
     * authors:
     * - feildmaster
     * - amaranth</pre></blockquote>
     * Is equivilant to this example:
     * <pre>authors: [Grum, feildmaster, aramanth]</pre>
     *
     * @return an immutable list of the extension's authors
     */
    @NotNull
    public List<String> getAuthors() {
        return authors;
    }

    /**
     * Gives the list of contributors for the extension.
     * <ul>
     * <li>Gives credit to those that have contributed to the extension, though
     *     not enough so to warrant authorship.
     * <li>Unlike {@link #getAuthors()}, contributors will not be mentioned in
     * server error messages as a means of contact.
     * <li>A SpigotMC forum handle or email address is recommended.
     * <li>Is displayed when a user types <code>/version ExtensionName</code>
     * <li><code>contributors</code> must be in <a
     *     href="http://en.wikipedia.org/wiki/YAML#Lists">YAML list
     *     format</a>.
     * </ul>
     * <p>
     * Example:
     * <blockquote><pre>authors: [Choco, md_5]</pre></blockquote>
     *
     * @return an immutable list of the extension's contributors
     */
    @NotNull
    public List<String> getContributors() {
        return contributors;
    }

    /**
     * Gives the extension's or extension's author's website.
     * <ul>
     * <li>A link to the Curse page that includes documentation and downloads
     *     is highly recommended.
     * <li>Displayed when a user types <code>/version ExtensionName</code>
     * </ul>
     * <p>
     * In the extension.yml, this entry is named <code>website</code>.
     * <p>
     * Example:
     * <blockquote><pre>website: http://www.curse.com/server-mods/minecraft/myextension</pre></blockquote>
     *
     * @return description of this extension, or null if not specified
     */
    @Nullable
    public String getWebsite() {
        return website;
    }

    /**
     * Gives a list of other extensions that the extension requires.
     * <ul>
     * <li>Use the value in the {@link #getName()} of the target extension to
     *     specify the dependency.
     * <li>If any extension listed here is not found, your extension will fail to
     *     load at startup.
     * <li>If multiple extensions list each other in <code>depend</code>,
     *     creating a network with no individual extension does not list another
     *     extension in the <a
     *     href=https://en.wikipedia.org/wiki/Circular_dependency>network</a>,
     *     all extensions in that network will fail.
     * <li><code>depend</code> must be in <a
     *     href="http://en.wikipedia.org/wiki/YAML#Lists">YAML list
     *     format</a>.
     * </ul>
     * <p>
     * In the extension.yml, this entry is named <code>depend</code>.
     * <p>
     * Example:
     * <blockquote><pre>depend:
     * - OneExtension
     * - AnotherExtension</pre></blockquote>
     *
     * @return immutable list of the extension's dependencies
     */
    @NotNull
    public List<String> getDepend() {
        return depend;
    }

    /**
     * Gives a list of other extensions that the extension requires for full
     * functionality.
     * <ul>
     * <li>Use the value in the {@link #getName()} of the target extension to
     *     specify the dependency.
     * <li>When an unresolvable extension is listed, it will be ignored and does
     *     not affect load order.
     * <li>When a circular dependency occurs (a network of extensions depending
     *     or soft-dependending each other), it will arbitrarily choose a
     *     extension that can be resolved when ignoring soft-dependencies.
     * <li><code>softdepend</code> must be in <a
     *     href="http://en.wikipedia.org/wiki/YAML#Lists">YAML list
     *     format</a>.
     * </ul>
     * <p>
     * In the extension.yml, this entry is named <code>softdepend</code>.
     * <p>
     * Example:
     * <blockquote><pre>softdepend: [OneExtension, AnotherExtension]</pre></blockquote>
     *
     * @return immutable list of the extension's preferred dependencies
     */
    @NotNull
    public List<String> getSoftDepend() {
        return softDepend;
    }

    /**
     * Gets the list of extensions that should consider this extension a
     * soft-dependency.
     * <ul>
     * <li>Use the value in the {@link #getName()} of the target extension to
     *     specify the dependency.
     * <li>The extension should load before any other extensions listed here.
     * <li>Specifying another extension here is strictly equivalent to having the
     *     specified extension's {@link #getSoftDepend()} include {@link
     *     #getName() this extension}.
     * <li><code>loadbefore</code> must be in <a
     *     href="http://en.wikipedia.org/wiki/YAML#Lists">YAML list
     *     format</a>.
     * </ul>
     * <p>
     * In the extension.yml, this entry is named <code>loadbefore</code>.
     * <p>
     * Example:
     * <blockquote><pre>loadbefore:
     * - OneExtension
     * - AnotherExtension</pre></blockquote>
     *
     * @return immutable list of extensions that should consider this extension a
     * soft-dependency
     */
    @NotNull
    public List<String> getLoadBefore() {
        return loadBefore;
    }

    /**
     * Gives the token to prefix extension-specific logging messages with.
     * <ul>
     * <li>This includes all messages using {@link Extension#getLogger()}.
     * <li>If not specified, the server uses the extension's {@link #getName()
     *     name}.
     * <li>This should clearly indicate what extension is being logged.
     * </ul>
     * <p>
     * In the extension.yml, this entry is named <code>prefix</code>.
     * <p>
     * Example:<blockquote><pre>prefix: ex-why-zee</pre></blockquote>
     *
     * @return the prefixed logging token, or null if not specified
     */
    @Nullable
    public String getPrefix() {
        return prefix;
    }

    /**
     * Returns the name of a extension, including the version. This method is
     * provided for convenience; it uses the {@link #getName()} and {@link
     * #getVersion()} entries.
     *
     * @return a descriptive name of the extension and respective version
     */
    @NotNull
    public String getFullName() {
        return name + " v" + version;
    }

    /**
     * Gets the libraries this extension requires. This is a preview feature.
     * <ul>
     * <li>Libraries must be GAV specifiers and are loaded from Maven Central.
     * </ul>
     * <p>
     * Example:<blockquote><pre>libraries:
     *     - com.squareup.okhttp3:okhttp:4.9.0</pre></blockquote>
     *
     * @return required libraries
     */
    @NotNull
    public List<String> getLibraries() {
        return libraries;
    }

    /**
     * @return unused
     * @deprecated unused
     */
    @Deprecated
    @Nullable
    public String getClassLoaderOf() {
        return classLoaderOf;
    }

    /**
     * Saves this ExtensionDescriptionFile to the given writer
     *
     * @param writer Writer to output this file to
     */
    public void save(@NotNull Writer writer) {
        YAML.get().dump(saveMap(), writer);
    }

    private void loadMap(@NotNull Map<?, ?> map) throws InvalidDescriptionException {
        try {
            name = rawName = map.get("name").toString();

            if (!VALID_NAME.matcher(name).matches()) {
                throw new InvalidDescriptionException("name '" + name + "' contains invalid characters.");
            }
            name = name.replace(' ', '_');
        } catch (NullPointerException ex) {
            throw new InvalidDescriptionException(ex, "name is not defined");
        } catch (ClassCastException ex) {
            throw new InvalidDescriptionException(ex, "name is of wrong type");
        }

        provides = makeExtensionNameList(map, "provides");

        try {
            version = map.get("version").toString();
        } catch (NullPointerException ex) {
            throw new InvalidDescriptionException(ex, "version is not defined");
        } catch (ClassCastException ex) {
            throw new InvalidDescriptionException(ex, "version is of wrong type");
        }

        try {
            main = map.get("main").toString();
            if (main.startsWith("org.bukkit.")) {
                throw new InvalidDescriptionException("main may not be within the org.bukkit namespace");
            }
        } catch (NullPointerException ex) {
            throw new InvalidDescriptionException(ex, "main is not defined");
        } catch (ClassCastException ex) {
            throw new InvalidDescriptionException(ex, "main is of wrong type");
        }

        try {
            pkg = map.get("package").toString();
            if (pkg.startsWith("org.bukkit.")) {
                throw new InvalidDescriptionException("package may not be within the org.bukkit namespace");
            }
        } catch (NullPointerException ex) {
            throw new InvalidDescriptionException(ex, "package is not defined");
        }

        if (map.get("class-loader-of") != null) {
            classLoaderOf = map.get("class-loader-of").toString();
        }

        depend = makeExtensionNameList(map, "depend");
        softDepend = makeExtensionNameList(map, "softdepend");
        loadBefore = makeExtensionNameList(map, "loadbefore");

        if (map.get("website") != null) {
            website = map.get("website").toString();
        }

        if (map.get("description") != null) {
            description = map.get("description").toString();
        }

        if (map.get("authors") != null) {
            ImmutableList.Builder<String> authorsBuilder = ImmutableList.builder();
            if (map.get("author") != null) {
                authorsBuilder.add(map.get("author").toString());
            }
            try {
                for (Object o : (Iterable<?>) map.get("authors")) {
                    authorsBuilder.add(o.toString());
                }
            } catch (ClassCastException ex) {
                throw new InvalidDescriptionException(ex, "authors are of wrong type");
            } catch (NullPointerException ex) {
                throw new InvalidDescriptionException(ex, "authors are improperly defined");
            }
            authors = authorsBuilder.build();
        } else if (map.get("author") != null) {
            authors = ImmutableList.of(map.get("author").toString());
        } else {
            authors = ImmutableList.of();
        }

        if (map.get("contributors") != null) {
            ImmutableList.Builder<String> contributorsBuilder = ImmutableList.builder();
            try {
                for (Object o : (Iterable<?>) map.get("contributors")) {
                    contributorsBuilder.add(o.toString());
                }
            } catch (ClassCastException ex) {
                throw new InvalidDescriptionException(ex, "contributors are of wrong type");
            }
            contributors = contributorsBuilder.build();
        } else {
            contributors = ImmutableList.of();
        }

        if (map.get("libraries") != null) {
            ImmutableList.Builder<String> contributorsBuilder = ImmutableList.builder();
            try {
                for (Object o : (Iterable<?>) map.get("libraries")) {
                    contributorsBuilder.add(o.toString());
                }
            } catch (ClassCastException ex) {
                throw new InvalidDescriptionException(ex, "libraries are of wrong type");
            }
            libraries = contributorsBuilder.build();
        } else {
            libraries = ImmutableList.of();
        }

        if (map.get("prefix") != null) {
            prefix = map.get("prefix").toString();
        }
    }

    @NotNull
    private static List<String> makeExtensionNameList(@NotNull final Map<?, ?> map, @NotNull final String key) throws InvalidDescriptionException {
        final Object value = map.get(key);
        if (value == null) {
            return ImmutableList.of();
        }

        final ImmutableList.Builder<String> builder = ImmutableList.builder();
        try {
            for (final Object entry : (Iterable<?>) value) {
                builder.add(entry.toString().replace(' ', '_'));
            }
        } catch (ClassCastException ex) {
            throw new InvalidDescriptionException(ex, key + " is of wrong type");
        } catch (NullPointerException ex) {
            throw new InvalidDescriptionException(ex, "invalid " + key + " format");
        }
        return builder.build();
    }

    @NotNull
    private Map<String, Object> saveMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("name", name);
        if (provides != null) {
            map.put("provides", provides);
        }
        map.put("main", main);
        map.put("version", version);

        if (depend != null) {
            map.put("depend", depend);
        }
        if (pkg != null) {
            map.put("package", pkg);
        }
        if (softDepend != null) {
            map.put("softdepend", softDepend);
        }
        if (website != null) {
            map.put("website", website);
        }
        if (description != null) {
            map.put("description", description);
        }

        if (authors.size() == 1) {
            map.put("author", authors.get(0));
        } else if (authors.size() > 1) {
            map.put("authors", authors);
        }

        if (contributors != null) {
            map.put("contributors", contributors);
        }

        if (libraries != null) {
            map.put("libraries", libraries);
        }

        if (classLoaderOf != null) {
            map.put("class-loader-of", classLoaderOf);
        }

        if (prefix != null) {
            map.put("prefix", prefix);
        }

        return map;
    }

    @NotNull
    private Map<?, ?> asMap(@NotNull Object object) throws InvalidDescriptionException {
        if (object instanceof Map) {
            return (Map<?, ?>) object;
        }
        throw new InvalidDescriptionException("Extension description file is empty or not properly structured. Is " + object + "but should be a map.");
    }

    /**
     * @return internal use
     */
    @NotNull
    public String getRawName() {
        return rawName;
    }
}