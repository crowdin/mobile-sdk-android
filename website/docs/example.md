# Example Project

The Crowdin [Android SDK Example](https://github.com/crowdin/mobile-sdk-android/tree/master/example) is a simple todo app designed to demonstrate how to integrate and use Crowdin SDK features in a real Android application. This example project serves as a practical reference for developers looking to implement dynamic localization in their own apps.

## App Overview

The example app provides a task management interface that showcases key SDK integration patterns. Through common app interactions like creating tasks, managing categories, and navigating between screens, you can see how the Crowdin SDK handles localization. The app demonstrates proper SDK initialization, context wrapping, real-time updates, and other essential integration techniques in a realistic application context.

## Getting Started

To test the Crowdin SDK integration with your own Crowdin project, follow these steps:

### 1. Repository Setup

Clone the [mobile-sdk-android repository](https://github.com/crowdin/mobile-sdk-android) to your local machine and open the `example` project.

### 2. Crowdin Project Configuration

* Upload the string resources (`res/values/strings.xml` files) from the **example** and **example-info** modules to your Crowdin project
* Add translations for your target languages
* Create and publish a distribution to get your distribution hash

### 3. SDK Configuration

Navigate to [`App.kt`](https://github.com/crowdin/mobile-sdk-android/blob/master/example/src/main/java/com/crowdin/platform/example/App.kt) and configure the SDK with your project details:

* Replace `your_distribution_hash` with your actual distribution hash from Crowdin
* Set your source language code (e.g., `en`)
* Configure optional features like real-time updates, screenshots, or authentication as needed
* For enterprise users, add your organization name

The example demonstrates various SDK configuration options, so you can enable or disable features to test different scenarios.

## Dynamic Language Switching

The example app includes a Settings screen that demonstrates dynamic language switching capabilities. This feature shows how users can change the app's language at runtime without restarting the application. When a language is selected, the Crowdin SDK automatically fetches the latest translations from your Crowdin project and stores them in the local repository.

This implementation showcases the SDK's [`LanguagePreferences.kt`](https://github.com/crowdin/mobile-sdk-android/blob/master/example/src/main/java/com/crowdin/platform/example/LanguagePreferences.kt) class and proper context handling, demonstrating how to persist user language choices and apply them consistently across app sessions.

## Jetpack Compose Support

The Crowdin Android SDK includes full support for Jetpack Compose, Android's modern declarative UI toolkit. The example project features a dedicated [`SampleComposeActivity.kt`](https://github.com/crowdin/mobile-sdk-android/blob/master/example/src/main/java/com/crowdin/platform/example/SampleComposeActivity.kt) that demonstrates how to integrate the Crowdin SDK with Compose-based user interfaces.

This Compose implementation showcases the same localization capabilities as the traditional View-based activities, but with a modern, declarative approach. The activity demonstrates proper context wrapping for Compose screens, ensuring that all string resources are automatically localized through the Crowdin SDK. This makes it easy for developers already using or planning to migrate to Jetpack Compose to implement dynamic localization in their applications.

## Multi-module Architecture

The example project demonstrates how the Crowdin SDK works across multiple Android modules:

* **example** - The main application module containing the core todo functionality and SDK configuration
* **example-info** - A separate module with its own string resources, demonstrating cross-module localization

This multi-module setup shows how the SDK handles string resources from different modules. You can access the info screen through the main menu to see how localization works across module boundaries, which is particularly useful for larger Android projects with modular architectures.

## Key SDK Features Demonstrated

The example app showcases several important Crowdin SDK capabilities:

* **Context Wrapping**: Proper implementation in both traditional Activities and Compose screens
* **Real-time Updates**: Live translation updates without app restarts
* **Screenshot Automation**: Automatic screenshot capture for visual context
* **Menu Localization**: Dynamic translation of navigation drawer and toolbar menus
* **Loading State Management**: Handling of translation loading states and data observers
* **Network Configuration**: Configurable network types for translation downloads

## SDK Controls

[SDK Controls](/advanced-features/sdk-controls) is an overlay widget designed to facilitate control of the Crowdin Android SDK. By default, this component is initialized in the [`MainActivity.kt`](https://github.com/crowdin/mobile-sdk-android/blob/master/example/src/main/java/com/crowdin/platform/example/MainActivity.kt).
