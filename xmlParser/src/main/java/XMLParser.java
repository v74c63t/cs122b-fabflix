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

public class SAXParserExample extends DefaultHandler {

    List<Employee> myEmpls;

    private String tempVal;

    //to maintain context
    private Employee tempEmp;

    public SAXParserExample() {
        myEmpls = new ArrayList<Employee>();
    }

    public void runExample() {
        parseDocument();
        printData();
    }

    private void parseDocument() {

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //parse the file and also register this class for call backs
            sp.parse("employees.xml", this);

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

        System.out.println("No of Employees '" + myEmpls.size() + "'.");

        Iterator<Employee> it = myEmpls.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";
        if (qName.equalsIgnoreCase("Employee")) {
            //create a new instance of employee
            tempEmp = new Employee();
            tempEmp.setType(attributes.getValue("type"));
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("Employee")) {
            //add it to the list
            myEmpls.add(tempEmp);

        } else if (qName.equalsIgnoreCase("Name")) {
            tempEmp.setName(tempVal);
        } else if (qName.equalsIgnoreCase("Id")) {
            tempEmp.setId(Integer.parseInt(tempVal));
        } else if (qName.equalsIgnoreCase("Age")) {
            tempEmp.setAge(Integer.parseInt(tempVal));
        }

    }

    public static void main(String[] args) {
        SAXParserExample spe = new SAXParserExample();
        spe.runExample();
    }

}