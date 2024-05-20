# Fabflix

## About
Fabflix is an AWS hosted full stack web app that allows logged-in users to browse and purchase movies using Tomcat and a MySQL database. There is also a simplified [Android version](/Android) that communicates with the backend api to perform fulltext search. On top of that, this repo includes an [XML parser](/xmlParser) capable of parsing XML files containing movies, stars, and genres to add into the database.

## Configuration

### TomCat
  - **Username:** `admin`
  - **Password:** `mypassword`

### MySQL
  - **Username:** `mytestuser`
  - **Password:** `My6$Password`

#### Set Up
    
   1. Create the database
      ```
      mysql -u mytestuser -p < create_table.sql
      ```
   2. Update the sales table
      ```
      mysql -u mytestuser -p < update_sales_table.sql
      ```
   3. Add stored procedures
      ```
      mysql -u mytestuser -p < stored-procedure.sql
      ```
   4. Create indexes
      ```
      mysql -u mytestuser -p < create_index.sql
      ```

## Deployment

### AWS

#### Single Instance
  - Run `mvn package` in the directory where pom.xml is located
  - Then run `cp ./target/*.war /var/lib/tomcat/webapps/` to copy the war file into tomcat/webapps

#### Scaling up
  - In both the master and slave instance:
    - Run `mvn package` in the directory where pom.xml is located
    - Then run `cp ./target/*.war /var/lib/tomcat/webapps/` to copy the war file into tomcat/webapps
  - Set up Apache2 webserver on the load balance instance by creating a load balancer proxy for the master and slave instance and make it so it is configured to enable load balancing, Connection Pooling, and sticky sessions

### Android
  - If the server is running on localhost, the Android app should be able to run properly without needing any changes
  - If the server is running on AWS, the urls will need to be changed in each of the files so it can make calls to the server

## Features

