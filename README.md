# Spring Boot Actuator Health Process Statistic Plugin

## Description
Dieses Projekt erweitert den Spring Boot Actuator Health Endpoint um einen Block für Verarbeitungsstatistiken.
Die Werte können zum Monitoring der Anwendung verwendet werden.

## Home
https://github.com/hjaegleiks/actuator-health-processstatistic

## How to Install and Run the Project

### Prerequisites
* [Java SDK](https://openjdk.org/) 1.8+
* [Apache Maven](https://maven.apache.org/) 3+
* [Spring Boot](https://spring.io/projects/spring-boot) 2.7+

### Install
Projekt aus GitHub Repository klonen:<br/>
https://github.com/hjaegleiks/actuator-health-processstatistic.git

Java Archiv Datei (JAR) erstellen mit Build Tool Maven und pom.xml:
```shell
mvn clean install
```
Erzeugt im Projekt-Unterverzeichnis target Datei actuator-health-processstatistic.jar.

### Usage
Erstelltes Java Archiv (JAR) in eigenes Spring Boot Projekt einbinden via pom.xml:
```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>com.iks-gmbh</groupId>
            <artifactId>actuator-health-processstatistic</artifactId>
            <version>1.6.0</version>
        </dependency>
```

Um auch die Reset-Funktion der Verarbeitungsstatistik nutzen zu können, sollte in der main-Class der Anwendung die
Spring Annotation @ComponentScan für das Package com.iksgmbh.actuator.health.procstat hinzugefügt werden.
```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.iksgmbh.actuator.health.procstat"})
public class SampleApp {
    public static void main(String[] args) {
        SpringApplication.run(SampleApp.class, args);
    }
}
```

Als Beispiel siehe GitHub Repository actuator-health-sample.<br/>
https://github.com/hjaegleiks/actuator-health-sample

## Author
H. Jägle<br/>
IKS GmbH<br/>
Hilden, Germany<br/>
http://www.iks-gmbh.com

## Contributing
Contributions, issues and feature requests are welcome.
Feel free to contact me.

## License
Copyright &copy; 2024 H. Jägle, IKS GmbH<br/>
This project is [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0) licensed.
