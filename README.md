# 🎬 Region & Platform Management Module

> **Comprehensive system for managing movie distribution platforms and geographical availability**

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**Developer:** Tanishk Gupta
**Module Duration:** 7 Days
**Project:** Platform and Region Management

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Database Schema](#database-schema)
- [API Documentation](#api-documentation)

---

## 🎯 Overview

The **Region & Platform Management Module** is a critical component of the Movie Management System that enables administrators to manage where and how movies are available to users. It provides a robust framework for tracking movie distribution across different platforms (streaming services, theaters, TV networks) and geographical regions.

### Purpose

This module solves the following business problems:

1. **Platform Management**: Track all distribution platforms (Netflix, Amazon Prime, PVR Cinemas, etc.)
2. **Regional Availability**: Manage movie availability across different countries/regions
3. **Discovery Enhancement**: Enable users to find movies available in their location and preferred platforms
4. **Licensing Management**: Track temporal availability windows (start/end dates)
5. **Deep Linking**: Store direct URLs to movies on respective platforms

### Key Capabilities

-  **CRUD Operations** for platforms and regions (Admin-only)
-  **Movie-Platform-Region Linking** with availability types
-  **Advanced Filtering** by region, platform, and availability type
-  **Temporal Availability** tracking with date ranges
-  **Cascade Delete** management for data integrity
-  **Role-Based Access Control** for security
-  **Comprehensive API Documentation** with Swagger
-  **Performance Optimized** with caching and indexing

---

##  Features

### Admin Features

- **Platform Management**
  - Create, update, and delete distribution platforms
  - Categorize platforms by type (OTT, Theater, TV, Other)
  - Store platform metadata (website, logo)
  - Bulk operations support

- **Region Management**
  - Create and manage geographical regions
  - ISO 3166-1 alpha-2 country code validation
  - Support for 200+ countries
  - Regional metadata management

- **Availability Linking**
  - Link movies to platform-region combinations
  - Define availability types (Streaming, Theatrical, Rental, Purchase)
  - Set temporal availability windows
  - Store deep links to movies on platforms
  - Bulk linking capabilities

### User Features

- **Movie Discovery**
  - Filter movies by region (e.g., "Show me movies available in India")
  - Filter movies by platform (e.g., "What's on Netflix?")
  - Filter by availability type (e.g., "Streaming only")
  - Filter by multiple criteria simultaneously
  - View currently available movies (based on date ranges)
  - Pagination and sorting support

- **Platform Exploration**
  - Browse all available platforms
  - View movies available on specific platforms
  - Check platform details and links

- **Regional Content**
  - Discover content available in specific regions
  - View platform availability per region

---

## 🏗️ Architecture

### High-Level Architecture
```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │   Platform   │  │    Region    │  │ Availability │       │
│  │  Controller  │  │  Controller  │  │  Controller  │       │
│  └──────────────┘  └──────────────┘  └──────────────┘       │
│         │                  │                  │             │
└─────────┼──────────────────┼──────────────────┼──────────────
          │                  │                  │
          ▼                  ▼                  ▼
┌─────────────────────────────────────────────────────────────┐
│                     Service Layer                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │   Platform   │  │    Region    │  │   Movie      │       │
│  │   Service    │  │   Service    │  │ Availability │       │
│  │              │  │              │  │   Service    │       │
│  └──────────────┘  └──────────────┘  └──────────────┘       │
│         │                  │                  │             │
└─────────┼──────────────────┼──────────────────┼──────────────
          │                  │                  │
          ▼                  ▼                  ▼
┌─────────────────────────────────────────────────────────────┐
│                  Data Access Layer                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │   Platform   │  │    Region    │  │   Movie      │       │
│  │  Repository  │  │  Repository  │  │ Availability │       │
│  │              │  │              │  │  Repository  │       │
│  └──────────────┘  └──────────────┘  └──────────────┘       │
└─────────────────────────────────────────────────────────────┘
          │                  │                  │
          ▼                  ▼                  ▼
┌─────────────────────────────────────────────────────────────┐
│                      Database Layer                         │
│         MySQL 8.0 with InnoDB Storage Engine                │
│    ┌──────────┐  ┌──────────┐  ┌──────────────────┐         │
│    │Platforms │  │ Regions  │  │MovieAvailability │         │
│    └──────────┘  └──────────┘  └──────────────────┘         │
└─────────────────────────────────────────────────────────────┘
```

### Component Interactions
```
User Request → SSO loggin (Google) → JWT Authentication → Controller → Service Layer
                                                      ↓
                                          Business Logic Validation
                                                      ↓
                                          Repository Layer
                                                      ↓
                                          Database Operations
                                                      ↓
                                          Response Mapping
                                                      ↓
                                          JSON Response
```

### Entity Relationship
```
┌──────────┐                  ┌────────────────────┐                  ┌──────────┐
│ Movies   │                  │MovieAvailability   │                  │Platforms │
│──────────│                  │────────────────────│                  │──────────│
│ id (PK)  │◄────────────────►│ id (PK)            │◄────────────────►│ id (PK)  │
│ title    │   One-to-Many    │ movie_id (FK)      │   Many-to-One    │ name     │
│ ...      │                  │ platform_id (FK)   │                  │ type     │
└──────────┘                  │ region_id (FK)     │                  │ website  │
                              │ availability_type  │                  │ logo     │
                              │ start_date         │                  └──────────┘
                              │ end_date           │
                              │ url                │
                              └────────────────────┘
                                       ▲
                                       │
                                       │ Many-to-One
                                       │
                              ┌────────▼───────┐
                              │   Regions      │
                              │────────────────│
                              │ id (PK)        │
                              │ name           │
                              │ code (UNIQUE)  │
                              └────────────────┘
```

---

## 🛠️ Technology Stack

### Backend Framework
- **Java 17** - Latest LTS version with enhanced features
- **Spring Boot 3.5.6** - Main application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence and ORM
- **Hibernate** - JPA implementation

### Database
- **MySQL 8.0** - Primary database
- **HikariCP** - Connection pooling


### Security
- **JWT (JSON Web Tokens)** - Stateless authentication
- **SSO** - Integration with Google

### Documentation
- **Springdoc OpenAPI 3** - API documentation
- **Swagger UI** - Interactive API testing

### Testing
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework


### Monitoring & Logging
- **Spring Boot Actuator** - Application monitoring
- **Logback** - Logging framework
- **SLF4J** - Logging facade


### Build & Deployment
- **Maven** - Build automation


### Additional Libraries
- **Lombok** - Reduce boilerplate code
- **Validation API** - Request validation

---

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

### Required Software

| Software | Version | Purpose |
|----------|---------|---------|
| Java JDK | 17+ | Runtime environment |
| Maven | 3.8+ | Build tool |
| MySQL | 8.0+ | Database |
| Git | 2.x | Version control |
| Postman | Latest | API testing |
| IntelliJ IDEA | 2023.x+ | IDE (recommended) |


---

## 🚀 Installation

### Step 1: Clone the Repository
```bash
# Clone the main project repository
git clone https://git.tudip.com/capston-project-java-gt1/capston-porject-java-gt1-rupesh-kumar.git

# Navigate to project directory
cd movie-management-system
```

### Step 2: Database Setup

####  Local MySQL Installation
```bash
# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE moviedb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Exit MySQL
exit;
```

### Step 3: Configure Application

Create `application.properties` in `src/main/resources/`:
```
spring.application.name=MovieDB
server.port=8080

spring.datasource.url=your data_source url
spring.datasource.username= your username
spring.datasource.password= your password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver


spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true


google.clientId= ${GOOGLE_CLIENT_ID}
google.clientSecret= ${GOOGLE_CLIENT_SECRET}
google.redirectUri= {baseUrl}/auth/callback/google
jwtSecret=your-super-secret-jwt-key-at-least-256-bits-long
app.jwtExpirationMs=86400000
```

### Step 4: Build the Application
```bash
# Clean and build
mvn clean install
```

### Step 5: Run Database Migrations
```bash
# Run Flyway migrations
mvn flyway:migrate

# Or using Spring Boot
mvn spring-boot:run
```

### Step 6: Start the Application
```bash
# Application should start on http://localhost:8080
```

---

## 🗄️ Database Schema



### Schema Relationships Explained

#### One-to-Many Relationships

1. **Movies → MovieAvailability**
   - One movie can have many availability records (multiple platforms and regions)
   - CASCADE DELETE: Deleting a movie removes all its availability records

2. **Platforms → MovieAvailability**
   - One platform can have many movies available on it
   - CASCADE DELETE: Deleting a platform removes all related availability records

3. **Regions → MovieAvailability**
   - One region can have many movies available in it
   - CASCADE DELETE: Deleting a region removes all related availability records

#### Unique Constraints

- **Platforms.name**: Prevents duplicate platform names
- **Regions.code**: Ensures unique ISO country codes
- **MovieAvailability(movie_id, platform_id, region_id)**: Prevents duplicate availability links

---

## 📡 API Documentation

### Base URL
```
Development: http://localhost:8080
```

### Authentication

All admin endpoints require JWT authentication:
```http
Authorization: Bearer <jwt_token>
```

---

## 🔧 Platform Management APIs

### 1. Create Platform

**Endpoint:** `POST /api/v1/platforms`

**Access:** Admin Only

**Description:** Create a new distribution platform

**Request Headers:**
```http
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Netflix",
  "type": "OTT",
  "website": "https://netflix.com",
}
```

**Field Validations:**
- `name`: Required, max 100 characters, must be unique
- `type`: Required, must be one of: OTT, THEATER, TV, OTHER
- `website`: Optional, must be valid URL format

**Response: 201 Created**
```json
{
  "id": 1,
  "name": "Netflix",
  "type": "OTT",
  "website": "https://netflix.com",
  "createdAt": "2025-10-30T10:30:00",
  "updatedAt": "2025-10-30T10:30:00"
}
```

---

### 2. Get Platform by ID

**Endpoint:** `GET /api/v1/platforms/{id}`

**Access:** Public

**Description:** Retrieve detailed information about a specific platform

**Request Headers:**
```http
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Path Parameters:**
- `id` (required): Platform ID

**Response: 200 OK**
```json
{
  "id": 1,
  "name": "Netflix",
  "type": "OTT",
  "website": "https://netflix.com",
  "createdAt": "2025-10-30T10:30:00",
  "updatedAt": "2025-10-30T10:30:00"
}
```

**Error Response: 404 Not Found**
```json
{
  "status": 404,
  "error": "Resource Not Found",
}
```

---

### 3. Update Platform

**Endpoint:** `PUT /api/v1/platforms/{id}`

**Access:** Admin Only

**Description:** Update existing platform details

**Request Headers:**
```http
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Path Parameters:**
- `id` (required): Platform ID

**Request Body:** (All fields optional for partial update)
```json
{
  "name": "Netflix India",
  "website": "https://netflix.com/in",
  "logo": "https://cdn.movieapp.com/logos/netflix-india.png"
}
```

**Response: 200 OK**
```json
{
  "id": 1,
  "name": "Netflix India",
  "type": "OTT",
  "website": "https://netflix.com/in",
  "logo": "https://cdn.movieapp.com/logos/netflix-india.png",
  "createdAt": "2025-10-30T10:30:00",
  "updatedAt": "2025-10-30T11:45:00"
}
```

---

### 4. Delete Platform

**Endpoint:** `DELETE /api/v1/platforms/{id}