# Remaining Implementation Plan

We have successfully locked in the core loop: **Auth → Dashboard → Task Creation → Task Completion → XP/Coin Progression** running entirely on ultra-premium Apple Glassmorphism with Framer Motion spring physics. 

Here is exactly what is left to build to finish the application, as per the product requirements:

## 1. Global Navigation & Routing
* **Task:** Convert the text links in the Dashboard header (`DASHBOARD`, `TASKS`, `HISTORY`, `REWARDS`) into a persistent `<Layout />` shell using React Router.
* **Goal:** Allow seamless switching between the core app pages without losing state or animations.

## 2. Tasks Management Page (`/tasks`)
* **Task:** Build a page to manage **Task Templates**.
* **Features:**
  * Fetch and display active recurring task templates (`GET /api/tasks/templates`).
  * Edit existing templates (`PATCH /api/tasks/templates/{id}`).
  * Deactivate/Delete templates (`DELETE /api/tasks/templates/{id}`).
* **Rule:** Editing a template here must strictly alter *future* behavior without rewriting the immutable historical snapshots.

## 3. History & Calendar Page (`/history`)
* **Task:** Build a visual timeline of past productivity.
* **Features:**
  * **Calendar Heatmap:** A visual grid showing activity density using `GET /api/history/calendar`.
  * **Daily Breakdown:** Clicking a past date reveals the historical task snapshots for that day (showing exact XP/priority they had *at that time*).

## 4. Progress & Statistics Page (`/progress`)
* **Task:** Build deep-dive analytics.
* **Features:**
  * Tabs for `DAILY`, `WEEKLY`, and `MONTHLY` statistics.
  * **Ledger View:** A scrollable table showing exact transactions of every XP point and Coin earned.

## 5. Rewards Shop (`/rewards`)
* **Task:** Build the coin redemption store.
* **Features:**
  * Display current Coin Balance prominently.
  * **Reward Cards:** List custom rewards (e.g., "Movie Night - 250 Coins").
  * **Redemption Flow:** A physical "Redeem" button that verifies balance and triggers the backend API.
  * **Create Reward Modal:** Form to let you add new things you want to buy with your coins.

## 6. Recovery Tokens (Stretch)
* **Task:** If the backend streak recovery system is fully implemented, build the UI to view token balance and use a token to recover a red "missed" day.

---
### Next Immediate Step Recommendation
I recommend building the **Global Layout & Navigation** first so we have a place to put the rest of the pages, followed immediately by the **Tasks Management Page**.
