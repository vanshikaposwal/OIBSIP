<div align="center">

# 📚 LibSphere — Digital Library Management System

### OIBSIP Java Developer Internship · Task 5

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Flyway](https://img.shields.io/badge/Flyway-Migration-CC0200?style=for-the-badge&logo=flyway&logoColor=white)](https://flywaydb.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-7C3AED?style=for-the-badge)](LICENSE)

A **full-stack digital library management system** built with Spring Boot, Spring Security (session-based), Spring Data JPA, Flyway migrations, and a vanilla HTML/CSS/JS frontend. It supports complete book catalog management, member borrowing workflows, automated fine calculation, reservation waitlists, and a role-separated admin panel.

[Features](#-features) · [Screenshots](#-screenshots) · [Tech Stack](#-tech-stack) · [Quick Start](#-quick-start) · [API Reference](#-rest-api-reference) · [Architecture](#-architecture)

</div>

---

## ✨ Features

### 👤 Member Portal
- 🔐 **Secure Authentication** — Session-based login/logout with BCrypt password hashing
- 📖 **Book Catalog Browsing** — Search, filter by category, paginated results
- 📦 **Self-Checkout** — Borrow books directly (max 5 active loans, 14-day term)
- 📋 **Reservation Waitlist** — Join queue when a book is out of stock; auto-fulfilled on return
- 💸 **Fine Tracking** — View overdue fines (₹5/day), one-click payment clearance
- 📨 **Support Queries** — Submit contact inquiries and track ticket status

### 🛡️ Admin Panel
- 📚 **Catalog Management** — Add, edit, delete books with quantity control
- 🔄 **Circulation Control** — Issue books on behalf of members, process returns
- 👥 **User Management** — View member accounts, assign roles, manage access
- 💰 **Fines Ledger** — Review all outstanding & paid fines across the library
- 🎫 **Support Tickets** — View and resolve member-submitted queries

### ⚙️ System Rules
| Rule | Value |
|---|---|
| Loan Duration | 14 days |
| Max Active Loans per Member | 5 books |
| Overdue Fine Rate | ₹5 / day |
| Block on Unpaid Fines | Yes (configurable) |
| Reservation Trigger | Only when `availableQuantity = 0` |
| Waitlist Fulfillment | Oldest reservation first (FIFO) |

---

## 📸 Screenshots

### 🏠 Home — Landing Page
![Home Page](docs/screenshots/home.jpg)
*Hero section with global search, live library stats, and feature highlights.*

---

### 🔑 Login Page
![Login Page](docs/screenshots/login.jpg)
*Clean sign-in form with quick demo login buttons for Admin and Member accounts.*

---

### 📊 Admin Dashboard
![Admin Dashboard](docs/screenshots/admin_dashboard.jpg)
*Real-time metrics: total books, active loans, outstanding fines, open tickets, and recent circulation activity.*

---

### 📚 Browse Catalog
![Browse Catalog](docs/screenshots/browse_catalog.jpg)
*Paginated book grid with search, category filter, availability badges, and one-click issue/waitlist actions.*

---

### 📖 My Books (Member)
![My Books](docs/screenshots/my_books.jpg)
*Member loan tracker with due-date countdowns, status badges (Active / Due Soon / Overdue), and return actions.*

---

### 🗂️ Manage Books (Admin)
![Admin Books](docs/screenshots/admin_books.jpg)
*Full inventory table with Add/Edit modal, quantity tracking, and delete controls.*

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.3.4 |
| **Security** | Spring Security (Session-based, BCrypt) |
| **Persistence** | Spring Data JPA + Hibernate |
| **Database** | MySQL 8.0 |
| **Migrations** | Flyway (V1 schema + V2 seed data) |
| **Build Tool** | Apache Maven |
| **Frontend** | Vanilla HTML5, CSS3, JavaScript (ES6+) |
| **UI Fonts & Icons** | Inter (Google Fonts), Bootstrap Icons |
| **Testing** | JUnit 5, Mockito, Spring MockMvc, H2 (in-memory) |

---

## 🚀 Quick Start

### Prerequisites

Make sure you have the following installed:
- **JDK 21+** → [Download](https://adoptium.net/)
- **Apache Maven 3.9+** → [Download](https://maven.apache.org/download.cgi)
- **MySQL 8.0+** → [Download](https://dev.mysql.com/downloads/mysql/)

### 1. Clone the Repository

```bash
git clone https://github.com/vanshikaposwal/JavaDev-Task5-DigitalLibraryManagementSystem.git
cd JavaDev-Task5-DigitalLibraryManagementSystem
```

### 2. Configure the Database

The application creates the database automatically (`createDatabaseIfNotExist=true`). Just ensure MySQL is running and set your credentials via environment variables **or** edit [`src/main/resources/application.yml`](src/main/resources/application.yml):

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/library_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=Asia/Kolkata
    username: root      # ← change if needed
    password: 1234      # ← change if needed
```

Or pass as environment variables at runtime:

```bash
export DB_URL=jdbc:mysql://localhost:3306/library_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=Asia/Kolkata
export DB_USER=root
export DB_PASSWORD=your_password
```

### 3. Build & Run

```bash
mvn clean package -DskipTests
java -jar target/digital-library-management-0.0.1-SNAPSHOT.jar
```

Or run directly with Maven:

```bash
mvn spring-boot:run
```

### 4. Open in Browser

```
http://localhost:8080
```

> **Flyway** will automatically create all tables and seed demo data on first startup.

---

## 👥 Demo Accounts (Pre-Seeded)

| Role | Email | Password | Access |
|---|---|---|---|
| **Administrator** | `admin@library.com` | `Admin@123` | Full admin panel + all member features |
| **Member** | `alice@example.com` | `Alice@123` | Standard borrower account |
| **Member** | `bob@example.com` | `Bob@12345` | Standard borrower account |
| **Member** | `carol@example.com` | `Carol@123` | Standard borrower account |
| **Member** | `david@example.com` | `David@123` | Standard borrower account |

> New users can also self-register at `/register.html`

---

## 🌐 Application Pages & Routes

| URL | Access | Description |
|---|---|---|
| `/` | Public | Home landing page with hero search and featured books |
| `/browse.html` | Public | Interactive book catalog with filters and issue actions |
| `/login.html` | Public | Sign-in page with quick demo buttons |
| `/register.html` | Public | New member registration |
| `/dashboard.html` | Authenticated | Admin metrics dashboard or Member loan overview |
| `/my-books.html` | Member | Borrowed books, due dates, countdowns, return actions |
| `/my-reservations.html` | Member | Active waitlists and cancellation |
| `/my-fines.html` | Member | Fine ledger and one-click payment |
| `/contact.html` | Public/Member | Support inquiry submission and ticket tracking |
| `/admin-books.html` | Admin | Book catalog management and stock control |
| `/admin-issues.html` | Admin | Circulation ledger and issue-on-behalf |
| `/admin-users.html` | Admin | Member accounts and role management |
| `/admin-fines.html` | Admin | Master fine ledger and receipts |
| `/admin-queries.html` | Admin | Support tickets and resolution |

---

## 📡 REST API Reference

All API endpoints are under `/api/`. Authenticated endpoints require an active session cookie.

### 🔐 Authentication `/api/auth`
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register a new member |
| `POST` | `/api/auth/login` | Public | JSON login, creates HTTP session |
| `GET` | `/api/auth/me` | Authenticated | Current user profile |
| `POST` | `/api/auth/logout` | Authenticated | Invalidate session |

### 📚 Books `/api/books`
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/books` | Public | Paginated search (`query`, `category`, `page`, `size`, `sortBy`, `dir`) |
| `GET` | `/api/books/{id}` | Public | Book details by ID |
| `POST` | `/api/books` | Admin | Create a new book entry |
| `PUT` | `/api/books/{id}` | Admin | Update book details and quantity |
| `DELETE` | `/api/books/{id}` | Admin | Remove a book from catalog |

### 🔄 Issues & Circulation `/api/issues`
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/issues/book/{bookId}` | Member | Self-borrow a book |
| `POST` | `/api/issues/admin/book/{bookId}/user/{userId}` | Admin | Issue on behalf of member |
| `POST` | `/api/issues/{id}/return` | Member | Return book (computes fines, fulfils waitlists) |
| `GET` | `/api/issues/my` | Member | Current user's active and past loans |
| `GET` | `/api/issues` | Admin | All library loans |

### 📋 Reservations `/api/reservations`
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/reservations/book/{bookId}` | Member | Join waitlist (only when `available = 0`) |
| `POST` | `/api/reservations/{id}/cancel` | Member | Cancel active reservation |
| `GET` | `/api/reservations/my` | Member | Current user's reservations |
| `GET` | `/api/reservations` | Admin | All waitlists |

### 💸 Fines `/api/fines`
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/fines/my` | Member | Current user's fines |
| `GET` | `/api/fines` | Admin | Master fine ledger |
| `POST` | `/api/fines/{id}/pay` | Member | Settle a fine |

### 🎫 Support Queries `/api/queries`
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/queries` | Public/Member | Submit a support query |
| `GET` | `/api/queries/my` | Member | Current user's submitted queries |
| `GET` | `/api/queries` | Admin | All support tickets |
| `POST` | `/api/queries/{id}/resolve` | Admin | Mark ticket as resolved |

---

## 🏗️ Architecture

This project follows standard **Spring Boot Layered Architecture**:

```
┌─────────────────────────────────────────┐
│     Frontend (HTML / CSS / JS)          │
│   Vanilla SPA-style pages + Fetch API   │
└────────────────────┬────────────────────┘
                     │ HTTP JSON
┌────────────────────▼────────────────────┐
│          Controllers (@RestController)  │
│  Receives requests, validates input,    │
│  returns ResponseEntity<T>             │
└────────────────────┬────────────────────┘
                     │
┌────────────────────▼────────────────────┐
│          Services (@Service)            │
│  Business logic, fine calculations,     │
│  loan rules, @Transactional boundaries  │
└────────────────────┬────────────────────┘
                     │
┌────────────────────▼────────────────────┐
│       Repositories (@Repository)        │
│  JpaRepository interfaces, JPQL queries │
└────────────────────┬────────────────────┘
                     │
┌────────────────────▼────────────────────┐
│    Entities & Database (@Entity/MySQL)  │
│  books, users, book_issues, fines,      │
│  reservations, contact_queries          │
└─────────────────────────────────────────┘
```

### Project Package Structure

```
src/main/java/com/library/management/
├── config/
│   ├── SecurityConfig.java          # Spring Security config, BCrypt bean
│   ├── CustomUserDetailsService.java # Loads user by email for auth
│   ├── LibraryProperties.java       # Binds library.* from application.yml
│   └── DataSeeder.java              # Provisions demo accounts on startup
├── controller/                      # REST controllers (one per domain)
├── service/                         # Business logic and transaction management
├── repository/                      # Spring Data JPA interfaces
├── entity/                          # JPA entities with builder pattern
├── dto/                             # Request/Response record classes
└── exception/                       # GlobalExceptionHandler, custom exceptions
```

### Key Design Decisions

| Concern | Approach |
|---|---|
| **Authentication** | Spring Security session (HttpSession) with BCrypt |
| **Authorization** | `ROLE_ADMIN` and `ROLE_USER`, endpoint-level guards |
| **Data Integrity** | `@Transactional` on service methods, DB unique constraints |
| **Input Validation** | Bean Validation (`@NotBlank`, `@Email`, `@Min`, etc.) |
| **Error Handling** | `@RestControllerAdvice` → uniform JSON error body |
| **DB Migrations** | Flyway V1 (schema) + V2 (seed) — no manual SQL needed |
| **Testing** | Unit tests (Mockito) + MockMvc integration tests (H2) |

---

## 🧪 Running Tests

```bash
mvn clean verify
```

Tests run against an in-memory **H2 (MySQL mode)** database with Flyway disabled. The suite covers:

- ✅ User registration and duplicate email rejection
- ✅ Book inventory limits and quantity consistency
- ✅ Loan rules: 14-day duration, 5-book max, duplicate loan blocks, zero-stock blocks
- ✅ Overdue fine computation (₹5/day) and FIFO waitlist fulfillment
- ✅ Reservation zero-stock requirement and cancellation
- ✅ MockMvc security: `401` unauthenticated, `403` user-forbidden on admin endpoints, `201` admin creation

---

## ⚙️ Configuration Reference

```yaml
# application.yml — key settings
library:
  loan-days: 14           # Loan duration in days
  fine-per-day: 5         # Fine rate in ₹ per day overdue
  max-active-issues: 5    # Max books a member can borrow at once
  block-on-unpaid-fines: true  # Block borrowing if member has unpaid fines

server:
  port: ${PORT:8080}
  servlet:
    session:
      timeout: 30m        # Session expiry
```

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m 'feat: add your feature'`
4. Push to the branch: `git push origin feature/your-feature`
5. Open a Pull Request

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<div align="center">

Made with ❤️ by **Vanshika Poswal** for the **Oasis Infobyte (OIBSIP)** Java Developer Internship

[![GitHub](https://img.shields.io/badge/GitHub-vanshikaposwal-181717?style=for-the-badge&logo=github)](https://github.com/vanshikaposwal)

</div>
