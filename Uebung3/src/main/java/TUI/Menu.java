package TUI;

import ChrisTime.ChrisDate;
import Parser.ProtocolParser;
import Statistics.StatsFromRemote;
import database.Extensions.MongoDBProtocolLoader;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Scanner;

import static java.util.Objects.isNull;

/**
 * Class that generates the commandline menu that can be navigated to explore the parsed information from the protocols.
 * @author Christian Bluemel
 */
public class Menu {

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private String[] filter = { "", "", "", "" };

    private boolean dateRangeFlag = false;

    private boolean dateFlag = false;

    private ChrisDate dateFilter = null;

    private ChrisDate[] dateRangeFilter = new ChrisDate[2];

    private StatsFromRemote stats;

    private MongoDBProtocolLoader protocols;

    /**
     * Empty Constructor.
     */
    public Menu(){

    }

    /**
     * Runs the menu and depending on the users input calls subMenu methods.
     *
     * @throws InterruptedException Will be thrown if the IO is interrupted.
     */
    public void runCommandlineMenu() throws InterruptedException, IOException, ParserConfigurationException, SAXException {
        boolean getConfigFlag = true;
        boolean runFlag = true;
        Scanner scanner = new Scanner(System.in);

        while (getConfigFlag) {
            System.out.println("Before any stats can be requested\nplease enter the path to a config file.");
            System.out.println("Alternatively type q to quit.");
            System.out.print(">");

            String input = scanner.nextLine();

            if ("q".equals(input)) {
                runFlag = false;
                getConfigFlag = false;
            } else {
                File configPath = new File(input);
                if (this.checkIfConfig(configPath)) { // If this passes the file is okay.
                    getConfigFlag = false;
                    Properties properties = new Properties();
                    FileInputStream config = new FileInputStream(configPath);
                    properties.load(config);
                    this.protocols = new MongoDBProtocolLoader(properties);
                    this.stats = new StatsFromRemote(protocols);
                } else {
                    System.out.println("The given config file was not accepted or does not exist.");
                }
            }
        }

        while (runFlag) {
            System.out.println("Choose an option by typing\nthe number in the square brackets:");
            System.out.println("\t0. Speaker Listing              \t[0]");
            System.out.println("\t1. Average Speech Length        \t[1]");
            System.out.println("\t2. Speaker Ranking              \t[2]");
            System.out.println("\t3. Search full text in speech   \t[3]");
            System.out.println("\t4. Session Listing (by duration)\t[4]");
            System.out.println("---------------------------------");
            System.out.println("\t8. Reset DB & reparse protocols \t[8]");
            System.out.println("---------------------------------");
            System.out.println("\t9. Quit                         \t[9]\n");
            System.out.printf(">");

            String input = scanner.nextLine();

            switch (input) {
                case "0":
                    this.basicSpeakerListing();
                    break;
                case "1":
                    this.averageSpeechLength();
                    break;
                case "2":
                    this.rankedSpeakerListing();
                    System.out.println("-----------------------");
                    System.out.println("Press Enter to return to menu...");
                    scanner.nextLine();
                    break;
                case "3":
                    System.out.println("Type the words you are looking for.");
                    System.out.println("If you put \" around subsets of words");
                    System.out.println("they will be looked for as a phrase.");
                    System.out.printf(">");
                    input = scanner.nextLine();
                    System.out.println(this.stats.searchForFullText(input));
                    System.out.println("-----------------------");
                    System.out.println("Press Enter to return to menu...");
                    scanner.nextLine();
                    break;
                case "4":
                    this.submenuSessionListing();
                    break;
                case "8":
                    try  {
                        this.resetAndReparse();
                    } catch (IOException e) {
                        System.out.println("IOException, but I caught it, so you can't hurt me!");
                    } catch (SAXException e) {
                        System.out.println("SAXException, but I caught it, so you can't hurt me!");
                    } catch (ParserConfigurationException e){
                        System.out.println("ParserConfigurationException, but I caught it, so you can't hurt me!");
                    }
                    break;
                case "9":
                    runFlag = false;
                    System.out.println("Shutting down. See ya!");
                    break;
                default:
                    System.out.println("Input not recognized. Try again!");
                    break;
            }
        }
    }

