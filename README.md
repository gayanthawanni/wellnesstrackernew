# WellnessTracker

Personal wellness manager that combines a Daily Habit Tracker, Mood Journal with emoji selector, Hydration reminders, and a simple 7‑day mood chart.

## Build & Run
1. Open the project in Android Studio Ladybug or newer.
2. Sync Gradle. Minimum SDK 24, target SDK 36.
3. Run on a device/emulator.

## Features
- Habits: Add/edit/delete daily habits. Tap Toggle to mark done today. Progress bar and badge show completion status for the current day.
- Mood journal: Add an entry with emoji and optional note. View as a list, share a 7‑day summary, and open the chart.
- Hydration reminder: Configure interval in minutes and enable switch. Uses `AlarmManager` + local notifications. Notification channel created for Android 8+.
- Chart: MPAndroidChart line chart shows counts of mood entries over the last 7 days.

## Data Persistence
No database is used. All data is persisted via `SharedPreferences` through `PrefsRepository` using JSON serialization with Gson:
- `key_habits`: List<Habit>
- `key_moods`: List<MoodEntry>
- `key_hydration_enabled`, `key_hydration_interval`: reminder settings

## Project Structure
- `model/` – `Habit`, `MoodEntry`
- `data/` – `PrefsRepository` for SharedPreferences
- `ui/` – Activities and Fragments (habits, mood, settings, chart)
- `util/` – `NotificationUtil`, `ReminderReceiver`

## Screenshots
- Placeholder 1: Habits screen
- Placeholder 2: Mood list and chart
- Placeholder 3: Settings hydration reminder


