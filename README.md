# Currency Converter

A Spring Boot application that provides real-time currency exchange rates and conversion using the ExchangeRates API.

---

## Tech Stack

- Java 21  
- Spring Boot 3.4.5  
- Maven  
- RestTemplate  
- JUnit 5 + Mockito  
- Lombok  
- Swagger UI (SpringDoc OpenAPI)

##  Setup & Run

### 1. Clone the Repository

git clone https://github.com/your-username/currency-converter.git
cd currency-converter
### 2. Add Configuration
Edit the src/main/resources/application.properties file:

spring.application.name=Currency-Converter
token=YOUR_API_KEY_HERE
currency.api.url=https://api.exchangeratesapi.io/v1/latest?access_key=
Replace YOUR_API_KEY_HERE with your actual API key from ExchangeRatesAPI.

### 3. Build & Run the Application

mvn clean install
mvn spring-boot:run
The application will start at: http://localhost:8080

#### API Endpoints
➤ GET /api/rates
Fetch exchange rates for a base currency.

Example:
GET /api/rates?base=USD
➤ POST /api/convert
Convert currency amount.

##### Request Body:
{
  "from": "USD",
  "to": "EUR",
  "amount": 100
}
##### Sample Response:
{
  "from": "USD",
  "to": "EUR",
  "originalAmount": 100.0,
  "convertedAmount": 85.0
}
### 4. Run Unit Tests
To execute unit tests:

mvn test
Test results will be displayed in the terminal.

### 5. Swagger API Docs
You can explore the API using Swagger UI once the app is running:

http://localhost:8080/swagger-ui/index.html

This interface allows interactive testing of all endpoints.

Author
Developed by Felix Deon Basil


