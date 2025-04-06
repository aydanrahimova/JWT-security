# JWT Authentication Demo

This is a demo project that implements **JWT-based authentication** using **access** and **refresh tokens**. It
demonstrates how to securely register users, authenticate them, manage token lifecycles, and implement logout
functionality.

## Features

- User Registration
- User Login
- Generate **Access Token** (valid for 15 minutes)
- Generate **Refresh Token** (valid for 7 days)
- Logout functionality with token invalidation (e.g., blacklisting)

## Technologies Used

- Java
- Spring Boot
- Spring Security
- JWT (JSON Web Tokens)
- PostgreSQL

## Endpoints Overview

| Endpoint              | Method | Description                            |
|-----------------------|--------|----------------------------------------|
| `/auth/register`      | POST   | Register a new user & get tokens       |
| `/auth/login`         | POST   | Authenticate user & get tokens         |
| `/auth/refresh-token` | POST   | Get new access token via refresh token |
| `/auth/logout`        | POST   | Logout and invalidate token            |

## Token Strategy

- **Access Token**
    - Short-lived
    - Used for accessing protected resources
    - Expires in **15 minutes**

- **Refresh Token**
    - Long-lived
    - Used to renew access token
    - Expires in **7 days**
    - Can be invalidated on logout

