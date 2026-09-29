# Digital Library Management System (LibSphere)

A modern, full-stack **Digital Library Management System** engineered with **Spring Boot 3.x**, **Spring Data JPA**, **Spring Security**, **Flyway**, and a responsive **HTML5 / CSS3 / Vanilla JavaScript** frontend.

Built for **OIBSIP Java Developer — Task 5**.

---

## 🌟 Key Features

### 📚 Catalog & Inventory
- **Real-Time Stock Tracking**: Atomically tracked `total_quantity` and `available_quantity` on books to eliminate race conditions and negative inventory.
- **Instant Search & Multi-Criteria Filtering**: Search by title, author, or ISBN; filter by subject category (Java, Programming, Database, Web Development, Computer Science, Fiction, Science, History); sort by publication year or stock level.
- **Dynamic CSS/SVG Book Covers**: Generated on the client using palette-matched gradients and title initials — zero external image dependencies.

### 🔄 Lending & Circulation Rules (Single Source of Truth)
- **14-Day Loan Period**: Configurable via `library.loan-days`.
- **Automatic Due Dates**: Calculated upon issue (`issueDate + loanDays`).
- **Pessimistic Locking / Atomic Decrement**: Prevents over-issuing when multiple users borrow simultaneously.
- **Borrowing Limit**: Up to 5 active loans per member (`library.max-active-issues`).
- **Duplicate Loan Prevention**: Users cannot borrow the same book while an active issue exists.
- **Overdue Fine Engine**: Overdue status is derived deterministically (`dueDate < today`). Fines accrue at ₹5/day (`library.fine-per-day`) upon return, uniquely tied 1:1 to the issue record.
- **Unpaid Fines Block**: Users with outstanding fines are prevented from borrowing new volumes (`library.block-on-unpaid-fines`).
- **Smart Waitlist Reservations**: Users can reserve books when stock reaches 0. Returning a copy automatically fulfills the oldest active reservation in the queue.

### 🛡️ Security & Access Control
- **Session-Based Authentication**: Secure `HttpSession` with BCrypt password hashing.
- **Role-Based Access Control**:
  - `ROLE_ADMIN`: Master book inventory CRUD, issue-on-behalf, user management, fine receipts, support inquiry resolution.
  - `ROLE_USER`: Self-issuing, returning, reservation waitlisting, fine payments, inquiry submissions.
- **CSRF Protection**: Single Page Application (SPA)-compatible `CookieCsrfTokenRepository` (`XSRF-TOKEN` cookie, `X-XSRF-TOKEN` header attached via `apiFetch`).
- **No Leaked Stack Traces**: Global `@RestControllerAdvice` formats errors into `{timestamp, status, error, message, path, fieldErrors}`.

---

## 🎨 UI Design Tokens

- **Palette**:
  - Navy `#0F172A` (Sidebar & Primary text)
  - Indigo `#4F46E5` & Violet `#7C3AED` (Buttons & Accents)
  - Slate Background `#F8FAFC` & White Card `#FFFFFF`
  - Success `#16A34A`, Warning `#D97706`, Danger `#DC2626`
- **Typography**: Inter (Google Fonts) with system-ui fallback.
- **Components**: Shared toasts, confirmation dialogs, status badges, skeleton loaders, button spinners, and responsive drawer sidebar.
- **Responsive**: Fully responsive down to 360px viewports with mobile drawer overlay and horizontally scrollable tables.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Java 21+ (Compatible up to Java 25) |
| **Framework** | Spring Boot 3.3.4 |
| **Persistence** | Spring Data JPA / Hibernate 6.x |
| **Database** | MySQL 8.x (Flyway schema migrations) |
| **Testing DB** | H2 Database (`MODE=MySQL`) |
| **Security** | Spring Security 6.x (BCrypt, Session, CSRF) |
| **Frontend** | Vanilla JavaScript (ES6+), HTML5, CSS3, Bootstrap Icons |
| **Build Tool** | Apache Maven 3.9+ |

---

## 🚀 Getting Started

### 1. Prerequisites
- **Java 21 or higher**
- **Maven 3.9+**
- **MySQL 8.0+**

### 2. Configure Environment Variables
Create a local `.env` file from `.env.example`:
```bash
cp .env.example .env
```

Edit your credentials:
```properties
DB_URL=jdbc:mysql://localhost:3306/library_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=Asia/Kolkata&allowPublicKeyRetrieval=true
DB_USER=root
DB_PASSWORD=your_mysql_password
PORT=8080
```

### 3. Run Flyway & Start the Application
```bash
mvn spring-boot:run
```

