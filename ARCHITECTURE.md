# LanhCare - Docker Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         DOCKER COMPOSE                          │
│                                                                 │
│  ┌───────────────────────┐        ┌───────────────────────┐   │
│  │   MySQL Container     │        │  Spring Boot Container│   │
│  │  (lanhcare-mysql)     │        │   (lanhcare-app)      │   │
│  │                       │        │                       │   │
│  │  Port: 3306           │◄───────┤  Port: 8080          │   │
│  │  Database:            │        │                       │   │
│  │   health_app_db       │        │  Depends on:         │   │
│  │                       │        │   - MySQL (healthy)   │   │
│  │  Volume:              │        │                       │   │
│  │   mysql_data          │        │  Health Check:        │   │
│  │                       │        │   /actuator/health    │   │
│  │  Init Script:         │        │                       │   │
│  │   init-data.sql       │        │  Auto-start after:    │   │
│  │                       │        │   MySQL ready         │   │
│  └───────────────────────┘        └───────────────────────┘   │
│           ▲                                    ▲                │
└───────────┼────────────────────────────────────┼────────────────┘
            │                                    │
            │                                    │
    ┌───────┴────────┐                  ┌────────┴─────────┐
    │  MySQL         │                  │   Browser        │
    │  Workbench     │                  │   /Postman       │
    │                │                  │                  │
    │  localhost:3306│                  │  localhost:8080  │
    └────────────────┘                  └──────────────────┘
```

## Data Flow

```
1. Docker Compose Start
   ↓
2. MySQL Container Starts
   ↓
3. Create Database (health_app_db)
   ↓
4. Wait for MySQL Health Check ✓
   ↓
5. Spring Boot Container Starts
   ↓
6. Hibernate Generates Tables from Entities
   ↓
7. MySQL Executes init-data.sql (INSERT sample data)
   ↓
8. Application Ready ✓
```

## Network Architecture

```
┌────────────────────────────────────────────────┐
│         lanhcare-network (Bridge)              │
│                                                │
│   mysql:3306  ◄──────► app:8080               │
│   (internal DNS resolution)                    │
└────────────────────────────────────────────────┘
         │                      │
         │                      │
    localhost:3306        localhost:8080
         │                      │
         ▼                      ▼
    [MySQL Workbench]      [Browser/API Client]
```

## Volume Management

```
mysql_data (Docker Volume)
├── Database Files (Persistent)
├── Logs
└── Config Files

Mounted Files:
├── init-data.sql → /docker-entrypoint-initdb.d/
```

## Build Process

```
[Dockerfile]
    ↓
Stage 1: Build
├── Maven 3.9.6 + JDK 21
├── Download Dependencies
├── Compile Source Code
└── Package → app.jar
    ↓
Stage 2: Runtime
├── JRE 21 (Lightweight)
├── Copy app.jar
└── ENTRYPOINT java -jar app.jar
```

## Environment Variables

```
MySQL Container:
├── MYSQL_ROOT_PASSWORD: rootpassword
├── MYSQL_DATABASE: health_app_db
├── MYSQL_CHARACTER_SET_SERVER: utf8mb4
└── TZ: Asia/Ho_Chi_Minh

App Container:
├── SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/...
├── SPRING_DATASOURCE_USERNAME: root
├── SPRING_DATASOURCE_PASSWORD: rootpassword
└── TZ: Asia/Ho_Chi_Minh
```

## Health Checks

```
MySQL:
├── Command: mysqladmin ping
├── Interval: 10s
├── Timeout: 5s
├── Retries: 5
└── Start Period: 30s

Spring Boot App:
├── Endpoint: /actuator/health
├── Interval: 30s
├── Timeout: 3s
├── Retries: 3
└── Start Period: 60s
```
