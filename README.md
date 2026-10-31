# 📐 ShelfFit — Shelf Spacing & Capacity Calculator

[![Android](https://img.shields.io/badge/Platform-Android-green.svg?logo=android)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg?logo=kotlin)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20%2F%20Material%203-blue.svg?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-26%20%28Android%208.0%29-brightgreen.svg)](https://developer.android.com/about/dashboards)
[![Build](https://img.shields.io/badge/Tests-8%20Passed-success.svg)](#testing)

**ShelfFit** is a modern, intuitive Android application built with **Jetpack Compose** and **Material 3** that helps woodworkers, interior designers, organizers, retailers, and book enthusiasts accurately calculate shelf item capacities, clearances, and spacing.

---

## ✨ Features

- 📏 **Precise Capacity Engine**: Calculates maximum item capacity based on total shelf width, left/right clearances, side spacing, object dimensions, and desired gap sizes.
- 🎨 **Interactive 2D Visualizer**: Real-time canvas visualization (`ShelfVisualizer`) rendering shelf bounds, clearances, items, and gaps.
- 📐 **Multi-Unit Measurement System**: Supports Millimeters (`mm`), Centimeters (`cm`), Meters (`m`), Inches (`in`), and Feet (`ft`) with seamless dynamic conversion.
- 📊 **Step-by-Step Calculation Breakdown**: Clear mathematical explanation of deductions, available width, formula evaluation, occupied space, and remaining free room.
- 🎯 **Target Quantity Validation**: Check if a specific number of items will fit, with automated extra width requirements if space is insufficient.
- ⚖️ **Distributed Even Spacing**: Automatically calculates uniform spacing options to evenly distribute items across the usable shelf length.
- 📜 **History & Saved Presets**: Save calculations locally, copy formatted results to clipboard, and reload past shelf setups anytime.
- 🎨 **Material Design 3 & Dark Theme**: Dynamic theme options (Light, Dark, System default) with custom color schemes and haptic feedback integration.
- 🔒 **100% Offline & Private**: Zero cloud dependency, no trackings, and local data persistence via SharedPreferences / JSON.

---

## 🧮 Calculation Logic & Mathematical Formula

ShelfFit converts all measurements internally to millimeters (`mm`) for maximum accuracy.

The capacity formula is defined as:

$$\text{Capacity} = \left\lfloor \frac{\text{Available Width} + \text{Gap}}{\text{Object Width} + \text{Gap}} \right\rfloor$$

Where **Available Width** is computed as:

$$\text{Available Width} = \text{Shelf Width} - \text{Left Clearance} - \text{Right Clearance} - (2 \times \text{Side Spacing})$$

---

## 📱 Screenshots & User Interface

The app consists of 7 core screens connected with Navigation Compose:

1. **Home Screen**: Quick overview, quick action shortcuts, recent history previews, and hero card.
2. **Calculator Screen**: Input parameters for shelf width, clearances, object width, gaps, target quantities, and measurement units.
3. **Result Screen**: Visual 2D shelf canvas, key stat metrics, step-by-step breakdown, and target fit report.
4. **History Screen**: Stored past shelf calculations with search, filtering, and reload features.
5. **Settings Screen**: Default measurement unit selector, theme mode switcher, and haptic feedback toggle.
6. **About Screen**: App information, mathematical formula explanation, and privacy policy.
7. **Splash Screen**: Animated branded entry screen.

---

## 🛠️ Architecture & Tech Stack

ShelfFit strictly follows modern Android architecture guidelines (MVVM + Clean Domain Layer):

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material 3](https://m3.material.io/)
- **Architecture Pattern**: MVVM (Model-View-ViewModel) + Clean Architecture
- **State Management**: Kotlin `StateFlow`, `SharedFlow`, and `Coroutines`
- **Navigation**: `androidx.navigation:navigation-compose`
- **Local Storage**: `SharedPreferences` with custom JSON serialization
- **Asynchronous Execution**: Kotlin Coroutines
- **Testing**: `JUnit 4` for unit tests and math engine validation

---

## 📁 Project Structure

```
ShelfFit/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/shelffit/
│   │   │   │   ├── data/            # Local data sources, preferences & repositories
│   │   │   │   │   ├── local/
│   │   │   │   │   ├── model/
│   │   │   │   │   └── repository/
│   │   │   │   ├── domain/          # Pure Kotlin calculation engine & models
│   │   │   │   │   ├── calculator/  # ShelfCalculatorEngine.kt
│   │   │   │   │   └── model/
│   │   │   │   ├── settings/        # App theme definitions
│   │   │   │   ├── ui/              # Compose screens, components & navigation
│   │   │   │   │   ├── components/  # ShelfVisualizer, StatCard, MeasurementInputField
│   │   │   │   │   ├── navigation/  # Navigation routes
│   │   │   │   │   ├── screens/     # HomeScreen, CalculatorScreen, ResultScreen, etc.
│   │   │   │   │   └── theme/       # Color, Type, Theme definitions
│   │   │   │   ├── utils/           # LengthUnit enum, HapticUtils, DateUtils
│   │   │   │   ├── viewmodel/       # ShelfViewModel.kt
│   │   │   │   └── MainActivity.kt  # Entry point activity
│   │   │   ├── res/                 # Resources (drawables, values, mipmaps)
│   │   │   └── AndroidManifest.xml
│   │   └── test/                    # Unit tests for calculation engine & history
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Studio Ladybug (2024.2.1) or newer recommended.
- **JDK**: Java 11 or higher.
- **Android SDK**: API level 26+ (Android 8.0+).

### Building & Running
1. Clone the repository:
   ```bash
   git clone https://github.com/abdullah-ateeq/ShelfFit.git
   cd ShelfFit
   ```
2. Open the project in **Android Studio**.
3. Sync Gradle project files.
4. Run the app on an Android Emulator or connected device:
   ```bash
   ./gradlew installDebug
   ```

### Running Unit Tests
To run the automated test suite for the calculator engine and data models:
```bash
./gradlew test
```

---

## 📄 License

```text
Copyright (2025) Abdullah Ateeq.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations hours specified in the License.
```
