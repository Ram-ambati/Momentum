# Momentum

Momentum is a gamified daily task and progress application designed to answer the fundamental question: *Don't just tell me what I have to do. Show me what I've actually accomplished.* 

This project combines task management with gamification (XP, coins, streaks, levels, and customizable rewards) to build a persistent record of productivity over time.

## Project Structure

This is a monorepo containing both the backend API and the frontend client, along with project documentation.

- `/backend` - The Spring Boot (Java 21) backend providing a RESTful API and PostgreSQL persistence.
- `/frontend` - (Coming Soon) The React + TypeScript frontend web client.
- `/docs` - Project documentation, architecture decisions, and feature specifications.

## Quick Start

### Backend

To run the backend locally:

1. Navigate to the `backend` directory:
   ```bash
   cd backend
   ```
2. Start the Spring Boot application (using the H2 in-memory test database by default):
   ```bash
   ./mvnw spring-boot:run
   ```

### Frontend

*Frontend development is scheduled for the next phase.*

## Architecture Summary

- **Backend**: Modular monolith built with Spring Boot, Spring Data JPA, and Spring Security.
- **Frontend**: Designed for React and TypeScript.
- **Database**: PostgreSQL (Production) / H2 (Local testing).

For more details, please see the [Features Documentation](docs/features.md) and [Plan](Plan.md).
