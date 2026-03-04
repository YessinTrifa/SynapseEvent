package com.synapseevent.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Set;

public final class Navigator {

    // ── Singleton ────────────────────────────────────────────────────────────
    private static Navigator instance;

    // ── Auth pages: nav bar is hidden on these ───────────────────────────────
    private static final Set<String> AUTH_PAGES = Set.of(
            "/fxml/landing.fxml",
            "/fxml/login.fxml",
            "/fxml/register.fxml"
    );

    // ── UI references ────────────────────────────────────────────────────────
    private final Stage     stage;
    private final StackPane host;
    private final HBox      navBar;      // the top HBox — shown only on dashboards
    private final Label     titleLabel;
    private final Button    backBtn;
    private final Button    forwardBtn;

    // ── History ──────────────────────────────────────────────────────────────
    private static class NavEntry {
        final String fxml;
        final String title;
        NavEntry(String fxml, String title) {
            this.fxml  = fxml;
            this.title = title;
        }
    }

    private final Deque<NavEntry> backStack    = new ArrayDeque<>();
    private final Deque<NavEntry> forwardStack = new ArrayDeque<>();
    private NavEntry current;

    // ── Constructor ──────────────────────────────────────────────────────────
    private Navigator(Stage stage, StackPane host, HBox navBar,
                      Label titleLabel, Button backBtn, Button forwardBtn) {
        this.stage       = stage;
        this.host        = host;
        this.navBar      = navBar;
        this.titleLabel  = titleLabel;
        this.backBtn     = backBtn;
        this.forwardBtn  = forwardBtn;
    }

    // ── Initialization ───────────────────────────────────────────────────────
    /**
     * Called ONCE from ShellController when the scene is ready.
     * Safe to call again after a reset().
     */
    public static void init(Stage stage, StackPane host, HBox navBar,
                            Label titleLabel, Button backBtn, Button forwardBtn) {
        if (instance != null) return;          // already initialized
        instance = new Navigator(stage, host, navBar, titleLabel, backBtn, forwardBtn);
        instance.updateButtons();
    }

    /**
     * Resets the singleton — call this if you ever need to reinitialize
     * (e.g., after a full application restart scenario).
     * For normal logout/login cycles you do NOT need this because the
     * Shell stays alive across the whole session.
     */
    public static void reset() {
        instance = null;
    }

    public static Navigator get() {
        if (instance == null) throw new IllegalStateException("Navigator not initialized yet.");
        return instance;
    }

    // ── Public navigation API ────────────────────────────────────────────────

    /** Navigate to a page, inferring the title automatically. */
    public void go(String fxmlPath) {
        go(fxmlPath, guessTitle(fxmlPath));
    }

    /** Navigate to a page with an explicit title. */
    public void go(String fxmlPath, String title) {
        Objects.requireNonNull(fxmlPath, "fxmlPath must not be null");

        // When navigating to an auth page, clear both stacks entirely.
        // This prevents going "back" from the login page to the dashboard.
        if (AUTH_PAGES.contains(fxmlPath)) {
            backStack.clear();
            forwardStack.clear();
            current = null;
        } else {
            if (current != null) {
                backStack.push(current);
                forwardStack.clear();
            }
        }

        loadEntry(new NavEntry(fxmlPath, title));
    }

    /** Go back one step in history. */
    public void back() {
        if (backStack.isEmpty()) return;
        if (current != null) forwardStack.push(current);
        loadEntry(backStack.pop());
    }

    /** Go forward one step in history. */
    public void forward() {
        if (forwardStack.isEmpty()) return;
        if (current != null) backStack.push(current);
        loadEntry(forwardStack.pop());
    }

    /** Reload the current page from scratch (like F5 in a browser). */
    public void refresh() {
        if (current == null) return;
        // Reload without touching the stacks
        loadEntry(new NavEntry(current.fxml, current.title));
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private void loadEntry(NavEntry entry) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(entry.fxml));
            Parent view = loader.load();

            host.getChildren().setAll(view);
            current = entry;

            // Update the window/stage title
            if (stage != null) stage.setTitle(entry.title);

            // Update the centered label in the nav bar
            if (titleLabel != null) titleLabel.setText(entry.title);

            // Show or hide the entire nav bar
            // Shell navbar permanently hidden — each dashboard has its own sidebar
            if (navBar != null) {
                navBar.setVisible(false);
                navBar.setManaged(false);
            }

            updateButtons();

        } catch (Exception e) {
            System.err.println("[Navigator] Failed to load: " + entry.fxml);
            e.printStackTrace();
        }
    }

    private void updateButtons() {
        if (backBtn    != null) backBtn.setDisable(backStack.isEmpty());
        if (forwardBtn != null) forwardBtn.setDisable(forwardStack.isEmpty());
    }

    /** Maps FXML paths to human-readable page titles shown in the nav bar. */
    private String guessTitle(String path) {
        if (path == null) return "SynapseEvent";
        if (path.contains("landing"))        return "SynapseEvent";
        if (path.contains("login"))          return "Login – SynapseEvent";
        if (path.contains("register"))       return "Register – SynapseEvent";
        if (path.contains("adminDashboard")) return "Admin Dashboard";
        if (path.contains("userDashboard"))  return "My Dashboard";
        if (path.contains("formation"))      return "Formation Events";
        if (path.contains("paddle"))         return "Paddle Events";
        if (path.contains("partying"))       return "Partying Events";
        if (path.contains("teamBuilding"))   return "Team Building Events";
        if (path.contains("anniversary"))    return "Anniversary Events";
        if (path.contains("customEventType"))return "Custom Event Types";
        if (path.contains("user.fxml"))      return "User Profile";
        return "SynapseEvent";
    }
}