# Frontend Component Implementation Plan

To bring the Momentum app to life with our premium glassmorphic design system, we need to break the UI down into reusable React components. Here is the structured plan for what we need to build next:

## 1. Core Layout & Navigation
* **`Sidebar.tsx`**: A sleek side navigation menu to switch between Dashboard, Rewards, Statistics, and Settings.
* **`Layout.tsx`**: A wrapper component for authenticated routes that renders the Sidebar on the left and the main page content on the right.

## 2. Task Management (Next Immediate Step)
* **`TaskCreateModal.tsx`**: A floating, blurred glass modal containing a form to create a new task.
  * **Inputs**: Title, Priority (Low/Med/High).
  * **Type Toggle**: Switch between "One-Time Goal" and "Recurring Habit".
  * **Smart Inputs**: Date Picker (if One-Time), Recurrence Rule dropdown (if Recurring).
* **`TaskCard.tsx`**: Extract the existing task rendering logic from the Dashboard into a reusable component so it can be used on historical views as well.

## 3. The Rewards Store
* **`RewardsPage.tsx`**: The main view where you can spend your hard-earned coins.
* **`RewardCreateModal.tsx`**: A form for defining a real-life reward (e.g., "1 Hour Video Games", "Order Pizza") and assigning a coin cost.
* **`RewardCard.tsx`**: Displays a specific reward, its coin cost, and a glowing "Redeem" button that verifies your coin balance before deducting.

## 4. Analytics & Progress
* **`StatisticsPage.tsx`**: A dedicated view for diving deep into your productivity.
* **`CalendarHeatmap.tsx`**: A visual grid (similar to GitHub's contribution graph) showing your task completion intensity over the month.
* **`LedgerList.tsx`**: A clean, scrollable table displaying your historical XP and Coin transactions.

## 5. UI Primitives (Our Design System)
* **`Button.tsx`**: Standardized buttons (Primary gradient, Secondary outline, Danger).
* **`Badge.tsx`**: Small pill-shaped indicators for Priority tags, XP tags, and Categories.
* **`Input.tsx`**: Standardized text inputs and dropdowns with consistent border/focus states.
