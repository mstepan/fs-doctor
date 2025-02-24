# FS Doctor CLI tool

* Written in `Java 23`
* Maven `v3.9.9` with the [wrapper](https://maven.apache.org/wrapper/)
* Compiled to native executable using [GraalVM](https://www.graalvm.org/)
* Uses [picocli](https://picocli.info/) under the hood
* Uses [virtual threads](https://docs.oracle.com/en/java/javase/23/core/virtual-threads.html) and [structured concurrency](https://docs.oracle.com/en/java/javase/23/core/structured-concurrency.html)

## Build & run

### Standard maven

* Build self-executable jar file
```bash
./mvnw clean package
```

* Run application (sequential/parallel)
Pay attention that we also need to provide `--enable-preview` during runtime because we have used 
[Structured Concurrency](https://docs.oracle.com/en/java/javase/23/core/structured-concurrency.html) which is in 
a preview mode for java 23.
```bash
java --enable-preview -jar target/fs-doctor-0.0.1-SNAPSHOT.jar . --dup
java --enable-preview -jar target/fs-doctor-0.0.1-SNAPSHOT.jar . --dup -p
```

### Native image

* Build native image using maven `native` profile

If you're using Windows make sure to have installed [Visual Studio 2022](https://visualstudio.microsoft.com/downloads/).
It's required to compile native images.

```bash
./mvnw clean package -Pnative
```

* Run native executable (Unix/Windows)
```bash
./target/fs-doctor . --dup -p
./target/fs-doctor.exe . --dup -p
```

## Quality checks

### OWASP check dependencies for vulnerabilities
* Run OWASP dependency checker. The OWASP checker attached to `check` maven phase that can be triggered using below
  command:

```bash
./mvnw verify
```

./mvnw org.owasp:dependency-check-maven:purge