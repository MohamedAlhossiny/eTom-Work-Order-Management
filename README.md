# eTom Work Order Management System

A RESTful API for managing work orders, including items, places, and related parties.

## Features

- Create, read, update, and delete work orders
- Manage work order items with actions and sequences
- Track locations and places associated with work orders
- Handle related parties (customers, technicians, etc.)
- Support for work order cancellation requests
- Date-based filtering and searching
- ISO-8601 formatted timestamps

## API Endpoints

### Work Orders

#### GET /workOrder
Retrieve all work orders with optional filters:
- `state`: Filter by work order state
- `priority`: Filter by priority level
- `externalId`: Filter by external ID
- `startDate`: Filter by start date
- `endDate`: Filter by end date
- `createdAfter`: Filter by creation date
- `createdBefore`: Filter by creation date

Example:
```bash
curl -X GET "http://localhost:8080/api/workOrder?state=active&priority=high"
```

#### GET /workOrder/{id}
Retrieve a specific work order by ID.

Example:
```bash
curl -X GET http://localhost:8080/api/workOrder/1
```

#### POST /workOrder
Create a new work order.

Example:
```bash
curl -X POST http://localhost:8080/api/workOrder \
-H "Content-Type: application/json" \
-d '{
    "externalId": "WO-001",
    "state": "active",
    "priority": "high",
    "description": "Test work order",
    "startDate": "2024-01-01T00:00:00.000Z",
    "endDate": "2024-12-31T23:59:59.000Z",
    "items": [
        {
            "action": "test_action",
            "description": "test item",
            "state": "pending",
            "sequence": 1
        }
    ],
    "places": [
        {
            "role": "location",
            "name": "Main Office",
            "address": "123 Main St",
            "city": "New York",
            "state": "NY",
            "zipCode": "10001"
        }
    ],
    "relatedParties": [
        {
            "role": "customer",
            "name": "John Doe",
            "email": "john.doe@example.com",
            "phone": "123-456-7890"
        }
    ]
}'
```

### Cancellation Requests

#### GET /cancelWorkOrder
Retrieve all cancellation requests with optional filters:
- `status`: Filter by request status
- `workOrderId`: Filter by work order ID
- `startDate`: Filter by request date
- `endDate`: Filter by request date

Example:
```bash
curl -X GET "http://localhost:8080/api/cancelWorkOrder?status=pending"
```

#### GET /cancelWorkOrder/{id}
Retrieve a specific cancellation request by ID.

Example:
```bash
curl -X GET http://localhost:8080/api/cancelWorkOrder/1
```

#### POST /cancelWorkOrder
Create a new cancellation request.

Example:
```bash
curl -X POST http://localhost:8080/api/cancelWorkOrder \
-H "Content-Type: application/json" \
-d '{
    "workOrderId": 1,
    "reason": "Customer requested cancellation",
    "requestedBy": "john.doe@example.com"
}'
```

## Data Models

### WorkOrder
- `id`: Unique identifier
- `externalId`: External reference ID
- `state`: Current state (active, completed, cancelled)
- `priority`: Priority level (high, medium, low)
- `description`: Work order description
- `startDate`: Start date and time
- `endDate`: End date and time
- `createdAt`: Creation timestamp
- `updatedAt`: Last update timestamp
- `items`: List of work order items
- `places`: List of associated places
- `relatedParties`: List of related parties

### WorkOrderItem
- `id`: Unique identifier
- `workOrderId`: Associated work order ID
- `action`: Action to be performed
- `description`: Item description
- `state`: Current state
- `sequence`: Execution sequence

### PlaceRef
- `id`: Unique identifier
- `workOrderId`: Associated work order ID
- `role`: Place role (location, site, etc.)
- `name`: Place name
- `address`: Street address
- `city`: City
- `state`: State/province
- `zipCode`: Postal code

### RelatedParty
- `id`: Unique identifier
- `workOrderId`: Associated work order ID
- `role`: Party role (customer, technician, etc.)
- `name`: Party name
- `email`: Email address
- `phone`: Phone number

### CancelWorkOrder
- `id`: Unique identifier
- `workOrderId`: Associated work order ID
- `reason`: Cancellation reason
- `status`: Request status (pending, approved, rejected)
- `requestedBy`: Requester identifier
- `requestedAt`: Request timestamp
- `processedAt`: Processing timestamp

## Date Format
All dates are formatted according to ISO-8601 standard with UTC timezone:
```
yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
```

Example: `2024-03-31T12:30:10.816Z`

## Development

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher

### Setup
1. Clone the repository
2. Configure database connection in `src/main/resources/application.properties`
3. Run database migrations:
```bash
mysql -u your_username -p your_database < DBCreate.sql
```
4. Build the project:
```bash
mvn clean package
```
5. Run the application:
```bash
java -jar target/workorder-1.0-SNAPSHOT.jar
```

## License
This project is licensed under the MIT License - see the LICENSE file for details. 