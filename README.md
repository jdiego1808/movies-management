# movies-management
A window-based application using Java Swing for managing movies.

As a client, you could see list of movies, you could add a movies in your favorites to see later perhaps. If you already watched the movies, you could mark it down.
As an admin user you could execute CRUD operations and all operations like a client.

The application is designed based on the MVC model. Controller layer is implemented in the Repositories directory with movie DAO(data access object) and user DAO. Model layer is implemented in Model directory adn GUI directory contains all Swing frames which interact to users.

## To run the application locally

- Set up database. There is a directory includes schemas.sql, functions.sql and triggers.sql.

   -- Open schemas.sql to create database and its tables. Then, run bulk insert statements also in this file.

   -- Create all procedures and functions as well as trigger in the other 2 files.

- Open file DBConfig.java in MovieManagement\src\main\java\com\mycompany\moviemanagement. Change your database authentication.

- Now, you could run the application by running file Startup.java
