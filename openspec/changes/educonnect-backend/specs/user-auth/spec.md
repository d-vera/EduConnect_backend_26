# Capability: User Identity & Project Metadata

## Overview
Handles user role representations (Teacher, Student, Admin) and serves the required metadata endpoints for the contest: Developers & Team presentation, and Home summary.

## Requirements

### Requirement: Get Developers Section Metadata
The system SHALL expose an endpoint presenting developer team credits, technological stack rationale, and academic course details to satisfy Section D of the contest evaluation.

#### Scenario: Request developer credits
- **When** the client sends a `GET /api/v1/developers` request
- **Then** the system returns HTTP 200 with:
  - Team members (names, roles, contact/GitHub)
  - Institution ("Instituto Técnico Nacional de Comercio 'Federico Álvarez Plata'")
  - Course ("Diseño y Programación Web III")
  - Career ("Sistemas Informáticos")
  - Technology stack specifications (Scala 3, ZIO 2, Tapir, PostgreSQL, Angular, Tailwind CSS)
  - Key architectural justifications (Type safety, asynchronous resilience, compile-time query safety).

### Requirement: Get Home Summary
The system SHALL provide an aggregated overview endpoint for the home page (Section A).

#### Scenario: Fetch home metrics
- **When** the client sends `GET /api/v1/home/summary`
- **Then** the system returns HTTP 200 containing:
  - Mission statement on effective communication
  - Total active articles count
  - Total quiz questions count
  - Latest urgent announcement snippet
  - Quick action links for the frontend navigation.
