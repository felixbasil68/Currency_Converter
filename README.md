# Currency_Converter
Currency Converter API Integration

A Spring Boot application that provides real-time currency exchange rates and conversion using the ExchangeRates API.

## Tech Stack

- Java 21  
- Spring Boot 3.4.5  
- Maven  
- RestTemplate  
- JUnit 5 + Mockito  
- Lombok  

## Setup & Run

### 1. Add Configuration

Update `src/main/resources/application.properties`:

application.properties

spring.application.name=Currency-Converter
token=YOUR_API_KEY_HERE
currency.api.url=https://api.exchangeratesapi.io/v1/latest?access_key=

### 2. Build and Run
bash
Copy
Edit
mvn clean install
mvn spring-boot:run
The application will start at: http://localhost:8080

### 3. API Endpoints
GET /api/rates
Fetch exchange rates for a base currency.
Example: /api/rates?base=USD

POST /api/convert
Convert currency amount.

####Request:
{
  "from": "USD",
  "to": "EUR",
  "amount": 100
}
#### Response:
{
  "from": "USD",
  "to": "EUR",
  "originalAmount": 100.0,
  "convertedAmount": 85.0
}


