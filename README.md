# School Management System

A comprehensive web-based school management system designed for primary schools to facilitate communication and tracking between teachers and parents.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Database Schema](#database-schema)
- [Installation](#installation)
- [Usage](#usage)
- [Contributors](#contributors)

## Overview

This School Management System streamlines the educational process by providing a centralized platform where teachers can manage student information, assignments, grades, and attendance, while parents can monitor their children's academic progress in real-time.

**Key Assumptions:**
- One teacher manages one class throughout the school year
- Primary focus on classes up to 4th grade
- Parents can have multiple children in the system
- Students are part of the system but are not direct users

## Features

### For Teachers
- **Student Management**: View and manage class rosters
- **Grade Management**: Add, edit, and track student grades with comments
- **Attendance Tracking**: Record student absences and manage excuse validation
- **Homework Assignment**: Create and manage homework assignments for the entire class
- **Excuse Validation**: Review and approve/reject absence excuses from parents

### For Parents
- **Academic Monitoring**: View children's grades and academic progress
- **Attendance Overview**: Track attendance records and absence history
- **Homework Tracking**: View current and upcoming homework assignments
- **Excuse Submission**: Submit excuses for student absences
- **Multi-Child Support**: Manage multiple children with a single account

### System Features
- **Secure Authentication**: Email-based login with password hashing
- **Role-Based Access**: Different interfaces for teachers and parents
- **Data Validation**: Input validation and error handling
- **Responsive Design**: Clean, user-friendly interface

## Technologies Used

- **Backend**: Java
- **Database**: MySQL
- **IDE**: IntelliJ IDEA
- **Architecture**: MVC (Model-View-Controller)
- **Authentication**: Password hashing with bcrypt
- **Design**: UML diagrams for system modeling


### Key Components:
- **User Management**: Account creation, authentication, and session management
- **Student Information System**: Student profiles and class management
- **Grade Management**: Grade recording and retrieval
- **Attendance System**: Absence tracking and excuse management
- **Homework Management**: Assignment creation and tracking

## Database Schema

The database consists of the following main entities:

- **User**: Stores user authentication and profile information
- **Teacher**: Teacher-specific information and class assignments
- **Parent**: Parent information and child relationships
- **Student**: Student profiles and class enrollment
- **Class Group**: Class information and teacher assignments
- **Grade**: Academic grades and assessments
- **Absence**: Attendance records and absence tracking
- **Excuse**: Absence excuse submissions and validation
- **Homework**: Assignment management and tracking

## Installation

### Prerequisites
- Java Runtime Environment (JRE) 8 or higher
- MySQL Server 5.7 or higher

### Quick Start (Using Pre-built JAR)

1. **Clone the repository**

2. **Database Setup**
   ```sql
   -- Create database
   CREATE DATABASE sms_project;
   
   -- Import the schema and sample data
   mysql -u root -p sms_project < sms_project.sql
   ```

3. **Run the Application**
   ```bash
   # Navigate to the directory containing the JAR file
   java -jar school-management-system.jar
   ```

## Usage

### Getting Started

1. **Account Creation**
   - Navigate to the login page
   - Click "Create Account"
   - Select account type (Teacher/Parent)
   - Fill in required information

2. **Login Process**
   - Enter registered email and password
   - System redirects to role-appropriate dashboard

3. **Navigation**
   - Teachers: Access student lists, grade management, and homework creation
   - Parents: View child information, grades, and homework assignments


## Contributors

This project was developed as part of the Systems Analysis and Testing course (Academic Year 2024/2025):

- **Kristian Kesar**
- **Thomas Radulescu**
- **Filip Raguž**


---

**Note**: This project was created for educational purposes as part of a Object-Oriented Programming course. It demonstrates practical application of software engineering principles, database design, and system architecture concepts.