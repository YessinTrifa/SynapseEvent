# SynapseEvent

A comprehensive JavaFX-based event management system designed to streamline the organization and management of events, reservations, payments, and participants. This application provides a user-friendly interface for managing various aspects of event planning and execution.

## Overview

SynapseEvent is built using modern Java technologies and follows best practices in software architecture. It serves as a complete solution for event organizers to handle everything from user management to payment processing.

## Features

- **User Management**: Create and manage users with different roles and associated entreprises
- **Event Modules**: Manage various types of events including:
  - Paddle Events
  - Formation Events
  - Partying Events
  - Team Building Events
  - Anniversary Events
- **Dashboard**: Centralized view for managing all aspects of the system

## Architecture

The application implements a clean, layered architecture that promotes separation of concerns and maintainability:

### Layers

1. **Entity Layer** (`entities/`): Data model classes representing business entities
   - `User.java` - User information with role and entreprise associations
   - `Role.java` - User roles
   - `Entreprise.java` - Company/organization information
   - `PaddleEvent.java` - Paddle event details
   - `FormationEvent.java` - Formation event details
   - `PartyingEvent.java` - Partying event details
   - `TeamBuildingEvent.java` - Team building event details
   - `AnniversaryEvent.java` - Anniversary event details

2. **Service Layer** (`service/`): Business logic and data access encapsulation
   - Service classes (e.g., `UserService.java`) that handle both business logic and database operations
   - Implements CRUD operations using JDBC directly
   - Uses prepared statements for security
   - Provides a clean API for controllers

3. **Presentation Layer** (`controller/`): JavaFX UI controllers
   - FXML-based controllers for each view
   - Handle user interactions and update the UI
   - Coordinate between services and views

4. **Utility Layer** (`utils/`): Helper classes
   - `MaConnection.java` - Database connection singleton
   - `AlertsUtil.java` - UI alert utilities
   - `DateUtil.java` - Date manipulation utilities

### Design Patterns Used

- **Service Layer Pattern**: Encapsulates business logic and data access
- **Singleton Pattern**: For database connection management
- **MVC Pattern**: Model-View-Controller for UI separation

## Technologies Used

- **Java 17**: Core programming language
- **JavaFX 17**: UI framework for desktop applications
- **MySQL**: Relational database management system
- **Maven**: Build automation and dependency management
- **JDBC**: Database connectivity

## Prerequisites

Before running the application, ensure you have the following installed:

- Java Development Kit (JDK) 17 or higher
- MySQL Server 8.0 or higher
- Maven 3.6 or higher
- An IDE like IntelliJ IDEA or Eclipse with JavaFX support

## Installation and Setup

1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd SynapseEvent
   ```

2. **Install Dependencies**
   ```bash
   mvn clean install
   ```

## Database Setup

1. **Create Database**
   ```sql
   CREATE DATABASE synapse_event;
   ```

2. **Update Connection Configuration**
   - Open `src/main/java/com/synapseevent/utils/MaConnection.java`
   - Modify the connection parameters if needed:
     ```java
     String url = "jdbc:mysql://localhost:3306/synapse_event";
     String user = "root"; // Change as needed
     String password = ""; // Change as needed
     ```

3. **Create Tables**
   The application expects the following tables (you may need to create them manually or via scripts):
   - `Utilisateur` (Users)
   - `Role`
   - `Enterprise`
   - `PaddleEvent`
   - `FormationEvent`
   - `PartyingEvent`
   - `TeamBuildingEvent`
   - `AnniversaryEvent`

   Example table creation for User:
   ```sql
   CREATE TABLE Utilisateur (
       id BIGINT AUTO_INCREMENT PRIMARY KEY,
       email VARCHAR(255) NOT NULL,
       nom VARCHAR(255) NOT NULL,
       prenom VARCHAR(255) NOT NULL,
       role_id BIGINT,
       enterprise_id BIGINT,
       FOREIGN KEY (role_id) REFERENCES Role(id),
       FOREIGN KEY (enterprise_id) REFERENCES Enterprise(id)
   );
   ```

   Example table creation for Event (e.g., PaddleEvent):
   ```sql
   CREATE TABLE PaddleEvent (
       id BIGINT AUTO_INCREMENT PRIMARY KEY,
       name VARCHAR(255) NOT NULL,
       date DATE,
       description TEXT
   );
   ```

## Running the Application

### From IDE
- Open the project in your IDE
- Run `Main.java` as a JavaFX application

### From Command Line
```bash
mvn javafx:run
```

### Testing CRUD Operations
Run the console-based test:
```bash
java -cp target/classes com.synapseevent.TestCRUD
```

This will perform basic CRUD operations on Role, Entreprise, and User entities.

## Project Structure

```
src/
├── main/
│   ├── java/com/synapseevent/
│   │   ├── Main.java                 # Application entry point
│   │   ├── TestCRUD.java             # Console CRUD test
│   │   ├── controller/               # JavaFX controllers
│   │   ├── entities/                 # Entity classes
│   │   ├── service/                  # Business logic and data access services
│   │   └── utils/                    # Utility classes
│   └── resources/
│       └── fxml/                     # FXML UI files
└── test/
    └── java/                         # Unit tests
```

## How It Works

### Data Flow
1. **User Interaction**: User interacts with JavaFX UI defined in FXML files
2. **Controller Action**: Controllers handle events and call appropriate services
3. **Business Logic & Data Access**: Services implement business rules and execute SQL queries directly via JDBC connection
4. **Database**: MySQL stores and retrieves data

### Key Workflows
- **Event Management**: Create and manage various types of events (Paddle, Formation, Partying, Team Building, Anniversary)
- **User Registration**: Users register with role and entreprise association

## Development Guidelines

- Follow the layered architecture strictly
- Services handle both business logic and data access directly
- Use prepared statements for all database queries
- Implement proper error handling and logging
- Write unit tests for services and utilities
- Maintain consistent naming conventions (French for entities, English for code)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Future Enhancements

- REST API for mobile app integration
- Advanced reporting and analytics for events
- Email notifications for event updates
- Multi-language support
- Cloud deployment options
- Integration between different event types