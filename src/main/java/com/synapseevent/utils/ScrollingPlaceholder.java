package com.synapseevent.utils;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.util.Duration;

public class ScrollingPlaceholder {
    
    private static final Duration SCROLL_DURATION = Duration.millis(500);
    private static final Duration PAUSE_DURATION = Duration.seconds(2);
    
    public static void setScrollingPlaceholder(TextInputControl field, String message) {
        if (message == null || message.length() <= 30) {
            // For short messages, use regular placeholder
            field.setPromptText(message);
            return;
        }
        
        // Create scrolling animation for long messages
        createScrollingAnimation(field, message);
    }
    
    public static void setPasswordFieldScrolling(TextInputControl field, String message) {
        if (message == null) {
            field.setPromptText("");
            return;
        }
        
        // Always create scrolling for password fields, regardless of length
        createScrollingAnimation(field, message);
    }
    
    private static void createScrollingAnimation(TextInputControl field, String message) {
        // Stop any existing animation
        stopScrolling(field);
        
        // Set the initial message immediately
        field.setPromptText(message);
        
        // Create scrolling animation
        Timeline scrollTimeline = new Timeline();
        
        // Store original message
        final String originalMessage = message;
        
        // Create a scrolling version with spaces
        String scrollingText = message + "    ";
        
        KeyFrame scrollFrame = new KeyFrame(SCROLL_DURATION, event -> {
            String currentText = field.getPromptText();
            if (currentText == null || currentText.isEmpty()) {
                currentText = scrollingText;
            }
            
            // Scroll the text
            if (currentText.length() > 1) {
                String newText = currentText.substring(1) + currentText.charAt(0);
                field.setPromptText(newText);
            } else {
                field.setPromptText(scrollingText);
            }
        });
        
        scrollTimeline.getKeyFrames().add(scrollFrame);
        scrollTimeline.setCycleCount(Animation.INDEFINITE);
        
        // Store the timeline in the field's properties for cleanup
        field.getProperties().put("scrollTimeline", scrollTimeline);
        
        // Start the animation after a short delay to ensure initial message is visible
        javafx.application.Platform.runLater(() -> {
            try {
                Thread.sleep(100); // Small delay to show initial message
                scrollTimeline.play();
            } catch (InterruptedException e) {
                scrollTimeline.play(); // Play immediately if sleep fails
            }
        });
        
        // Stop animation when user starts typing
        field.textProperty().addListener((obs, old, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                stopScrolling(field);
            }
        });
        
        // Stop animation when field loses focus
        field.focusedProperty().addListener((obs, old, newValue) -> {
            if (!newValue) {
                stopScrolling(field);
            }
        });
    }
    
    public static void stopScrolling(TextInputControl field) {
        Timeline timeline = (Timeline) field.getProperties().get("scrollTimeline");
        if (timeline != null) {
            timeline.stop();
            field.getProperties().remove("scrollTimeline");
        }
    }
    
    public static void clearFieldWithScrollingPlaceholder(TextInputControl field, String originalPlaceholder) {
        stopScrolling(field);
        field.clear();
        field.setStyle("");
        field.setPromptText(originalPlaceholder);
    }
}
