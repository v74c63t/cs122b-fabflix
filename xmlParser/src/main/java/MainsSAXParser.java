import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class MainsSAXParser extends DefaultHandler {

    // since we are deciding to implement a in-memory hashmap
    // wouldn't the other SAXParsers need access to it too
        // like Casts to for (stars_in_movies)
    // im thinking we can create a another file that holds the hashmap
        // parse mains first to figure out (dups/inconsistencies/genres)
        // parse casts that opreates on filtered out movies (hashmap) from last parse
        // casts should be parsed last
        // need to parse mains AND actors before casts and then pass the fids and actor names with their associated ids
        // (this includes the stars already in the db as well) so casts can use to make sure movies/actors exist

    // replace with hashmap? so can check if dupes within xml file quickly
//    List<Movie> myMovies;
    HashMap<String, Movies> myMovies;
    // get data from db and store in in memory hashmap for fast lookup?
    // key: ?? val: ??
    // dont think there are dupes between db and xml for movies
    // so we only need to get genres from db i think
    // movies without genres are considered inconsistent so report that in file

    //If we are mainly storing films in this we can maybe make a hashmap of "fid" since they are unqiuqe for each film

    // nvm it was reported as inconsistent because no genres so fid can be key just need to store director, title, year as value
    // to check for dupes so we can skip and report them

    // after looking at the demo
        // duplicates -- same multiple fid
        // movieempty -- not sure whats in this ( looked at the fid and cross reference and the fields exists ( movieid, title, year, director, genre, actors )
        // inconsistent -- have no director/genres
        // movienotfound -- not sure whats in it either
            // i thikn they dont add movies with no genres? which is why when trying to find them for casts
            // they are reported as missing???

    private String tempVal;

    //to maintain context
    private Movie tempMovie;

    private int movieDupe = 0;

    private int movieInconsistent = 0;

    public MainsSAXParser() {
        myMovies = new ArrayList<Movie>();
    }

    public void runExample() {
        parseDocument();
        printData(); // write to csv file
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("stanford-movies/mains243.xml", this);

        } catch (SAXException se) {
            se.printStackTrace();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printData() {

        System.out.println("No of Movies: " + myMovies.size());
        System.out.println("No of Inserted Genres: ");
        System.out.println("No of Records Inserted into Genres_in_Movies: " + myMovies.size());
        System.out.println("No of Duplicated Movies: " + movieDupe);
        System.out.println("No of Movie Inconsistencies: " + movieInconsistent);
        // also need ot print dupes/inconsistencies
        // in this set write to csv file i guess
        Iterator<Employee> it = myMovies.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
        // then load data
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        // not sure what to put here for mains
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            //create a new instance of employee
            // figure out how to store everything in hashmap i guess
            tempMovie = new Movie();
//            tempEmp.setType(attributes.getValue("type"));
        }

        // Think

    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if ( qName.equalsIgnoreCase("dirname")) {
            // Check if its starsWith "Unknown"
            //  if so, add to inconsistent
            if ( tempVal.lower().strip().startsWith("unknown") ) {
                // in case trailing spaces
                movieInconsistent++;
                //write to movie inconsistent file
            }
            // keep name as a variable somewhere so can set for all movies directed

        } else if (qName.equalsIgnoreCase("film")) {
            //add it to the list
            // check if dupe
            if ( myMovies.containsKey(tempVal.strip()) ) {
                // if dupe write to movies dupe file id title director year
                // increment dup count
                movieDupe++;
            } else {
                // if not add to hashmap
                myMovies.put(tempVal, tempMovie);
            }

        } else if (qName.equalsIgnoreCase("fid")) {
            // check if dupe
            // there are dupes fids in the file
            if( myMovies.containsKey(tempVal.strip()) ){
                movieDupe++;
                //write to movie dupe file
            }
            else {
                // keep in hashmap as key? for dupe checking later

                // add to tempMovie
                    // tempMovie.setId(tempVal.strip());
            }
        }else if (qName.equalsIgnoreCase("t")) {
            // store for dupe checking
            if(tempVal.strip() == "") { // i mean title is required not to be null so this should be a fair assumption?
                movieInconsistent++;
                // write to movie inconsistent file
            }
            else if(tempVal == null) {
                movieInconsistent++;
                // write to movie inconsistent file
            }
            else {
                //set

                // add to tempMovie
                    // tempMovie.setTitle(tempVal.strip());
            }

        }else if (qName.equalsIgnoreCase("year")) {
            // there are some with invalid ints ex: 199x, 19yy, etc
            // dk how to deal with these b/c we cant set as null since tables require them to be not null
            // maybe report as inconsistent????
            // store for dupe checking
            if(tempVal.strip() == "") { // i mean year is required not to be null so this should be a fair assumption?
                movieInconsistent++;
                // write to movie inconsistent file
            }
            else if(tempVal == null) {
                movieInconsistent++;
                // write to movie inconsistent file
            }
            else {
                try () {
                    // add to tempMovie
                        // tempMovie.setYear(Integer.parseInt(tempVal.strip()));
                } catch (Exception e) {
                    // report inconsistent i guess
                    movieInconsistent++;
                    // write to movie inconsistent file
                }
            }
//            tempEmp.setName(tempVal);
        } else if(qName.equalsIgnoreCase("cat")) {
            // need to do substring matching to check for if exists in db
            // need to use .lower() b/c 'dram', 'DRam', etc
            // also may need to combine similar genres together ex: adult
            // store in list?
            // if empty report inconsistent
            // some have trailing spaces so use .strip()
            // some are combined into one tag ex: 'Romt Comd', 'Dram Docu'
                // use .strip() then .split()
            // refer to http://infolab.stanford.edu/pub/movies/doc.html#CATS
            // to figure out what each cat stands for
            // maybe create a map for this as well?
            // Susp -> Thriller
            // CnR -> Cops and Robbers -> Crime??? or new cat
                // some of them are added as CnRb
            // Dram -> Drama
            // West -> Western
            // Myst -> Mystery
            // S.F. -> Sci-Fi
                // but some of them are written as ScFi so idk
            // Advt -> Adventure
            // Horr -> Horror
            // Romt -> Romantic -> Romance
            // Comd -> Comedy
                // some are combined for some reason ex: Romt Comd
            // Musc -> Musical
            // Docu -> Documentary
            // Porn -> Adult
            // Noir -> black (just store as noir i guess)
            // BioP -> biographical Picture -> Biography? idrk what to consider this as
            // TV -> TV show
            // TVs -> TV series
            // TVm -> TV miniseries

            // some that are not listed on that page
            // Actn -> Action
            // Cart -> Cartoon
            // Bio -> Biography
            // Crim -> Crime
            // Faml -> Family
            // Fant -> Fantasy
            // Hist -> History

            // for rest just add as new

//            if( cat tag doesnt exist ) {
            if(tempVal.strip() == "") { // check if this is correct // in case trailing spaces
                movieInconsistent++;
                // write to movie inconsistent file
            } else if(tempVal == null) {
                movieInconsistent++;
                // write to movie inconsistent file
            } else {
                // add to tempMovie
                // have to check genre before adding
                // have to .strip() b/c trailing spaces and .lower()
                // have to .split() in case combined genres
                // refer to comments above to get correct genre
                // have to add to somewhere so can insert into genres in movie
                // maybe a hashmap with movie id as key and list of genre ids as values
                // if new genre add to another hashmap? for genre table key: name, val: id
                // and tehn keep id
                    // tempMovie.addGenre(tempVal.strip());
            }

        }
//        else if (qName.equalsIgnoreCase("Age")) {
//            tempEmp.setAge(Integer.parseInt(tempVal));
//        }

    }

    public static void main(String[] args) {
        MainsSAXParser msp = new MainsSAXParser();
        msp.runExample();
    }

}
