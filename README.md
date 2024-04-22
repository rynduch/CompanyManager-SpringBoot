## CompanyManager-SpringBoot

CompanyManager-SpringBoot is an application designed to facilitate work organization within a company. It allows users to add and delete employees, groups and ratings. Additionally, it enables users to modify employee statuses (such as sick leave, on delegation, present, or absent), as well as add and remove employees within a group.

## Requirements

- Java 17 or greater
- Maven

## MySQL Database Configuration

To configure the project with a MySQL database, you need to provide database URL address, username and password in the `hibernate.cfg.xml` file.

An example SQL code for creating the database is available in the `create-database.sql` file.

## Run

You can run the project by executing the following command:
```
./mvnw spring-boot:run
```
Make sure you are in the directory `CompanyManager` before executing the command.

The application should be listened to on port: http://localhost:8080/api.
