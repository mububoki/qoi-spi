# qoi-spi

QOI (Quite OK Image) format support for IntelliJ-based IDEs via Java ImageIO SPI.

## Overview

This plugin registers a QOI ImageReader through Java's ImageIO Service Provider Interface, enabling IntelliJ-based IDEs (IntelliJ IDEA, GoLand, etc.) to display `.qoi` images natively.

No native dependencies — pure JVM implementation.

## Install

Download the plugin zip from [Releases](https://github.com/mububoki/qoi-spi/releases) and install it via:

**Settings** → **Plugins** → **⚙️** → **Install Plugin from Disk...**

## Build

```bash
./gradlew build
```

The plugin zip will be generated at `build/distributions/qoi-spi-<version>.zip`.

To launch a sandbox IDE for testing:

```bash
./gradlew runIde
```
