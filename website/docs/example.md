# Example project

Crowdin [Android SDK Example](https://github.com/crowdin/mobile-sdk-android/tree/master/example) is a simple todo app designed to illustrate how you can use Crowdin SDK features with a real Android app. This app's primary purpose is to show the Crowdin SDK integration process in action and test the possibilities it provides.

## App Overview

In the Crowdin Example app, you can create a simple task, add a specific category for it, set the date and time, mark the task as read and delete it.
Additionally, you can create new categories and review the history of the completed tasks.

## Connecting Crowdin project with Crowdin Example app

To connect the project with your Crowdin account and test the content delivery as well as other features, follow these steps:

* Clone the current repository
* Crowdin project setup:
  * Add the resources (`res/values/strings.xml` files) from the **example / example-info** modules to your Crowdin project. If you’d like to use files from the different modules, check out the [instructions](/guides/multiple-flavor-app)
  * Translate the resources
  * Create a distribution
* App setup:
  * Navigate to the `App.kt` class and paste in your `distribution_hash` obtained in Crowdin, enable the other required options for your test case.

## In-app language changes

On the Settings page, you can switch the UI language used by the Crowdin Example app. When you change the language, Crowdin SDK fetches the latest translations from the Crowdin project and stores them in the local repository.

## Multi-module support

The app consists of the following modules:

* **example** - the main app classes
* **example-info** - for simplicity, this module contains only one UI screen - `InfoActivity.kt` that has its own string resources

You can navigate to this screen by clicking on the `Info` item using the main screen menu.

## SDK Controls

[SDK Controls](/advanced-features/sdk-controls) is an overlay widget designed to facilitate control of the Crowdin Android SDK. By default, this component is initialized in the `MainActivity.kt`.
