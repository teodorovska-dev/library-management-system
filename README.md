# Library Management System

A modern web-based library management system developed using Spring Boot, PostgreSQL, and Angular.  
The application provides functionality for managing books, authentication, favorites, role-based access control, and administrative management of the library collection.

---

## Overview

Library Management System is a full-stack client-server application designed to simplify digital library management.  
The system allows users to browse books, search and filter the catalog, manage favorite books, and interact with a personalized user profile.  
Administrators have access to advanced functionality for managing the library collection, controlling book availability, and monitoring statistical information about the system.

The project follows modern backend and frontend development practices using RESTful APIs, JWT authentication, layered architecture, and responsive UI design.

---

## Live Demo

### Frontend
```text
https://library-management-frontend-teodorovska-devs-projects.vercel.app/profile
```

### Backend API
```text
https://library-management-system-0ea7.onrender.com
```

### Swagger API Documentation
```text
https://library-management-system-0ea7.onrender.com/swagger-ui/index.html
```

---

## Main Features

### Authentication & Authorization
- JWT-based authentication
- Secure login and registration
- Password reset via email verification code
- Role-based access control (`USER`, `ADMIN`)
- Encrypted password storage using Spring Security
- Persistent authenticated sessions

### Book Catalog
- Dynamic book catalog
- Search by title, author, ISBN, and categories
- Filtering by genres, languages, and availability
- Sorting by title, author, and publication year
- Detailed book information page
- Responsive UI with dynamic splash colors
- Trending books section

### Favorites System
- Add books to favorites
- Personalized favorite books section
- Favorite books management
- Trending books generation based on user activity

### User Profile
- Profile management
- Avatar upload support
- Personal information editing
- Personalized dashboard
- Favorite books overview

### Admin Functionality
- Add new books
- Edit existing books
- Upload book covers
- Manage book availability
- Write-off books (`written_off` status)
- Library statistics dashboard
- Book inventory management

---

## System Roles

### USER
- Browse the catalog
- Search and filter books
- View detailed book information
- Add books to favorites
- Manage personal profile

### ADMIN
- Full library management access
- Add and edit books
- Manage availability and status
- Monitor system statistics
- Access administrative dashboard

---

## Technology Stack

### Backend
- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA (Hibernate)
- JWT Authentication
- Maven

### Frontend
- Angular
- TypeScript
- SCSS
- Ionic Components

### Database
- PostgreSQL
- Neon PostgreSQL Cloud Database

### Cloud & Deployment
- Render (Backend Deployment)
- Vercel (Frontend Deployment)
- Cloudinary (Image Storage)

---

## Project Architecture

Backend architecture follows a layered structure:

```text
controller
service
repository
entity
dto
mapper
security
config
exception
```

---

## Database Structure

The database is designed using a relational model and includes the following main entities:

- `users`
- `roles`
- `books`
- `favorites`
- `book_genres`
- `book_languages`
- `password_reset_codes`

Relationships between entities are implemented using foreign keys and JPA entity mappings to ensure data integrity and scalability.

---

## Security

The application implements several security mechanisms:

- JWT token authentication
- Stateless session management
- Password encryption using Spring Security
- Role-based endpoint protection
- Secure password reset flow
- Validation of user input data
- Protected administrative functionality

---

## Installation & Setup

### Clone Repository

```bash
git clone https://github.com/teodorovska-dev/library-management-system.git
cd library-management-system
```

---

## Backend Setup

Navigate to the backend directory:

```bash
cd backend
```

Run the backend application:

```bash
./mvnw spring-boot:run
```

---

## Frontend Setup

Navigate to the frontend directory:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

Run the Angular application:

```bash
ng serve
```

---

## Environment Variables

The project uses the following environment variables:

```text
ADMIN_REGISTRATION_KEY

CLOUDINARY_API_KEY
CLOUDINARY_API_SECRET
CLOUDINARY_CLOUD_NAME

FRONTEND_URL
JAVA_TOOL_OPTIONS

JWT_EXPIRATION
JWT_SECRET

MAIL_FROM
MAIL_HOST
MAIL_PASSWORD
MAIL_PORT
MAIL_USERNAME

SPRING_DATASOURCE_PASSWORD
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_JPA_HIBERNATE_DDL_AUTO
SPRING_JPA_SHOW_SQL
```

---

## API Documentation

Swagger/OpenAPI documentation is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

or production environment:

```text
https://library-management-system-0ea7.onrender.com/swagger-ui/index.html
```

---

## Future Improvements

- Book borrowing system
- Reservation functionality
- Notifications and reminders
- Recommendation system
- Advanced analytics
- Multi-language support
- Email subscriptions
- Reading history tracking

---

## Author

**Anastasiia Teodorovska**  
Applied Mathematics Student  
Lviv Polytechnic National University

GitHub:
```text
https://github.com/teodorovska-dev
```

---

## License

This project was developed for educational purposes as a university course project.
