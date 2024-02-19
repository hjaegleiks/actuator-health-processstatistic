# Spring Boot Actuator Health Process Statistic Plugin

## Beschreibung
Dieses Projekt erweitert den Spring Boot Actuator Health Endpoint um einen Block für Verarbeitungsstatistiken.
Die Werte können zum Monitoring der Anwendung verwendet werden.

## Was ist Spring Boot?
[Spring Boot](https://spring.io/projects/spring-framework) ist ein Open-Source Framework zur Erstellung von lauffähigen
Anwendungen in Java. Es unterstützt Entwickler bei der Erstellung von Webanwendungen oder REST-Webservices durch Module
für Logging, Auditing, Security, Validierung, Datenbankanbindung (JDBC, JPA), Messaging (Kafka, RabbitMQ), Batchverarbeitung
und vieles andere mehr. Diese werden leicht durch Programmcode, Annotations, Konfigurationsdateien oder externen
Umgebungsvariablen an die gewünschten Gegebenheiten angepasst.

## Was ist Actuator?
Das Spring Feature [Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html) hilft bei
der Überwachung und Monitoring der eigenen, laufenden Spring Boot Anwendung, Java VM und Systemumgebung. Es zeigt
Informationen wie
* installierte Version der Anwendung,
* aktuelle Konfiguration,
* CPU-Auslastung,
* Speicherauslastung,
* Plattenplatz,
* Status Netzwerkadapter,
* verwendete Datenbankverbindungen und Auslastung Connectionpool,
* aktive Webservicesitzungen sowie
* aktueller Health Status der Anwendung.

Diese können über zusätzliche REST-Schnittstellen abgerufen und in Überwachungstools wie [checkmk](https://checkmk.com/),
[Nagios](https://www.nagios.org/) oder anderen, selbstgebastelten Dashboards eingebunden werden.
Hat die überwachte Anwendung Probleme und läuft nicht mehr, gehen bei den Administratoren / IT-Operating "rote Lampen"
an 🚨 und sie können (hoffentlich) zeitnah darauf reagieren. 

## Was macht Health Process Statistic Plugin?
Dieses Plugin kann das Spring Boot Feature `Actuator` um weitere Metriken zur Verarbeitungsstatistik erweitern:
* Liste ausgewählter Fehlermeldungen (Anzahl konfigurierbar),
* Anzahl erfolgreicher Requests,
* Zeitpunkt letzter erfolgreicher Request,
* Anzahl fehlgeschlagener Requests,
* Zeitpunkt letzter fehlgeschlagener Request,
* Zeitpunkt wann Anwendung zuletzt gestartet wurde.

Die Daten werden In-Memory gehalten und NICHT in einer Datenbank persistiert. Sie können zu einem beliebigen Zeitpunkt
zurückgesetzt werden, ohne die Anwendung neu starten zu müssen.

## Wie funktionert das?

### Einbinden in eigene Anwendung
Ziel war es, dieses Plugin so leicht und schnell wie möglich out-of-the-box in bestehende Spring Boot Anwendungen
einbinden zu können. Voraussetzungen dafür sind:
* [Java SDK](https://openjdk.org/) 1.8+
* [Apache Maven](https://maven.apache.org/) 3+
* [Spring Boot](https://spring.io/projects/spring-boot) 2.7+
* Dieses Plugin actuator-health-processstatisic.jar

Dazu dieses Projekt aus GitHub Repository in ein neues Projekt-Verzeichnis klonen:  
https://github.com/iks-gmbh-projects/actuator-health-processstatistic.git

Dort Java Archiv Datei (JAR) erstellen mit Build Tool Maven und `pom.xml`:
```shell
mvn clean install
```
Erzeugt im Projekt-Unterverzeichnis `target` Datei `actuator-health-processstatistic.jar`.

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

### Verwendung in eigener Anwendung
Nun stehen dem Entwickler über die Bean `HealthProcessStatisticData` im `@ApplicationScope` neue Funktionen zur Verfügung.
Die Namen der `statisticDataKey` können beliebig vergeben werden, eine Verwendung von Java `Enum` bietet sich hier an.
```java
    healthProcessStatisticData.incrementCounter("myFunctionRequest");
```
Erhöht den Zähler des `statisticDataKey` "myFunctionRequest" und setzt dessen Zeitstempel auf die aktuelle Systemzeit.

```java
    healthProcessStatisticData.addError("myFunction", 
            "ERR-001", 
            "Funktion ist fehlgeschlagen weil xyz.",
            "Bitte Problem xyz beseitigen und noch mal versuchen.", 
            entity.getId());
```
Fügt einen Eintrag in die Fehlerliste hinzu. Der Zähler `errorCounter` wird erhöht und Zeitstempel `errorTimestamp`
auf die aktuelle Systemzeit gesetzt.

Beispiel Anwendung:
```java
public class MyService {
    @Autowired
    private final HealthProcessStatisticData healthProcessStatisticData;

    public void myFunction(MyEntity myEntity) {
        healthProcessStatisticData.incrementCounter("myFunctionRequest");
        
        try {
            // do anything here
            doAnything(myEntity);
            
            // everything ok
            healthProcessStatisticData.incrementCounter("myFunctionSuccess");
            
        } catch (RuntimeException e) {
            // something went wrong :(
            healthProcessStatisticData.addError("myFunction", 
                    "ERR-001", 
                    "Ausführung myFunction ist fehlgeschlagen weil xyz.",
                    "Bitte Problem xyz beseitigen und noch mal versuchen.", 
                    myEntity.getId());            
        
            healthProcessStatisticData.incrementCounter("myFunctionFailed");
        }
    }
}
```

### Konfiguration in eigener Anwendung
Zusätzlich bekommt die eigene Anwendung neue REST-Endpoints. Konfiguriert werden diese beispielsweise in der Datei 
`application.properties`:
```properties
spring.application.name=myapp

server.servlet.context-path=/${spring.application.name}
server.port=8080

management.server.base-path=/${spring.application.name}
management.server.port=8080
management.endpoints.web.base-path=/actuator
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=when_authorized
management.health.processStatistic.enabled=true
management.health.processStatistic.errorlist.maxsize=5
```

Die URLs der HTTP GET Requests lauten dann:  
http://localhost:8080/myapp/actuator/  
http://localhost:8080/myapp/actuator/health  

```json
{
    "status": "UP",
    "components": {
        "diskSpace": {
            "status": "UP",
            "details": {
                "total": 510716276736,
                "free": 363641962496,
                "threshold": 10485760,
                "exists": true
            }
        },
        "ping": {
            "status": "UP"
        },
        "processStatistic": {
            "status": "WARNING",
            "details": {
                "serviceStartTimestamp": "2024-02-01T12:01:00.000+01:00",
                "errorList": [{
                        "timestamp": "2024-02-01T12:05:01.000+01:00",
                        "function": "myFunction",
                        "returnCode": "ERR-001",
                        "messageText": "Ausführung myFunction ist fehlgeschlagen weil xyz.",
                        "instructionText": "Bitte Problem xyz beseitigen und noch mal versuchen.",
                        "referenceId": "MyEntity{id=1}"
                    }
                ],
                "errorCounter": 1,
                "errorTimestamp": "2024-02-01T12:05:01.000+01:00",
                "myFunctionRequestCounter": 2,
                "myFunctionRequestTimestamp": "2024-02-01T12:06:00.000+01:00",
                "myFunctionSuccessCounter": 1,
                "myFunctionSuccessTimestamp": "2024-02-01T12:06:01.000+01:00",
                "myFunctionFailedCounter": 1,
                "myFunctionFailedTimestamp": "2024-02-01T12:05:02.000+01:00"
            }
        }
    }
}
```

## Was kann überwacht werden?
Ausgewertet werden kann die JSON-Datenstruktur des Health REST-Endpoints. Am einfachsten kann der JSON-Pfad des ersten
Health Status geprüft werden. Alle untergeordneten Komponenten können diesen beeinflussen.

* `UP` = Anwendung ist "oben" und einsatzbereit.
* `DOWN` = Anwendung ist "unten" und nicht einsatzbereit.
* `OUT_OF_SERVICE` = Anwendung ist oben, kann aber nicht reagieren.
* `UNKNOWN` = Zustand der Anwendung ist unbekannt.

Ist der Health REST-Endpoint nicht erreichbar oder liefert keine Antwort, kann der Health Status der Anwendung als `DOWN`
gewertet werden.

1. Ist die Anwendung aktiv und einsatzbereit?
    ```jsonpath
    $.status
    ```
2. Hat der Server, auf der die Anwendung läuft, noch genügend Plattenkapazität?
    Oder legen Transferdateien, Logdateien den Server lahm?
    ```jsonpath
    $.components.diskSpace.status
    ```
3. Ist die angebundene Datenbank verfügbar?
    ```jsonpath
    $.components.db.status
    ```
4. Ist der Netzwerkadapter verfügbar?
    ```jsonpath
    $.components.ping.status
    ```
5. War die Verarbeitung der Anwendungsfunktionen erfolgreich?
    ```jsonpath
    $.components.processStatistic.status
    ```
6. Wann fand die letzte Ausführung statt?
    ```jsonpath
    $.components.processStatistic.details
    ```
und weitere.

## Wie kann die Verarbeitungsstatistik den Health Status beeinflussen?
Durch eine eigene Implementierung der Methode `HealthProcessStatisticStatusDecider.checkHealth()` kann der Health Status
`$.components.processStatistic.status` und des übergeordneten Status nach eigenen Wünschen gesetzt werden. Durch die
Spring Annotationen `@Component` und `@Primary` wird die Vorlage des Plugins `actuator-health-processstatistic` ersetzt. 
Geprüft werden kann beispielsweise, ob
* der Zeitstempel des letzten Fehlers NACH dem Zeitstempel der letzten erfolgreichen Verarbeitung einer Funktion liegt.
* der Fehlerzähler einer Funktion größer 0 ist.
* irgendwas anderes zutrifft.

```java
@Component
@Primary
public class MyHealthProcessStatisticStatusDecider extends HealthProcessStatisticStatusDecider {

    @Override
    public Health checkHealth() {

        // DOWN - Errors occurred
        if (isErrorTimestampGreaterSuccessTimestamp("myFunctionFailed", "myFunctionSuccess")) {
            return Health.down()
                    .withDetails(healthProcessStatisticData.getHealthProcessStatisticDataMap())
                    .build();
        }

        // WARNING - Errors occurred in the past (error list)
        if (isCounterGreaterZero("error")) {
            return Health.status(HEALTH_STATUS_CODE_WARNING)
                    .withDetails(healthProcessStatisticData.getHealthProcessStatisticDataMap())
                    .build();
        }

        // UP - Everything ok
        return Health.up()
                .withDetails(healthProcessStatisticData.getHealthProcessStatisticDataMap())
                .build();
    }
}
```

## Health Status zurücksetzen
Sind die Fehler der Anwendung behoben worden, kann die Verarbeitungsstatistik und der Health Status zurückgesetzt werden.
Dazu diese URL mit HTTP GET aufrufen:
http://localhost:8080/myapp/actuator/healthProcessStatistic/reset?reset=true

Ein Neustart der Anwendung setzt ebenso die Verarbeitungsstatistik und den Health Status zurück.

## Fertig

Wünsche viel Erfolg beim Ausprobieren!

- - -
Version: 1.6.0 - 2024-02-19
