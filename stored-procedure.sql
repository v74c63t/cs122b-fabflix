DELIMITER $$
CREATE PROCEDURE add_movie (IN title VARCHAR(100), year INT, director VARCHAR(100)) -- add in genre and star info too
BEGIN
    -- check if movie already exists
    -- check if star already exists
    -- check if genre already exists
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

