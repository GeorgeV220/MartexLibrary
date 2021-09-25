# API

# Adding API as a dependency to your build system

### Maven

You can have your project depend on API as a dependency through the
following code snippets:

```xml
<project>
    <repositories>
        <repository>
            <id>georgev22</id>
            <url>https://artifactory.georgev22.com/artifactory/georgev22/</url>
        </repository>
    </repositories>
  
    <dependencies>
        <dependency>
            <groupId>com.georgev22</groupId>
            <artifactId>api</artifactId>
            <version>1.7</version>
            <scope>compile</scope>
        </dependency>
    </dependencies>
</project>
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
    compileOnly "com.georgev22:api:1.7"
}
```
