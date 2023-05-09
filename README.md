# CS122B Project 3
## Instructions
### Deployment
Run `mvn package` in the directory where pom.xml is located.<br>Then run `cp ./target/*.war /var/lib/tomcat/webapps/` to copy the war file into tomcat/webapps.
### Demo
**URL:** 
### AWS
**URL:** 
### TomCat
**Username:** `admin`<br>**Password:** `mypassword`
### MySQL
**Username:** `mytestuser`<br>**Password:** `My6$Password`<br>**Create Database File:** `create_table.sql`
## Additional Notes
### Substring Matching Design
  - %AB%: For a query 'AB', it will return all strings the contain the pattern 'AB' in the results
  - LIKE '%AB%'
### Files with Prepared Statements
  - GenreResultServlet.java
  - StartTitleResultServlet.java
  - SearchResultServlet.java
  - SingleMovieServlet.java
  - SingleStarServlet.java
### Parsing Time Optimization Strategies
  1. 
  2. 
## Contributions
### Vanessa
  - reCaptcha Error Message
  - HTTPS
  - Encrypted Password Changes
  - Prepared Statements
  - Dashboard Home
  - Stored Procedures
  - Add Star Servlet
  - Add Genre Servlet
  - Movie Page
  - Star Page
  - Genre Page
  - Making Revisions/Style Changes to Other Pages
  - Debugging
  - 
### Haver
  - reCaptcha Setup
  - Encrypted Password Changes
  - Employees Login
  - Dashboard Home
  - Metadata Servlet
  - Stored Procedures
  - Add Movie Servlet
  - Movie Page
  - Star Page
  - Genre Page
  - Making Revisions/Style Changes to Other Pages
  - Debugging
  - 
