# TaskStream - Task Management System

TaskStream is a backend-only Task Management System developed using Spring Boot. This application allows for efficient management of tasks within projects, including creating, retrieving, updating, and deleting tasks. It integrates with GitHub for exporting project reports and is containerized with Docker for deployment. The application is hosted on AWS EC2 and features a CI/CD pipeline set up with Jenkins.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Database Schema](#database-schema)
- [API Endpoints](#api-endpoints)
- [Exporting Project Reports](#exporting-project-reports)
- [Docker Configuration](#docker-configuration)
- [CI/CD Pipeline](#cicd-pipeline)
- [License](#license)

## Introduction

TaskStream is designed to manage tasks within projects efficiently. It provides functionalities to manage tasks and export project summaries as GitHub gists. The backend application is built with Spring Boot and is fully containerized using Docker for easy deployment on AWS EC2. The CI/CD pipeline is managed with Jenkins for automated builds and deployments.

## Features

- **Task Management**:
  - Create new tasks
  - Retrieve tasks by project
  - Update task details
  - Delete tasks from projects

- **Project Reporting**:
  - Export project summaries as GitHub gists

## Technologies Used

- **Spring Boot**: Framework for building Java applications.
- **Spring Security**: Provides authentication and authorization.
- **JWT Authentication**: Secure token-based authentication mechanism.
- **MySQL**: Relational database management system.
- **JPA/Hibernate**: Java Persistence API for ORM (Object-Relational Mapping).
- **Docker**: Containerization platform.
- **Jenkins**: CI/CD pipeline for automated builds and deployments.
- **GitHub Gists**: For exporting project summaries.

## Getting Started

### Prerequisites

- **Java 17 or higher**: Install Java Development Kit (JDK) from [Oracle's official website](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html).
- **Maven**: Download from [official Maven website](https://maven.apache.org/download.cgi) or install via your package manager.
- **MySQL**: Download and install from [official MySQL website](https://dev.mysql.com/downloads/).
- **Docker**: Containerization platform.

### Installation

1. **Clone the repository**

   ```bash
   git clone https://github.com/suhanmhd/TaskStream.git
