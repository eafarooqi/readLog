# ReadLog

ReadLog is a simple Android book reading journal app. It helps users save books they are reading, want to read, or have already finished. Each entry can include the book title, author, reading status, current page, rating, Open Library link, favourite quote, and personal notes.

The app was developed as part of the **DLBCSEMSE02 – Mobile Software Engineering II** project.

## Features

- View all saved book entries on the main screen
- Add a new book journal entry
- Open a detailed view of a saved book
- Edit an existing book entry
- Delete an entry with confirmation
- Store entries locally on the device
- Validate required input fields before saving

## Technologies Used

- Kotlin
- Android Studio
- XML layouts
- Room database / SQLite
- RecyclerView
- JUnit 4
- MockK
- kotlinx-coroutines-test

## Project Structure

The app is divided into simple parts:

- `data` – contains the Room entity, DAO, and database class
- `repository` – contains the repository used between the database and activities
- `ui` – contains the Android activities and RecyclerView adapter
- `util` – contains validation logic
- `test` – contains unit tests for validation and repository behaviour

## Requirements

Before running the project, make sure you have:

- Android Studio installed
- An Android emulator or a physical Android device
- Internet connection for the first Gradle sync

## How to Run the App

1. Clone the repository:

```bash
git clone https://github.com/eafarooqi/readLog.git
```

2. Open the cloned project folder in Android Studio.

3. Wait for Android Studio to finish the Gradle sync.

4. Choose a device:
   - Start an Android emulator from **Device Manager**, or
   - Connect a real Android phone with **USB debugging** enabled.

5. Select the device in Android Studio and click **Run**.

6. After the app starts, use the plus button to add a new book entry.