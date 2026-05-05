@echo off
SET "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.11"
SET "PATH=%JAVA_HOME%\bin;%PATH%"
SET "MVN=C:\Users\hp\maven\apache-maven-3.9.5\bin\mvn.cmd"

echo Starting EthioTube Web Bridge...
echo Make sure your Database and RMI Nodes are running!
echo.
call "%MVN%" clean spring-boot:run -Dspring-boot.run.mainClass=web.WebBridgeApplication
pause
