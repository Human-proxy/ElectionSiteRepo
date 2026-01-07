# StemWijs Documentation

Welcome to the documentation for the StemWijs election platform. This documentation covers everything from project setup to detailed technical specifications.

## Table of Contents

### Getting Started
- [Readme Overview](README.md) - Project introduction and setup
- [Team Collaboration Contract](samenwerkingscontract.md) - Team agreements and workflows

### Technical Documentation

#### Architecture & Design
- [Domain Models (UML)](UML/README.md) - Database architecture with separate Election and User domain diagrams
- [API Documentation](api_documentation.md) - Complete REST API reference
- [Database Structure Changes](DATABASE_STRUCTURE_CHANGES.md) - Schema evolution history

#### Feature Documentation
- **Authentication System**
  - [Authentication Setup Guide](technical/Aydin/authentication_system_setup_guide.md) - JWT implementation details
  - [Email Verification Flow](technical/Aydin/README_QUIZ_FEATURE.md) - 4-digit code verification
  
- **Forum System**
  - Post creation and management
  - Comment system
  - Tag-based filtering

- **Quiz Feature**
  - [Quiz filter Implementation](technical/Aydin/README_QUIZ_FEATURE.md) - Election data filtering based on user responses
  
- **Caching & Performance**
  - [Database Caching](technical/DB_CACHING.md) - Query optimization
  - [Page Tracking](technical/PAGE_TRACKING.md) - User activity monitoring

### User Testing
- [User tests](usertests/) - Think Make Check cycles and feedback summaries

### Sprint Documentation
- **Retrospectives**
  - [Sprint 1 Retro](Retro/Retro sprint 1/Retro_Sprint_1.md)
  - [Sprint 2 Retro](Retro/Retro sprint 1/Retro_Sprint_2.md)
  - [Sprint 3 Retro](Retro/Retro sprint 1/Retro_sprint_3.md)

## Quick Links

### For Developers
- **Backend:** Start with [API Documentation](api_documentation.md)
- **Frontend:** Check [Authentication Guide](technical/authentication_system_setup_guide.md)
- **Database:** Review [Domain Models UML](UML/README.md) and [Structure Changes](DATABASE_STRUCTURE_CHANGES.md)

## Project Structure Overview

```
StemWijs/
├── election-frontend/    # Vue.js application
├── election-backend/     # Spring Boot API
├── docs/                 # This documentation
│   ├── UML/             # Database architecture diagrams
│   ├── technical/       # Technical specs
│   ├── research/        # Research documents
│   ├── Onderzoek/       # Student investigations
│   ├── Retro/           # Sprint retrospectives
│   └── usertests/       # User testing results
└── README.md            # Main project README
```

## Documentation Standards

All documentation in this project follows these principles:
- **Clear and concise** - Written for both technical and non-technical audiences
- **Up-to-date** - Reflects current implementation
- **Well-structured** - Easy to navigate and find information
- **Code examples** - Includes practical examples where relevant

## Last Updated

This documentation was last updated in **November 2025** during Sprint 4.

For the most recent changes, check the [Database Structure Changes](DATABASE_STRUCTURE_CHANGES.md) log.

---

*Need help? Contact your team members or Product Owner.*
