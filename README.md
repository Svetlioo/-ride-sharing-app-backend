# Ride-Sharing App Backend

## Overview
This backend system powers a ride-sharing application using **Spring Boot** and **MongoDB**. It focuses on efficient geospatial queries to match riders with nearby drivers, complete ride lifecycle management, and transaction processing. The system leverages MongoDB's geospatial indexing capabilities for high-performance location-based searches.

---

## Features

### Core Features
- **Rider Management**: User registration and ride history  
- **Driver Management**: Registration, real-time location tracking, and availability status  
- **Geospatial Driver Search**: Find available drivers near specific locations using efficient geospatial queries  
- **Ride Lifecycle Management**: From request to completion with status tracking  
- **Transaction Processing**: Handle payments with various methods and status tracking  

### Technical Features
- MongoDB geospatial indexing for efficient proximity searches  
- RESTful API with Swagger documentation  
- MongoDB aggregation pipelines for analytics  
- Document-based data model with references between collections  

---

## Technology Stack
- Java 17  
- Spring Boot  
- MongoDB  
- Spring Data MongoDB (with geospatial support)  
- Swagger/OpenAPI (for API documentation)  
- Maven (build tool)  

---

## Architecture

### Data Model

The application follows a domain-driven design with the following key models:

#### Users
- **User**: Abstract base class for users in the system  
- **Rider**: Passengers who request rides  
- **Driver**: Vehicle operators who provide rides  

#### Core Domain Objects
- **Location**: Stores geospatial coordinates with MongoDB's `GeoJsonPoint` and address information  
- **Ride**: Represents a journey with pickup and destination locations, status, and pricing  
- **Transaction**: Records payment details associated with completed rides  

---

## Key Controllers
- **RiderController**: Manages rider registration and information  
- **DriverController**: Handles driver registration, location updates, and availability  
- **RideController**: Processes ride requests, status updates, and completion  
- **TransactionController**: Provides payment processing and financial reporting  

---

## Key Technical Implementations

### Geospatial Driver Search

The system uses MongoDB's geospatial indexing and query capabilities to efficiently find available drivers near a rider's location:

- Filters only **available** drivers  
- Uses MongoDB’s `$near` operator for proximity search  
- Returns drivers **sorted by distance** from the rider  
- Sets a **maximum search radius** in meters  
- Leverages the `GEO_2DSPHERE` index for performance  

### Ride State Management

The system manages rides through a defined lifecycle:

- `REQUESTED`: Initial state when a rider requests a ride  
- `ACCEPTED`: A driver has been assigned to the ride  
- `PICKED_UP`: The driver has picked up the rider  
- `COMPLETED`: The ride has been completed  
- `CANCELLED`: The ride was cancelled by the rider or driver  

When a ride transitions to `COMPLETED`, the system automatically generates a transaction.

### Business Analytics with MongoDB Aggregation

The system uses MongoDB's **aggregation pipeline** for efficient business intelligence queries. This aggregation calculates total revenue for completed transactions within a specified date range without loading all records into memory.

---

## API Endpoints

### Rider APIs
- `POST /api/riders`: Register a new rider  
- `GET /api/riders/{id}`: Get rider details  

### Driver APIs
- `POST /api/drivers`: Register a new driver  
- `GET /api/drivers/nearby`: Find available drivers near a location  
- `PUT /api/drivers/{id}/location`: Update driver's current location  
- `PUT /api/drivers/{id}/availability`: Update driver's availability status  

### Ride APIs
- `POST /api/rides`: Request a new ride  
- `GET /api/rides/{id}`: Get ride details  
- `GET /api/rides/driver/{driverId}`: Get all rides for a driver  
- `GET /api/rides/rider/{riderId}`: Get all rides for a rider  
- `POST /api/rides/{id}/assign-driver`: Assign a driver to a ride  
- `PUT /api/rides/{id}/status`: Update ride status  
- `GET /api/rides/{id}/price`: Calculate ride price  

### Transaction APIs
- `GET /api/transactions/ride/{rideId}`: Get transactions for a specific ride  
- `GET /api/transactions/stats/revenue`: Get revenue statistics for a time period  
- `GET /api/transactions/stats/status`: Get transaction counts by status  

---

## Setup Instructions

### Prerequisites
- Java 17+  
- MongoDB 4.4+  
- Maven 3.6+  
