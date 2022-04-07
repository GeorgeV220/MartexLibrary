# API

# Adding API as a dependency to your build system

### Maven

You can have your project depend on API as a dependency through the following code snippets:

```xml

<project>
    <repositories>
        <repository>
            <id>Jitpack</id>
            <url>https://jitpack.io/</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>com.github.GeorgeV220</groupId>
            <artifactId>API</artifactId>
            <version>v3.0.0</version>
        </dependency>
    </dependencies>
</project>
```

Is best to relocate api classes to your own package. Add this code snippet to your shade plugin configuration section:

```xml

<relocations>
    <relocation>
        <pattern>com.georgev22.api</pattern>
        <shadedPattern>your_package.api</shadedPattern>
    </relocation>
</relocations>
```

### Gradle

You can include API into your gradle project using the following lines:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.GeorgeV220:API:v3.0.0'
}
```
