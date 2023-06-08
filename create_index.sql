USE moviedb;

CREATE FULLTEXT INDEX titleFullTextIdx ON movies(title);

CREATE INDEX simMovieIdIdx ON stars_in_movies(movieId);

CREATE INDEX starIdIdx ON stars(id);

CREATE INDEX gimGenreIdIdx ON genres_in_movies(genreId);

CREATE INDEX genreIdIdx ON genres(id);

CREATE INDEX movieIdIndex ON movies(id);

CREATE INDEX ratingIndex ON ratings(rating);

CREATE INDEX titleIndex ON movies(title);
