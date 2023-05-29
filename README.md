# CS122B Project 5
## Instructions
### Deployment
Run `mvn package` in the directory where pom.xml is located.<br>Then run `cp ./target/*.war /var/lib/tomcat/webapps/` to copy the war file into tomcat/webapps.
### Demo
**URL:** ` `
### AWS
**URL:** ` `
### TomCat
**Username:** `admin`<br>**Password:** `mypassword`
### MySQL
**Username:** `mytestuser`<br>**Password:** `My6$Password`<br>**Create Database File:** `create_table.sql`<br>**Stored Procedures File:** `stored-procedure.sql`
## Additional Notes
### Substring Matching Design
  - %AB%: For a query 'AB', it will return all strings the contain the pattern 'AB' in the results
  - LIKE '%AB%'
### Files with Prepared Statements
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
### Stored Procedures
  - Existing Star: Both the star name and the star birth year inputted matches a star in the database
  - Existing Movie: The title, director, and year inputted matches a movie in the database
  - Existing Genre: The name inputted matches a genre in the database
### XMLParser
There is a [README file](xmlParser/README.md) in the xmlParser directory that details what each inconsistency file contains and what assumptions were made when parsing the xml files.
#### Parsing Time Optimization Strategies
  1. We used in memory hash maps to store information from the database and information we plan to insert into the database so we did not have to query against the database constantly in order to find duplicates or to find whether a movie/star/genre already exists. 
  2. We wrote the data that was parsed from the xml files to csv files (one csv file for each table that is going to be inserted into) so we can use LOAD DATA from SQL to load all the information into each of the tables all at once instead of having to send multiple insert queries throughout parsing to the database. 
## Contributions
### Vanessa
  - 
### Haver
  - 
