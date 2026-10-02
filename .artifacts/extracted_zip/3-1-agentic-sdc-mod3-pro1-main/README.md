# Document Assistant - Android + Python

Complete Android Studio project for a document upload and vector-style search application.

## Android app
- Kotlin + XML layouts
- Android ViewBinding
- Material Components
- RecyclerView
- File picker
- Local document storage
- On-device hashed vector search (works without an API key)
- Dark mode resources

Supported text/code files: TXT, MD, CSV, JSON, XML, KT, JAVA, PY.

## Python backend
The `python_backend` folder contains an optional Flask service using TF-IDF and cosine similarity. It is independent from the Android app and does not require an external AI API key.

## Open in Android Studio
1. Extract the ZIP.
2. Open the extracted root folder (`DocumentAssistant`).
3. Let Gradle sync.
4. Select an emulator or USB Android phone.
5. Run the `app` configuration.

## Run Python backend
Windows: double-click `scripts/run_backend.bat`, or follow `python_backend/README.md`.

## Project structure

```text
DocumentAssistant/
├── app/
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/project/handson1/
│       │   ├── MainActivity.kt
│       │   ├── data/
│       │   ├── ui/
│       │   └── utils/
│       └── res/
│           ├── drawable/
│           ├── layout/
│           ├── mipmap-*/
│           ├── values/
│           ├── values-night/
│           └── xml/
├── gradle/
├── python_backend/
└── scripts/
```
