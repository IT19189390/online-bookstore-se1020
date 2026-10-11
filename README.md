# Online Bookstore Management System

SE1020 – Object Oriented Programming project (Spring Boot + MySQL, layer-based architecture).

## Requirements
- JDK 26
- MySQL Server 8 (running on port 3306)
- IntelliJ IDEA (free version is fine) or VS Code with Extension Pack for Java

## Setup
1. Install and start MySQL. Note your `root` password.
2. Open `src/main/resources/application.properties` and set your MySQL password:
   ```properties
   spring.datasource.password=your_password
   ```
3. Open the project folder and wait for Maven to load.
4. Set the project SDK to **JDK 26**.
5. Create an **Application** run configuration:
   - **Main class:** `com.bookstore.BookstoreApplication`
   - **Working directory:** the project folder
6. Click ▶ **Run**, then open **http://localhost:8080**.

The `bookstore` database, the tables and the sample data are created automatically on startup.
No Tomcat install is needed. Spring Boot starts its own.

## Config
In `src/main/resources/application.properties`:
- `server.port`: change this if port 8080 is busy.
- `spring.datasource.url`, `username`, `password`: your MySQL connection.

Database files in `src/main/resources`:
- `schema.sql`: creates the tables (`CREATE TABLE IF NOT EXISTS`).
- `data.sql`: inserts the sample rows (`INSERT IGNORE`, so restarts don't duplicate them).

## Architecture
```
Browser
  ↓
controller   → handles HTTP requests, picks the page (Presentation layer)
  ↓
service      → business rules (Business layer)
  ↓
repository   → SQL queries with JdbcTemplate (Data access layer)
  ↓
MySQL
```

```
com.bookstore
├── BookstoreApplication.java
├── controller   DashboardController, InventoryController, NavigationAdvice
├── service      InventoryService (interface)
│   └── impl     InventoryServiceImpl
├── repository   InventoryRepository
├── model        InventoryItem, PrintedBookStock, EBookStock,
│                StockStatus, InventorySummary, DashboardModule
├── dto          InventoryForm
├── exception    InventoryNotFoundException, DuplicateStockException
└── util         IsbnUtils
```

## Troubleshooting
| Problem | Fix |
|---------|-----|
| `Access denied for user 'root'` | Wrong password in `application.properties`. |
| `Communications link failure` | MySQL isn't running. Start it from Services (`services.msc` → MySQL80). |
| `Port 8080 was already in use` | Stop the other app, or change `server.port`. |
