# XML Parser

## Using the Parser
***Note:** This assumes that both `stanford-movies` and `fabflix` are placed in the same directory and you are currently in that directory*

1. Obtain the [Stanford Movie XML files](http://infolab.stanford.edu/pub/movies/dtd.html) and place them in a folder called `stanford-movies`
   - This folder should contain `actors63.xml`, `casts124.xml`, and `mains243.xml`
2. Copy this folder into the xmlParser directory

   ```
   cp -r ./stanford-movies ./fabflix/xmlParser/stanford-movies 
   ```
3. Move into the xmlParser directory

   ```
   cd  fabflix/xmlParser 
   ```
4. Compile using Maven

   ```
   mvn compile
   ```
5. Execute the parser using Maven

   ```
   mvn exec:java -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="xmlParser"
   ```

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
Inconsistency files are text files generated in the `xmlParser` directory when the XML parser is ran. Listed below are the different files generated while parsing each XML file and the contents of each file.

### Parsing `mains243.xml`

#### [MovieDuplicates.txt](MovieDuplicate.txt)
- Any movie with a fid that was already seen earlier during parsing will be considered a duplicate
- Movies that are duplicates will not be added to the database

### [MovieInconsistent.txt](MovieInconsistent.txt)
- Any movie that has a year in an invalid format meaning it is not an integer (ex: 19yy, 199x) will be considered inconsistent
- Any movie that does not have any genres associated with it will be considered inconsistent
- Any movie that does not have a known director (ex: director is not listed, director is listed as unknown) will be considered inconsistent
- Movies with inconsistent formats will not be added to the database

### Parsing `actors63.xml`

#### [StarDuplicates.txt](StarDuplicates.txt)
- Any actor that shares a name and birth year with an existing star in the database will be considered a duplicate
- Any actor that shares a name with an existing star in the database and has a null birth year will be considered a duplicate
- Actors that are duplicates will not be added to the database

### Parsing `casts124.xml`

#### [MovieNotFound.txt](MovieNotFound.txt)
- Any casts listing that lists an actor participating in a movie with a fid not associated with any movies parsed from `mains243.xml` will not be added to the database
- Movies for those cast listings will be recorded as not found and missing

#### [StarNotFound.txt](StarNotFound.txt)
- Any casts listing that lists an actor whose name cannot be found in the database nor previously while parsing `actors63.xml` will not be added to the database
- These actors will be recorded as not found and missing


## Assumptions/Interpretations

### `mains243.xml`
- All movies listed are not already in the database

### `casts124.xml`
- All movies are associated with the correct director
- All actor names are coreectly formatted

### `actors63.xml`
- All stage names are correctly formatted
- Actors with a date of birth in an incorrect format (not a valid integer) are considered valid and will have their dob be set as null

### \<Cat> to Genre

***Note***: *This is how genres with a `cat` tag are interpreted when parsing `mains243.xml`. These are largely based off of the 3.13 Categories table and the 4.4 Categories table from [http://infolab.stanford.edu/pub/movies/doc.html](http://infolab.stanford.edu/pub/movies/doc.html). Certain genres are combined with already existing genres in the database because of their similarities.*

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
