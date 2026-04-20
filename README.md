# Library Management System

This is a CLI-based Library Management System I built for my JD521 module at university.

It handles the usual stuff - managing members, checking out and returning books, tracking overdue fines, searching the catalogue, and sending background notifications using Java's ScheduledExecutorService.

## Tech

- Java 17
- Maven

## Build and Run

Make sure you have Java 17 and Maven installed, then:

```bash
cd chanemarais\ JD521\ FA3/LibraryManagementSystem
mvn clean package
java -jar target/LibraryManagementSystem-1.0-SNAPSHOT.jar
```

Or run it directly with Maven:

```bash
mvn compile exec:java
```

## Notes

This was a formative assessment (FA3) for JD521. It's not meant to be production-ready - just a solid demo of OOP principles, scheduling, and file-based persistence in Java.
