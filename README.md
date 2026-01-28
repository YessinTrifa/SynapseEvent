# SynapseEvent

A comprehensive JavaFX-based event management system designed to streamline the organization and management of corporate events, bookings, and custom requests. This application provides a user-friendly interface for managing various aspects of event planning and execution within enterprises.

## Overview

SynapseEvent is built using modern Java technologies and follows best practices in software architecture. It serves as a complete solution for event organizers to handle everything from user management to event booking, reviews, and custom requests.

## Features

- **User Management**: Create and manage users with different roles (Admin, User, Manager) and associated enterprises
- **Unified Event Management**: All events are managed through a unified EventInstance system with type classification:
  - Anniversary Events
  - Formation Events
  - Paddle Events
  - Partying Events
  - Team Building Events
- **Event Browsing by Type**: Users can browse available events organized by type in separate tabs, preventing information overload
- **Consolidated Admin Dashboard**: Admins can view and manage all events in a single consolidated interface with dynamic filtering by event type and the ability to create new event categories on-the-fly
- **Event Booking**: Users can view published events and make bookings with a streamlined booking system
- **Booking Management**: Admins can view and manage user bookings
- **Custom Event Requests**: Users can submit requests for custom events; admins can approve or manage them
- **Review System**: Users can submit reviews for events and bookings to provide feedback
- **User Preferences**: Users can set preferences for event types, notifications, and personalization
- **Event Templates**: Admins can create and manage event templates for quick and consistent event creation
- **Role-Based Dashboards**: Separate dashboards for admins (full management) and users (booking, requests, reviews, and preferences)
- **Data Migration**: Utility to migrate existing data to the unified event structure

## Architecture

The application implements a clean, layered architecture that promotes separation of concerns and maintainability:

### Layers

1. **Entity Layer** (`entities/`): Data model classes representing business entities
    - `User.java` - User information with role and entreprise associations
    - `Role.java` - User roles
    - `Entreprise.java` - Company/organization information
    - `EventInstance.java` - Unified event entity with type classification (Anniversary, Formation, Paddle, Partying, TeamBuilding)
    - `Booking.java` - Booking information linking users to events
    - `CustomEventRequest.java` - Custom event request details
    - `EventSummary.java` - Event summary for display purposes
    - `EventInstanceSummary.java` - Enhanced event summary with full details
    - `Review.java` - Review details for events and bookings
    - `UserPreferences.java` - User preference settings
    - `EventTemplate.java` - Event template information
    - Legacy entities (`AnniversaryEvent.java`, `FormationEvent.java`, etc.) - Maintained for compatibility

2. **Service Layer** (`service/`): Business logic and data access encapsulation
    - `EventInstanceService.java` - Unified service for all event operations with type-based filtering
    - Service classes for each entity (e.g., `UserService.java`, `BookingService.java`, `CustomEventRequestService.java`, `ReviewService.java`, `UserPreferencesService.java`, `EventTemplateService.java`)
    - Legacy event services (`AnniversaryEventService.java`, `FormationEventService.java`, etc.) maintained for compatibility
    - Implements CRUD operations using JDBC directly
    - Uses prepared statements for security
    - Provides a clean API for controllers

3. **Presentation Layer** (`controller/`): JavaFX UI controllers
    - FXML-based controllers for login, admin dashboard, user dashboard, and individual event management views
    - Handle user interactions and update the UI
    - Coordinate between services and views

4. **Utility Layer** (`utils/`): Helper classes
    - `MaConnection.java` - Database connection singleton
    - `AlertsUtil.java` - UI alert utilities
    - `DateUtil.java` - Date manipulation utilities
    - `CurrentUser.java` - Current user session management
    - `DatabaseInitializer.java` - Database initialization from schema.sql
    - `PasswordUtil.java` - Password hashing and verification utilities
    - `DataMigration.java` - Utility for migrating existing event data to unified structure

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

3. **Initialize Database**
    Run the InitDatabase class to create tables and insert sample data:
    ```bash
    java -cp target/classes com.synapseevent.InitDatabase
    ```
    This will execute the `schema.sql` file automatically, creating all necessary tables and inserting sample data.

4. **Migrate Existing Data (if upgrading)**
    If you have existing event data from previous versions, run the data migration utility:
    ```bash
    java -cp target/classes com.synapseevent.utils.DataMigration
    ```
    This will consolidate events from separate tables (AnniversaryEvent, FormationEvent, etc.) into the unified `event_instance` table with proper type classification.

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
│   │   ├── InitDatabase.java         # Database initialization
│   │   ├── Main.java                 # Application entry point
│   │   ├── TestCRUD.java             # Console CRUD test
│   │   ├── controller/               # JavaFX controllers
│   │   ├── entities/                 # Entity classes
│   │   ├── service/                  # Business logic and data access services
│   │   └── utils/                    # Utility classes
│   └── resources/
│       ├── schema.sql                # Database schema and sample data
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
- **User Authentication**: Users log in based on their roles (Admin, User, Manager)
- **Admin Event Management**: Admins create and manage events through a consolidated dashboard with unified EventInstance system; view all events in one table with dynamic filtering by type (Formation, Paddle, Partying, TeamBuilding, Anniversary, and custom types); create new event categories on-the-fly; manage bookings and custom requests; create and manage event templates
- **User Event Browsing**: Users browse published events organized by type in separate tabs, preventing information overload and improving discoverability
- **User Event Booking**: Users view published events by type and make bookings with a streamlined process
- **Custom Event Requests**: Users submit custom event requests; admins review and update statuses
- **Review Submission**: Users can submit reviews for events and bookings to provide feedback
- **User Preferences Management**: Users can set and update their preferences for event types and notifications
- **Dashboard Management**: Role-based dashboards provide appropriate functionalities including reviews and preferences
- **Data Migration**: One-time migration utility to consolidate existing event data into the unified structure

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

## Recent Updates

### v1.1.0 - Admin Dashboard UI Consolidation
- **Consolidated Event Management**: Unified all event type tabs (Formation, Paddle, Partying, TeamBuilding, Anniversary) into a single "Events" tab for improved admin user experience
- **Dynamic Event Filtering**: Added ComboBox filter to view events by type or see all events at once
- **Dynamic Event Type Creation**: Admins can now create new event categories directly from the UI without code changes
- **Streamlined Interface**: Reduced tab clutter and improved navigation efficiency in the admin dashboard

## Future Enhancements

- REST API for mobile app integration
- Advanced reporting and analytics for events
- Email notifications for event updates
- Multi-language support
- Cloud deployment options
- Integration between different event types