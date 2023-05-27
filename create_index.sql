USE moviedb;

CREATE FULLTEXT INDEX titleFullTextIdx ON movies(title);
