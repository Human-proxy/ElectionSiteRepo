# StemWijs - Dutch Election Platform

A modern web application that helps Dutch citizens understand election results, learn about the political system, and engage in political discussions. Built for Gen Z voters (18-25) with a focus on accessibility and user experience.

## Project Overview

StemWijs is an educational and interactive platform that provides:
- **Real-time election results** with visual representations
- **Educational content** explaining the Dutch political system in 5 simple steps
- **Community forum** for political discussions and engagement
- **Party comparison tool** to help users understand different political positions
- **Filter quiz** to filter the data that the user wants to see based on their preferences

**Important:** This project is part of a university assignment. Election data XML files (±2GB per election) are **not** included in the repository.

## Architecture

### Tech Stack
**Frontend:**
- Vue.js 3 (Composition API)
- Vuetify 3 (Material Design component framework)
- Vue Router for navigation
- Axios for HTTP requests
- Leaflet + Proj4Leaflet for interactive map
- Chart.js + Vue-ChartJS for data visualization
- Vite as build tool and dev server

**Backend:**
- Java 21 with Spring Boot 3.5.5
- Custom JWT authentication (handcrafted security implementation)
- Spring Data JPA for database access
- BCrypt password hashing (spring-security-crypto only)
- JavaMail for email verification
- H2 (development) / MySQL (production) database

**Infrastructure:**
- GitLab CI/CD pipeline
- MkDocs for documentation
- JUnit 5 & Mockito for testing
- Maven for dependency management

### Project Structure
```
├── election-frontend/              # Vue.js application
│   ├── src/
│   │   ├── components/            # Vue components
│   │   │   ├── auth/              # Login, register, verification
│   │   │   ├── Forum/             # Forum posts and discussions
│   │   │   ├── Profile/           # User profile management
│   │   │   ├── Quiz/              # Filter quiz feature
│   │   │   ├── Dashboard/         # User dashboard
│   │   │   ├── LayoutComponent/   # Navigation, footer, layout
│   │   │   ├── LearnPage/         # Educational content components
│   │   │   ├── VergelijkingPagina/ # Party comparison
│   │   │   └── admin/             # Admin panel components
│   │   ├── services/              # API communication layer
│   │   ├── composables/           # Reusable Vue composition functions
│   │   ├── router/                # Route configuration
│   │   ├── utils/                 # Authentication & helper functions
│   │   └── assets/                # Static assets (CSS, images, GeoJSON)
│   └── public/                    # Public static files
│
├── election-backend/               # Spring Boot API
│   └── src/main/java/nl/hva/dederdekamer/election_backend/
│       ├── XMLParser/             # Election data XML processing
│       │   ├── api/               # Parser API controllers
│       │   ├── service/           # XML parsing services
│       │   ├── model/             # XML data models
│       │   ├── factory/           # Parser factories
│       │   └── utils/             # XML transformers & helpers
│       ├── controller/            # REST API endpoints
│       ├── service/               # Business logic
│       ├── repository/            # JPA data access layer
│       ├── entities/              # Database entities (JPA)
│       ├── dto/                   # Data transfer objects
│       ├── security/              # JWT authentication & filters
│       ├── config/                # Spring configuration
│       ├── store/                 # In-memory verification store
│       ├── exception/             # Custom exceptions
│       └── util/                  # Utility classes
│
└── docs/                          # Documentation (MkDocs)
    ├── technical/                 # Technical documentation
    ├── research/                  # Student research documents
    ├── Onderzoek/                 # Individual investigations
    ├── PO-docs/                   # Product Owner materials
    ├── Retro/                     # Sprint retrospectives
    ├── usertests/                 # User testing results
    └── DomainModel.puml           # UML class diagram
```

## Getting Started

### Prerequisites
- **Node.js** 18+ and npm (for frontend)
- **Java** 17+ and Maven (for backend)
- **Git** for version control

### Installation

#### 1. Clone the Repository
```bash
git clone <repo-url>
cd quufoogeenee32
```

#### 2. Backend Setup
```bash
cd election-backend

# Configure database connection
cp src/main/resources/application.properties.example src/main/resources/application.properties
# Edit application.properties with your database credentials

# Run the application
./mvnw spring-boot:run
```

The backend will start on `http://localhost:8080`

#### 3. Frontend Setup
```bash
cd election-frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

The frontend will start on `http://localhost:5173`

### Environment Variables

**Backend** (`application.properties`):
```properties
spring.datasource.url=jdbc:url_to_your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
app.jwt.secret=your-secret-key-here
app.jwt.expiration-minutes=60
```

**Frontend** (`.env`):
```env
VITE_API_URL=http://localhost:8080
```

## Documentation

Documentation is available in the `/docs` folder.

### Key Documentation Files
- [Documentation Index](docs/index.md) - Complete documentation overview and navigation
- [API Documentation](docs/api_documentation.md) - Complete REST API reference
- [Domain Models (UML)](docs/UML/README.md) - Database architecture with separate Election and User domain diagrams
- [Authentication Guide](docs/technical/Aydin/authentication_system_setup_guide.md) - JWT implementation details
- [Functional Design](docs/functioneel_ontwerp.md) - Requirements and user stories
- [Database Changes](docs/DATABASE_STRUCTURE_CHANGES.md) - Schema evolution history

## Key Features

### Authentication System
- Email-based registration with 4-digit verification codes
- JWT token authentication (60-minute expiration)
- Password reset functionality
- Profile management with image upload
- Role-based access control (USER/ADMIN)

### Forum System
- Create and view discussion posts
- Comment on posts
- Tag-based filtering
- Pagination support
- User moderation (admin only)

### Election Data
- vote visualization
- Party seat distribution
- Regional breakdowns by municipality
- Historical election comparisons

### Educational Content
- 5-step explanation of Dutch political system
- Interactive timeline of election process
- Visual seat distribution (150 Tweede Kamer seats)
- CTA sections encouraging civic engagement


## Development Workflow

### Creating a Feature Branch
```bash
git checkout -b feature/your-feature-name
# Make changes
git add .
git commit -m "Description of changes"
git push origin feature/your-feature-name
```

### Code Style
- **Java**: Follow Spring Boot conventions, use meaningful variable names
- **JavaScript/Vue**: Use ESLint configuration, Composition API preferred
- **CSS**: BEM-like naming, scoped styles in components

## Team

This project was developed by a team of 5 students as part of the HvA Software Engineering curriculum (Year 2, Semester 1).

**Team Members:**
- Akif
- Aydin
- Dominik
- Milan
- Wessel

## License

This project is created for educational purposes as part of the HvA curriculum. All rights reserved.

## Acknowledgments

- Election data provided by Kiesraad (Dutch Electoral Council)
- Icons from Lucide Icons and Icons8
- Spring Boot and Vue.js communities for documentation

## Support

For questions or issues:
1. Check the [documentation](docs/)
2. Review existing [GitLab issues](../../issues)
3. Contact your Product Owner or teaching staff

---

**Last Updated:** November 2025  
**Project Status:** Active Development (Sprint 4)
