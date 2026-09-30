# Momentum App Features

Momentum is a personal productivity application designed with gamification mechanics to build and reward consistency.

## Core Mechanics

### 1. Daily Task Management
- **One-Time Tasks**: Tasks tied to a specific calendar date (e.g., "Submit assignment").
- **Recurring Tasks**: Habits and routines that repeat automatically (e.g., "Daily DSA Practice").
- **Task History**: Completed and missed tasks are permanently logged, creating an accurate productivity timeline.

### 2. Gamification System
- **XP (Experience Points)**: Represents permanent progress. Every completed task awards XP, which accumulates to level up your profile. XP transactions are immutable.
- **Leveling**: A configurable tier system based on lifetime XP earned. 
- **Coins**: A spendable currency earned alongside XP for completing tasks.

### 3. Rewards & Store
- **Custom Rewards**: Users define their own real-world or digital rewards (e.g., "Watch a movie" - 100 coins).
- **Redemption Ledger**: Spending coins securely logs a transaction, ensuring you have a history of your redeemed rewards.

### 4. Streaks
- **Overall Consistency**: Tracks how many consecutive days you have maintained baseline productivity.
- **Task-Specific Streaks**: Tracks consistency on individual recurring tasks (e.g., "14 days of Backend Study").

### 5. Analytics & Insights
- **Daily Summaries**: Visual indicators of daily performance (Excellent, Good, Average).
- **Calendar Heatmap**: (Upcoming) A visual month-view of your productivity density.
- **Ledgers**: Transparent, financial-style ledgers for XP and Coin transactions to prove exactly how you earned your progress.

## System Characteristics
- **Multi-tenant**: The system natively isolates user data, allowing multiple users securely.
- **Idempotent Rewards**: Prevents duplicate rewards for network retries or double-clicks.
- **Strict Timezone Handling**: Calculates daily streaks and boundaries based on the user's local timezone.
