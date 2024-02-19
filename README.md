# Spring Boot Actuator Health Process Statistic Plugin

## Description
Dieses Projekt erweitert den Spring Boot Actuator Health Endpoint um einen Block für Verarbeitungsstatistiken.
Die Werte können zum Monitoring der Anwendung verwendet werden.

## How to Install and Run the Project

### Prerequisites
* [Java SDK](https://openjdk.org/) 1.8+
* [Apache Maven](https://maven.apache.org/) 3+
* [Spring Boot](https://spring.io/projects/spring-boot) 2.7+

### Install
Projekt aus GitHub Repository in ein neues Projekt-Verzeichnis klonen:  
https://github.com/iks-gmbh-projects/actuator-health-processstatistic.git

Dort Java Archiv Datei (JAR) erstellen mit Build Tool Maven und `pom.xml`:
```shell
mvn clean install
```
Erzeugt im Projekt-Unterverzeichnis `target` Datei `actuator-health-processstatistic.jar`.

### Usage
Um Spring Actuator und Health Process Statistic in eine Anwendung einzubinden, folgende Dependencies in die
eigene `pom.xml` aufnehmen:
```xml
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>com.iks-gmbh</groupId>
            <artifactId>actuator-health-processstatistic</artifactId>
            <version>1.6.0</version>
        </dependency>
    </dependencies>
```

Um auch die Reset-Funktion der Verarbeitungsstatistik nutzen zu können, sollte in der main-Class der Anwendung die
Spring Annotation `@ComponentScan` für das Package `com.iksgmbh.actuator.health.procstat` hinzugefügt werden.
```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.iksgmbh.actuator.health.procstat"})
public class SampleApp {
    public static void main(String[] args) {
        SpringApplication.run(SampleApp.class, args);
    }
}
```

Als Beispiel siehe GitHub Repository `actuator-health-sample`.  
https://github.com/iks-gmbh-projects/actuator-health-sample

Mehr Details erklärt das Dokument [/docs/manual.md](./docs/manual.md) .

## Author
H. Jägle  
IKS GmbH  
Hilden, Germany  
http://www.iks-gmbh.com

## Contributing
Contributions, issues and feature requests are welcome.
Feel free to contact me.

## License
Copyright &copy; 2024 H. Jägle, IKS GmbH  
This project is [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0) licensed.
