# Employee Database CRUD Application (Java JDBC)

This project is a single-file Java application demonstrating **C**reate, **R**ead, **U**pdate, and **D**elete (CRUD) operations on a MySQL database using **Java Database Connectivity (JDBC)** and `PreparedStatement`s to manage employee records.

The entire application—Model (`Employee`), Data Access Object (`EmployeeDAO`), and Main Application Logic (`EmployeeManager`)—is contained within the single `EmployeeManager.java` file.

-----

## ✨ Features

The application provides a console-based menu to perform the following operations:

1.  **Add New Employee (Create)**: Insert a new employee record into the database.
2.  **View All Employees (Read)**: Retrieve and display all employee records.
3.  **Update Employee**: Find an employee by ID and modify their name, department, or salary.
4.  **Delete Employee**: Remove an employee record by ID.
5.  **Exit**: Close the application.

-----

## 🛠️ Prerequisites

Before running this application, you must have the following set up:

  * **Java Development Kit (JDK)**: Version 8 or higher.
  * **MySQL Server**: A running instance of a MySQL database.
  * **MySQL Connector/J JAR**: The official JDBC driver for MySQL. This file must be added to your project's classpath.

-----

## ⚙️ Database Setup

You must create a database named `employeedb` and an `employees` table within it.

### 1\. Create Database

```sql
CREATE DATABASE employeedb;
USE employeedb;
```

### 2\. Create Employees Table

The application expects the following table structure:

```sql
CREATE TABLE employees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(50),
    salary DECIMAL(10, 2)
);
```

-----

## 🔑 Configuration

The database connection details are hardcoded in the `EmployeeDAO` class. **You must modify these to match your local MySQL configuration.**

Locate and change the following lines in the `EmployeeManager.java` file:

```java
// In class EmployeeDAO
private static final String DB_URL = "jdbc:mysql://localhost:3306/employeedb";
private static final String USER = "root";       // <-- Your MySQL username
private static final String PASS = "password";   // <-- Your MySQL password
```

-----

## ▶️ How to Run

Assuming the `EmployeeManager.java` file is saved and your prerequisites are met:

### 1\. Compile the Java File

You must include the MySQL Connector/J JAR in the classpath during compilation. Replace `path/to/mysql-connector-j.jar` with the actual path to your JDBC driver file.

```bash
javac -cp path/to/mysql-connector-j.jar EmployeeManager.java
```

### 2\. Execute the Application

Run the compiled class file, again ensuring the JDBC driver is on the classpath:

```bash
java -cp .:path/to/mysql-connector-j.jar EmployeeManager
# On Windows, you might use a semicolon (;) instead of a colon (:)
# java -cp .;path/to/mysql-connector-j.jar EmployeeManager
```

The application will launch with the main menu, allowing you to interact with your `employeedb` database.
