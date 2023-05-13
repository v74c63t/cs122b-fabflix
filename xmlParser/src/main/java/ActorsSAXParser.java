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
there are directors that have a dirn of "Unknown0"
    - maybe we exclude and skip over them since director in schema is required
there are also those with multiple directors
    - <directorfilms>
        <director>
            <dirname>NAME</dirname>
        </director>
        <films>
            <film>
                <dirs>
                    <dir>DIR1</dir>
                    <dir>DIR2</dir>
                </dirs>
            </film>
        </films>
      </directorfilms>
    - we can probably ignore the <dirs> field ( dont put in starElement event ) from <films>
        - use just from <director>
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

/*
    Star Duplicates: already exists in db/already appeared in xml file before
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class ActorsSAXParser extends DefaultHandler {

    List<Employee> myEmpls; // replace with hashmap? so can check if dupes within xml file quickly
    // get data from db and store in in memory hashmap for fast lookup?
    // maybe key: name, value: birthYear?
    // tehre are dupes within xml file and db

    private String tempVal;

    //to maintain context
    private Employee tempEmp;

    private int starDupe = 0;

    public ActorsSAXParser() {
        myEmpls = new ArrayList<Employee>();
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
            sp.parse("stanford-movies/actors63.xml", this);

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

        System.out.println("No of Stars: " + myEmpls.size());
        System.out.println("No of Duplicated Stars: " + starDupe);
        // also need ot print dupes/inconsistencies
        // in this set write to csv file i guess
        Iterator<Employee> it = myEmpls.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
        // then load data
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("actor")) {
            //create a new instance of employee
            // figure out how to store everything in hashmap i guess
            tempEmp = new Employee();
//            tempEmp.setType(attributes.getValue("type"));
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("actor")) {
            //add it to the list
            // check if dupe
                // if dupe write to stars dupe file the name and birthyear of star (make sure to check with db info too)
                // if not add to hashmap
            myEmpls.add(tempEmp);

        } else if (qName.equalsIgnoreCase("stagename")) {
            // might have to .lower() and .strip() to check if dupe? not sure if
            // everythign is capitalized properly
            if( already exists previous in file or exists in db ) {
                starDupe++;
                //write to star dupe file
            }
            tempEmp.setName(tempVal);
        } else if (qName.equalsIgnoreCase("dob")) {
            // if empty set null
            // if not valid set null?? not too sure dont rly get what to report for inconsistency stuff
            tempEmp.setId(Integer.parseInt(tempVal));
        }
//        else if (qName.equalsIgnoreCase("Age")) {
//            tempEmp.setAge(Integer.parseInt(tempVal));
//        }

    }

    public static void main(String[] args) {
        ActorsSAXParser asp = new ActorsSAXParser();
        asp.runExample();
    }

}