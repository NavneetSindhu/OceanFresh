# OceanFresh - Grocery Delivery App

OceanFresh is a modern, Blinkit-style mini grocery delivery application built with Kotlin and Jetpack Compose.

## Screenshots

| Login Screen | Home Screen | Cart Screen |
| :---: | :---: | :---: |
| <img src="https://github.com/user-attachments/assets/f2c96ccc-8d64-4096-ab86-3cf26c81bb64" width="300" height="600" /> | <img src="https://github.com/user-attachments/assets/7b15a14c-4bc7-4a16-851e-befa3f866842" width="300" height="600" /> | <img src="https://github.com/user-attachments/assets/15c515cf-c0cb-4cdd-9cfa-15fac462a4e0" width="300" height="600" /> |

| Checkout Screen | Order Success | Dark Mode |
| :---: | :---: | :---: |
| <img src="https://github.com/user-attachments/assets/72c8b0f4-0b85-418c-9f5c-d10bbb2ff22c" width="300" height="600" /> | <img src="https://github.com/user-attachments/assets/bf4115d1-7e2b-4b0a-9116-15962055f2f1" width="300" height="600" /> | <img src="https://github.com/user-attachments/assets/3de83022-1b9c-4bf1-aa95-30dc2853bc48" width="300" height="600" /> |




## Features

* **Authentication**: Secure mobile number input with fake OTP verification (1234).
* **Product Discovery**: Browse products by categories with real-time search and filtering.
* **Cart Management**: Add/remove products with O(1) reactive lookups and persistent local storage.
* **Checkout Flow**: Manual address entry with "Current Location" mock integration and payment selection.
* **Order Tracking**: Generated Order ID, success animations, and estimated delivery timing.
* **Dynamic UI**: Full support for Light and Premium Dark mode with smooth transitions.

## Tech Stack

* **Language**: Kotlin 2.0.21
* **UI Framework**: Jetpack Compose (Material 3)
* **Architecture**: MVVM (Model-View-ViewModel)
* **Local Database**: Room DB with KSP for high-performance persistence.
* **State Management**: Kotlin Flows & StateFlow for reactive UI updates.
* **Navigation**: Compose Navigation Component with shared ViewModel scoping.
* **Dependency Injection**: Manual Constructor Injection with ViewModel Factories.

## How to Run

1. Clone this repository to your local machine.
2. Open the project in the latest version of Android Studio (Ladybug or newer recommended).
3. Ensure the Kotlin version is set to 2.0.21 in your build settings.
4. Sync Project with Gradle Files.
5. Run the application on a physical device or emulator (API 24+).
6. Use mobile number for login and `1234` for the OTP.
