@echo off
SET "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.0.36-hotspot"
SET "PATH=%JAVA_HOME%\bin;%PATH%"
SET "MVN=C:\Users\hp\maven\apache-maven-3.9.5\bin\mvn.cmd"
SET "CP=target\classes;target\dependency\*"

echo [1/4] Killing old processes...
taskkill /F /IM java.exe /T 2>nul

echo [2/4] Compiling project...
call "%MVN%" clean compile dependency:copy-dependencies

echo [3/4] Starting RMI Nodes...
start "Node 1" cmd /k "java -cp %CP% -Djava.rmi.server.hostname=127.0.0.1 server.RMIServer Node1 1099"
timeout /t 5    
start "Node 2" cmd /k "java -cp %CP% -Djava.rmi.server.hostname=127.0.0.1 server.RMIServer Node2 1100"
timeout /t 5

echo [4/4] Starting JavaFX UI...
call "%MVN%" javafx:run

pause
