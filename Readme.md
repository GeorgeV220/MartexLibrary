# MartexLibrary

# Adding MartexLibrary as a dependency to your build system

### Maven

You can have your project depend on MartexLibrary as a dependency through the following code snippets:

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
            <artifactId>MartexLibrary</artifactId>
            <version>/*latest tag*/</version>
        </dependency>
    </dependencies>
</project>
```

Is best to relocate MartexLibrary classes to your own package. Add this code snippet to your shade plugin configuration section:

```xml

<relocations>
    <relocation>
        <pattern>com.georgev22.library</pattern>
        <shadedPattern>your_package.library</shadedPattern>
    </relocation>
</relocations>
```

### Gradle

You can include MartexLibrary into your gradle project using the following lines:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.GeorgeV220:MartexLibrary:/*latest tag*/'
}
```

Is best to relocate MartexLibrary classes to your own package(shadowJar gradle plugin example):

```groovy

shadowJar {
    relocate 'com.georgev22.library', 'your_package.library'
}
```
