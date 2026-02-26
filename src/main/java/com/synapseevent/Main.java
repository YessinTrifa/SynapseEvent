package com.synapseevent;

import com.synapseevent.utils.DatabaseInitializer;
import com.synapseevent.utils.MaConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("=== SynapseEvent Application Starting ===");
        
        try {
            // Initialize database on startup
            System.out.println("Step 1: Initializing database...");
            DatabaseInitializer.initializeDatabase();
            System.out.println("✓ Database initialized successfully");
        } catch (Exception e) {
            System.err.println("✗ Database initialization failed");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            // Continue anyway - the application can still run without DB
        }
        
        try {
            System.out.println("Step 2: Loading UI...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/landing.fxml"));
            
            if (loader.getLocation() == null) {
                throw new Exception("landing.fxml not found in resources!");
            }
            
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            stage.setScene(scene);
            stage.setTitle("SynapseEvent Management");
            stage.setWidth(1000);
            stage.setHeight(700);
            stage.show();
            
            System.out.println("✓ UI loaded successfully");
            System.out.println("=== Application Ready ===");
        } catch (Exception e) {
            System.err.println("✗ Failed to load UI");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args) {
        System.out.println("JVM Version: " + System.getProperty("java.version"));
        System.out.println("User Directory: " + System.getProperty("user.dir"));
        System.out.println("JavaFX Version: 17.0.8");
        launch(args);
    }
}