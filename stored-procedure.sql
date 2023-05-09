DELIMITER $$
CREATE PROCEDURE add_movie (IN in_title VARCHAR(100), in_year INT, in_director VARCHAR(100), in_star VARCHAR(100), in_genre VARCHAR(32) ) -- add in genre and star info too
BEGIN
    -- check if movie already exists
        -- IF EXISTS(SELECT * FROM movies WHERE title = in_title AND year = in_year) THEN
    -- check if star already exists
        -- IF EXISTS(SELECT * FROM stars WHERE name = in_star) THEN
    -- check if genre already exists
        -- IF EXISTS(SELECT * FROM genre WHERE name = in_genre) THEN
    -- if not insert them
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