    /**
     * Submenu that gives the options to list sessions filtered by date (if requested).
     */
    private void submenuSessionListing() {
        boolean runFlag = true;
        Scanner scanner = new Scanner(System.in);
        while (runFlag) {
            System.out.println("Choose an option by typing\nthe number in the square brackets:");
            System.out.println("\t0. Set date filters for [1]                   \t[0]");
            System.out.println("\t1. List sessions (sorted by duration)         \t[1]");
            System.out.println("\t-------------------------------------------------");
            System.out.println("\t9. Return to main menu.                       \t[9]\n");
            System.out.print(">");

            String input = scanner.nextLine();

            switch (input) {
                case "0":
                    this.generateDateFilter();
                    break;
                case "1":
                    System.out.println(this.stats.listAllSessionsSortedByDurationFilteredBy(
                            this.dateFilter,
                            this.dateRangeFilter,
                            this.dateRangeFlag,
                            this.dateFlag)
                    );
                    System.out.println("-----------------------");
                    System.out.println("Press Enter to return to menu...");
                    scanner.nextLine();
                    break;
                case "9":
                    this.filter = new String[] {"", "", "", ""};
                    runFlag = false;
                    break;
                default:
                    System.out.println("Input not recognized. Try again!");
                    break;
            }
        }
    }

    /**
     * Submenu to set up a filter for the listing of sessions.
     */
    private void generateDateFilter() {
        boolean runFlag = true;
        Scanner scanner = new Scanner(System.in);
        while (runFlag) {
            System.out.println("Date filter status:");

            System.out.print("\tFilter for specific date: ");
            if (!this.dateFlag) {
                System.out.print("OFF");
            } else {
                System.out.print("ON");
            }
            System.out.println();

            System.out.print("\tFilter for date range: ");
            if (!this.dateRangeFlag) {
                System.out.print("OFF");
            } else {
                System.out.print("ON");
            }
            System.out.println();
            System.out.println("Date filter values:");
            if (isNull(this.dateFilter)) {
                System.out.println("\tNo specific date filter.");
            } else {
                System.out.println("\tSpecific date filter:");
                System.out.println("\t" + dateFilter.toString());
            }

            if (isNull(this.dateRangeFilter[0]) || isNull(this.dateRangeFilter[1])) {
                System.out.println("\tNo date range filter.");
            } else {
                System.out.println("\tDate range filter:");
                System.out.println("\tFrom:" + dateRangeFilter[0].toString());
                System.out.println("\tTo:" + dateRangeFilter[1].toString());
            }
            System.out.println("Choose an option by typing\nthe number in the square brackets:");

            if (!this.dateFlag) {
                System.out.println("\t0. Activate date filter                          \t[0]");
            } else {
                System.out.println("\t0. Deactivate date filter                        \t[0]");
            }
            if (!this.dateRangeFlag) {
                System.out.println("\t1. Activate date range filter                    \t[1]");
            } else {
                System.out.println("\t1. Deactivate date range filter                  \t[1]");
            }
            System.out.println("\t2. Set specific date filter                      \t[2]");
            System.out.println("\t3. Set date range filter                         \t[3]");
            System.out.println("\t-------------------------------------------------");
            System.out.println("\t9. Return to menu.                             \t[9]\n");
            System.out.printf(">");

            String input = scanner.nextLine();

            switch (input) {
                case "0":
                    if (isNull(dateFilter) && !dateFlag){
                        System.out.println("You have to set a specific date filter before activating");
                        System.out.println("Press enter to continue.");
                        input = scanner.nextLine();
                    } else if (dateFlag == false && dateRangeFlag == true) {
                        dateFlag = true;
                        dateRangeFlag = false;
                    } else {
                        dateFlag = !dateFlag;
                    }
                    break;
                case "1":
                    if (isNull(dateRangeFilter[0]) && !dateRangeFlag){
                        System.out.println("You have to set a date range filter before activating");
                        System.out.println("Press enter to continue.");
                        input = scanner.nextLine();
                    } else if (dateFlag == true && dateRangeFlag == false) {
                        dateFlag = false;
                        dateRangeFlag = true;
                    } else {
                        dateRangeFlag = !dateRangeFlag;
                    }
                    break;
                case "2":
                    System.out.println("Type what date you wanna filter by:");
                    System.out.println("(Format: dd:MM:yyyy)");
                    input = scanner.nextLine();
                    try {
                        dateFilter = new ChrisDate(LocalDate.parse(input, formatter));
                    } catch (DateTimeException e) {
                        System.out.println("The format you typed in does not comply to the demanded format.");
                        System.out.println("Press enter to continue.");
                        input = scanner.nextLine();
                    }
                    break;
                case "3":
                    boolean workedFlag = true;
                    System.out.println("From which date does the filter range start?");
                    System.out.println("(Format: dd:MM:yyyy)");
                    input = scanner.nextLine();
                    try {
                        dateRangeFilter[0] = new ChrisDate(LocalDate.parse(input, formatter));
                    } catch (DateTimeException e) {
                        System.out.println("The format you typed in does not comply to the demanded format.");
                        System.out.println("Returning to submenu.");
                        System.out.println("Press enter to continue.");
                        workedFlag = false;
                        input = scanner.nextLine();
                    }
                    if (workedFlag) {
                        System.out.println("To which date does the filter range go?");
                        System.out.println("(Format: dd:MM:yyyy)");
                        input = scanner.nextLine();
                        try {
                            dateRangeFilter[1] = new ChrisDate(LocalDate.parse(input, formatter));
                        } catch (DateTimeException e) {
                            System.out.println("The format you typed in does not comply to the demanded format.");
                            System.out.println("Returning to submenu.");
                            System.out.println("Press enter to continue.");
                            input = scanner.nextLine();
                        }
                    }
                    break;
                case "9":
                    runFlag = false;
                    break;
                default:
                    System.out.println("Input not recognized. Try again!");
                    break;
            }
        }
    }


