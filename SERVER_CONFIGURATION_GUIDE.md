# 🖥️ Ethiopian YouTube - Server Configuration Guide
# Windows Server 2019 Deployment Instructions

## 📋 **Server Setup Checklist**

### **🔧 Prerequisites**
- [ ] Windows Server 2019 (Standard or Datacenter edition)
- [ ] JDK 17+ installed
- [ ] MySQL Server 8.0+ installed
- [ ] Maven 3.6+ installed
- [ ] Git for Windows installed
- [ ] Administrative privileges

---

## 🚀 **Step 1: Install Java Development Kit**

### **Download and Install JDK 17**
```cmd
# Download from Oracle: https://www.oracle.com/java/technologies/javase-jdk17-downloads.html
# Run installer as Administrator
# Set JAVA_HOME environment variable
setx JAVA_HOME "C:\Program Files\Java\jdk-17"
setx PATH "%PATH%;%JAVA_HOME%\bin"
```

### **Verify Installation**
```cmd
java -version
javac -version
echo %JAVA_HOME%
```

---

## 🗄️ **Step 2: Install MySQL Server**

### **Download and Install MySQL 8.0**
```cmd
# Download from: https://dev.mysql.com/downloads/mysql/
# Run MySQL Installer as Administrator
# Choose "Server only" installation
# Set root password and remember it
```

### **Configure MySQL for Ethiopian YouTube**
```sql
-- Connect to MySQL as root
mysql -u root -p

-- Create database
CREATE DATABASE ethiopian_youtube;
USE ethiopian_youtube;

-- Create user for the application
CREATE USER 'ethio_user'@'localhost' IDENTIFIED BY 'StrongPassword123!';
GRANT ALL PRIVILEGES ON ethiopian_youtube.* TO 'ethio_user'@'localhost';
FLUSH PRIVILEGES;

-- Create video metadata table
CREATE TABLE videos (
    id VARCHAR(50) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    filename VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    duration INT,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    node_location VARCHAR(50) NOT NULL,
    views INT DEFAULT 0,
    description TEXT
);

-- Create indexes for performance
CREATE INDEX idx_node_location ON videos(node_location);
CREATE INDEX idx_upload_date ON videos(upload_date);
```

---

## 📁 **Step 3: Create Project Directory Structure**

### **Create Directories**
```cmd
# Create main project directory
mkdir C:\EthiopianYouTube
cd C:\EthiopianYouTube

# Create storage directories for nodes
mkdir Node1
mkdir Node2
mkdir logs
mkdir config
mkdir backups

# Set permissions
icacls Node1 /grant Everyone:(OI)(CI)F /T
icacls Node2 /grant Everyone:(OI)(CI)F /T
icacls logs /grant Everyone:(OI)(CI)F /T
```

### **Directory Structure**
```
C:\EthiopianYouTube\
├── Node1\                 # Storage for Node1 videos
├── Node2\                 # Storage for Node2 videos
├── logs\                  # Application logs
├── config\                # Configuration files
├── backups\               # Database backups
└── src\                  # Source code (when cloned)
```

---

## 🔥 **Step 4: Configure Windows Firewall**

### **Open Required Ports**
```cmd
# Open RMI ports for Node1
netsh advfirewall firewall add rule name="EthiopianYouTube-Node1" dir=in action=allow protocol=TCP localport=1099

# Open RMI ports for Node2
netsh advfirewall firewall add rule name="EthiopianYouTube-Node2" dir=in action=allow protocol=TCP localport=1100

# Open MySQL port (if remote access needed)
netsh advfirewall firewall add rule name="EthiopianYouTube-MySQL" dir=in action=allow protocol=TCP localport=3306

# Verify firewall rules
netsh advfirewall firewall show rule name="EthiopianYouTube-*"
```

---

## 🌐 **Step 5: Configure Network Settings**

### **Static IP Configuration (Recommended)**
```cmd
# Set static IP for server
netsh interface ip set address "Ethernet" static 192.168.1.100 255.255.255.0 192.168.1.1

# Verify IP configuration
ipconfig /all
```

