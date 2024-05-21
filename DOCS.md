# API Reference

  - [API Reference](#api-reference)
    - [Authentication](#authentication)
      - [`/api/login`](#apilogin)
      - [`/api/employee-login`](#apiemployee-login)
    - [Employee Dashboard](#employee-dashboard)
      - [`/api/add-genre`](#apiadd-genre)
      - [`/api/add-movie`](#apiadd-movie)
      - [`/api/add-star`](#apiadd-star)
      - [`/api/metadata`](#apimetadata)
    - [Main Page](#main-page)
      - [`/api/maininit`](#apimaininit)
    - [Shopping Cart + Payment](#shopping-cart--payment)
      - [`/api/cart`](#apicart)
      - [`/api/confirmation`](#apiconfirmation)
      - [`/api/payment`](#apipayment)
    - [Search](#search)
      - [`/api/fulltext`](#apifulltext)
      - [`/api/by-genre`](#apiby-genre)
      - [`/api/by-search`](#apiby-search)
      - [`/api/by-start-title`](#apiby-start-title)
    - [Single Movie Page](#single-movie-page)
      - [`/api/single-movie`](#apisingle-movie)
    - [Single Star Page](#single-star-page)
      - [`/api/single-star`](#apisingle-star)
    - [Top 20 Page](#top-20-page)
      - [`/api/movies`](#apimovies)

## Authentication

### [`/api/login`](src/LoginServlet.java)

  - Description: This is for general user login. It checks to make sure that there exists a customer with the email provided and the password matches. Depending on the device sending the request, it will also check to make sure the user completed the reCaptcha. For the Android app and JMeter accessing the site, reCaptcha verification is not done.
  - Input
    - email: String
    - password: String
    - device: the device sending the login request | ("Android", "JMeter", NULL)
  - Ouput
    - Success
      - N/A
    - Fail
      - message: the error message shown to the user | ("reCaptcha Verification Error", "Incorrect password", "User with that email doesn't exist")
      - errorMessage: any error caused by an exception

### [`/api/employee-login`](src/EmployeeLoginServlet.java)

  - Description: This is for employee login. It checks to make sure that there exists a employee with the email provided and the password matches. It also makes sure that the user has completed the reCaptcha.
  - Input
    - email: String
    - password: String
  - Ouput
    - Success
      - N/A
    - Fail
      - message: the error message shown to the user | ("reCaptcha Verification Error", "Incorrect password", "User with that email doesn't exist")
      - errorMessage: any error caused by an exception

## Employee Dashboard

### [`/api/add-genre`](src/AddGenreServlet.java)

  - Description: This is for an employee to add a new genre into the database. If the genre does not already exist, it will be added to the database and its genre id will be returned.
  - Input
    - genre: String
  - Output
    - Success
      - message: success message with the genre added and its associated genre id
    - Fail
      - message: the error message shown to the user if the genre already exists
      - errorMessage: any error caused by an exception

### [`/api/add-movie`](src/AddMovieServlet.java)

  - Description: This is for an employee to add a new movie to the database. If the movie does not already exist, it will be added to the database and its movie id will be returned. On top of that, if the star and the genre in the input does not already exist in the database, those will be added too and their ids will also be returned.
  - Input
    - movie_title: String
    - movie_year: int
    - movie_director: String
    - star_name: String
    - star_birth_year: int
    - genre_name: String
  - Output
    - Success
      - message: success message with the movie added and its associated movie id and/or the added star/genre and their associated ids
    - Fail
      - message: the error message shown to the user if the movie already exists
      - errorMessage: any error caused by an exception

### [`/api/add-star`](src/AddStarServlet.java)

  - Description: This is for an employee to add a new star to the database. Once the star is added to the database, their associated id will be returned.
  - Input
    - star_name: String
    - star_birth_year: int
  - Output
    - Success
      - message: success message with the star added and its associated star id
    - Fail
      - message: the error message shown to the user
      - errorMessage: any error caused by an exception

### [`/api/metadata`](src/MetadataServlet.java)

  - Description: This shows the metadata of the database meaning it returns all the tables with their fields and their field types.
  - Output
    - Success
      - Array of JSON objects
        - table_name: String
        - fields: Array(String)
        - types: Array(String)
    - Fail
      - errorMessage: any error caused by an exception

## Main Page

### [`/api/maininit`](src/MainInitServlet.java)

  - Description: This helps initialize the main page. It returns all the current genres in the database and their ids to populate the browse by genre component. 
  - Output
    - Success
      - Array of JSON objects
        - genre_name: String
        - genre_id: int
    - Fail
       - errorMessage: any error caused by an exception

## Shopping Cart + Payment

### [`/api/cart`](src/CartServlet.java)

  - Description: This endpoint is called to add movies to the shopping cart. It will calculate the price of depending on the quantity of the movie and return the current list of movies in the shopping cart alongside the newly added item.
  - Input
    - item: movie id | String
    - quantity: int
  - Output
    - Success
      - Array of JSON objects
        - movie_id: String
        - movie_title: String
        - movie_quantity: int
        - movie_price: float
    - Fail
      - errorMessage: any error caused by an exception

### [`/api/confirmation`](src/ConfirmationServlet.java)

  - Description: This is used to populate the confirmation page with the purchased items and their information.
  - Output
    - Success
      - Array of JSON objects
        - sale_id: String
        - movie_id: String
        - movie_title: String
        - movie_quantity: int
        - movie_price: float
        - movie_total: float
    - Fail
      - errorMessage: any error caused by an exception

### [`/api/payment`](src/PaymentServlet.java)

  - Description: This verifies the credentials of the credit card information provided and process the payment if all the information is correct.
  - Input
    - ccId: String
    - firstName: String
    - lastName: String
    - expirationDate: String
  - Output
    - Success
      - N/A
    - Fail
      - message: the error message shown to the user | ("Cart is empty", "Invalid credit card information")
      - errorMessage: any error caused by an exception

## Search

### [`/api/fulltext`](src/FulltextServlet.java)

  - Description: This performs a full text search based on the query provided and returns a list of movies based on the sorting and filtering criterias.
  - Input
    - query: String
    - sortBy: String
    - numRecords: int
    - firstRecord: int
  - Output
    - Success
      - movie_id: String
      - movie_title: String
      - movie_year: int
      - movie_director: String
      - movie_rating: float | NULL
      - movie_stars: Array(String)
      - movie_genres: Array(String)
      - max_records: int
    - Fail
      - errorMessage: any error caused by an exception

### [`/api/by-genre`](src/GenreResultServlet.java)

  - Description: This searchs for any movies with the genre provided and returns a list of movies based on the sorting and filtering criterias.
  - Input
    - genreId: int
    - sortBy: String
    - numRecords: int
    - firstRecord: int
  - Output
    - Success
      - movie_id: String
      - movie_title: String
      - movie_year: int
      - movie_director: String
      - movie_rating: float | NULL
      - movie_stars: Array(String)
      - movie_genres: Array(String)
      - max_records: int
    - Fail
      - errorMessage: any error caused by an exception

### [`/api/by-search`](src/SearchResultServlet.java)

  - Description: This searchs for any movies with a particular title, year, director, and/or star and returns a list of movies based on the sorting and filtering criterias. The matching for this uses `%LIKE%` to find valid movies.
  - Input
    - title: String
    - year: int
    - director: String
    - star: String
    - sortBy: String
    - numRecords: int
    - firstRecord: int
  - Output
    - Success
      - movie_id: String
      - movie_title: String
      - movie_year: int
      - movie_director: String
      - movie_rating: float | NULL
      - movie_stars: Array(String)
      - movie_genres: Array(String)
      - max_records: int
    - Fail
      - errorMessage: any error caused by an exception

### [`/api/by-start-title`](src/StartTitleResultServlet.java)

  - Description: This searchs for any movies that starts with the character provided and returns a list of movies based on the sorting and filtering criterias.
  - Input
    - startTitle: char | [A-Z, 0-9, *]
    - sortBy: String
    - numRecords: int
    - firstRecord: int
  - Output
    - Success
      - movie_id: String
      - movie_title: String
      - movie_year: int
      - movie_director: String
      - movie_rating: float | NULL
      - movie_stars: Array(String)
      - movie_genres: Array(String)
      - max_records: int
    - Fail
      - errorMessage: any error caused by an exception

## Single Movie Page

### [`/api/single-movie`](src/SingleMovieServlet.java)

  - Description: This retrieves information about the movie associated with the provided id.
  - Input
    - id: movie id | String
  - Output
    - Success
      - movie_id: String
      - movie_title: String
      - movie_year: int
      - movie_director: String
      - movie_rating: float | NULL
      - movie_stars: Array(String)
      - movie_genres: Array(String)
    - Fail
      - errorMessage: any error caused by an exception

## Single Star Page

### [`/api/single-star`](src/SingleStarServlet.java)

  - Description: This retrieves information about the star associated with the provided id. It returns their general information and the movies they starred in.
  - Input
    - id: star id | String
  - Output
    - Success
      - Array of JSON objects of the movies that the star was in
        - star_id: String
        - star_name: String
        - star_dob: int
        - movie_id: String
        - movie_title: String
        - movie_year: int
        - movie_director: String
    - Fail
      - errorMessage: any error caused by an exception

## Top 20 Page

### [`/api/movies`](src/MoviesServlet.java)

  - Description: This retrieves the top 20 rated movies alongside their 3 most popular stars and all the movie's genres to display on the top 20 page.
  - Output
    - Success
      - movie_id: String
      - movie_title: String
      - movie_year: int
      - movie_director: String
      - movie_rating: float | NULL
      - movie_stars: Array(String)
      - movie_genres: Array(String)
    - Fail
      - errorMessage: any error caused by an exception

# Database

## Database Schema

<img width="1122" alt="Screenshot 2024-05-18 at 3 46 09 AM" src="https://github.com/v74c63t/fabflix/assets/78942001/96a51db6-6e17-4e7c-87d2-bef9e7f32924">

*Database schema diagram created using [QuickDBD](https://app.quickdatabasediagrams.com/)*
