DELIMITER $$
CREATE PROCEDURE add_movie (IN in_title VARCHAR(100), in_year INT, in_director VARCHAR(100), in_star VARCHAR(100), in_birth INT, in_genre VARCHAR(32) ) -- add in genre and star info too
BEGIN
    DECLARE movieId VARCHAR(10);
    DECLARE starId VARCHAR(10);
    DECLARE genreId INT;
    -- check if movie already exists
        -- IF EXISTS(SELECT * FROM movies WHERE title = in_title AND year = in_year) THEN
    -- check if star already exists
    -- check if genre already exists
    -- if not insert them

    -- CHECK STAR
    IF EXISTS(SELECT * FROM stars WHERE name = in_star AND birthYear = in_birth) THEN
		SET starId = (SELECT id FROM stars WHERE name = in_star AND birthYear = in_birth);
    ELSE
        -- parse and increment id
		SET starId = (select concat(substring(max(id), 1,2), (CAST(substring(max(id), 3) AS UNSIGNED) + 1)) from stars);
    END IF;

    -- CHECK GENRE
    IF EXISTS(SELECT * FROM genres WHERE name = in_genre) THEN
		SET genreId = (SELECT id FROM genres WHERE name = in_genre);
    ELSE
        -- parse and increment id
		SET genreId = (select max(id) + 1 from genres);
    END IF;


#     IF (x > 5) THEN
#         SELECT CONCAT(x, " is higher") as answer;
#     ELSE
#         SELECT CONCAT(x, " is lower") as answer;
#     END IF;
END
$$
-- Change back DELIMITER to ;
DELIMITER ;
# SHOW PROCEDURE STATUS WHERE db = 'moviedb';

