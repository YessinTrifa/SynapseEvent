# SynapseEvent

A comprehensive JavaFX-based event management system designed to streamline the organization and management of events, reservations, payments, and participants. This application provides a user-friendly interface for managing various aspects of event planning and execution.

## Overview

SynapseEvent is built using modern Java technologies and follows best practices in software architecture. It serves as a complete solution for event organizers to handle everything from user management to payment processing.

## Features

- **User Management**: Create and manage users with different roles and associated entreprises
- **Event Management**: Define events with details like name, type, description, base price, and maximum capacity
- **Reservation System**: Handle event reservations with participant tracking
- **Payment Processing**: Manage payments for reservations
- **Participant Management**: Track and manage event participants
- **Options Management**: Configure additional options for events
- **Dashboard**: Centralized view for managing all aspects of the system

## Architecture

The application implements a clean, layered architecture that promotes separation of concerns and maintainability:

### Layers

1. **Entity Layer** (`entities/`): Data model classes representing business entities
   - `User.java` - User information with role and entreprise associations
   - `Event.java` - Event details including pricing and capacity
   - `Reservation.java` - Reservation records
   - `Payment.java` - Payment transactions
   - `Participant.java` - Event participants
   - `Role.java` - User roles
   - `Entreprise.java` - Company/organization information
   - `Option.java` - Event options

2. **Data Access Layer** (`dao/`): Database interaction classes
   - Each entity has a corresponding DAO class (e.g., `EventDAO.java`)
   - Implements CRUD operations using JDBC
   - Uses prepared statements for security
   - Singleton connection management via `MaConnection`

3. **Service Layer** (`service/`): Business logic encapsulation
   - Service classes (e.g., `EventService.java`) that orchestrate DAO operations
   - Implements business rules and validation
   - Provides a clean API for controllers

4. **Presentation Layer** (`controller/`): JavaFX UI controllers
   - FXML-based controllers for each view
   - Handle user interactions and update the UI
   - Coordinate between services and views

5. **Utility Layer** (`utils/`): Helper classes
   - `MaConnection.java` - Database connection singleton
   - `AlertsUtil.java` - UI alert utilities
   - `DateUtil.java` - Date manipulation utilities

### Design Patterns Used

- **DAO Pattern**: Separates data access logic from business logic
- **Service Layer Pattern**: Encapsulates business logic
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
   - `Evenement` (Events)
   - `Utilisateur` (Users)
   - `Role`
   - `Entreprise`
   - `Reservation`
   - `Paiement` (Payments)
   - `Participant`
   - `Option`

   Example table creation for Event:
   ```sql
   CREATE TABLE Evenement (
       id BIGINT AUTO_INCREMENT PRIMARY KEY,
       nom VARCHAR(255) NOT NULL,
       type VARCHAR(100),
       description TEXT,
       prixBase DECIMAL(10,2),
       capaciteMax INT
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
│   │   ├── dao/                      # Data Access Objects
│   │   ├── entities/                 # Entity classes
│   │   ├── service/                  # Business logic services
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
3. **Business Logic**: Services implement business rules and delegate to DAOs
4. **Data Persistence**: DAOs execute SQL queries via JDBC connection
5. **Database**: MySQL stores and retrieves data

### Key Workflows
- **Event Creation**: Admin creates events with capacity and pricing
- **User Registration**: Users register with role and entreprise association
- **Reservation Process**: Users reserve spots in events
- **Payment Handling**: Process payments for reservations
- **Participant Tracking**: Manage event attendees

## Development Guidelines

- Follow the layered architecture strictly
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
- Advanced reporting and analytics
- Email notifications
- Multi-language support
- Cloud deployment options