On first startup:
1. Flyway runs `V1__schema.sql` to construct tables and constraints.
2. `DataSeeder` automatically provisions the admin account, sample users, and 12+ catalog books with BCrypt-hashed passwords.

---

## 👥 Demo Accounts (Pre-Seeded)

| Role | Email | Password | Purpose |
|---|---|---|---|
| **Administrator** | `admin@library.com` | `Admin@123` | Full administrative control & inventory management |
| **Member** | `alice@example.com` | `Alice@123` | Standard borrower account |
| **Member** | `bob@example.com` | `Bob@12345` | Standard borrower account |
| **Member** | `carol@example.com` | `Carol@123` | Standard borrower account |
| **Member** | `david@example.com` | `David@123` | Standard borrower account |

*New users can also register freely via `/register.html`.*

---

## 🧪 Running Automated Tests

Run the full suite of unit and MockMvc integration tests:
```bash
mvn clean verify
```

Tests run against an in-memory **H2 (MySQL mode)** database with Flyway disabled (`application-test.yml`), verifying:
- User registration and duplicate email rejection
- Book inventory limits and quantity consistency
- Loan rules: 14-day duration, 5-book max, duplicate loan blocks, zero-stock blocks
- Overdue return fine computation (₹5/day) & oldest-reservation fulfillment
- Reservation zero-stock requirement and cancellation
- MockMvc security rules: 401 unauthenticated, 403 user-forbidden from admin endpoints, 201 admin creation

---

## 🌐 Application Pages & Routes

| URL Path | Access | Description |
|---|---|---|
| `/` | Public | Home landing page with hero search and featured books |
| `/browse.html` | Public | Interactive book catalog with search, filters, details, and issue actions |
| `/login.html` | Public | Sign-in page with quick demo login buttons |
| `/register.html` | Public | Member registration form |
| `/dashboard.html` | Authenticated | Dynamic dashboard (Admin metrics or Member loans) |
| `/my-books.html` | Member | Borrowed books, due dates, countdowns, and return buttons |
| `/my-reservations.html` | Member | Active waitlists and cancellation |
| `/my-fines.html` | Member | Fine ledger and one-click payment clearance |
| `/contact.html` | Public / Member | Support inquiry submission & ticket status tracking |
| `/admin-books.html` | Admin | Book catalog management, Add/Edit modal, stock controls |
| `/admin-issues.html` | Admin | Master circulation ledger & issue-on-behalf dialog |
| `/admin-users.html` | Admin | Member accounts, roles, and suspension controls |
| `/admin-fines.html` | Admin | Fines ledger and payment receipts |
| `/admin-queries.html` | Admin | Support tickets viewer and resolution actions |

---

## 📡 REST API Reference

### Authentication (`/api/auth`)
- `POST /api/auth/register` — Register new member
- `POST /api/auth/login` — JSON login with session creation
- `GET /api/auth/me` — Current authenticated user profile
- `POST /api/auth/logout` — Invalidate session

### Books (`/api/books`)
- `GET /api/books` — Paginated search (params: `query`, `category`, `page`, `size`, `sortBy`, `dir`)
- `GET /api/books/{id}` — Book details
- `POST /api/books` — *(Admin)* Create book
- `PUT /api/books/{id}` — *(Admin)* Update book details & quantity
- `DELETE /api/books/{id}` — *(Admin)* Remove book

### Issues & Circulation (`/api/issues`)
- `POST /api/issues/book/{bookId}` — Member self-issue
- `POST /api/issues/admin/book/{bookId}/user/{userId}` — *(Admin)* Issue on behalf of member
- `POST /api/issues/{id}/return` — Return book (calculates fines & fulfills waitlists)
- `GET /api/issues/my` — Current user's issues
- `GET /api/issues` — *(Admin)* All library loans

### Reservations (`/api/reservations`)
- `POST /api/reservations/book/{bookId}` — Join waitlist (only if available = 0)
- `POST /api/reservations/{id}/cancel` — Cancel active reservation
- `GET /api/reservations/my` — Current user's reservations
- `GET /api/reservations` — *(Admin)* All waitlists

### Fines (`/api/fines`)
- `GET /api/fines/my` — Current user's fines
- `GET /api/fines` — *(Admin)* Master fine ledger
- `POST /api/fines/{id}/pay` — Pay / settle fine

### Support & Inquiries (`/api/queries`)
- `POST /api/queries` — Submit query (public or authenticated)
- `GET /api/queries/my` — Current user's submitted queries
- `GET /api/queries` — *(Admin)* All support tickets
- `POST /api/queries/{id}/resolve` — *(Admin)* Mark ticket as resolved
