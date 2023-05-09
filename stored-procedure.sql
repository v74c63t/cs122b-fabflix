USE moviedb; -- not sure if this is needed
-- HELPER FOR NEXT AVAILABLE INT
--      MOVIE - CONCAT('tt', CAST(movie AS UNSIGNED))
--      STAR - CONCAT('nm', CAST(star AS UNSIGNED))
-- TO BE MOVED TO CREATE_TABLE???
CREATE TABLE availableInt (
    movie INT,
    star INT,
    genre INT
);

INSERT INTO availableInt VALUES(
    (SELECT CAST(SUBSTRING(MAX(id),3) AS UNSIGNED)+1 FROM movies),
    (SELECT CAST(SUBSTRING(MAX(id),3) AS UNSIGNED)+1 FROM stars),
    (SELECT MAX(id) + 1 FROM genres)
);

DELIMITER $$
CREATE PROCEDURE add_movie (IN movie_title VARCHAR(100), movie_year INT, movie_director VARCHAR(100), star_name VARCHAR(100), star_birth_year INT, genre_name VARCHAR(32) ) -- add in genre and star info too
BEGIN
    DECLARE movie_id VARCHAR(10);
    DECLARE star_id VARCHAR(10);
    DECLARE genre_id INT;
    -- check if movie already exists
    -- IF EXISTS(SELECT * FROM movies WHERE title = in_title AND year = in_year) THEN
    -- check if star already exists
    -- check if genre already exists
    -- if not insert them

    IF EXISTS(SELECT * FROM movies WHERE title = movie_title AND year = movie_year AND director = movie_director) THEN
        -- send a message saying the movie already exists and end the procedure
        SELECT CONCAT("Movie(", movie_title, ") already exists") as message;
    ELSE
        SET movie_id = (SELECT movie FROM availableInt);
        UPDATE availableInt SET movie = movie + 1;

        -- CHECK STAR
        IF EXISTS(SELECT * FROM stars WHERE name = star_name AND birthYear = star_birth_year) THEN
            SET star_id = (SELECT id FROM stars WHERE name = star_name AND birthYear = star_birth_year);
        ELSE
            -- parse and increment id
            SET star_id = (SELECT star FROM availableInt);
            UPDATE availableInt SET star = star + 1;
            INSERT INTO stars (id, name, birthYear) VALUES (star_id, star_name, star_birth_year);
        END IF;

        -- CHECK GENRE
        IF EXISTS(SELECT * FROM genres WHERE name = genre_name) THEN
            SET genre_id = (SELECT id FROM genres WHERE name = genre_name);
        ELSE
            -- parse and increment id
            # 		SET genreId = (select max(id) + 1 from genres);
            -- but its autoincrement so i dont think we need to set genreId?
            SET genre_id = (SELECT genre FROM availableInt);
            UPDATE availableInt SET genre = genre + 1;
            INSERT INTO genres (id, name) VALUES (genre_id, genre_name);
        END IF;

        INSERT INTO movies (id, title, year, director) VALUES(movie_id, movie_title, movie_year, movie_director);
        INSERT INTO stars_in_movies(starId, movieId) VALUES (star_id, movie_id);
        INSERT INTO genres_in_movies(genreId, movieId) VALUES (genre_id, movie_id);
        -- send a message saying movie was successfully added
        SELECT CONCAT("Movie(", movie_title, ") was successfully added") as message;
    END IF;
END
$$
-- Change back DELIMITER to ;
DELIMITER ;
# SHOW PROCEDURE STATUS WHERE db = 'moviedb';

