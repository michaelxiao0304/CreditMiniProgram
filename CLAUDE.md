# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A WeChat mini-program for displaying loan product information with a Spring Boot backend. The system provides loan product listings, consultant contacts, user favorites, browsing history, and feedback functionality.

## Architecture

```
CreditMiniProgram/
├── backend/           # Spring Boot 3.2 REST API (port 8443, HTTPS)
│   └── src/main/java/com/credit/miniapp/
│       ├── controller/   # REST endpoints (user & admin APIs)
│       ├── service/      # Business logic
│       ├── repository/   # MyBatis Plus data access
│       ├── entity/       # Data models
│       ├── dto/         # Data transfer objects
│       ├── config/      # Spring configuration
│       ├── security/    # JWT authentication
│       └── util/        # Utilities (JWT, crypto)
├── admin-backend/     # Spring Boot 3.2 Admin Panel API (port 8081)
│   └── src/main/java/com/credit/admin/
│       ├── controller/   # Admin management APIs
│       ├── service/      # Business logic
│       ├── repository/   # MyBatis Plus data access
│       ├── entity/       # Data models
│       ├── dto/         # Data transfer objects
│       ├── config/      # JWT interceptors
│       └── util/        # Utilities (JWT, crypto)
├── admin/             # Static HTML admin panel frontend
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

### Backend (User Mini-program API)
```bash
# Build
cd backend && mvn clean package -DskipTests

# Run locally (requires MySQL & Redis)
# Note: Runs on port 8443 with HTTPS enabled (SSL certificate in backend/ssl/)
java -jar target/miniapp-backend-1.0.0.jar
```

### Admin Backend (Management Panel API)
```bash
# Build
cd admin-backend && mvn clean package -DskipTests

# Run locally (requires MySQL)
java -jar target/admin-backend-1.0.0.jar
# Runs on port 8081
```

### Run with Docker
```bash
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

### API Endpoints
- **User API (port 8443, HTTPS)**: `/api/products`, `/api/banks`, `/api/consultant/{productId}`
- **Auth (port 8443, HTTPS)**: `/api/auth/login`
- **User Data (port 8443, HTTPS)**: `/api/favorites`, `/api/history`, `/api/feedback`
- **Admin API (port 8443, HTTPS)**: `/api/admin/products`, `/api/admin/banks`, `/api/admin/consultants`
- **Admin Panel API (port 8081, HTTP)**: `/admin-api/auth`, `/admin-api/products`, `/admin-api/banks`, `/admin-api/consultants`, `/admin-api/feedback`
- Docs: `https://localhost:8443/swagger-ui.html`

## Development Notes

- Admin APIs require JWT authentication (except login)
- User APIs for favorites/history require JWT token in Authorization header
- Phone numbers are encrypted with AES in database, returned masked (e.g., `138****5678`)
- Use `@TableField(exist = false)` for non-database fields in MyBatis Plus entities
- **Two separate backends**: `backend/` (port 8443, HTTPS) serves user mini-program; `admin-backend` (port 8081) serves admin panel
- The `admin/` directory contains static HTML served by the admin-backend or deployed separately
- SSL certificates stored in `backend/ssl/` (self-signed for development)

## WeChat Mini-Program Compatibility

**Important**: When developing the mini-program frontend, you MUST use ES5 syntax only (not ES6) to ensure compatibility with `lazyCodeLoading: requiredComponents` configuration.

### Prohibited ES6 Features
- `const` / `let` → use `var` instead
- Arrow functions `() => {}` → use `function() {}` instead
- Template literals `` `${var}` `` → use string concatenation instead
- Spread operator `...obj` → use `Object.assign({}, obj)` instead
- Destructuring `{ a, b } = obj` → use `obj.a` and `obj.b` instead
- `new Set()` / `new Map()` → use plain objects for lookups
- `arr.includes()` → use `arr.indexOf() !== -1` instead
- `str.startsWith()` / `str.endsWith()` → use `str.indexOf() !== -1` instead
- `export` / `import` → use `module.exports` / `require()` instead
- `async` / `await` → NOTE: These are supported, use them freely

### Code Style
```javascript
// ❌ Wrong (ES6)
const app = getApp();
const { token, userInfo } = app.globalData;
const list = items.map(item => ({ ...item, id: item.id + 1 }));

// ✅ Correct (ES5)
var app = getApp();
var token = app.globalData.token;
var userInfo = app.globalData.userInfo;
var list = items.map(function(item) {
  var obj = Object.assign({}, item);
  obj.id = item.id + 1;
  return obj;
});
```

## Build Outputs to Ignore
- `backend/target/` - Maven build artifacts
- `backend/ssl/` - SSL certificates (sensitive)
- `admin-backend/target/` - Maven build artifacts
- `*.jar` - Compiled Java packages
- `frontend/node_modules/` - NPM dependencies (if added)
