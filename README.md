# mreader

A lightweight Java Swing desktop application for reading locally stored manga and comics. No server, database, or internet connection required.

## Features

- Reads manga from local directories
- Supports common image formats (JPEG, PNG) and **WebP**
- Aspect-ratio-preserving image scaling
- Chapter and page navigation via toolbar or side buttons
- Fullscreen toggle on double-click
- Drag-to-move window (undecorated frame)
- Dark theme via FlatLaf

## Requirements

- Java 17+
- Maven 3.x (to build from source)

## Directory structure

The reader expects the manga directory to follow this layout:

```
manga-title/
  1/
    001.jpg
    002.jpg
  2/
    001.jpg
    002.webp
  3/
    ...
```

- Chapter folders must be **integer-named** (`1`, `2`, `3`, …). Non-integer directories are ignored.
- Images within a chapter are sorted by their **numeric prefix**.

## Build

```bash
mvn clean package
```

Produces `target/mreader-1.0-shaded.jar` — a self-contained uber JAR.

## Run

```bash
java -jar target/mreader-1.0-shaded.jar /path/to/manga
```

If no path is provided, the current working directory is used.

## Install (Linux)

Copy the JAR and the launcher script so it is available system-wide:

```bash
sudo cp target/mreader-1.0-shaded.jar /usr/bin/mreader.jar
sudo cp cli_scripts/mreader /usr/local/bin/mreader
sudo chmod +x /usr/local/bin/mreader
```

Then launch from anywhere:

```bash
mreader /path/to/manga
```

## Install (Windows)

Copy `target/mreader-1.0-shaded.jar` and `cli_scripts/mreader.bat` to the same folder and run:

```bat
mreader.bat C:\path\to\manga
```

## Tests

```bash
mvn test

# Single test class
mvn test -Dtest=AppTest
```

## Dependencies

| Library | Purpose |
|---|---|
| [FlatLaf 3.5.1](https://www.formdev.com/flatlaf/) | Modern flat look-and-feel |
| [TwelveMonkeys ImageIO WebP 3.11](https://github.com/haraldk/TwelveMonkeys) | WebP image format support |
| JUnit 5.11 | Testing |
