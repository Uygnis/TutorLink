@echo off

echo Building project...
call mvn clean install -DskipTests

if %errorlevel% neq 0 (
    echo Maven build failed!
    exit /b %errorlevel%
)

echo Running Java application...
java -jar target\springboot-auth-be-0.0.1-SNAPSHOT.jar
pause