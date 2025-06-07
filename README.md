# 🕒 Job Scheduling System

A Spring Boot-based backend service to schedule and manage recurring jobs (Hourly, Daily, Weekly) with persistence using PostgreSQL. The system supports automatic reloading and execution of tasks on server restart using `ScheduledExecutorService`.

## 📌 Features

- Schedule jobs to run:
  - Every hour at a specific minute
  - Daily at a fixed time
  - Weekly on specific days at a given time
- Automatically reloads scheduled tasks on application start
- Cancel scheduled jobs
- View all active jobs
- Built with clean and modular code using Spring Boot

## 🛠️ Tech Stack

- Java 17
- Spring Boot 3.x
- Spring Web
- Spring Data JPA
- PostgreSQL
- Maven

---

## 🧩 API Endpoints

### ✅ Create a Job

```http
POST http://localhost:8080/api/jobs/schedule
GET http://localhost:8080/api/jobs/list
DELET http://localhost:8080/api/jobs/cancel/{jobId}
