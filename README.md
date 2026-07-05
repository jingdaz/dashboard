# dashboard
Dashboard service implementation for federal holiday management

### Reference Documentation
For requirements, please check the document "Federal Holiday Assessment.md".

### Implementation Notes
- The project is implemented using Spring Boot framework.
- The project uses postgresql database for storing the holidays.
- Uploaded files for federal holidays are in json format.
- Country/Date should be unique for each holiday, so the combination of country and date is used as a unique constraint in the database.

### Guides
- Database Setup Instructions:
> docker run --name my-postgres -e POSTGRES_PASSWORD=test -p 5555:5432 -d postgres

- Test instructions:
0) Ensure that the PostgreSQL container is running and accessible at localhost:5555 with username "postgres" and password "test".
1) Run "mvn clean package" at the project root directory to build the project.
2) Run "mvn spring-boot:run" at the project root directory to start the application.
3) Start Postman to test the API endpoints and test file in Postman collection is provided in the project /resources/test_data directory. You can import it into Postman to test the API endpoints.
4) Swagger documentation is available at http://localhost:8080/swagger-ui.html after running.
5) Test upload file is provided in the project /resources/test_data directory, named as "holidays.json" and etc for uploading.
6) After testing, you can stop the application by pressing Ctrl+C in the terminal where the application is running.
7) To stop the PostgreSQL container, run the following command in your terminal:
   docker stop my-postgres