    /**
     * Submenu that gives the options to print all kinds of different speech length averages.
     */
    private void averageSpeechLength(){
        boolean runFlag = true;
        Scanner scanner = new Scanner(System.in);
        while (runFlag) {
            System.out.println("Choose an option by typing\nthe number in the square brackets:");
            System.out.println("\t0. Get average speech length                  \t[0]");
            System.out.println("\t1. Get average speech length per speaker      \t[1]");
            System.out.println("\t2. Get average speech length per faction      \t[2]");
            System.out.println("\t3. Get average speech length per party        \t[3]");
            System.out.println("\t-------------------------------------------------");
            System.out.println("\t9. Return to main menu.                       \t[9]\n");
            System.out.print(">");

            String input = scanner.nextLine();

            switch (input) {
                case "0":
                    System.out.println(stats.AvgSpeechLength());
                    System.out.println("-----------------------");
                    System.out.println("Press Enter to return to menu...");
                    scanner.nextLine();
                    break;
                case "1":
                    System.out.println(stats.listAllSpeakerWithAvgSpeechLength());
                    System.out.println("-----------------------");
                    System.out.println("Press Enter to return to menu...");
                    scanner.nextLine();
                    break;
                case "2":
                    System.out.println(stats.listAllFactionWithAvgSpeechLength());
                    System.out.println("-----------------------");
                    System.out.println("Press Enter twice to return to menu...");
                    scanner.nextLine();
                    break;
                case "3":
                    System.out.println(stats.listAllPartiesWithAvgSpeechLength());
                    System.out.println("-----------------------");
                    System.out.println("Press Enter twice to return to menu...");
                    scanner.nextLine();
                    break;
                case "9":
                    runFlag = false;
                    break;
                default:
                    System.out.println("Input not recognized. Try again!");
                    break;
            }
        }
    }

    /**
     * Submenu that collects makes it possible to reparse the protocols and upload them directly to the mongoDB database.
     *
     * @throws ParserConfigurationException If the parser can't handle the protocols.
     * @throws IOException  If the input breaks something.
     * @throws SAXException If the SAX parser encounters problems while parsing.
     */
    private void resetAndReparse() throws ParserConfigurationException, IOException, SAXException {
        boolean protocolFlag = true;
        boolean stammdatenFlag = true;

        Scanner scanner = new Scanner(System.in);

        while (protocolFlag) {
            System.out.println("Before we even touch the database I need you to show me some");
            System.out.println("viable Bundestags plenary protocols.");
            System.out.println("Type in the directory path where the plenary protocols are:");
            System.out.println("(q to return to the main menu)");
            System.out.print(">");

            String input = scanner.nextLine();
            File protocol = new File(input);

            if (input.equals("q")) {
                protocolFlag = false;
            } else if (this.checkIfProtocolDir(protocol)) {
                while (stammdatenFlag) {
                    System.out.println("That worked. Now we need the directory of a Stammdatenblatt.");
                    System.out.println("This directory must contain a file called 'MDB_STAMMDATEN.XML'.");
                    System.out.println("(q to return to the main menu)");
                    System.out.print(">");

                    input = scanner.nextLine();
                    File stammdaten = new File(input);

                    if (input.equals("q")) {
                        protocolFlag = false;
                        stammdatenFlag = false;
                    } else if (this.checkIfStammdatenDir(stammdaten)) {
                        protocolFlag = false;
                        stammdatenFlag = false;
                        FilenameFilter xmlFilter = new FilenameFilter() {
                            @Override
                            public boolean accept(File dir, String name) {
                                return name.toLowerCase().endsWith(".xml");
                            }
                        };
                        File stamm = stammdaten.listFiles(xmlFilter)[0];
                        File[] prot = protocol.listFiles(xmlFilter);
                        ProtocolParser parsor = new ProtocolParser(stamm, prot);
                        this.protocols.resetCollections();
                        this.protocols.uploadProtocolAssembly(parsor);
                    } else {
                        System.out.println("Stammdatenblatt directory not recognized. Try again.");
                    }
                }
            }
        }
    }

