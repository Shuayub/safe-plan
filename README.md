# 📱 Safe Plan

**Safe Plan** is an Android application designed to provide users with a secure environment for safety planning, emergency response access, and customizable reminders. The app features onboarding, authentication, support resources, and logic-driven surveys to guide users through safety-related decisions.

---

## 🔑 Environment Variables & Testing Credentials

To fully run and test the app, ensure the following:

- `google-services.json` file is added under `/app/` for Firebase features (authentication, Firestore).
- Firebase project includes:
  - Authentication enabled (Email/Password, Google Sign-In)
  - Firestore database configured
- 
- The initial Screen is disguised with a weatherapp, in order to go to the safe-plan app, 
- type login into the textbox and press search
- 
- Example testing account:
Email: test@gmail.com
Password: 123456


> ⚠️ Replace these values with your own credentials or testing accounts. Do not commit real credentials.

---

## 🌟 Features

- 🔐 **PIN & Google Sign-In Authentication**
- 🧭 **Multi-step Onboarding & Registration**
- 📝 **Survey System** with question logic and tip responses from JSON
- 💬 **Support Resources** auto-loaded from local JSON
- 🔔 **Reminders** with Android notification support
- 📁 **Safety Plan Documents** management
- 🚪 **Emergency Exit Button** that quickly closes the app
- 🌤️ **Weather Screen** (optional location/weather feature)
- 🧱 Modular structure using Activities, Fragments, and DialogFragments

---

## 📁 Assets Overview

- `questions_and_tips.json` – Survey flow, logic, and safety tips
- `support.json` – Support resources such as shelters, helplines, etc.

---

## 👥 Team Member Contributions

### 🧑‍💻 Sami
- Home Button navigation and structure
- Document upload & management

### 🧑‍💻 Saabir
- Support screen UI and logic
- Reminder system with Android notifications

### 🧑‍💻 Jasjeet
- Registration screen (multi-step)
- Login screen (including PIN logic)

### 🧑‍💻 Additya
- Reminder backend support and logic
- Emergency Exit button
- Weather screen

### 🧑‍💻 Shuayub
- Tips system linked to survey answers
- Survey engine and logic flow using JSON

---

## 🛠️ Setup Instructions

1. **Clone the repository**:
 git clone https://github.com/your-username/safe-plan.git
 cd safe-plan
2. Open the project in Android Studio

3. Add required files:
 google-services.json in app/

4. Sync Gradle and build the project

5. Run on emulator or physical device