# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A WeChat mini-program for displaying loan product information with a Spring Boot backend. The system provides loan product listings, consultant contacts, user favorites, browsing history, and feedback functionality.

## Architecture

```
CreditMiniProgram/
├── backend/           # Spring Boot 3.2 REST API
│   └── src/main/java/com/credit/miniapp/
│       ├── controller/   # REST endpoints (user & admin APIs)
│       ├── service/      # Business logic
│       ├── repository/   # MyBatis Plus data access
│       ├── entity/       # Data models
│       ├── dto/         # Data transfer objects
│       ├── config/      # Spring configuration
│       ├── security/    # JWT authentication
│       └── util/        # Utilities (JWT, crypto)
├── frontend/          # WeChat mini-program (native)
│   ├── pages/
│   │   ├── index/    # Product list with filters
│   │   ├── mine/     # User profile & menu
│   │   ├── favorites/ # Saved products
│   │   └── history/  # Browsing history
│   └── utils/        # API client & helpers
└── docker-compose.yml
```

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.2, MyBatis Plus 3.5, MySQL 8.0, Redis
- **Frontend**: Native WeChat mini-program (WXML/WXSS/JS)
- **Auth**: JWT tokens, WeChat login integration

## Common Commands

### Backend
```bash
# Build
cd backend && mvn clean package -DskipTests

# Run locally (requires MySQL & Redis)
java -jar target/miniapp-backend-1.0.0.jar

# Run with Docker
cd /home/ubuntu/Code/CreditMiniProgram
docker-compose up -d
```

### Database
```bash
# Initialize database
sudo mysql -u root < backend/src/main/resources/init.sql

# Reset root password (for local dev)
sudo mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY ''; FLUSH PRIVILEGES;"
```

## Key Configuration

### Required Environment Variables
- `wechat.appid` - WeChat mini-program AppID
- `wechat.secret` - WeChat mini-program AppSecret
- `jwt.secret` - JWT signing key (change in production)
- `crypto.key` - 16-character AES encryption key

### API Endpoints (Port 8080)
- User: `/api/products`, `/api/banks`, `/api/consultant/{productId}`
- Auth: `/api/auth/login`
- User Data: `/api/favorites`, `/api/history`, `/api/feedback`
- Admin: `/api/admin/products`, `/api/admin/banks`, `/api/admin/consultants`
- Docs: `http://localhost:8080/swagger-ui.html`

## Development Notes

- Admin APIs require JWT authentication (except login)
- User APIs for favorites/history require JWT token in Authorization header
- Phone numbers are encrypted with AES in database, returned masked (e.g., `138****5678`)
- Use `@TableField(exist = false)` for non-database fields in MyBatis Plus entities

## Build Outputs to Ignore
- `backend/target/` - Maven build artifacts
- `*.jar` - Compiled Java packages
- `frontend/node_modules/` - NPM dependencies
