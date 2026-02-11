# SynapseEvent

A comprehensive event management platform designed to streamline the organization and management of corporate events, from intimate celebrations to large-scale team building activities.

## Project Description

SynapseEvent is an all-in-one event management solution that helps organizations plan, organize, and execute various types of corporate events. The platform provides tools for managing user accounts, creating and browsing events, making bookings, submitting custom requests, and gathering feedback through reviews.

## Modules and Features

### 1. User Management Module
- **User Registration**: Create new user accounts with personal and company information
- **User Login**: Secure authentication to access the platform
- **User Profiles**: Manage user profiles with preferences and settings
- **Role-Based Access**: Different access levels for regular users and administrators

### 2. Event Management Module
Organize and manage five distinct types of corporate events:

- **Anniversary Events**: Plan and manage company anniversary celebrations and milestone events
- **Formation Events**: Organize training sessions, workshops, and professional development programs
- **Paddle Events**: Manage paddle sports activities and team building sessions
- **Partying Events**: Coordinate social gatherings, parties, and celebration events
- **Team Building Events**: Plan and execute team building activities to strengthen workplace relationships

### 3. Booking Management Module
- **Event Browsing**: Browse available events organized by type
- **Make Bookings**: Reserve spots for events with a streamlined booking process
- **Booking History**: View all past and current bookings
- **Admin Booking Oversight**: Administrators can view and manage all user bookings

### 4. Custom Event Request Module
- **Submit Custom Requests**: Request custom-tailored events outside the standard offerings
- **Request Tracking**: Track the status of custom event requests
- **Admin Review**: Administrators can review, approve, and manage custom requests

### 5. Review System Module
- **Submit Reviews**: Share feedback and ratings for attended events
- **Event Quality Improvement**: Help improve future events based on user feedback
- **Booking-Based Reviews**: Reviews linked to specific bookings for authenticity

### 6. Admin Dashboard Module
A comprehensive management interface for administrators:
- **Unified Event View**: View and manage all events in one centralized dashboard
- **Event Filtering**: Filter events by type for easy navigation
- **Dynamic Categories**: Create new event categories on-the-fly
- **Booking Management**: Oversee and manage all user bookings
- **Custom Request Management**: Review and process custom event requests
- **Event Templates**: Create and manage reusable event templates

### 7. User Dashboard Module
Personalized space for regular users:
- **Event Browsing**: Browse available events organized by category
- **My Bookings**: View and manage personal bookings
- **Custom Requests**: Submit and track custom event requests
- **My Reviews**: Manage reviews submitted for attended events
- **Preferences**: Set personal preferences for event types and notifications

## Setup

### Prerequisites
- Java Development Kit (JDK) 17 or higher
- MySQL Server 8.0 or higher
- Maven 3.6 or higher

### Installation

1. **Clone the Repository**
   ```bash
   git clone git@github.com:YessinTrifa/SynapseEvent.git
   cd SynapseEvent
   ```

2. **Install Dependencies**
   ```bash
   mvn clean install
   ```

3. **Database Setup**
   - Create a new MySQL database named `synapse_event`
   - Update database connection settings in `src/main/java/com/synapseevent/utils/MaConnection.java`
   - Run the initialization script to create tables and sample data

4. **Run the Application**
   ```bash
   mvn javafx:run
   ```

## GitHub Repository

[SynapseEvent GitHub Repository](git@github.com:YessinTrifa/SynapseEvent.git)

## Support

For questions or support, please contact the development team through the GitHub repository.