### **DNS Configuration**
```cmd
# Add server to local DNS (if needed)
# Or update hosts file for testing
notepad C:\Windows\System32\drivers\etc\hosts
# Add: 192.168.1.100 ethiopian-youtube.local
```

---

## 💾 **Step 6: Clone and Build Project**

### **Clone from GitHub**
```cmd
cd C:\EthiopianYouTube
git clone https://github.com/Nehimi/ethiopian-youtube-distributed.git src
cd src
```

### **Build with Maven**
```cmd
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package application
mvn package

# Build should create target/ directory with JAR files
```

---

## ⚙️ **Step 7: Configure Application Properties**

### **Create Database Configuration**
```cmd
# Create config file
notepad config\database.properties
```

**database.properties content:**
```properties
# Database Configuration
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/ethiopian_youtube
db.username=ethio_user
db.password=StrongPassword123!
db.pool.size=10
db.connection.timeout=30000

# Server Configuration
server.node1.port=1099
server.node2.port=1100
server.node1.storage=C:/EthiopianYouTube/Node1
server.node2.storage=C:/EthiopianYouTube/Node2

# Logging Configuration
log.level=INFO
log.file=C:/EthiopianYouTube/logs/application.log
log.max.size=10MB
log.max.files=5
```

---

## 🚀 **Step 8: Start the Servers**

### **Start MySQL Service**
```cmd
# Start MySQL service
net start mysql80

# Verify service is running
sc query mysql80
```

### **Start RMI Registry**
```cmd
# Open two command prompt windows

# Window 1 - Start RMI Registry for Node1
cd C:\EthiopianYouTube\src
start rmiregistry 1099

# Window 2 - Start RMI Registry for Node2  
start rmiregistry 1100
```

### **Start Application Servers**
```cmd
# Window 3 - Start Node1 Server
cd C:\EthiopianYouTube\src
java -cp "target/classes;target/dependency/*" -Djava.rmi.server.hostname=192.168.1.100 server.RMIServer Node1 1099

# Window 4 - Start Node2 Server
java -cp "target/classes;target/dependency/*" -Djava.rmi.server.hostname=192.168.1.100 server.RMIServer Node2 1100
```

### **Start Frontend Application**
```cmd
# Window 5 - Start JavaFX Application
java -cp "target/classes;target/dependency/*" ui.MainApplication
```

---

## 🔍 **Step 9: Verify Installation**

### **Check Server Status**
```cmd
# Check if RMI ports are listening
netstat -an | findstr ":1099"
netstat -an | findstr ":1100"

# Check MySQL connection
mysql -u ethio_user -p -e "USE ethiopian_youtube; SHOW TABLES;"

# Check application logs
type C:\EthiopianYouTube\logs\application.log
```

### **Test Basic Functionality**
1. **Open JavaFX Application** - Should load main interface
2. **Test Upload** - Try uploading a small video file
3. **Test Database** - Verify video metadata is saved
4. **Test Streaming** - Try playing uploaded video
5. **Test Load Balancing** - Verify videos distributed across nodes

---

## 🛡️ **Step 10: Security Configuration**

### **Create Windows Service (Optional)**
```cmd
# Install Node1 as Windows Service
sc create EthiopianYouTubeNode1 binPath= "C:\Program Files\Java\jdk-17\bin\java.exe -cp \"C:\EthiopianYouTube\src\target\classes;C:\EthiopianYouTube\src\target\dependency\*\" -Djava.rmi.server.hostname=192.168.1.100 server.RMIServer Node1 1099"

# Start the service
net start EthiopianYouTubeNode1

# Set service to start automatically
sc config EthiopianYouTubeNode1 start=auto
```

### **Configure SSL/TLS (Production)**
```cmd
# Generate SSL certificate
keytool -genkeypair -alias ethiopian-youtube -keyalg RSA -keysize 2048 -validity 365 -keystore ethiopian-youtube.jks

# Configure RMI to use SSL
# Add to java command: -Djavax.net.ssl.keyStore=ethiopian-youtube.jks -Djavax.net.ssl.keyStorePassword=password
```

---

## 📊 **Step 11: Monitoring and Maintenance**

