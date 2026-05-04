# 🖥️ Ethiopian YouTube - Final Server Configuration Guide
**Documented on: May 4, 2026**

This guide provides the exact steps taken to configure and run the distributed video streaming system on Windows Server 2019.

---

## 🚀 Step 1: Java Environment Setup
Ensure Java Development Kit (JDK) is correctly installed and recognized by the system.

1. **Set JAVA_HOME:**
   Open PowerShell as Administrator and run:
   ```powershell
   setx JAVA_HOME "C:\Program Files\Java\jdk-21.0.11"
   ```
2. **Update PATH:**
   ```powershell
   setx PATH "%PATH%;C:\Program Files\Java\jdk-21.0.11\bin"
   ```
3. **Verify:** Restart your terminal and type `java -version`.

---

## 📦 Step 2: Maven Configuration
Maven is required to manage dependencies (like SQL Drivers and JavaFX).

1. **Set Maven Path:**
   If `mvn` is not recognized, add your Maven `bin` folder to the Environment Variables.
2. **VS Code Settings:**
   Ensure the Maven extension points to the correct executable:
   `C:\Users\hp\maven\apache-maven-3.9.5\bin\mvn.cmd` (or your local path).

---

## 🗄️ Step 3: SQL Server Configuration (Critical)
We resolved several connection and authentication issues here.

1. **Enable Mixed Mode Authentication:**
   SQL Server must allow both Windows and SQL Server Authentication.
   * **CLI Method:** Run in PowerShell (Admin):
     ```powershell
     Set-ItemProperty -Path 'HKLM:\SOFTWARE\Microsoft\Microsoft SQL Server\MSSQL*.SQLEXPRESS\MSSQLServer' -Name LoginMode -Value 2
     Restart-Service -Name 'MSSQL$SQLEXPRESS' -Force
     ```
2. **Configure Database User (`yt_admin`):**
   Run the following script in SQL Server Management Studio (SSMS):
   ```sql
   -- Fix login and password
   ALTER LOGIN yt_admin WITH PASSWORD = 'Admin123', CHECK_POLICY = OFF;
   ALTER LOGIN yt_admin ENABLE;
   
   -- Grant permissions
   USE YouTubeEthiopia;
   IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'yt_admin')
       CREATE USER yt_admin FOR LOGIN yt_admin;
   ALTER ROLE db_owner ADD MEMBER yt_admin;
   ```
3. **Copy Database Drivers:**
   Run this in your project folder to ensure the SQL driver is available at runtime:
   ```powershell
   mvn dependency:copy-dependencies
   ```

---

## 🌐 Step 4: Network and IP Configuration
All components must point to the same Server IP.

1. **Current Server IP:** `10.198.73.40`
2. **Files Updated:**
   * `RMIServer.java`: Updated `java.rmi.server.hostname` to `.40`.
   * `DatabaseManager.java`: Updated SQL connection string to `.40`.
   * `LoadBalancer.java`: Updated HOST to `.40`.
   * `RMIClient.java` & `QuickTest.java`: Updated IP to `.40`.

---

## 🏃 Step 5: Running the System
Always follow this order to start the application:

1. **Build the Project:**
   ```powershell
   mvn clean compile dependency:copy-dependencies
   ```
2. **Start Server Node 1:**
   ```powershell
   java -cp "target/classes;target/dependency/*" server.RMIServer Node1 1099
   ```
3. **Start Server Node 2:**
   ```powershell
   java -cp "target/classes;target/dependency/*" server.RMIServer Node2 1100
   ```
4. **Start the UI (Client):**
   ```powershell
   mvn javafx:run
   ```

---

## 🛠️ Troubleshooting Commands
* **Kill stuck Java processes:** `taskkill /F /IM java.exe /T`
* **Test SQL Connection via CLI:** `sqlcmd -S .\SQLEXPRESS -U yt_admin -P Admin123`
* **Check Port Usage:** `netstat -ano | findstr :1099`

---
**✅ System Status: Operational**
All Metadata is saved to SQL Server `YouTubeEthiopia` database.
All Videos are stored in `Node1Storage` and `Node2Storage` folders.
