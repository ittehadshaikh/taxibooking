# A Taxi Booking System
This is a taxi booking system that provides a REST API to book from a collection of 3 taxis.
Please refer to the REST API documentation for the API details and also examples of requests & responses.

## Design
The server uses the Sprint Boot infrastructure and Maven for build & test management.

### Classes
* **TaxiBooker**: This is the main REST Resource Controller class that handles HTTP requests.
* **Taxi**: Represents a taxi and actions that it can perform.
* **BookingStatus**: Represents the information returned to the client following a successful booking.
* **RideCoordinates**: Represents the source & destination 2D coordinates sent by the client when requesting a booking.

## To Run
### Prerequisites
* Maven version 3.5.2 or later
* Java 1.8
### Build
* Download this GIT repo
* Use "mvn compile" to build.
* Use "mvn package" to run the test cases and build the JAR.
### Launch
* Use "mvn spring-boot:run" to launch application
### Book Taxis
* Use "curl" or any such HTTP client library to send messages. See the REST API documentation for examples.
