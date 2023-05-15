# CS122B Project 3
## Instructions
### Deployment
Run `mvn package` in the directory where pom.xml is located.<br>Then run `cp ./target/*.war /var/lib/tomcat/webapps/` to copy the war file into tomcat/webapps.
### Demo
**URL:** `https://youtu.be/LrEOEWVIfdo`
### AWS
**URL:** `https://52.53.201.247:8443/cs122b-project3`
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
### XMLParser
There is a [README file](xmlParser/README.md) in the xmlParser directory that details what each inconsistency file contains and what assumptions were made when parsing the xml files.
#### Parsing Time Optimization Strategies
  1. We used in memory hash maps to store information from the database and information we plan to insert into the database so we did not have to query against the database constantly in order to find duplicates or to find whether a movie/star/genre already exists. 
  2. We wrote the data that was parsed from the xml files to csv files (one csv file for each table that is going to be inserted into) so we can use LOAD DATA from SQL to load all the information into each of the tables all at once instead of having to send multiple insert queries throughout parsing to the database. 
#### Results
No of Inserted Movies: 8648
No of Inserted Genres: 27
No of Inserted Stars: 6215
No of Records Inserted into Genres_in_Movies: 9724
No of Records Inserted into Stars_in_Movies: 27973
No of Movie Inconsistencies: 3423
No of Duplicated Movies: 34
No of Duplicated Stars: 648
No of Missing Movies: 1687
No of Missing Stars: 13931
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  7.755 s
[INFO] Finished at: 2023-05-15T06:39:15Z
[INFO] ------------------------------------------------------------------------
## Contributions
### Vanessa
  - reCaptcha Error Message
  - HTTPS
  - Encrypted Password Changes
  - Prepared Statements
  - Employees Login Filter
  - Dashboard Home (HTML/CSS)
  - Stored Procedures
  - Add Star Servlet
  - Add Genre Servlet
  - Add Movie Page (HTML/CSS)
  - Add Star Page (HTML/CSS/JS)
  - Add Genre Page (HTML/CSS/JS)
  - xmlParser
  - Making Revisions/Style Changes to Other Pages
  - Debugging
### Haver
  - reCaptcha Setup
  - Encrypted Password Changes
  - Employees Login (HTML/CSS/JS)
  - Employees Login Servlet/Filter
  - Dashboard Home (HTML/CSS/JS)
  - Metadata Servlet
  - Stored Procedures
  - Add Movie Servlet
  - Add Movie Page (HTML/CSS/JS)
  - Add Star Page (HTML/CSS)
  - Add Genre Page (HTML/CSS)
  - xmlParser
  - Making Revisions/Style Changes to Other Pages
  - Debugging
