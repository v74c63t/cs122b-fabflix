USE moviedb;

CREATE FULLTEXT INDEX titleFullTextIdx
ON movies(title); -- check if correct before adding

-- create any additional index to make queries faster if need now that there are more records in the db

