# Log4Fit - Exercise Tracker Project Requirements

## Project Overview
Log4Fit is a backend application for an exercise tracking platform. The goal is to allow users to log their workouts, track their progress, and view statistics to stay motivated and achieve their fitness goals.

## Core Features (MVP)
These are the essential features required for the initial release:

1.  **User Management**
    *   User registration (with password and email validation).
    *   User login (JWT-based authentication).
    *   Token refresh mechanism.
    *   User profile management.

2.  **Exercise Management**
    *   **Exercise Library**: A predefined list of exercises, categorized for easy searching.
        *   **Strength Training (Gym)**: Squat, Bench Press, Deadlift, Overhead Press, Barbell Row.
        *   **Outdoor Gym / Calisthenics**: Push-ups, Pull-ups, Dips, Inverted Row, Vertical Press, Leg Raises, Sit-ups.
        *   **Cardio**: Running, Cycling, Swimming, Rowing.
    *   **Custom Exercises**: Ability for users to create their own exercises.

3.  **Workout Tracking**
    *   **Start Workout**: Users can start a workout session.
    *   **Log Sets**: Users can log sets for an exercise (reps, weight, RPE, rest time).
    *   **Finish Workout**: Users can finish and save a workout session.
    *   **Workout History**: Users can view a list of their past workouts.

4.  **Statistics & Progress**
    *   **Volume Tracking**: Track total volume lifted over time.
    *   **Personal Records (PRs)**: Automatically track and notify users of new PRs (1RM, max reps, etc.).
    *   **Frequency**: Track workout frequency (workouts per week/month).

## Suggested Features (Future Roadmap)
These features are planned for future updates to enhance the user experience:

1.  **Workout Plans & Routines**
    *   Create and save reusable workout routines (e.g., "Push Day", "Leg Day").
    *   Share routines with other users.
    *   Follow pre-made programs (e.g., 5x5, PPL).

2.  **Social & Community**
    *   **Friend System**: Follow friends and see their workouts.
    *   **Leaderboards**: Compete with friends on volume or frequency.
    *   **Feed**: A social feed of friends' activities.

3.  **Advanced Analytics**
    *   **Muscle Group Breakdown**: Visual representation of trained muscle groups (heatmaps).
    *   **Progress Charts**: Graphs showing strength progression over time for specific exercises.
    *   **Body Measurements**: Track weight, body fat %, and body measurements (bicep size, waist, etc.).

4.  **Gamification**
    *   **Achievements**: Badges for milestones (e.g., "Lifted 10,000kg total", "Worked out 7 days in a row").
    *   **Streaks**: Track daily or weekly workout streaks.

5.  **Integration**
    *   **Wearables**: Integration with Apple Health, Google Fit, or smartwatches.
    *   **Export/Import**: Ability to export data to CSV/JSON.

6.  **Media**
    *   **Progress Photos**: Securely store and compare progress photos.
    *   **Exercise Videos**: Link to or upload videos for form checks.

## Technical Requirements
*   **Backend**: Kotlin with Ktor.
*   **Database**: PostgreSQL (Production), H2 (Local Development).
*   **Authentication**: JWT (JSON Web Tokens).
*   **Deployment**: Docker & Docker Compose.
*   **Documentation**: OpenAPI / Swagger.
