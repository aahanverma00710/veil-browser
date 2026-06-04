# Veil Browser

Veil is a modern, privacy-focused Android browser built with Jetpack Compose, Material 3, and Kotlin. It features built-in ad-blocking, tab management, and a clean, user-friendly interface.

## Features

- 🚀 **Modern UI**: Built entirely with Jetpack Compose and Material 3.
- 🛡️ **Ad-Blocker**: Built-in ad-blocking capabilities using a customizable blocklist.
- 📑 **Tab Management**: Easily manage and switch between multiple open tabs.
- 🔖 **Bookmarks**: Save and manage your favorite websites for quick access.
- 🏠 **Homepage**: A speed dial grid of your bookmarked sites and a search bar.
- 🌓 **Dark Mode**: Support for both Light and Dark themes with dynamic color.
- 🛠️ **Built with Best Practices**: Follows MVVM architecture, Clean Architecture principles, and uses Hilt for Dependency Injection.

## Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Design System**: [Material 3](https://m3.material.io/)
- **Dependency Injection**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Database**: [Room](https://developer.android.com/training/data-storage/room)
- **Navigation**: [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- **Architecture**: MVVM (Model-View-ViewModel)

## Getting Started

### Prerequisites

- Android Studio Ladybug or newer.
- JDK 17 or higher.
- Android device or emulator running API 24 (Android 7.0) or higher.

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/aahanverma00710/veil-browser.git
   ```
2. Open the project in Android Studio.
3. Sync the project with Gradle files.
4. Run the app on your device or emulator.

## Project Structure

- `adblock/`: Logic for ad-blocking and request interception.
- `data/`: Local database (Room), entities, DAOs, and repository implementations.
- `di/`: Hilt modules for dependency injection.
- `domain/`: Domain models and business logic.
- `ui/`: Compose screens, ViewModels, navigation graph, and theme definitions.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
