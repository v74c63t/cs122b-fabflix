/*
General Outline:
    - Parse the XML Files
    - Validate them using in memory hash tables?
    - Write them to files (preferably csv files)
        - (one file for each table [movies, stars, genres, stars_in_movies, genres_in_movies])
    - Use SQL LOAD DATA to load each file into the tables
 */

/*
Some notes abt the XML files:
    - cat - > genre
        - a lot of the genres listed in the files are shortened
            - ex: Comd => Comedy, Susp => Suspense, Dram => Drama, Romt => Romance?, Musc => Music, Myst => Mystery,
            Docu => Documentary, Advt => Adventure, Actn => Action, ScFi => Sci-Fi, etc
        - some are all lower case or have upper and lower case characters throughout the name
            - ex: 'susp', 'DRam'
            - might have to use .lower() to check some of these
 */
