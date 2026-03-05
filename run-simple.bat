@echo off
echo Setting JAVA_HOME...
set JAVA_HOME="C:\Program Files\Java\jdk-17"

echo Running application...
%JAVA_HOME%\bin\java --module-path "target/dependency" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp "target/classes;target/dependency/*" com.synapseevent.Main

pause
