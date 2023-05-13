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

public class MainsSAXParser extends DefaultHandler {

    List<Employee> myEmpls; // replace with hashmap? so can check if dupes within xml file quickly
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
    private Employee tempEmp;

    private int movieDupe = 0;

    private int movieInconsistent = 0;

    public MainsSAXParser() {
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

        System.out.println("No of Movies: " + myEmpls.size());
        System.out.println("No of Records Inserted into Genres_in_Movies: " + myEmpls.size());
        System.out.println("No of Duplicated Movies: " + movieDupe);
        System.out.println("No of Movie Inconsistencies: " + movieInconsistent);
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
        // not sure what to put here for mains
        tempVal = "";
        if (qName.equalsIgnoreCase("actor")) {
            //create a new instance of employee
            // figure out how to store everything in hashmap i guess
            tempEmp = new Employee();
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
            if ( unknown ) {
                movieInconsistent++;
                //write to movie inconsistent file
            }

        } else if (qName.equalsIgnoreCase("film")) {
            //add it to the list
            // check if dupe
            // if dupe write to movies dupe file id title director year
            // if not add to hashmap
            // according to the demo vid its added to inconsistent file if there are movies with the same name but everything
            // else diff (id, director name, year)? not sure abt this
            myEmpls.add(tempEmp);

        } else if (qName.equalsIgnoreCase("fid")) {
            // check if dupe
            // there are dupes fids in the file
            if( fid already found ){
                movieDupe++;
                //write to movie dupe file
            }
        }else if (qName.equalsIgnoreCase("t")) {
            // store for dupe checking

        }else if (qName.equalsIgnoreCase("year")) {
            // there are some with invalid ints ex: 199x, 19yy, etc
            // dk how to deal with these b/c we cant set as null since tables require them to be not null
            // maybe report as inconsistent????
            // store for dupe checking
            tempEmp.setName(tempVal);
        } else if(qName.equalsIgnoreCase("cat")) {
            // need to do substring matching to check for if exists in db
            // need to use .lower() b/c 'dram', 'DRam', etc
            // also may need to combine similar genres together ex: adult
            // store in list?
            // if empty report inconsistent
            // refer to http://infolab.stanford.edu/pub/movies/doc.html#CATS
            // to figure out what each cat stands for
            // maybe create a map for this as well?
//            if( cat tag doesnt exist ) {
            if(tempVal == "") { // check if this is correct
                movieInconsistent++;
                // write to movie inconsistent file
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
