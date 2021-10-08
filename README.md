# API

# Adding API as a dependency to your build system

### Maven

You can have your project depend on API as a dependency through the following code snippets:

```xml

<project>
    <dependencies>
        <dependency>
            <groupId>com.georgev22</groupId>
            <artifactId>api</artifactId>
            <version>1.8</version>
            <scope>compile</scope>
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
    maven {
        url 'https://artifactory.georgev22.com/artifactory/georgev22/'
    }
}

dependencies {
    compileOnly "com.georgev22:api:1.8"
}
```
