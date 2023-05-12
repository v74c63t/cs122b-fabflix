/*
General Outline:
    - Parse the XML Files
    - Validate them using in memory hash maps?
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

/*
There are dupes within the xml files
    - have to keep track of stuff to be inserted to check these
DOM may cause problems with AWS because of memory?
    - try on AWS to see how long it takes
        - tbh, SAX might be the best option because of the memory usage of DOM
            - also because the website isn't done ( still more projects left )
    - if too long optimize or switch to SAX
maybe different parser files for each file? (one for mains, one for casts, one for actors)
    - with how each xml is structured, this will be a better option
 */


/*
Elements Needed
-----------------
    Main
    -----------------
        <directorfilms>
	        <director>
		        <dirname>
	        <films>
		        <film>
			        <fid> // film_id
			        <t> //title
			        <year>
				        <released>
			        <cats>  // categories
				        <cat>?
				        <cattext>?

    Cast
    -----------------
        <dirfilms>
	        <is>? // shouldnt be needed imo
	        <filmmc>
		        <m>
                    <f> // film_id
                    <a> // actor_name
                    // should only need f and a
                    // if f is used as id can simply get the id of a and then insert f and a_id

    Actor
    -----------------
    <actor>
	    <stagename>
	    <dob>
 */