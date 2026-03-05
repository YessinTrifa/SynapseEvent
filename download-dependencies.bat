@echo off
echo Setting JAVA_HOME...
set JAVA_HOME="C:\Program Files\Java\jdk-17"

echo Downloading dependencies...
call mvnw.cmd dependency:copy-dependencies

echo Dependencies downloaded to target/dependency/
echo You can now run the application using run.bat
