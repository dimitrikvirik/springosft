# Spring Boot Application: User and Order Services

This Spring Boot application consists of two main services: User Service and Order Service. This README provides instructions on how to run the application and execute tests on different operating systems.

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Running the Application](#running-the-application)
    - [On Windows](#on-windows)
    - [On Unix-based Systems (Linux/macOS)](#on-unix-based-systems-linuxmacos)


## Prerequisites

Ensure you have the following installed on your system:
- Java Development Kit (JDK) 21 or higher
- Docker
- Docker Compose

## Running the Application

### On Windows

1. Open a command prompt in the project root directory.
2. Run the following command:

```
run.bat
```

### On Unix-based Systems (Linux/macOS)

1. Open a terminal in the project root directory.
2. Make sure the script has execute permissions:

```
chmod +x run.sh
```

3. Run the following command:

```
./run.sh
```

The application should now start, and you should see log output in the console.

## Running Tests

To run the tests for both User and Order services:

1. Open a terminal or command prompt in the project root directory.
2. Execute the following Gradle command:

```
./gradlew test
```
