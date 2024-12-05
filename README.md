# FS Doctor CLI tool

* Written in `Java 23`
* Maven `v3.9.9` with the [wrapper](https://maven.apache.org/wrapper/)
* Compiled to native executable using [GraalVM](https://www.graalvm.org/)
* Uses [picocli](https://picocli.info/) under the hood

## Build & run

### Standard maven

* Build self-executable jar file
```bash
mvn clean package
```

* Run application
```bash
java -jar target/fs-doctor-0.0.1-SNAPSHOT.jar
```

### Native image

* Build native image using maven `native` profile

If you're using Windows make sure to have installed [Visual Studio 2022](https://visualstudio.microsoft.com/downloads/).
It's required to compile native images.

```bash
mvn clean package -Pnative
```

* Run native executable
```
./target/fs-doctor <-- for Unix
./target/fs-doctor.exe <-- for Windows
```