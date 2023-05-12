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

    private String tempVal;

    //to maintain context
    private Employee tempEmp;

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

        System.out.println("No of Movies '" + myEmpls.size());
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
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equalsIgnoreCase("film")) {
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
        }else if (qName.equalsIgnoreCase("t")) {
            // some movies has same title (but that seems normal to me imo but demo had them in inconsistent so idk)

        }else if (qName.equalsIgnoreCase("year")) {
            // there are some with invalid ints ex: 199x, 19yy, etc
            // dk how to deal with these b/c we cant set as null since tables require them to be not null
            tempEmp.setName(tempVal);
        } else if (qName.equalsIgnoreCase("dob")) {
            // if empty set null
            // if not valid set null?? not too sure dont rly get what to report for inconsistency stuff
            tempEmp.setId(Integer.parseInt(tempVal));
        } else if(qName.equalsIgnoreCase("cat")) {
            // need to do substring matching
            // also may need to combine similar genres together ex: adult
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
