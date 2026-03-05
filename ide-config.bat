@echo off
echo Configuration pour IDE IntelliJ/Eclipse
echo.
echo VM Options à ajouter dans votre configuration de run :
echo --module-path target/dependency --add-modules javafx.controls,javafx.fxml,javafx.graphics --add-opens javafx.controls/javafx.scene.control=ALL-UNNAMED --add-opens javafx.graphics/javafx.scene=ALL-UNNAMED --add-opens javafx.fxml/javafx.fxml=ALL-UNNAMED
echo.
echo Ou utilisez simplement: run-simple.bat
pause