    /**
     * Method that checks if a given directory contains one file that can be read as Bundestags Stammdatenblatt.
     * @param stammdaten File class object of the directory supposedly containing the Stammdatenblatt XML.
     * @return true, if the directory checks out as a container of xml Stammdatenblatt.
     */
    private boolean checkIfStammdatenDir(File stammdaten) {
        boolean output = true;
        FilenameFilter xmlFilter = new FilenameFilter() {@Override
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".xml");
        }};

        if (stammdaten.isDirectory()){
            File[] xmlList;
            xmlList = stammdaten.listFiles(xmlFilter);
            assert xmlList != null;
            if (xmlList.length == 1) {
                if (!(xmlList[0].toString().endsWith("MDB_STAMMDATEN.XML"))){
                    output = false;
                }
            } else {
                output = false;
            }
        }
        return output;
    }

    /**
     * Method that checks if a given directory contains xml files that can be read as Bundestags plenary protocols.
     * @param protocolDir File class object of the directory supposedly containing the xml files.
     * @return true, if the directory checks out as a container of xml plenary protocols.
     */
    private boolean checkIfProtocolDir(File protocolDir) {
        boolean output = true;
        FilenameFilter xmlFilter = new FilenameFilter() {@Override
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".xml");
        }};

        if (protocolDir.isDirectory()) {
            File [] xmlList = protocolDir.listFiles(xmlFilter);
            assert xmlList != null;
            if ( xmlList.length == 0 ) output = false;
        } else {
            output = false;
        }




        return output;
    }

    /**
     * The submenu to do ranked listing of the legislators. Can also lead to further subMenus to adjust settings.
     */
    private void rankedSpeakerListing(){
        boolean runFlag = true;
        Scanner scanner = new Scanner(System.in);
        while (runFlag) {
            System.out.println("Choose an option by typing\nthe number in the square brackets:");
            System.out.println("\t0. Set name/party/faction filter for [2]      \t[0]");
            System.out.println("\t1. List speakers sorted by speech count.      \t[1]");
            System.out.println("\t-------------------------------------------------");
            System.out.println("\t9. Return to main menu.                       \t[9]\n");
            System.out.print(">");

            String input = scanner.nextLine();

            switch (input) {
                case "0":
                    this.generateFilter();
                    break;
                case "1":
                    System.out.println(stats.listAllRednerSortedBySpeechCount(this.filter));
                    System.out.println("-----------------------");
                    System.out.println("Press Enter twice to return to menu...");
                    scanner.nextLine();
                    break;
                case "9":
                    this.filter = new String[] {"", "", "", ""};
                    runFlag = false;
                    break;
                default:
                    System.out.println("Input not recognized. Try again!");
                    break;
            }
        }
    }

    /**
     * The submenu to do basic listing of the legislators. Can also lead to further subMenus to adjust settings.
     */
    private void basicSpeakerListing(){
        boolean runFlag = true;
        Scanner scanner = new Scanner(System.in);
        while (runFlag) {
            System.out.println("Choose an option by typing\nthe number in the square brackets:");
            System.out.println("\t0. Set name/party/faction filter for [1]      \t[0]");
            System.out.println("\t1. List all speakers                          \t[1]");
            System.out.println("\t2. List all speakers per topic                \t[2]");
            System.out.println("\t3. List session leaders + sessions lead count \t[3]");
            System.out.println("\t-------------------------------------------------");
            System.out.println("\t9. Return to main menu.                       \t[9]\n");
            System.out.print(">");

            String input = scanner.nextLine();

            switch (input) {
                case "0":
                    this.generateFilter();
                    break;
                case "1":
                    System.out.println(this.stats.toStringFilteredSortedRedners(this.filter));
                    System.out.println("-----------------------");
                    System.out.println("Press Enter to return to menu...");
                    scanner.nextLine();
                    break;
                case "2":
                    System.out.println("For which session do you want to do this? [<SessionNumber>]");
                    input = scanner.nextLine();
                    System.out.println(stats.listAllRednerOfSitzungPerTagesordnungspunkt(input));
                    System.out.println("-----------------------");
                    System.out.println("Press Enter twice to return to menu...");
                    scanner.nextLine();
                    break;
                case "3":
                    System.out.println(stats.listSitzungsleiterAndLeadSessionCount());
                    System.out.println("-----------------------");
                    System.out.println("Press Enter twice to return to menu...");
                    scanner.nextLine();
                    break;
                case "9":
                    this.filter = new String[] {"", "", "", ""};
                    runFlag = false;
                    break;
                default:
                    System.out.println("Input not recognized. Try again!");
                    break;
            }
        }
    }

    /**
     * Submenu to set up a filter for the listing of speakers.
     */
    private void generateFilter() {
        boolean runFlag = true;
        Scanner scanner = new Scanner(System.in);
        while (runFlag) {
            System.out.println("The current filters are:");
            if (this.filter[0].equals("")) {
                System.out.println("No forename filter.");
            } else {
                System.out.println("Forename must contain: '" + this.filter[0] +"'");
            }
            if (this.filter[1].equals("")) {
                System.out.println("No surname filter.");
            } else {
                System.out.println("Surname must contain: '" + this.filter[1] +"'");
            }
            if (this.filter[2].equals("")) {
                System.out.println("No faction filter.");
            } else {
                System.out.println("Faction must contain: '" + this.filter[2] +"'");
            }
            if (this.filter[3].equals("")) {
                System.out.println("No party filter.");
            } else {
                System.out.println("Party must contain: '" + this.filter[3] +"'");
            }
            System.out.println("Choose an option by typing\nthe number in the square brackets:");
            System.out.println("\t0. Set forename filter                         \t[0]");
            System.out.println("\t1. Set surname filter                          \t[1]");
            System.out.println("\t2. Set faction filter                          \t[2]");
            System.out.println("\t3. Set party filter                            \t[3]");
            System.out.println("\t-------------------------------------------------");
            System.out.println("\t9. Return to menu.                             \t[9]\n");
            System.out.printf(">");

            String input = scanner.nextLine();

            switch (input) {
                case "0":
                    System.out.println("Type what the forename must contain:");
                    filter[0] = scanner.nextLine();
                    break;
                case "1":
                    System.out.println("Type what the surname must contain:");
                    filter[1] = scanner.nextLine();
                    break;
                case "2":
                    System.out.println("Type what the faction must contain:");
                    filter[2] = scanner.nextLine();
                    break;
                case "3":
                    System.out.println("Type what the party must contain:");
                    filter[3] = scanner.nextLine();
                    break;
                case "9":
                    runFlag = false;
                    break;
                default:
                    System.out.println("Input not recognized. Try again!");
                    break;
            }
        }
    }


    /**
     * Method that checks if a config File actually exists and contains all the necessary information to
     * connect to the mongoDB.
     * @param configPath File class object to the config file that will be checked.
     * @return boolean that is true, if the file checks out.
     * @throws FileNotFoundException Throws this error, if the file is not found.
     */
    private boolean checkIfConfig(File configPath) throws FileNotFoundException {
        String[] requiredBits = {
                "remote_host = ", "remote_database = ", "remote_user = ",
                "remote_password = ", "remote_port = ", "remote_collection = "
        };
        try (FileInputStream configStream = new FileInputStream(configPath)) {
            boolean output = true;
            StringBuilder configTextBuilt = new StringBuilder("");

            int i;

            while ((i = configStream.read()) != -1) {
                char letter = (char) i;
                configTextBuilt.append(letter);
            }

            String configText = configTextBuilt.toString();
            String[] configLines = configText.split("\n");

            for (i = 0; i < configLines.length; i++){
                if (!configLines[i].contains(requiredBits[i])){
                    System.out.print("\nConfig file does not contain: ");
                    System.out.printf(requiredBits[i]);
                    output = false;
                    System.out.print("\n");
                }
            }
            return output;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}



