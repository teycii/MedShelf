# MedShelf

## A Personal Medical Document Library and Emergency Access Mobile Application

MedShelf is an Android mobile application developed to help users organize, store, and manage important medical documents in one accessible place. It is designed for individuals as well as caretakers or family members who need to manage medical records for their loved ones.

The application focuses on making medical documents easier to retrieve during checkups, consultations, follow-up appointments, and urgent situations where records may be needed quickly.

---

## Project Information

| Information | Details |
|---|---|
| Project Title | MedShelf: A Personal Medical Document Library and Emergency Access Mobile Application |
| Course | IT112 – Mobile Application |
| Program | Bachelor of Science in Information Technology |
| Section | BSIT 2C |
| Institution | Camarines Norte State College |

---

## Developer

**Antoinette Stacy C. Lurcha**  
BSIT 2C  
Camarines Norte State College  

**Role:** Sole Developer

Responsible for the conceptualization, user interface design, application development, local database implementation, testing, and documentation of the MedShelf mobile application.

---

## Introduction

Medical documents such as prescriptions, laboratory results, medical certificates, vaccination records, and consultation records are often kept in physical folders, phone galleries, or scattered digital files. Because of this, users may have difficulty finding the correct document when it is needed.

MedShelf provides a centralized mobile application where users can record, organize, and access medical document information according to the person who owns the record. The system may be used for personal medical files or for managing the records of family members under the user's care.

---

## Problem Statement

Managing medical documents manually can become difficult, especially when records belong to multiple family members or need to be retrieved immediately. Paper documents may be misplaced, while digital files may become difficult to identify when stored without proper labels or organization.

MedShelf addresses this problem by providing a simple and organized mobile-based medical document library that allows users to:

- Store document information in a structured format.
- Assign medical documents to the appropriate family member or patient.
- View saved documents in one application.
- Add reminders for important medical-related tasks.
- Retrieve records more efficiently when needed.

---

## Objectives

### General Objective

To develop an Android mobile application that helps users organize and manage personal or family medical documents through a simple and accessible digital library.

### Specific Objectives

The application aims to:

1. Provide a user-friendly interface for managing medical documents.
2. Allow users to add and manage family members or patients.
3. Enable documents to be associated with their correct owner.
4. Provide reminder functionality for medical-related activities.
5. Store application data locally using a database.
6. Present medical records through an organized dashboard and document list.
7. Support faster retrieval of stored medical document information.

---

## Target Users

MedShelf is intended for:

- Individuals organizing their own medical records.
- Parents managing the records of their children.
- Family members assisting elderly relatives.
- Caretakers managing records for people under their care.
- Users who need an organized way to access medical document information.

---

## Main Features

### 1. Dashboard

The dashboard serves as the main screen of the application. It provides users with a clean overview of the system and quick access to major functions such as medical documents, family members, and reminders.

### 2. Medical Document Management

Users can add and manage medical document records inside the application.

Each document may contain the following information:

- Document name
- Document type
- Owner or patient
- Document date and time
- Clinic or hospital
- Notes
- Attached file or image reference
- Record creation timestamp

Examples of medical documents that may be recorded include:

- Prescriptions
- Laboratory results
- Medical certificates
- Vaccination records
- Consultation records
- Diagnostic reports
- Imaging results
- Other medical-related documents

### 3. Family Member Management

Users can add family members or patients whose medical documents will be stored in the application.

This feature helps organize records properly, especially when one user is managing medical documents for multiple people.

### 4. Reminder Management

Users can add medical-related reminders to help monitor important schedules or tasks.

Examples include:

- Medical appointments
- Follow-up checkups
- Laboratory schedules
- Medicine-related reminders
- Submission or renewal of medical documents

### 5. Organized Navigation

The application uses a simple mobile navigation structure to allow users to move between important sections of the app easily.

---

## Application Modules

| Module | Description |
|---|---|
| Dashboard | Displays the main overview and access points of the application. |
| Documents | Displays saved medical document records. |
| Add Document | Allows the user to enter and attach medical document information. |
| Family Members | Allows the user to add and manage patients or family members. |
| Reminders | Allows the user to create and manage medical-related reminders. |

---

## Technologies Used

| Technology | Purpose |
|---|---|
| Android Studio | Integrated development environment used to develop the application |
| Kotlin | Main programming language of the mobile application |
| Jetpack Compose | Framework used to create the user interface |
| Room Database | Local database used for saving application records |
| Material Design 3 | UI components and visual styling |
| Android Notification / Alarm Functions | Used for reminder-related functionality |

---

## Data Stored by the Application

### Document Records

The application manages medical document information such as:

- Document ID
- Document name
- Document type
- Owner
- Document date
- Clinic or hospital
- Notes
- File URI
- Creation timestamp

### Family Member Records

The application stores information about each family member or patient added by the user so that medical documents can be properly grouped and identified.

### Reminder Records

The application stores reminder information for medical-related tasks or schedules created by the user.

---

## System Workflow

1. The user opens the MedShelf application.
2. The dashboard displays the available functions.
3. The user may add a family member or patient.
4. The user selects the option to add a medical document.
5. The user enters the document details and selects its owner.
6. The medical document record is saved in the application.
7. The user may add reminders for medical schedules or tasks.
8. Saved documents and reminders can be viewed and managed through the corresponding application screens.

---

## Installation and Setup

### Requirements

To open and run the project, the following are required:

- Android Studio
- Android SDK
- Android Emulator or Android mobile phone
- USB debugging enabled when using a physical device

### Steps to Run the Application

1. Open **Android Studio**.
2. Select **Open**.
3. Locate and open the MedShelf project folder.
4. Wait for the Gradle synchronization process to complete.
5. Connect an Android phone through USB debugging or start an Android Emulator.
6. Click the **Run** button.
7. Choose the target device.
8. Wait for the application to install and open.

---

## Building an APK Installer

To create an APK file that can be installed on an Android phone:

1. Open the MedShelf project in Android Studio.
2. Click **Build** from the top menu.
3. Select **Build Bundle(s) / APK(s)**.
4. Click **Build APK(s)**.
5. Wait for Android Studio to finish building the APK.
6. Click **Locate** when the build notification appears.

The generated APK file is commonly located at:

```text
app/build/outputs/apk/debug/app-debug.apk
