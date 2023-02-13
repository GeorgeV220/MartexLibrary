# MartexLibrary

# Adding MartexLibrary as a dependency to your build system

### Maven

You can have your project depend on MartexLibrary as a dependency through the following code snippets:

```xml

<project>
    <repositories>
        <repository>
            <id>reposilite-repository</id>
            <name>GeorgeV22 Repository</name>
            <url>https://repo.georgev22.com/{repository}</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>com.georgev22.library</groupId>
            <artifactId>{artifact}</artifactId>
            <version>9.3.0</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

### Gradle

You can include MartexLibrary into your gradle project using the following lines:

```groovy
repositories {
    maven {
        url "https://repo.georgev22.com/{repository}"
    }
}

dependencies {
    compileOnly "com.georgev22.library:{artifact}:9.3.0:all"
}
```

# Building MartexLibrary

### Gradle
MartexLibrary can be built by running the following: `gradle clean build shadowJar`. The resultant jar is built and written
to `{artifact}/build/libs/{artifact}-{version}.jar`.

The build directories can be cleaned instead using the `gradle clean` command.

If you want to clean (install) and build the plugin use `gradle clean build shadowJar` command.

# Contributing

MartexLibrary is an open source `GNU General Public License v3.0` licensed project. I accept contributions through pull
requests, and will make sure to credit you for your awesome contribution.
