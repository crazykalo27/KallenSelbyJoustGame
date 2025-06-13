# Development Log

## Project Overview

- **Project:** A clone of the arcade game "Joust".
- **Technology:** Java, LibGDX, Gradle.
- **Platforms:** Desktop and Web (HTML5/GWT).
- **Build System:** Multi-module Gradle project (`core`, `desktop`, `html`).
- **Deployment:** Configured for deployment to GitHub Pages. The `html` module builds to the `docs` directory for hosting.

## Initial Analysis (from `PROJECT_HISTORY.md`)

The project has undergone significant recent improvements:

- **Code Refactoring:**
    - Level loading has been rewritten to match the original game's logic.
    - Physics for the hero and enemies have been re-implemented.
    - Platform behaviors (truss, slime, ice, etc.) are corrected.
    - UI and camera systems have been fixed for an 800x600 resolution.
- **Build & Deployment:**
    - The project now uses a modern multi-module Gradle setup.
    - The GWT plugin has been updated for compatibility with Gradle 8.5.
    - A `deployToGitHubPages` Gradle task is available to build the web version into the `docs/` folder.
- **Project Status:**
    - The project appears to be in a stable, buildable, and deployable state.
    - Unnecessary files and build artifacts have been cleaned up.

## Core Game Structure

- **Entry Point:** `core/src/com/joust/JoustGame.java`
    - Initializes the `SpriteBatch` and `GameAssetManager`.
    - Sets the initial screen to `LoadingScreen`.
- **Screen Flow:**
    1.  `LoadingScreen`: Displays loading progress while assets are loaded asynchronously by `GameAssetManager`.
    2.  Once loading is complete, it transitions to the `MenuScreen`.
- **Packages:**
    - `screens`: Contains different game states (`LoadingScreen`, `MenuScreen`, `GameScreen`, etc.).
    - `managers`: Likely contains helper classes (e.g., `GameAssetManager`).
    - `entities`: Should contain game objects like the player, enemies, etc.

## Manager Classes

- **`GameAssetManager`:**
    - A singleton that wraps LibGDX's `AssetManager`.
    - Queues all game assets (textures) for asynchronous loading.
    - Provides easy access to loaded textures via friendly names (e.g., "hero_left").
    - Contains GWT-specific code for web compatibility.
- **`LevelLoader`:**
    - Reads text-based level files from `assets/levels/`.
    - Parses a grid of characters to create game objects (`Hero`, `Enemy`, `Platform`).
    - Handles the crucial conversion from a Y-down (Swing) to Y-up (LibGDX) coordinate system.
    - The code comments indicate this is a direct port from an older Swing version of the game.
- **`CollisionManager`:** (Not yet analyzed)
- **`InputManager`:** (Not yet analyzed)

## Entity System

- **`GameObject` (Base Class):**
    - An abstract base class for all game objects.
    - Provides fundamental properties: `position`, `velocity`, `width`, `height`, `bounds`, `texture`.
    - Defines an abstract `update()` method and a standard `render()` method.
- **`Hero` (Player Character):**
    - Extends `GameObject`.
    - Implements player-specific physics for movement, flying, gravity, and friction.
    - Manages player state (e.g., `facingRight`, `onGround`, `shouldDie`).
    - Contains detailed collision handling logic:
        - **Platform Collision:** Responds to different platform types (normal, lava, ice, slime).
        - **Enemy Collision:** Implements the core "jousting" mechanic where the higher entity wins.
        - **Egg Collision:** Allows the player to collect eggs.
- **Other Entities:** (Not yet analyzed in detail, but structure is clear)
    - `Enemy`: Likely extends `GameObject` and has different AI behaviors.
    - `Platform`: Represents the different types of platforms.
    - `Egg`: An entity spawned when an enemy is defeated.

## HTML5 Deployment Alignment

- **Goal:** Updated the project's HTML5 build configuration to align with modern best practices for GitHub Pages deployment.
- **`html/build.gradle`:**
    - Added GWT compiler optimizations (`optimize = 9`, `draftCompile = false`) for a smaller, faster production build.
    - Removed the legacy `war` and `dist` tasks.
    - Added a standard `processResources` block to ensure assets from `core/assets` are correctly copied into the build output (`html/build/dist/assets`).
- **`html/src/webapp/index.html`:**
    - Replaced the existing `index.html` with a modern, standardized template.
    - Added the `<base href="./">` tag to ensure assets load correctly on GitHub Pages (or any subdirectory).
    - Updated the `<script>` tag to point to the correct GWT module output (`GdxDefinition.nocache.js`).

This log will be updated as I continue to explore the project. 