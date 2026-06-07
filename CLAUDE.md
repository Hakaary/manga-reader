# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
# Build uber JAR
mvn clean package

# Run
java -jar target/mreader-1.0-shaded.jar /path/to/manga

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=AppTest
```

The CLI wrapper scripts in `cli_scripts/` launch the JAR via `nohup` (Linux) or `javaw` (Windows). They expect the JAR at `/usr/bin/mreader.jar`.

## Architecture

A Java Swing desktop application for reading locally stored manga/comics. No server, database, or external APIs.

**Package root:** `hakaary.app`

### Module breakdown

- **`reader/Reader.java`** — Scans a directory tree for images. Expects chapter directories named with integers (`1/`, `2/`, etc.) and images named with numeric prefixes (`001.jpg`). Returns `HashMap<Integer, ArrayList<String>>` mapping chapter number → sorted image paths.

- **`reader/PageManager.java`** — Stateful (static fields) navigation controller. Holds current chapter, page index, and loaded image. Exposes `setNextPage()`, `setPrevPage()`, `setCurrentChapter(int)`. Lazy-loads images via `ImageIO` on `getCurrentPage()`.

- **`gui/AppFrame.java`** — Main `JFrame` (undecorated). Hosts `AppImageDisplay` (center) and `AppNavbar` (bottom). Wires event handlers via `setFuncs()`. Supports drag-to-move and double-click fullscreen toggle.

- **`gui/AppImageDisplay.java`** — Custom `Canvas` subclass. Renders images with aspect-ratio-preserving scaling using `Graphics2D`. Optionally shows `<`/`>` side navigation buttons.

- **`gui/AppNavbar.java`** — Bottom toolbar with chapter `JComboBox`, page counter label (`"5/42"`), and Prev/Next/Close buttons.

- **`main/Main.java`** — Entry point. Takes an optional directory path argument (defaults to CWD), runs `Reader`, initializes `PageManager`, and builds the `AppFrame`.

### Data flow

```
CLI arg (directory)
  → Reader.getChaptersPages()  →  HashMap<chapter → [image paths]>
  → PageManager (static state)
  → AppFrame + AppImageDisplay + AppNavbar
  → user navigation events → PageManager updates → canvas repaint
```

### Key constraints

- Chapter directories must be integer-named; non-integer dirs are silently skipped.
- Images within a chapter are sorted by numeric prefix extracted from filenames.
- WebP support comes from the TwelveMonkeys ImageIO plugin (registered automatically at startup).
- `PageManager` uses static fields — not safe for multiple concurrent instances.
