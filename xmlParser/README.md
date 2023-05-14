# XML Parser
## Inconsistency Files
### MovieDuplicates.txt
- When parsing mains, movie has a fid that belongs to a movie that was already seen before in the xml file
### MovieInconsistent.txt
- Movie contains invalid year that is not an integer (ex: 19yy, 199x)
- Movie does not have any genres associated with it
- Movie does not have a director (ex: director is listed as unknown)
### MovieNotFound.txt
- When parsing casts, the fid listed is not associated with any movies that were inserted from the mains xml file
### StarDuplicates.txt
- When parsing actors, star has a name and birth year that was already found in the database
- When parsing actors, star has a name that was already found in the database and a null birth year
### StarNotFound.txt
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
- CnR/CnRb -> Cops and Robbers -> Crime
- Dram -> Drama
- West -> Western
- Myst -> Mystery
- S.F./ScFi -> Sci-Fi
- Advt -> Adventure
- Horr -> Horror
- Romt -> Romantic -> Romance
- Comd -> Comedy
- Musc/Stage Musical -> Musical
- Docu -> Documentary
- Porn -> Adult
- Noir -> Noir
- Bio -> Biography
- BioP -> Biographical Picture -> Biography
- TV -> TV Show
- TVs -> TV Series
- TVm -> TV Miniseries
- Actn -> Violence -> Action
- Cart -> Cartoon
- Camp now -> Camp
- Crim -> Crime
- Faml -> Family
- Fant -> Fantasy
- Hist -> History
- Disa -> Disaster
- Epic -> Epic
- Surl -> Surreal
- AvGa/Avant Garde -> Avant Garde