### Fabflix Desktop
  <details> <summary><strong>General User</strong></summary>
    
  - Login
  - Login Filter
  - reCaptcha Verification
  - Top 20 Movies Page
  - Single Movie Page
  - Single Star Page
  - Search by Genre
  - Search by Start Character
  - Advanced Search
    - Search by Title, Director, Year, and/or Star
    - Substring Matching Design
      - %AB%: For a query 'AB', it will return all strings the contain the pattern 'AB' in the results
      - LIKE '%AB%'
  - Fulltext Search
  - Search Autocomplete
  - Persisting Search Results Page
    - Even if the user navigates to a different page, their previous search results will be kept and can be accessed on the search results page
  - Search Results Pagination
  - Search Results Filters
    - Sorting
      - Sort by Title
      - Sort by Rating
      - Any combination of sorting ascending or descending can be applied
    - Page Size Limits
  - Add Movie to Cart
  - Movie Cart Page
    - Users can increment/decrement the copies of a movie in their cart
    - Users can delete a movie from their cart
  - Payment Page
  - Payment Confirmation Page
  </details>
 
  <details><summary><strong>Employees/Developers</strong></summary>
    
  - Employee Login
  - Employee Login Filter
  - reCaptcha Verifications
  - Database Metadata Page
  - Database Modifications using Stored Procedures
    - Adding a New Movie
      - Existing Movie: The title, director, and year inputted matches a movie in the database
    - Adding a New Star
      - Existing Star: Both the star name and the star birth year inputted matches a star in the database
    - Adding a New Genre
      - Existing Genre: The name inputted matches a genre in the database
  </details>

  <details><summary><strong>Others</strong></summary>
    
  - XML Parser
    - To find more information about this, refer to the [XML Parser README](xmlParser/README.md)
  - Password Encryption
  - Prepared Statements
    - Files with Prepared Statements
      - [GenreResultServlet.java](src/GenreResultServlet.java)
      - [StartTitleResultServlet.java](src/StartTitleResultServlet.java)
      - [SearchResultServlet.java](src/SearchResultServlet.java)
      - [SingleMovieServlet.java](src/SingleMovieServlet.java)
      - [SingleStarServlet.java](src/SingleStarServlet.java)
      - [MoviesServlet.java](src/MoviesServlet.java)
      - [PaymentServlet.java](src/PaymentServlet.java)
      - [CartServlet.java](src/CartServlet.java)
      - [ConfirmationServlet.java](src/ConfirmationServlet.java)
      - [MainInitServlet.java](src/MainInitServlet.java)
      - [MetadataServlet.java](src/MetadataServlet.java)
      - [AddGenreServlet.java](src/AddGenreServlet.java)
      - [AddMovieServlet.java](src/AddMovieServlet.java)
      - [AddStarServlet.java](src/AddStarServlet.java)
  - Connection Pooling
    - All code/configuration files using JDBC Connection Pooling
        - [GenreResultServlet.java](src/GenreResultServlet.java)
        - [StartTitleResultServlet.java](src/StartTitleResultServlet.java)
        - [SearchResultServlet.java](src/SearchResultServlet.java)
        - [SingleMovieServlet.java](src/SingleMovieServlet.java)
        - [SingleStarServlet.java](src/SingleStarServlet.java)
        - [MoviesServlet.java](src/MoviesServlet.java)
        - [PaymentServlet.java](src/PaymentServlet.java)
        - [CartServlet.java](src/CartServlet.java)
        - [ConfirmationServlet.java](src/ConfirmationServlet.java)
        - [MainInitServlet.java](src/MainInitServlet.java)
        - [MetadataServlet.java](src/MetadataServlet.java)
        - [AddGenreServlet.java](src/AddGenreServlet.java)
        - [AddMovieServlet.java](src/AddMovieServlet.java)
        - [AddStarServlet.java](src/AddStarServlet.java)
        - [Autocomplete.java](src/Autocomplete.java)
        - [FulltextServlet.java](src/FulltextServlet.java)
        - [LoginServlet.java](src/LoginServlet.java)
        - [EmployeeLoginServlet.java](src/EmployeeLoginServlet.java)
    
    - How is Connection Pooling utlized in the code?
        - Any servlet file in the src directory that needs to access the database should be using JDBC Connection Pooling
        - Multiple connections are established with a pool which saves having to open and close a connection each time a computation is done
        - When a connection is need to do a computation, an available connection from the pool is used and then it is put back after the computation is complete
    
    - How does Connection Pooling works with the two backend SQLs
        - Since there are two backend SQL (Master and Slave), there will be a connection pool for each of them meaning there are two separate connection pools, one for Master and one for Slave
        - For each datasource based on how they are defined in [context.xml](WebContent/META-INF/context.xml):
            - There will be at most 100 connections (maxTotal)
            - If more than 30 connections are not used, some of the connections will be closed to save resources (maxIdle)
            - The connection will timeout and fail after waiting for 10000 ms (maxWaitMillis)
  
  - Master/Slave Setup
    - All code/configuration files that contains routing queries to Master/Slave SQL.
        - [context.xml](WebContent/META-INF/context.xml) define the datasources for routing queries
          - *Note: This is currently set to localhost. To use this, the master SQL url has to be changed*
        - These files have their queries routed to the Master SQL because of inserting data into the database:
            - [PaymentServlet.java](src/PaymentServlet.java)
            - [AddGenreServlet.java](src/AddGenreServlet.java)
            - [AddMovieServlet.java](src/AddMovieServlet.java)
            - [AddStarServlet.java](src/AddStarServlet.java)
        - These files have their queries routed to the localhost which is randomized by the load balancer:
            - [Autocomplete.java](src/Autocomplete.java)
            - [CartServlet.java](src/CartServlet.java)
            - [ConfirmationServlet.java](src/ConfirmationServlet.java)
            - [EmployeeLoginServlet.java](src/EmployeeLoginServlet.java)
            - [FulltextServlet.java](src/FulltextServlet.java)
            - [GenreResultServlet.java](src/GenreResultServlet.java)
            - [LoginServlet.java](src/LoginServlet.java)
            - [MainInitServlet.java](src/MainInitServlet.java)
            - [MetadataServlet.java](src/MainInitServlet.java)
            - [MoviesServlet.java](src/MoviesServlet.java)
            - [SearchResultServlet.java](src/SearchResultServlet.java)
            - [SingleMovieServlet.java](src/SingleMovieServlet.java)
            - [SingleStarServlet.java](src/SingleStarServlet.java)
            - [StartTitleResultServlet](src/StartTitleResultServlet.java)

    - #### How are read/write requests routed to Master/Slave SQL?
        - Read requests should go to either the Master or Slave SQL since it does not involve making any changes to the database this is done by the load balancer
        - Write requests should only go to the Master SQL because only changes made in the master will be replicated to the slave and changes in slave will not be replicated to the master, so for when a record is inserted into the databases (ex. payment, adding movie/star/genre) it will directly call the Master SQL to do the insertion so both databases will remain identical
  
  - Load Balancer
  - JMeter Logs Processing
    - To find more information about this, refer to the [JMeter Logs Processor README](logs/README.md)
  </details>

### Fablix Mobile
  - Login
  - Fulltext Search
  - Search Result Pagination
  - Single Movie Page

## Walkthrough

### Fabflix Desktop
  - General User Demo: [https://youtu.be/8nQBS5R8PmY](https://youtu.be/8nQBS5R8PmY)
  - XML Parser + Employees Demo: [https://www.youtube.com/watch?v=SvKjiEYw5qw](https://www.youtube.com/watch?v=SvKjiEYw5qw)
  - JMeter + Log Processing Demo: [https://www.youtube.com/watch?v=8HXejHavZqo](https://www.youtube.com/watch?v=8HXejHavZqo)
  - YouTube Playlist of Previous Demos: [https://www.youtube.com/playlist?list=PL1J9ZWxAQEApzhaZzw-9r8DWgL3SLbRZs](https://www.youtube.com/playlist?list=PL1J9ZWxAQEApzhaZzw-9r8DWgL3SLbRZs)
  - ***Note:** Some of the demos were recorded before all the features were implemented, which is why some features are missing in some videos*

### Fabflix Mobile
<img src='img/fabflix-mobile-demo.gif' height=420 width=auto alt='fabflix mobile video walkthrough'/>

## Additional Notes
### XMLParser
  - There is a [README file](xmlParser/README.md) in the `xmlParser` directory with additional information about how to run the parser, the assumptions made while parsing the files, and the inconsistency files logged during parsing
### JMeter Log Processing
  - There is a [README file](logs/README.md) in the `logs` directory with additional information about how to run the JMeter logs processor and JMeter measurements for the web app
### API + Database
  - Refer to [API.md](API.md) for more information about the API endpoints and database schema

## Contributors
Vanessa Tang [@v74c63t](https://github.com/v74c63t)

Haver Ho [@haverh](https://github.com/haverh)
