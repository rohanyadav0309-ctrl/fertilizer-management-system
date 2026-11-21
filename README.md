# 🌱 Fertilizer Management System
> **Second-Year Engineering College Java Laboratory Project**  
> Built with **Core Java**, **Java Swing (GUI)**, **JDBC**, and **MySQL Database**.

---

## 📋 Table of Contents
1. [Project Overview](#-project-overview)
2. [Key Features & Screens](#-key-features--screens)
3. [Technology Stack](#-technology-stack)
4. [Project File Structure](#-project-file-structure)
5. [Database Setup (MySQL)](#-database-setup-mysql)
6. [How to Run the Project](#-how-to-run-the-project)
   - [Option A: Running in NetBeans IDE (Most Common)](#option-a-running-in-netbeans-ide)
   - [Option B: Running in Eclipse IDE](#option-b-running-in-eclipse-ide)
   - [Option C: Running in IntelliJ IDEA](#option-c-running-in-intellij-idea)
   - [Option D: Running via Windows Command Prompt](#option-d-running-via-windows-command-prompt)
7. [Simple Project Explanation & Flow](#-simple-project-explanation--flow)
8. [Core Java Concepts Used](#-core-java-concepts-used)
9. [15 Basic Viva Questions and Answers](#-15-basic-viva-questions-and-answers)

---

## 🌾 Project Overview

The **Fertilizer Management System** is a desktop application designed for fertilizer retailers, agricultural cooperatives, and farm stores to manage fertilizer inventory, record customer details, and handle sales billing.

- **Theme**: Clean Green and White color scheme inspired by agriculture.
- **Audience**: Second-year Computer Science / IT / Engineering students.
- **Design Philosophy**: Beginner-friendly, straightforward code, no complex frameworks (no Spring, Hibernate, or JavaFX), pure Core Java and Swing.

---

## ✨ Key Features & Screens

1. **🔐 Screen 1: Admin Login Screen (`Login.java`)**
   - Secure username & password input fields.
   - Validates credentials against the `admin` table in MySQL.
   - Built-in offline fallback mode (`admin` / `admin123`) for smooth lab evaluations.

2. **📊 Screen 2: Dashboard / Home Screen (`Dashboard.java`)**
   - Live statistic cards:
     - Total Fertilizer Varieties
     - Total Registered Customers
     - Available Total Stock (in bags)
     - Low-Stock Alerts Count (< 10 bags)
   - Prominent red/pink warning banner when any stock drops below 10 bags.
   - Quick navigation tiles to all modules.
   - Real-time refresh button and Logout.

3. **📦 Screen 3: Fertilizer Management Screen (`Fertilizer.java`)**
   - Add new fertilizer records (Name, Type, Price, Quantity).
   - View all fertilizers in an interactive `JTable`.
   - Click any table row to automatically populate the form fields.
   - Update existing fertilizer details.
   - Delete fertilizer with confirmation dialog.
   - Live Search by Fertilizer Name or ID.

4. **👥 Screen 4: Customer Management Screen (`Customer.java`)**
   - Register new customers / farmers (Name, 10-digit Mobile Number, Address/Village).
   - View customer list in `JTable`.
   - Update and Delete customer records.
   - Search customers by Name or Mobile Number.

5. **🛒 Screen 5: Sales & Billing Screen (`Sales.java`)**
   - Dropdown selection of fertilizers with automatic display of unit price and available stock.
   - Customer selection or manual entry.
   - Automatic calculation of **Total Price = Unit Price × Quantity** as you type.
   - Strict inventory check: prevents selling more than available in stock.
   - Automatic deduction of stock from the `fertilizer` table upon sale.
   - Pop-up printable sales receipt with full transaction summary.
   - History table of all sales.

6. **📈 Screen 6: Stock & Inventory Screen (`Stock.java`)**
   - Real-time inventory table with status indicators (`In Stock`, `Low Stock (<10)`, `Out of Stock`).
   - Warning banner alerting storekeepers about low supplies.
   - Quick Restock feature: Add incoming bags with one click.
   - Filter buttons: *All Items*, *Low Stock Only*, *Out of Stock Only*.

---

## 🛠 Technology Stack

- **Programming Language**: Java (JDK 8, 11, 17, or 21)
- **GUI Framework**: Java Swing & AWT (`JFrame`, `JPanel`, `JButton`, `JTable`, etc.)
- **Database**: MySQL (version 5.7 or 8.x) / MariaDB
- **Connectivity**: JDBC (Java Database Connectivity) with `mysql-connector-j-8.x.jar`
- **Supported IDEs**: NetBeans, Eclipse, IntelliJ IDEA, or CLI

---

## 📂 Project File Structure

```text
FertilizerManagement/
│
├── Main.java              # Application entry point; sets Look & Feel and launches Login
├── Login.java             # Screen 1: Admin Login & Authentication
├── Dashboard.java         # Screen 2: Home Page with live stats & navigation tiles
├── Fertilizer.java        # Screen 3: Fertilizer CRUD & Search module
├── Customer.java          # Screen 4: Customer/Farmer CRUD & Search module
├── Sales.java             # Screen 5: Sales billing, stock deduction & receipts
├── Stock.java             # Screen 6: Stock monitoring, low-stock warnings & restock
├── DBConnection.java      # Centralized JDBC connection manager
├── database.sql           # MySQL database creation script with sample records
├── compile_and_run.bat    # Windows batch script to build and run easily
└── README.md              # Complete documentation and viva guide
```

---

## 🗄 Database Setup (MySQL)

### Using phpMyAdmin (XAMPP / WAMP)
1. Open XAMPP Control Panel and click **Start** next to **Apache** and **MySQL**.
2. Open your browser and go to `http://localhost/phpmyadmin`.
3. Click on the **Import** tab at the top.
4. Click **Choose File** and select `database.sql` from the `FertilizerManagement` folder.
5. Scroll down and click **Import** (or **Go**).
6. The database `fertilizer_management` and tables (`admin`, `fertilizer`, `customer`, `sales`) are now created with sample data!

### Using MySQL Command Line
```sql
mysql -u root -p
```
*(Press Enter if there is no password)*
```sql
SOURCE C:/Users/LENVO/FertilizerManagement/database.sql;
```

---

## 🚀 How to Run the Project

### Option A: Running in NetBeans IDE
1. Open **NetBeans IDE**.
2. Go to **File -> New Project -> Java with Ant -> Java Application**.
3. Set Project Name to `FertilizerManagement`.
4. Copy all `.java` files from `FertilizerManagement/` into the `src/` folder of your NetBeans project.
5. **Add MySQL Driver**:
   - Right-click on **Libraries** in the Projects pane.
   - Select **Add JAR/Folder**.
   - Browse to your downloaded `mysql-connector-j-8.x.jar` (or select MySQL JDBC Driver from NetBeans bundled libraries).
6. Right-click `Main.java` and select **Run File** (or press `Shift + F6`).

### Option B: Running in Eclipse IDE
1. Open **Eclipse**.
2. Go to **File -> New -> Java Project**, name it `FertilizerManagement`.
3. Copy the `.java` files into the `src` folder.
4. Right-click the project -> **Build Path -> Configure Build Path...**
5. Go to the **Libraries** tab -> click **Add External JARs...**
6. Select `mysql-connector-j-8.x.jar` and click **Apply and Close**.
7. Right-click `Main.java` -> **Run As -> Java Application**.

### Option C: Running in IntelliJ IDEA
1. Open **IntelliJ IDEA**.
2. Click **Open** and select the `FertilizerManagement` folder.
3. Go to **File -> Project Structure -> Libraries**.
4. Click the `+` icon -> **Java** -> select `mysql-connector-j-8.x.jar`.
5. Open `Main.java` and click the green **Run** arrow next to `public class Main`.

### Option D: Running via Windows Command Prompt
1. Place `mysql-connector-j-8.x.jar` inside the `FertilizerManagement` folder.
2. Open Command Prompt in the `FertilizerManagement` directory.
3. Run:
   ```cmd
   javac -cp ".;mysql-connector-j-8.x.jar" *.java
   java -cp ".;mysql-connector-j-8.x.jar" Main
   ```
*(Or simply double-click `compile_and_run.bat`)*

---

## 💡 Simple Project Explanation & Flow

1. **User Login**: The application starts at `Main.java`, which opens `Login.java`. When the user enters their username and password, `DBConnection.getConnection()` provides a connection to MySQL. A `PreparedStatement` queries the `admin` table. If valid, the `Dashboard` is opened.
2. **Dashboard Overview**: `Dashboard.java` executes SQL aggregate queries (`COUNT(*)`, `SUM(quantity)`) to show real-time metrics and alerts if any items have low stock (< 10 bags).
3. **Fertilizer & Customer CRUD**: Both `Fertilizer.java` and `Customer.java` allow the user to execute `INSERT`, `SELECT`, `UPDATE`, and `DELETE` queries. When a table row is clicked, a `MouseAdapter` extracts cell values and fills the form fields.
4. **Sales Transaction & Stock Reduction**: In `Sales.java`, selecting a fertilizer automatically populates its unit price and stock. Typing quantity triggers `DocumentListener` to multiply Price × Quantity. When **Complete Sale** is clicked:
   - A check verifies that `quantity <= availableStock`.
   - A SQL transaction records the sale in the `sales` table.
   - An `UPDATE fertilizer SET quantity = quantity - ?` reduces the stock.
   - A formatted receipt dialog is shown.

---

## 🧠 Core Java Concepts Used

| Concept | Where & How It Is Used |
| :--- | :--- |
| **Classes & Objects** | Every screen (`Login`, `Dashboard`, `Fertilizer`, `Customer`, `Sales`, `Stock`) is a class instantiated as an object. |
| **Constructors** | Parameterized and default constructors initialize window settings, layout managers, and GUI components. |
| **Encapsulation** | Private variables (`txtUsername`, `tblFertilizer`) with public/package access methods keep data safe. |
| **Inheritance** | GUI classes extend `JFrame` to inherit desktop window capabilities (`setSize`, `setVisible`). |
| **Event Handling** | Implements `ActionListener` and `MouseListener` to handle button clicks and table selections. |
| **Exception Handling** | `try-catch-finally` blocks manage `SQLException`, `ClassNotFoundException`, and `NumberFormatException`. |
| **JDBC Architecture** | Uses `DriverManager`, `Connection`, `PreparedStatement`, `Statement`, and `ResultSet` to interact with MySQL. |
| **Data Types & Variables** | `int` for quantities and IDs, `double` for prices, `String` for names and addresses, `boolean` for checks. |

---

## 🎯 15 Basic Viva Questions and Answers

### Q1: What is JDBC and what are its main components?
**Answer:** JDBC stands for **Java Database Connectivity**. It is an API in Java that enables Java applications to connect and execute queries with relational databases. Its key interfaces/classes are:
1. `DriverManager`: Manages database drivers.
2. `Connection`: Represents the session between Java and the database.
3. `Statement` / `PreparedStatement`: Used to execute SQL queries.
4. `ResultSet`: Stores the records returned from an SQL query.

### Q2: What is the difference between `Statement` and `PreparedStatement`?
**Answer:** 
- `Statement` is used for executing static SQL queries without parameters. It compiles queries each time it is executed.
- `PreparedStatement` is pre-compiled by the database. It allows parameterized queries using `?` placeholders, is faster for repeated executions, and **prevents SQL Injection attacks**.

### Q3: Why is `SwingUtilities.invokeLater()` used in `Main.java`?
**Answer:** Swing components are not thread-safe. `SwingUtilities.invokeLater()` ensures that the GUI creation and updates are executed on the **Event Dispatch Thread (EDT)**, preventing race conditions and UI freezes.

### Q4: How does the system handle database connectivity?
**Answer:** We created a centralized class `DBConnection.java`. It defines the database URL (`jdbc:mysql://localhost:3306/fertilizer_management`), username (`root`), and password (`""`). The `getConnection()` method loads the MySQL driver (`com.mysql.cj.jdbc.Driver`) and returns an active `Connection` object using `DriverManager.getConnection()`.

### Q5: What is `ResultSet` and how do you retrieve data from it?
**Answer:** `ResultSet` is an object that maintains a cursor pointing to the current row of data returned by a query. We use `rs.next()` in a `while` loop to advance the cursor, and getter methods like `rs.getInt("column_name")`, `rs.getString("column_name")`, and `rs.getDouble("column_name")` to extract data.

### Q6: How does the Sales module automatically update the inventory stock?
**Answer:** When a sale is confirmed in `Sales.java`, we execute two SQL commands:
1. `INSERT INTO sales (...) VALUES (...)` to record the sale transaction.
2. `UPDATE fertilizer SET quantity = quantity - ? WHERE fertilizer_id = ?` to subtract the sold bags from the stock. We also turn off auto-commit (`conn.setAutoCommit(false)`) to ensure both succeed together as a transaction.

### Q7: What is the purpose of `DefaultTableModel` in Swing?
**Answer:** `DefaultTableModel` is the data model behind a `JTable`. It holds column names and rows. We manipulate table contents dynamically using methods like `addRow(Object[])`, `setRowCount(0)` (to clear), and `getValueAt(row, col)`.

### Q8: How is event handling implemented for buttons in this project?
**Answer:** We implement the `ActionListener` interface and override the `actionPerformed(ActionEvent e)` method. Inside this method, we check `e.getSource() == btnAdd` to identify which button was clicked and call the appropriate handler method.

### Q9: How do you populate form fields when a user clicks on a table row?
**Answer:** We attach a `MouseAdapter` to the `JTable` listening for `mouseClicked`. When triggered, `tblFertilizer.getSelectedRow()` gives the selected index, and `tableModel.getValueAt(row, col)` retrieves the data to put into the `JTextField` components.

### Q10: What validations are performed on user input?
**Answer:**
1. Empty checks: ensures no mandatory field is left blank.
2. Number format checks: `Double.parseDouble()` and `Integer.parseInt()` inside `try-catch` ensure price and quantity are valid numbers.
3. Positive values: price must be `> 0`, quantity must be `>= 0`.
4. Mobile number check: validated with regex `^[0-9]{10}$` to ensure exactly 10 digits.
5. Stock sufficiency check: prevents selling more quantity than available in inventory.

### Q11: What is the difference between `JFrame` and `JPanel`?
**Answer:**
- `JFrame` is a top-level container with title bar, minimize, maximize, and close buttons.
- `JPanel` is a lightweight container used inside a `JFrame` to group, organize, and layout components.

### Q12: Why do we use `JPasswordField` instead of `JTextField` for passwords?
**Answer:** `JPasswordField` masks entered characters with dots/asterisks for privacy and security. It provides `getPassword()` which returns a `char[]` array rather than a plain `String`, allowing sensitive data to be erased from memory after use.

### Q13: What is the role of `FOREIGN KEY` in the `sales` table?
**Answer:** In `sales`, `fertilizer_id` is a foreign key referencing `fertilizer(fertilizer_id)`. It maintains **referential integrity**, ensuring that sales can only be recorded for fertilizers that actually exist in the inventory.

### Q14: How does the system implement the Low-Stock Warning feature?
**Answer:** The system runs a query `SELECT COUNT(*) FROM fertilizer WHERE quantity < 10`. If the count is greater than zero, `Dashboard.java` and `Stock.java` display a highlighted warning banner and badge alerting the store owner that specific fertilizers need to be reordered.

### Q15: What exception will occur if the MySQL server is not running?
**Answer:** A `java.sql.SQLException` (specifically Communications Link Failure) occurs. In `DBConnection.java`, we catch this exception and display a clear dialog informing the user to check their MySQL server (e.g., in XAMPP) rather than letting the application crash.

---
*Created for 2nd Year Computer Science / Information Technology Engineering Practical Examination.*
