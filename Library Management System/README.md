# Library Management System

A desktop-based Library Management System developed using Java and MySQL to manage books, students, issue/return records, and library operations efficiently.

## Technologies Used

- Java
- MySQL
- JDBC
- NetBeans 

## Features

- login/ registration
- Add new books
- View/search books
- Update book information
- Student management
- Book issue and return system
- due/overdue Remainder

## Project Setup Instructions

### 1. Clone Repository

```bash
git clone https://github.com/Munniakterr2ait/Library-Management-system.git
cd Library-Management-system
```

### 2. Setup Database

- Install and open XAMPP
- Start **MySQL**
- Open phpMyAdmin:
  
```bash
http://localhost/phpmyadmin
```

- Create a new database:

```sql
CREATE DATABASE library;
```

### 3. Import Database File

- Open database `library`
- Click **Import**
- Upload the provided SQL file from the project folder  
  (example: `library.sql`)
- Click **Go**

### 4. Configure Database Connection

Open database connection file (example: `DBConnection.java`) and update:

```java
String url = "jdbc:mysql://localhost:3306/library_db";
String user = "root";
String password = "root123";
```

### 5. Add MySQL JDBC Driver

Add MySQL Connector JAR file to project libraries.

- NetBeans: Project → Properties → Libraries → Add JAR
- File → Project Structure → Libraries

### 6. Run Project

- Open project in NetBeans 
- Locate main file (`Main.java` / `Login.java`)
- Click **Run**

## Repository Link

https://github.com/Munniakterr2ait/Library-Management-system

## Author

Software Development II (Database) Lab Final Project
Northern University Bangladesh