### **Set Up Log Rotation**
```cmd
# Create log rotation script
notepad C:\EthiopianYouTube\scripts\rotate-logs.bat
```

**rotate-logs.bat content:**
```batch
@echo off
cd C:\EthiopianYouTube\logs
for /f "skip=5 delims=" %%i in ('dir /b /o-d *.log') do del "%%i"
echo Log rotation completed at %date% %time%
```

### **Create Backup Script**
```cmd
# Create database backup script
notepad C:\EthiopianYouTube\scripts\backup-database.bat
```

**backup-database.bat content:**
```batch
@echo off
set BACKUP_DIR=C:\EthiopianYouTube\backups
set TIMESTAMP=%date:~-4%%date:~-7,2%%date:~-10,2%_%time:~0,2%%time:~3,2%%time:~6,2%
mysqldump -u ethio_user -pStrongPassword123! ethiopian_youtube > "%BACKUP_DIR%\backup_%TIMESTAMP%.sql"
echo Database backup completed: %BACKUP_DIR%\backup_%TIMESTAMP%.sql
```

### **Schedule Automated Tasks**
```cmd
# Schedule log rotation (daily)
schtasks /create /tn "EthiopianYouTube-LogRotation" /tr "C:\EthiopianYouTube\scripts\rotate-logs.bat" /sc daily /st 02:00

# Schedule database backup (every 6 hours)
schtasks /create /tn "EthiopianYouTube-Backup" /tr "C:\EthiopianYouTube\scripts\backup-database.bat" /sc hourly /mo 6
```

---

## 🚨 **Troubleshooting**

### **Common Issues and Solutions**

#### **RMI Connection Issues**
```cmd
# Problem: Connection refused
# Solution: Check firewall and RMI registry
netstat -an | findstr ":1099"
telnet localhost 1099

# Problem: Class not found
# Solution: Check classpath
java -cp "target/classes;target/dependency/*" ...
```

#### **Database Connection Issues**
```cmd
# Problem: Access denied
# Solution: Check MySQL user permissions
mysql -u root -p
SHOW GRANTS FOR 'ethio_user'@'localhost';

# Problem: Can't connect to MySQL
# Solution: Check MySQL service
net start mysql80
sc query mysql80
```

#### **JavaFX Issues**
```cmd
# Problem: JavaFX not found
# Solution: Add JavaFX to classpath
java -cp "target/classes;target/dependency/*;path/to/javafx/lib/*" ...

# Problem: Graphics issues
# Solution: Update graphics drivers
# Or run in headless mode (if applicable)
```

---

## 📞 **Support and Contact**

### **Emergency Contacts**
- **System Administrator**: [Admin Phone]
- **Database Administrator**: [DBA Phone]
- **Network Administrator**: [Network Admin Phone]
- **Application Support**: TESHALE SULE (Group Leader)

### **Log Files Location**
- **Application Logs**: `C:\EthiopianYouTube\logs\application.log`
- **Database Logs**: `C:\ProgramData\MySQL\MySQL Server 8.0\Data\`
- **System Logs**: Windows Event Viewer

### **Configuration Files**
- **Database Config**: `C:\EthiopianYouTube\config\database.properties`
- **Server Config**: `C:\EthiopianYouTube\config\server.properties`
- **Log Config**: `C:\EthiopianYouTube\config\log4j2.xml`

---

## ✅ **Final Verification Checklist**

- [ ] JDK 17+ installed and JAVA_HOME set
- [ ] MySQL 8.0+ running with database created
- [ ] Firewall ports 1099, 1100, 3306 open
- [ ] Project cloned and built successfully
- [ ] Node1 server running on port 1099
- [ ] Node2 server running on port 1100
- [ ] JavaFX application launches successfully
- [ ] Test video upload and playback working
- [ ] Load balancing distributing videos correctly
- [ ] Logs being written correctly
- [ ] Backup scripts scheduled

---

**🎉 Ethiopian YouTube server is now configured and ready for production use!**

**Note:** This guide assumes a single-server deployment. For multi-server deployment, adjust network configurations and security settings accordingly.
