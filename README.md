<div align="center">
  <img src="frontend/public/favicon.svg" alt="Momentum Logo" width="120" />

  <h1> Momentum</h1>

  <p>
    <strong>Don't just tell me what I have to do. Show me what I've actually accomplished.</strong>
  </p>

  <p>
    A dark-themed, glass-morphism gamified habit and task tracker built to help you build a persistent record of productivity over time.
  </p>

  <p>
    <a href="https://momentum-taskapp.vercel.app">
      <img src="https://img.shields.io/badge/🔴_Live_Demo-momentum--taskapp.vercel.app-FF3B30?style=for-the-badge" alt="Live Demo" />
    </a>
  </p>

  <div>
    <img src="https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB" alt="React" />
    <img src="https://img.shields.io/badge/TypeScript-007ACC?style=for-the-badge&logo=typescript&logoColor=white" alt="TypeScript" />
    <img src="https://img.shields.io/badge/Vite-B73BFE?style=for-the-badge&logo=vite&logoColor=FFD62E" alt="Vite" />
    <img src="https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot" alt="Spring Boot" />
    <img src="https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=java&logoColor=white" alt="Java" />
    <img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
  </div>
</div>

---

## ✨ Features

- **🎮 Gamified Progression**: Earn **XP** and **Coins** for completing tasks. Level up as you build consistency.
- **🎁 Custom Rewards**: Spend your hard-earned coins in the shop to unlock personal real-life rewards (e.g., "1 Hour of Gaming" or "Buy a Coffee").
- **🔥 Streaks & Consistency**: Visual github-style contribution calendars and progress bars keep you motivated.
- **📱 Progressive Web App (PWA)**: Installable directly to your phone's home screen with a mobile-first UI and bottom-navigation bar.
- **🎨 Liquid Glass Aesthetics**: A stunning dark-mode interface featuring blurred backdrops, glowing borders, and smooth Framer Motion animations.

## 🏗️ Architecture

Momentum is a full-stack monorepo:

- **`/frontend`**: A **React + Vite** SPA written in **TypeScript**. It utilizes `framer-motion` for fluid animations and custom vanilla CSS for its Apple-inspired glass-morphism design.
- **`/backend`**: A modular **Spring Boot (Java 21)** REST API. Features state-less JWT authentication and data persistence via Spring Data JPA.
- **Database**: Powered by a managed **Supabase PostgreSQL** instance.

## 🚀 Deployment

The application is fully configured for production deployment:

- **Frontend**: Hosted on [Vercel](https://vercel.com). Includes `vercel.json` and `_redirects` for flawless client-side routing.
- **Backend**: Containerized via a multi-stage `Dockerfile` and hosted on [Render](https://render.com). Configured to dynamically bind to cloud environment ports.

## 💻 Local Development

### Prerequisites
- Node.js 18+
- Java 21+
- Maven

### Running the Backend

1. Navigate to the backend folder:
   ```bash
   cd backend
   ```
2. Start the Spring Boot server:
   ```bash
   ./mvnw spring-boot:run
   ```
   *The backend will automatically start on `http://localhost:8080`. (For local development without Supabase, you can swap the PostgreSQL configuration in `application.yml` for an H2 in-memory database).*

### Running the Frontend

1. Navigate to the frontend folder:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the Vite development server:
   ```bash
   npm run dev
   ```
   *The frontend will start on `http://localhost:5173` and will proxy API requests to the backend.*

---
<div align="center">
  <sub>Built with ❤️ and Momentum.</sub>
</div>
