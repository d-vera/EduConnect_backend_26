# Capability: Communication Hub

## Overview
Provides core teacher-to-student communication capabilities: official academic announcements, priority flags, and course/subject inquiries.

## Requirements

### Requirement: List Announcements
The system SHALL return active announcements sorted by priority and publication date.

#### Scenario: Fetch announcement board
- **Given** active announcements in PostgreSQL
- **When** the client sends a `GET /api/v1/announcements` request
- **Then** the system returns HTTP 200 with an array of announcements containing ID, title, message body, priority level (`URGENT`, `NORMAL`, `INFO`), author name/role, and creation timestamp.

#### Scenario: Filter announcements by priority
- **Given** published announcements with mixed priority tags
- **When** the client sends `GET /api/v1/announcements?priority=URGENT`
- **Then** the system returns HTTP 200 containing only urgent announcements.

### Requirement: Publish Announcement
The system SHALL permit instructors or authorized staff to publish new academic announcements.

#### Scenario: Successful announcement creation
- **When** an authorized instructor sends `POST /api/v1/announcements` with:
  ```json
  {
    "title": "Cambio de horario de tutoría",
    "content": "La sesión de consultas se traslada al viernes 19:30.",
    "authorName": "Prof. García",
    "priority": "URGENT",
    "courseId": "opt-course-id"
  }
  ```
- **Then** the system persists the announcement in PostgreSQL
- **And** returns HTTP 201 Created with the generated announcement object.

### Requirement: Student Inquiry / Q&A Thread
The system SHALL allow students to submit questions directed to instructors and view responses.

#### Scenario: Post a student question
- **When** a student sends `POST /api/v1/queries` with their name, subject, and question
- **Then** the system records the inquiry and returns HTTP 201 Created.
