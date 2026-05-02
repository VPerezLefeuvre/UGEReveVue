# UGEReveVue

A full-stack microservices social platform for developers to share code snippets, perform peer reviews, and execute test classes in a sandbox environment. Built with Spring Boot, Next.js, and Docker.

## 🛠 Tech Stack

- **Backend:** Java 17, Spring Boot 3, Spring Data JPA
- **Frontend:** Next.js 16, React, TypeScript
- **Database:** PostgreSQL
- **DevOps:** Docker, Docker Compose
- **Deployment:** Render, Vercel & Supabase

## 🚀 Getting Started

### Prerequisites
* **Docker Desktop** (Required for the database and containerized backend)
* **Java 17** & **Maven** (Optional, if running locally without Docker)

### Installation & Launch
1. **Clone the repository:**
   ```bash
   git clone https://github.com/VPerezLefeuvre/UGEReveVue.git
   cd UGEReveVue
   ```
2. **Start the infrastructure:**
   ```bash
   docker-compose up --build
   ```

3. **Verify the status:**
    - **Backend API:** `http://localhost:8080`
    - **Frontend Client:** `http://localhost:3000`
    - **Database:** Reachable at `localhost:5432`

## ⚙️ Environment Variables

The application uses environment variables to switch between local development and production environments seamlessly.

| Variable | Description | Local (Docker) | Production (Render/Supabase) |
| :--- | :--- | :--- | :--- |
| `SPRING_DATASOURCE_URL` | JDBC Connection URL | `jdbc:postgresql://db:5432/ugerevevue_db` | Injected by Render (Supabase URI) |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` | Injected by Render |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `password` | Injected by Render |
| `HIBERNATE_DDL` | Hibernate DDL mode | `update` | `validate` |
| `PORT` | Server port | `8080` | Dynamic (Assigned by Render) |

## 🗄 Database Access (Local)

To connect via **IntelliJ Database Tool**, **DBeaver**, or **TablePlus**:
- **Host:** `localhost`
- **Port:** `5432`
- **Database:** `ugerevevue_db`
- **Username:** `postgres`
- **Password:** `password`

## 🛠 Development Workflow

This project follows professional standards to ensure code quality and maintainability.

### Commit Conventions
We use [Conventional Commits](https://www.conventionalcommits.org/) to keep a clean and readable history:
- `feat:` for new features.
- `fix:` for bug fixes.
- `docs:` for documentation changes.
- `chore:` for maintenance (build, config, dependencies).
- `refactor:` for code improvements without functional changes.

### Branching Strategy
- `master`: Production-ready, stable code only.
- `dev`: Integration branch for new features.
- `feat/feature-name`: Topic branches for specific tasks.

## 📁 Project Structure

```text
UGEReveVue/
├── backend/
├── frontend/
├── docker-compose.yml
└── README.md
```