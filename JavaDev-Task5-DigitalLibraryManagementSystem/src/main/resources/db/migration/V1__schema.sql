-- V1__schema.sql  –  Digital Library Management System DDL
-- Managed by Flyway; Hibernate ddl-auto=validate

SET FOREIGN_KEY_CHECKS = 0;

-- ──────────────────────────────────────────────
-- 1. users
-- ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    name          VARCHAR(120) NOT NULL,
    email         VARCHAR(180) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          ENUM('ROLE_USER','ROLE_ADMIN') NOT NULL DEFAULT 'ROLE_USER',
    phone         VARCHAR(20),
    address       TEXT,
    enabled       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ──────────────────────────────────────────────
-- 2. books
-- ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS books (
    id                 BIGINT        NOT NULL AUTO_INCREMENT,
    title              VARCHAR(300)  NOT NULL,
    author             VARCHAR(200)  NOT NULL,
    isbn               VARCHAR(20)   NOT NULL,
    category           VARCHAR(80)   NOT NULL,
    publisher          VARCHAR(200),
    publish_year       SMALLINT,
    description        TEXT,
    total_quantity     INT           NOT NULL DEFAULT 1,
    available_quantity INT           NOT NULL DEFAULT 1,
    created_at         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_books_isbn (isbn),
    CONSTRAINT chk_books_quantity CHECK (available_quantity >= 0 AND available_quantity <= total_quantity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ──────────────────────────────────────────────
-- 3. book_issues
-- ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS book_issues (
    id          BIGINT   NOT NULL AUTO_INCREMENT,
    user_id     BIGINT   NOT NULL,
    book_id     BIGINT   NOT NULL,
    issue_date  DATE     NOT NULL,
    due_date    DATE     NOT NULL,
    return_date DATE,
    status      ENUM('ISSUED','RETURNED') NOT NULL DEFAULT 'ISSUED',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_issues_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_issues_book FOREIGN KEY (book_id) REFERENCES books(id),
    -- prevent same user from having two active issues of the same book
    CONSTRAINT uq_active_issue UNIQUE (user_id, book_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ──────────────────────────────────────────────
-- 4. fines
-- ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS fines (
    id         BIGINT         NOT NULL AUTO_INCREMENT,
    issue_id   BIGINT         NOT NULL,
    user_id    BIGINT         NOT NULL,
    amount     DECIMAL(10,2)  NOT NULL,
    paid       BOOLEAN        NOT NULL DEFAULT FALSE,
    paid_at    DATETIME,
    created_at DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_fines_issue (issue_id),
    CONSTRAINT fk_fines_issue FOREIGN KEY (issue_id) REFERENCES book_issues(id),
    CONSTRAINT fk_fines_user  FOREIGN KEY (user_id)  REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ──────────────────────────────────────────────
-- 5. reservations
-- ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS reservations (
    id              BIGINT   NOT NULL AUTO_INCREMENT,
    user_id         BIGINT   NOT NULL,
    book_id         BIGINT   NOT NULL,
    status          ENUM('ACTIVE','FULFILLED','CANCELLED') NOT NULL DEFAULT 'ACTIVE',
    reservation_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fulfilled_at    DATETIME,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_res_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_res_book FOREIGN KEY (book_id) REFERENCES books(id),
    -- one active reservation per user per book
    CONSTRAINT uq_active_reservation UNIQUE (user_id, book_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ──────────────────────────────────────────────
-- 6. contact_queries
-- ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS contact_queries (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    user_id    BIGINT,
    name       VARCHAR(120) NOT NULL,
    email      VARCHAR(180) NOT NULL,
    subject    VARCHAR(300) NOT NULL,
    message    TEXT         NOT NULL,
    status     ENUM('OPEN','RESOLVED') NOT NULL DEFAULT 'OPEN',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_cq_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
