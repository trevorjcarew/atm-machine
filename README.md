# atm-machine

To build and run tests use: mvn clean install

To run use: mvn spring-boot:run

Using in memory h2 database with two basic stand-alone tables (account, bank_note). 
data.sql populates these and DB is accessible at: http://localhost:8080/console/
jdbc url: jdbc:h2:mem:example-app
username: sa
password: sa

To test: Swagger Url - http://localhost:8080/swagger-ui/

Metrics avaialable via - http://localhost:8080/actuator/

This project uses lombok so plugin may need to be installed in IDE if not already done (this shoudln't affect building or running the application)