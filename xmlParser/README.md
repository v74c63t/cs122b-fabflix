# XML Parser
## Results
```
No of Inserted Movies: 8648
No of Inserted Genres: 27
No of Inserted Stars: 6215
No of Records Inserted into Genres_in_Movies: 9724
No of Records Inserted into Stars_in_Movies: 27973
No of Movie Inconsistencies: 3423
No of Duplicated Movies: 34
No of Duplicated Stars: 648
No of Missing Movies: 1687
No of Missing Stars: 13931
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  7.498 s
[INFO] Finished at: 2023-05-15T07:55:06Z
[INFO] ------------------------------------------------------------------------
```
## Parsing Time Optimization Strategies
   1. We used in memory hash maps to store information from the database and information we plan to insert into the database so we did not have to query against the database constantly in order to find duplicates or to find whether a movie/star/genre already exists. 
   2. We wrote the data that was parsed from the xml files to csv files (one csv file for each table that is going to be inserted into) so we can use LOAD DATA from SQL to load all the information into each of the tables all at once instead of having to send multiple insert queries throughout parsing to the database. 

## Inconsistency Files
### [MovieDuplicates.txt](MovieDuplicate.txt)
- When parsing mains, movie has a fid that belongs to a movie that was already seen before in the xml file
### [MovieInconsistent.txt](MovieInconsistent.txt)
- Movie contains invalid year that is not an integer (ex: 19yy, 199x)
- Movie does not have any genres associated with it
- Movie does not have a director (ex: director is listed as unknown)
### [MovieNotFound.txt](MovieNotFound.txt)
- When parsing casts, the fid listed is not associated with any movies that were inserted from the mains xml file
### [StarDuplicates.txt](StarDuplicates.txt)
- When parsing actors, star has a name and birth year that was already found in the database
- When parsing actors, star has a name that was already found in the database and a null birth year
### [StarNotFound.txt](StarNotFound.txt)
- When parsing casts, the name of the star is not found in the database or the actors xml file


## Assumptions
### Mains
- Assume all movies are not already in our database
### Casts
- Assume Director/Movie is correctly associated in "casts" xml file
- Assume all actor names are correctly formatted
### Actors
- An actor is still considered valid even if dob is in an incorrect format (not a valid integer), it will simply be set as null
- Assume all stage names are correctly formatted

### Cat to Genres
***Note***: *This is how we interpreted the genres from \<cat> when parsing the mains xml file. These are largely based off of the 3.13 Categories table and the 4.4 Categories table from http://infolab.stanford.edu/pub/movies/doc.html. We have also decided to combine certain genres with already existing genres in the database because of their similarities.*
- Susp -> Thriller
- CnR/CnRb/cmr/cnrbb -> Cops and Robbers -> Crime
- Dram/dramd/drama/dram>/anti-dram/dram.actn\/draam -> Drama
- West/west1 -> Western
- Myst/mystp -> Mystery
- S.F./ScFi/scif/sxfi -> Sci-Fi
- Advt/adctx/adct -> Adventure
- Horr/hor -> Horror
- Romt/romtx/ront/ram/romt.\/romtadvt -> Romantic -> Romance
- Comd/cond/comdx -> Comedy
- Musc/Stage Musical/muusc/muscl/scat -> Musical
- Docu/duco/ducu/dicu -> Documentary
- Porn/porb/kinky -> Adult
- Noir -> Noir
- Bio/biog/biob -> Biography
- BioP/biopx/biopp -> Biographical Picture -> Biography
- TV -> TV Show
- TVs -> TV Series
- TVm/tvmini -> TV Miniseries
- Actn/viol/sctn/axtn/act -> Violence -> Action
- Cart/cartoon -> Cartoon
- Camp now/camp -> Camp
- Crim -> Crime
- Faml -> Family
- Fant/fanth* -> Fantasy
- Hist -> History
- Disa/dist -> Disaster
- Epic -> Epic
- Surl/surr/surreal -> Surreal
- AvGa/Avant Garde -> Avant Garde
- ctxx/ctcxx/ctxxx/txx/h/h0/h*/h** -> Uncategorized
