# 🧘 Still — Minimalist Passive Safety Timer
<p align="center">
  <img src="https://github.com/thezayin/still-safety-timer/blob/main/app/src/main/res/drawable/ic_still_logo.xml" width="200" height="200" alt="Still Logo">
</p>

<p align="center">
  <strong>"The Art of Stillness in Personal Safety"</strong><br>
  An Android-native passive protection system built with MVI and Clean Architecture.
</p>


## ✨ Overview

**Still** is a departure from traditional "Panic Button" safety apps. It operates on a **Negative-Check-In** protocol: instead of requiring the user to act during an emergency, it assumes an emergency is occurring if the user *fails* to act within a specific timeframe.

### 🛡️ Core Features

This application offers a refined suite of functionalities designed for quiet assurance and maximum reliability:

* **⏱️ The Safety Pulse:** Set a custom safety interval (24h, 48h, or custom). The app maintains a silent background "Pulse" that watches over you.
* **✅ Passive Check-In:** A simple, minimalist interface to reset your pulse. No loud noises, no complex menus.
* **🚨 Zero-Hour Protocol:** If the timer reaches zero, **Still** automatically triggers emergency protocols, notifying your **Inner Circle** with your last known status.
* **👥 Inner Circle Management:** Securely store and manage your most trusted contacts who will receive alerts during a protocol violation.
* **🔋 Battery Optimization Guard:** Built-in intelligence to bypass Android "Doze Mode" and App Standby, ensuring your safety timer never stops.
* **☢️ Nuclear Wipe:** A high-security feature to instantly purge all PII (Personally Identifiable Information), contact logs, and safety history with a single tap.

---

## 🏗️ Architecture

**Still** is engineered following **Clean Architecture** principles, ensuring a robust, scalable, and maintainable codebase that can be trusted in critical situations.

### 📂 Layered Structure

* **🏛️ Domain Layer:** The core of the app. Contains platform-independent business logic, `UseCases` (e.g., `StartTimerUseCase`, `TriggerZeroHourUseCase`), and Repository interfaces.
* **💾 Data Layer:** Implements repository interfaces. Handles data via **Encrypted DataStore** for local persistence and **Ktor** for secure remote emergency dispatching.
* **🎨 Presentation Layer:** Manages the UI using **Jetpack Compose**. It follows the **MVI (Model-View-Intent)** pattern for predictable, reactive state management.

### 🔄 MVI UI Flow

The app uses a strict unidirectional data flow:

1. **User Intent:** (e.g., *Click Check-In*)
2. **ViewModel:** Processes intent and orchestrates business logic.
3. **State:** ViewModel emits a new immutable state.
4. **UI:** Compose UI renders the state instantly.

---

## 🛠️ Tech Stack & Libraries

* **🌍 Platform:** Android Native (Kotlin)
* **🎨 UI:** Jetpack Compose (Glassmorphism / Twilight Theme)
* **💉 DI:** Koin (Lightweight Dependency Injection)
* **📡 Networking:** Ktor Client (Asynchronous HTTP)
* **⏰ Background:** WorkManager & AlarmManager (High-precision timing)
* **🔐 Security:** Jetpack Security (Crypto) & DataStore
* **📱 Analytics:** Firebase Analytics & Crashlytics
* **📳 Haptics:** Standardized `LocalHapticFeedback` for tactile assurance

---

## 📱 UI Flow Overview

1. **Splash Screen:** Initiates background safety protocols and setup.
2. **The Safety Contract:** A premium onboarding experience defining the shared protocols of the app.
3. **The Timer Dial:** The central hub showing your remaining "Stillness" and check-in actions.
4. **Profile Setup:** Step-by-step configuration of your identity and your "Inner Circle."
5. **Settings:** Control over intervals, permissions, and the "Nuclear Wipe" protocol.

---

## 🚀 Getting Started

### Prerequisites

* Android Studio Ladybug (2024.2.1) or higher.
* JDK 17 or 21.
* A physical Android device (recommended for Haptic Feedback testing).

### Installation

1. **Clone the Repo:**
```bash
git clone https://github.com/thezayin/still-safety-timer.git

```


2. **Firebase Setup:** Place your `google-services.json` in the `app/` directory.
3. **Sync & Build:** Let Gradle download dependencies and hit **Run**.

---

## 🤝 Contributing

Contributions to the **Stillness** protocols are welcome!

1. Fork the repository.
2. Create your feature branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -m 'feat: Add some AmazingFeature'`).
4. Push to the branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

---

## 📧 Contact

**Zain Shahid (thezayin)**
For professional inquiries regarding safety protocols or technical architecture:

* **Email:** [thezayenshahid@gmail.com]()
* **GitHub:** [@thezayin](https://github.com/thezayin)
