# Capability: Educational Content

## Overview
Provides educational guides, articles, and recommendations centered around Effective Communication between educators and students. Supports categorized content retrieval, article details by slug, and thematic tags.

## Requirements

### Requirement: List Educational Articles
The system SHALL return a paginated list of published educational articles, filterable by category and search keyword.

#### Scenario: Retrieve all articles
- **Given** existing articles in the database
- **When** the client sends a `GET /api/v1/content/articles` request
- **Then** the system returns HTTP 200 with an array of article summaries including title, slug, excerpt, category, read time, and creation date.

#### Scenario: Filter articles by category
- **Given** existing articles across categories "Assertiveness", "Feedback", and "Active Listening"
- **When** the client sends `GET /api/v1/content/articles?category=assertiveness`
- **Then** the system returns HTTP 200 containing only articles matching that category.

### Requirement: Get Article by Slug
The system SHALL retrieve full article details using a human-readable URL slug.

#### Scenario: Article exists
- **Given** an article with slug "constructive-feedback-techniques"
- **When** the client sends `GET /api/v1/content/articles/constructive-feedback-techniques`
- **Then** the system returns HTTP 200 with the complete markdown/HTML content, author, and associated references.

#### Scenario: Article not found
- **Given** no article matches the slug "unknown-article"
- **When** the client sends `GET /api/v1/content/articles/unknown-article`
- **Then** the system returns HTTP 404 with a structured error payload.

### Requirement: List Content Categories
The system SHALL return available categories for grouping communication content.

#### Scenario: Fetch categories
- **When** the client sends `GET /api/v1/content/categories`
- **Then** the system returns HTTP 200 with an array of categories (ID, name, slug, description, and article count).
