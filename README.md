# FS Doctor CLI tool

* written in java
* compiled to native executable using GraalVM
* use [picocli](https://picocli.info/) under the hood

## Build & run

### Standard maven

* build self-executable jar file
```bash
mvn clean package
```

* Run application
```bash
java -jar target/fs-doctor-0.0.1-SNAPSHOT.jar
```

### Native image

* build native image using maven `native` profile
```bash
mvn clean package -Pnative
```

* run native executable
```bash
./target/fs-doctor
```