@echo off
echo Setting JAVA_HOME...
set JAVA_HOME="C:\Program Files\Java\jdk-17"

echo Running Main class directly with JavaFX modules...
%JAVA_HOME%\bin\java --module-path "target/dependency/javafx-base-17.0.8.jar;target/dependency/javafx-base-17.0.8-win.jar;target/dependency/javafx-controls-17.0.8.jar;target/dependency/javafx-controls-17.0.8-win.jar;target/dependency/javafx-fxml-17.0.8.jar;target/dependency/javafx-fxml-17.0.8-win.jar;target/dependency/javafx-graphics-17.0.8.jar;target/dependency/javafx-graphics-17.0.8-win.jar" ^
     --add-modules javafx.controls,javafx.fxml,javafx.graphics ^
     --add-opens javafx.controls/javafx.scene.control=ALL-UNNAMED ^
     --add-opens javafx.graphics/javafx.scene=ALL-UNNAMED ^
     --add-opens javafx.fxml/javafx.fxml=ALL-UNNAMED ^
     -cp "target/classes;target/dependency/mysql-connector-j-8.0.33.jar;target/dependency/bcrypt-0.10.2.jar;target/dependency/bytes-1.5.0.jar" ^
     com.synapseevent.Main

pause
