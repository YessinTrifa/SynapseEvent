package com.synapseevent;

import com.synapseevent.utils.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Initialize database on startup
        DatabaseInitializer.initializeDatabase();
        
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("SynapseEvent Management");
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}