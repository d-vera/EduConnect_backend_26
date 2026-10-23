# Capability: Interactive Quiz

## Overview
Implements the interactive quiz module on Effective Communication. Questions and answer choices are persisted in PostgreSQL. The backend validates answers, computes score metrics, produces qualitative feedback, and logs student attempts.

## Requirements

### Requirement: Retrieve Quiz Questions
The system SHALL deliver randomized or ordered quiz questions without exposing the correct answer flags in the response payload.

#### Scenario: Request active quiz questions
- **Given** configured quiz questions with multiple-choice options in the database
- **When** the client sends a `GET /api/v1/quiz/questions` request
- **Then** the system returns HTTP 200 with an array of questions, each with its ID, prompt text, order number, and a list of options (option ID and option text only).
- **And** the `is_correct` indicator is strictly excluded from the payload.

### Requirement: Submit Quiz Attempt
The system SHALL process a student's submitted answers, calculate their total score, store the attempt record, and return evaluation results.

#### Scenario: Successful submission
- **Given** valid question IDs and selected option IDs submitted by a participant
- **When** the client sends `POST /api/v1/quiz/submit` with:
  ```json
  {
    "participantName": "Juan Pérez",
    "answers": [
      { "questionId": "uuid-1", "selectedOptionId": "uuid-opt-a" },
      { "questionId": "uuid-2", "selectedOptionId": "uuid-opt-c" }
    ]
  }
  ```
- **Then** the system computes the total score out of 100 points
- **And** stores the attempt with a timestamp in PostgreSQL
- **And** returns HTTP 200 with attempt ID, total score, percentage, correct answer breakdown, and communication profile feedback (e.g. "Assertive Communicator").

#### Scenario: Incomplete or invalid submission
- **When** the client sends `POST /api/v1/quiz/submit` with empty answers or malformed IDs
- **Then** the system returns HTTP 400 Bad Request with field validation errors.

### Requirement: Retrieve Attempt Results
The system SHALL permit fetching details of a previously completed quiz attempt by its ID.

#### Scenario: Lookup attempt by ID
- **Given** an existing attempt with ID `attempt-123`
- **When** the client sends `GET /api/v1/quiz/attempts/attempt-123`
- **Then** the system returns HTTP 200 with score breakdown, timestamp, and feedback summary.
