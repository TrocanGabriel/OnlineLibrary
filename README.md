The application uses h2 database.
When running the integration tests inside the classes BookControllerIntegrationTest and CustomerControllerIntegrationTest edit the environment variables in the run configuration with the following line:
-Dpassword="SET_PASSWORD"
By doing so you are setting the password used for the test user that will be created when the integration tests run.
