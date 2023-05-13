## XML Parser
### Inconsistency Files
#### MovieDuplicates.txt
- Movie has a fid that belongs to a movie that was already seen before in the xml file
#### MovieInconsistent.txt
- Movie contains invalid year that is not an int (ex: 19yy, 199x)
- Movie does not have any genres associated with it
- Movie does not have a director (ex: director is listed as unknown)
#### MovieNotFound.txt
- When parsing casts, the fid listed is not associated with any movies that were inserted
#### StarDuplicates.txt
- Star has name/birth year that was previously found in the xml file or already in the movie db
#### StarNotFound.txt
- When parsing casts, the name of the star is not found in the db or the actors xml file


### Assumptions
#### Mains
- Assume all movies are not already in our database
#### Casts
- Assume Director/Movie is correctly associated in "casts" xml file
#### Actors
- An actor is still considered valid even if dob is in an incorrect format, it will simply be set as null