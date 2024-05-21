# API Reference

## Authentication

### [`/api/login`](src/LoginServlet.java)

  - Input
    - email: String
    - password: String
    - device: the device sending the login request | ("Android", "JMeter", NULL)
      - Android and JMeter do not need reCaptcha verification so it is skipped in those cases
  - Ouput
    - Success
      - N/A
    - Fail
      - message: the error message shown to the user | ("reCaptcha Verification Error", "Incorrect password", "User with that email doesn't exist")
      - errorMessage: any error caused by an exception

### [`/api/employee-login`](src/EmployeeLoginServlet.java)

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

  - Input
    - genre: String
  - Output
    - Success
      - message: success message with the genre added and its associated genre id
    - Fail
      - message: the error message shown to the user if the genre already exists
      - errorMessage: any error caused by an exception

### [`/api/add-movie`](src/AddMovieServlet.java)

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

  - Output
    - Success
      - Array of JSON objects
        - genre_name: String
        - genre_id: int
    - Fail
       - errorMessage: any error caused by an exception

## Shopping Cart + Payment

### [`/api/cart`](src/CartServlet.java)

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

  - Input
  - Output
    - Success
    - Fail
      - errorMessage: any error caused by an exception

### [`/api/payment`](src/PaymentServlet.java)

  - Input
  - Output
    - Success
    - Fail
      - errorMessage: any error caused by an exception

## Search

### [`/api/fulltext`](src/FulltextServlet.java)

  - Input
  - Output
    - Success
    - Fail
      - errorMessage: any error caused by an exception

### [`/api/by-genre`](src/GenreResultServlet.java)

  - Input
  - Output
    - Success
    - Fail
      - errorMessage: any error caused by an exception

### [`/api/by-search`](src/SearchResultServlet.java)

  - Input
  - Output
    - Success
    - Fail
      - errorMessage: any error caused by an exception

### [`/api/by-start-title`](src/StartTitleResultServlet.java)

  - Input
  - Output
    - Success
    - Fail
      - errorMessage: any error caused by an exception

## Single Movie Page

### [`/api/single-movie`](src/SingleMovieServlet.java)

  - Input
  - Output
    - Success
    - Fail
      - errorMessage: any error caused by an exception

## Single Star Page

### [`/api/single-star`](src/SingleStarServlet.java)

  - Input
  - Output
    - Success
    - Fail
      - errorMessage: any error caused by an exception

## Top 20 Page

### [`/api/movies`](src/MoviesServlet.java)

  - Input
  - Output
    - Success
    - Fail
      - errorMessage: any error caused by an exception

# Database

## Database Schema

<img width="1122" alt="Screenshot 2024-05-18 at 3 46 09 AM" src="https://github.com/v74c63t/fabflix/assets/78942001/96a51db6-6e17-4e7c-87d2-bef9e7f32924">

*Database schema diagram created using [QuickDBD](https://app.quickdatabasediagrams.com/)*
