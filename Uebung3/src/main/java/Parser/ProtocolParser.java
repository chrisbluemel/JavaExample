package Parser;

import Interfaces.*;
import Interfaces.Extensions.File.*;
import ProtocolInterface.ProtocolAssembly;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class ProtocolParser implements ProtocolAssembly {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private String Wahlperiode;
    private LinkedHashSet<String> factions = new LinkedHashSet<String>();
    private LinkedHashSet<String> parties = new LinkedHashSet<String>();
    private final String[] sitzungsleiterIDs = {"11001938", "11004191", "11003820", "11003212", "11001235", "11003206", "11003124"};
    private final String[][] sitzungsleiterNames = { {"Schäuble", "Wolfgang"},
                                                     {"Ziegler", "Dagmar"},
                                                     {"Oppermann", "Thomas"},
                                                     {"Claudia", "Roth"},
                                                     {"Kubicki", "Wolfgang"},
                                                     {"Pau", "Petra"},
                                                     {"Friedrich", "Hans-Peter"}};

    private ArrayList<Rede> reden = new ArrayList<Rede>();
    private ArrayList<Redner> redners = new ArrayList<Redner>();
    private ArrayList<Anlage> anlagen = new ArrayList<Anlage>();
    private ArrayList<Sitzung> sitzungen = new ArrayList<Sitzung>();
    private ArrayList<Kommentar> kommentare = new ArrayList<Kommentar>();
    private ArrayList<Tagesordnungspunkt> tagesordnungspunkte = new ArrayList<Tagesordnungspunkt>();
    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private Document StammdatenDocument;

    // To prevent ID redundancy.
    private LinkedHashSet<String> topIDsForTopParse = new LinkedHashSet<>();
    private LinkedHashSet<String> topIDsForSessionParse = new LinkedHashSet<>();

    /**
     * Constructor for the protocolparser. Takes the already wc3.DOM parsed documents of the Bundestags Stammdatenblatt
     * and the Bundestags plenary protocols and parses them for all necessary information.
     * @param stammdatenblatt File class object that contains the path to the Stammdatenblatt.xml
     * @param plenarprotokolle File class object array that contains the paths to the plenarprotokoll.xmls
     */
    public ProtocolParser(File stammdatenblatt, File[] plenarprotokolle) throws ParserConfigurationException, IOException, SAXException {
        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        StammdatenDocument = builder.parse(stammdatenblatt);

        System.out.println("Parsing:");
        Document protocolDocument;

        int fullCount = plenarprotokolle.length;
        int counter = 1;

        System.out.println("Collecting Redner class objects. " + fullCount);
        for (File protocol : plenarprotokolle){
            protocolDocument = builder.parse(protocol);
            this.parseRedner(protocolDocument);
        }
        System.out.println("Confirming speakers using the Stammdatenblatt.");
        this.fixRedner();

        counter = 1;

        System.out.println("Parsing for all information." + fullCount);

        for (File protocol : plenarprotokolle){
            protocolDocument = builder.parse(protocol);
            this.parseSitzung(protocolDocument);
            this.parseTagesordnungspunkt(protocolDocument);
            this.parseAnlage(protocolDocument);
        }
        System.out.println();

        System.out.println("Fixing party and faction affiliation of speakers");
        parties.add("CDU");
        parties.add("CSU");
        for (Redner redner : redners){
            if (redner.getFaction().contains("LINKE") || redner.getParty().contains("LINKE")) {
                    redner.setParty("LINKE");
                    redner.setFaction("LINKE");
                    parties.add("LINKE");
            } else if (redner.getFaction().contains("BÜNDNIS") || redner.getParty().contains("BÜNDNIS")) {
                    redner.setParty("BÜNDNIS 90/DIE GRÜNEN");
                    redner.setFaction("BÜNDNIS 90/DIE GRÜNEN");
                    parties.add("BÜNDNIS 90/DIE GRÜNEN");
            } else if (redner.getFaction().contains("SPD") || redner.getParty().contains("SPD")){
                    redner.setParty("SPD");
                    redner.setFaction("SPD");
                    parties.add("SPD");
            } else if (redner.getFaction().contains("AfD") || redner.getParty().contains("AfD")) {
                    redner.setParty("AfD");
                    redner.setFaction("AfD");
                    parties.add("AfD");
            } else if (redner.getFaction().contains("FDP") || redner.getParty().contains("FDP")) {
                    redner.setParty("FDP");
                    redner.setFaction("FDP");
                    parties.add("FDP");
            } else if (redner.getFaction().contains("fraktionslos")) {
                    parties.add(redner.getParty());
            } else if (redner.getParty().equals("CDU") || redner.getParty().equals("CSU")){
                    redner.setFaction("CDU/CSU");
            }

            for (String id : redner.getReden()){
                for (Rede rede : reden){
                    if ( rede.getID().equals(id) ){
                        rede.setParty(redner.getParty());
                    }
                }
            }
        }



        System.out.println("Counting words of speeches and setting parties.");
        System.out.println();
        for (Rede rede : reden){
            rede.setInhalt(rede
                    .getInhalt()
                    .replace(" ", " "));
            rede.setWordCount(
                    rede
                    .getInhalt().split(" ").length);

        }

        System.out.println("Calculating duration of sessions.");
        for (Sitzung sitzung : sitzungen){
            sitzung.setDuration(sitzung.calcDuration());
        }

        System.out.println();

        System.out.println("+-------------------------+");
        System.out.println("|General Parse Stats: \t  |");
        System.out.println("+-------------------------+");
        System.out.println("| Speakers:\t\t" + redners.size() + "\t\t  |");
        System.out.println("| Topics:\t\t" + tagesordnungspunkte.size()  + "\t  |");
        System.out.println("| Addendums:\t" + anlagen.size() + "\t  |");
        System.out.println("| Sessions:\t\t" + sitzungen.size() + "\t\t  |");
        System.out.println("| Speaches:\t\t" + reden.size() + "\t  |");
        System.out.println("| Comments:\t\t" + kommentare.size() + "\t  |");
        System.out.println("+-------------------------+");
    }

    /**
     * Parses through a Plenarprotokoll XML and gets the basic stats of the entire session. Time, date,
     * discussed topics and session leaders.
     * @param protocolDocument A DOM parsed Bundestags Plenarprotokoll of the Document org.wc3.dom.Document class.
     */
    private void parseSitzung(Document protocolDocument){
        Sitzung_File_Impl sitzung = new Sitzung_File_Impl();
        Element elem;
        NodeList nodes;
        Node node;

        // Get Wahlperiode
        nodes = protocolDocument.getElementsByTagName("wahlperiode");
        sitzung.setLegislaturperiode(nodes.item(0).getTextContent());
        this.Wahlperiode = nodes.item(0).getTextContent();

        // Get ID
        nodes = protocolDocument.getElementsByTagName("sitzungsnr");
        sitzung.setID(nodes.item(0).getTextContent());

        // Get Titel
        nodes = protocolDocument.getElementsByTagName("sitzungstitel");
        sitzung.setTitle(nodes.item(0).getTextContent().replace("\n", "").replace("\t", ""));

        // Get Datum
        nodes = protocolDocument.getElementsByTagName("datum");
        elem = (Element) nodes.item(0);
        sitzung.setDate(LocalDate.parse(elem.getAttribute("date"), formatter));

        // Get Sitzungsbeginn
        nodes = protocolDocument.getElementsByTagName("sitzungsbeginn");
        elem = (Element) nodes.item(0);
        sitzung.setBegin(elem.getAttribute("sitzung-start-uhrzeit"));

        // Get Sitzungsende
        nodes = protocolDocument.getElementsByTagName("sitzungsende");
        elem = (Element) nodes.item(0);
        sitzung.setEnd(elem.getAttribute("sitzung-ende-uhrzeit"));


        // Get Sitzungsleiter
        nodes = protocolDocument.getElementsByTagName("name");
        // For all Sitzungsleiter check if they are registered under a name tag in this session.
        for (int i = 0; i < sitzungsleiterNames.length; i++) {
            String nachname = sitzungsleiterNames[i][0];
            String vorname = sitzungsleiterNames[i][1];
            for (int j = 0; j < nodes.getLength(); j++){
                String nameQuery = nodes.item(j).getTextContent();
                if (nameQuery.contains(nachname) && nameQuery.contains(vorname)){
                    sitzung.addSitzungsleiter(sitzungsleiterIDs[i]);
                    break;
                }
            }
        }


        nodes = protocolDocument.getElementsByTagName("tagesordnungspunkt");
        // Collect all Tagesordnungspunkt IDs contained in this Sitzung.
        for (int i = 0; i < nodes.getLength(); i++){
            String id;
            StringBuilder topID = new StringBuilder("");
            topID.append(sitzung.getID());
            topID.append("-");
            elem = (Element) nodes.item(i);

            String parseID = elem.getAttribute("top-id");
            parseID = parseID.replace(" ", " "); // The elusive annoying alternative whitespace
            if (parseID.split(" ").length == 3) {
                if (parseID.contains("usatz")) {
                    topID.append( parseID.split(" ")[1] );
                    topID.append( parseID.split(" ")[2] );
                    topID.append( "Z" );
                } else {
                    topID.append( parseID.split(" ")[1] );
                    topID.append( parseID.split(" ")[2] );
                }
            } else if (parseID.split(" ").length == 2) {
                if (parseID.contains("usatz")) {
                    topID.append( parseID.split(" ")[1] );
                    topID.append( "Z" );
                } else {
                    topID.append( parseID.split(" ")[1] );
                }
            } else {
                if (parseID.contains("usatz")) {
                    topID.append( "000" );
                    topID.append( "Z" );
                } else {
                    topID.append( "000" );
                }
            }

            while (topIDsForSessionParse.contains(topID.toString())){
                topID.append("Z");
            }
            topIDsForSessionParse.add(topID.toString());
            topID.append("-TOP");

            sitzung.addTagesordnungspunkt(topID.toString());
        }


        nodes = protocolDocument.getElementsByTagName("anlage");
        // Collect all Anlage IDs contained in this Sitzung.
        for (int i = 0; i < nodes.getLength(); i++){
            String id;
            StringBuilder anlID = new StringBuilder("");
            anlID.append(sitzung.getID());
            anlID.append("-");
            node = nodes.item(i).getFirstChild().getNextSibling(); // this is the anlagen-titel
            if (node.getTextContent().contains(" ")) {
                id = node.getTextContent().split(" ")[1];
            } else if (node.getTextContent().contains(" ")) {
                id = node.getTextContent().split(" ")[1];
            } else {
                id = "00";
            }
            anlID.append(id);
            anlID.append("-ANL");
            sitzung.addAnlage(anlID.toString());
        }
        sitzung.getDuration(); // this trigger parsing the Begin and the End time for the duration
                                // for the first time and the value gets stored in the class object.
        sitzungen.add(sitzung);
    }

    /**
     * Parses through a Plenarprotokoll XML and gets the stats of all persons that spoke. If a speaker has already been
     * encountered in a previous protocol, their data will be overwritten. The IDs are assumed as reliable identifiers
     * for speakers, which in reality is not entirely true. See how often you will be able to find Wolfgang Schaeuble.
     * @param protocolDocument A DOM parsed Bundestags Plenarprotokoll of the Document org.wc3.dom.Document class.
     */
    private void parseRedner(Document protocolDocument){
        Element elem;
        NodeList nodes;
        Node node;
        Node child;
        Node grandchild;
        String id;
        int newRednerIndex;
        Redner redner;

        nodes = protocolDocument.getElementsByTagName("rednerliste");
        nodes = nodes.item(0).getChildNodes();
        for ( int i = 0; i < nodes.getLength(); i++) {
            node = nodes.item(i);
            if ( node.getNodeType() == 1 && node.getNodeName().equals("redner")) {
                elem = (Element) node;
                id = elem.getAttribute("id");
                newRednerIndex = findRednerIndex(id);
                if (newRednerIndex == -1) { // The Redner did not already exist and will get initialized.
                    redner = new Redner_File_Impl();
                    redner.setID(id);
                    redners.add(redner);
                } else {
                    redner = redners.get(newRednerIndex); // The Redner did already exist and will get updated.
                }
                node = node.getFirstChild().getNextSibling(); // This is the name Node. Risky skip of the day. I put my trust in the XML consistency!
                for (int j = 0; j < node.getChildNodes().getLength(); j++) {
                    child = node.getChildNodes().item(j);
                    if ( child.getNodeType() == 1) {
                        switch(child.getNodeName()){
                            case "vorname":
                                redner.setVorname(child.getTextContent());
                                break;
                            case "nachname":
                                redner.setNachname(child.getTextContent());
                                break;
                            case "titel":
                                redner.setTitle(child.getTextContent());
                                break;
                            case "fraktion": // No parties in the speaker lists. Gotta get those later. Always be parsin...
                                // This reduces the Factions to the ones that actually exist. I hate alternative whitespaces so much.
                                if (child.getTextContent().equals("DIE LINKE")){
                                    redner.setFaction("LINKE");
                                    factions.add("LINKE");
                                } else if (child.getTextContent().contains("BÜNDNIS")){// Filter for different version of the Greens
                                    redner.setFaction("BÜNDNIS 90/DIE GRÜNEN");
                                    factions.add("BÜNDNIS 90/DIE GRÜNEN");
                                } else if (child.getTextContent().contains("SPD")) {
                                    redner.setFaction("SPD");
                                    factions.add("SPD");
                                } else if (child.getTextContent().contains("CDU/")) {
                                    redner.setFaction("CDU/CSU");
                                    factions.add("CDU/CSU");
                                }else if (child.getTextContent().contains("AfD")) {
                                    redner.setFaction("AfD");
                                    factions.add("AfD");
                                } else if (child.getTextContent().equals("Erklärung nach § 30 GO") ||
                                           child.getTextContent().equals("zur Geschäftsordnung") ||
                                           child.getTextContent().equals("Bremen")) { break; // hardcoded filter for unwanted factions. Nonsense
                                } else if (child.getTextContent().contains("raktionslos")) {
                                    redner.setFaction("fraktionslos");
                                    factions.add("fraktionslos");
                                } else {
                                    redner.setFaction(child.getTextContent());
                                    factions.add(child.getTextContent()); // Add this faction to the set of factions.
                                }
                                break;
                            case "rolle":
                                for (int l = 0; l < child.getChildNodes().getLength(); l++){ // Roles have two subcategories, I gotta parse.
                                    grandchild = child.getChildNodes().item(l);
                                    if (grandchild.getNodeType() == 1 && grandchild.getNodeName().equals("rolle_kurz")) {
                                        redner.setRole(grandchild.getTextContent());
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Parses through a Plenarprotokoll XML and gets the stats of all topics that were discussed.
     * @param protocolDocument A DOM parsed Bundestags Plenarprotokoll of the Document org.wc3.dom.Document class.
     */
    private void parseTagesordnungspunkt(Document protocolDocument){
        Element elem;

        NodeList nodes;

        Node node;
        Node child;

        StringBuilder topID;
        StringBuilder title;
        StringBuilder commentId;

        String commentText;
        String redeID;

        int topCommentCounter = 0;

        nodes = protocolDocument.getElementsByTagName("tagesordnungspunkt");

        for (int i = 0; i < nodes.getLength(); i++){
            Tagesordnungspunkt top = new Tagesordnungspunkt_File_Impl();
            node = nodes.item(i);
            elem = (Element) node;

            // Assemble the ID for later reference.
            topID = new StringBuilder("");
            topID.append(sitzungen.get(sitzungen.size()-1).getID());
            topID.append("-");

            String parseID = elem.getAttribute("top-id");
            parseID = parseID.replace(" ", " "); // The elusive annoying alternative whitespace
            if (parseID.split(" ").length == 3) {
                if (parseID.contains("usatz")) {
                    topID.append( parseID.split(" ")[1] );
                    topID.append( parseID.split(" ")[2] );
                    topID.append( "Z" );
                } else {
                    topID.append( parseID.split(" ")[1] );
                    topID.append( parseID.split(" ")[2] );
                }
            } else if (parseID.split(" ").length == 2) {
                if (parseID.contains("usatz")) {
                    topID.append( parseID.split(" ")[1] );
                    topID.append( "Z" );
                } else {
                    topID.append( parseID.split(" ")[1] );
                }
            } else {
                if (parseID.contains("usatz")) {
                    topID.append( "000" );
                    topID.append( "Z" );
                } else {
                    topID.append( "000" );
                }
            }

            while (topIDsForTopParse.contains(topID.toString())){
                topID.append("Z");
            }
            topIDsForTopParse.add(topID.toString());
            topID.append("-TOP");
            top.setID(topID.toString());

            // Collect the title, the comments, the speeches, and the comments within speeches.
            title = new StringBuilder("");
            for (int j = 0; j < node.getChildNodes().getLength(); j++){
                child = node.getChildNodes().item(j);
                if (child.getNodeType() == 1){
                    switch(child.getNodeName()) {
                        case "p":
                            elem = (Element) child;
                            if (elem.getAttribute("klasse").equals("T_NaS")){ // Each node of this class contains a part of the title.
                                title.append(child.getTextContent());
                            }
                            break;
                        case "kommentar":
                            Kommentar comment = new Kommentar_File_Impl();
                            commentText = child.getTextContent().replace(" ", " ");

                            // Add the text.
                            comment.setInhalt(commentText);

                            // Assemble the Kommentar ID.
                            commentId = new StringBuilder("");
                            commentId.append(top.getID());
                            commentId.append("-");
                            commentId.append(topCommentCounter++); // Check the Kommentar Interface JavaDoc for ID explanations.
                            comment.setID(commentId.toString());

                            // Collect the factions.
                            for (String faction : factions){
                                if (commentText.contains(faction)){
                                    comment.addFaction(faction);
                                }
                            }

                            // Collect the commentors, depending on if both their first and last name are in the comment.
                            for (Redner redner : redners){
                                if (commentText.contains(redner.getVorname()) && commentText.contains(redner.getNachname())){
                                    comment.addRedner(redner.getID());
                                    redner.addKommentar(commentId.toString());
                                }
                            }

                            // Add to the list of comments.
                            kommentare.add(comment);

                            // Add ID to the Tagesordnungspunkt for later reference.
                            top.addKommentar(comment.getID());

                            break;
                        case "rede":
                            redeID = parseRede(child,
                                    top.getID(),
                                    sitzungen.get(sitzungen.size()-1).getID()); // This will be so expansive that it's better to move it to a subparser.
                            top.addRede(redeID);
                            break;
                        default:
                            break;
                    }
                }
            }
            top.setTitle(title.toString());
            topCommentCounter = 0; // Set it back to 0 for the next Tagesordnungspunkt.
            tagesordnungspunkte.add(top);
        }
    }

    /**
     * Parses through a Plenarprotokoll XML and gets the stats of all addendums that were added.
     * @param protocolDocument A DOM parsed Bundestags Plenarprotokoll of the Document org.wc3.dom.Document class.
     */
    private void parseAnlage(Document protocolDocument) {
        Element elem;

        NodeList nodes;

        Node node;
        Node child;

        StringBuilder anlId;
        StringBuilder title;

        String redeID;

        nodes = protocolDocument.getElementsByTagName("anlage");

        for (int i = 0; i < nodes.getLength(); i++) {
            Anlage anl = new Anlage_File_Impl();
            node = nodes.item(i);
            title = new StringBuilder("");

            // Assemble the ID for later reference.
            anlId = new StringBuilder("");
            anlId.append(sitzungen.get(sitzungen.size() - 1).getID());
            anlId.append("-");
            for (int j = 0; j < node.getChildNodes().getLength(); j++) {
                child = node.getChildNodes().item(j);
                if (child.getNodeType() == 1) {
                    elem = (Element) child;
                    switch (child.getNodeName()) {
                        case "anlagen-titel":
                            if (child.getTextContent().contains(" ")) { // The elusive annoying alternative whitespace
                                anlId.append(child.getTextContent().split(" ")[1]);
                            } else if (child.getTextContent().contains(" ")) {
                                anlId.append(child.getTextContent().split(" ")[1]);
                            } else {
                                anlId.append("00");
                            }
                            break;
                        case "p":
                            if (elem.getAttribute("klasse").contains("Anlage_")) {
                                title.append(child.getTextContent());
                            }
                            break;
                        case "rede":
                            redeID = parseRede(child,
                                    anlId.toString(),
                                    sitzungen.get(sitzungen.size()-1).getID()); // This will be so expansive that it's better to move it to a subparser.
                            anl.addRede(redeID);
                            break;
                    }
                }
            }
            anl.setTitle(title.toString());
            anl.setID(anlId.toString());
            anlagen.add(anl);
        }
    }

    /**
     * Parses through a org.wc3.dom.Node with the name rede and retrieves all information about the Rede.
     * @param node  org.wc3.dom.Node with the name rede
     * @return String that is the ID of the Rede to be inserted into the related Tagesordnungspunkt.
     */
    private String parseRede(Node node, String containedInID, String sessionID){
        Rede rede = new Rede_File_Impl();
        Element elem = (Element) node;
        Node child;
        Node grandchild;
        boolean rednerFound = false;
        int commentCounter = 0;
        String redeID = elem.getAttribute("id");
        String rednerID = "";
        String rednerFaction = "";
        String commentText;
        StringBuilder commentID;
        StringBuilder text = new StringBuilder("");

        for (int i = 0; i < node.getChildNodes().getLength(); i++){
            child = node.getChildNodes().item(i);
            if (child.getNodeType() == 1 ) {
                elem = (Element) child;
                switch (child.getNodeName()) {
                    case "p":
                        // This can only trigger once.
                        if (elem.getAttribute("klasse").equals("redner") && !rednerFound) {
                            for (int j = 0; j < child.getChildNodes().getLength(); j++) {
                                grandchild = child.getChildNodes().item(j);
                                if (grandchild.getNodeType() == 1 && grandchild.getNodeName().equals("redner")) {
                                    elem = (Element) grandchild;
                                    rednerID = elem.getAttribute("id");
                                    rednerFound = true;
                                    for (Redner rednerObj : redners) {
                                        if (rednerObj.getID().equals(rednerID)){
                                            rednerObj.addRede(redeID);
                                            rednerFaction = rednerObj.getFaction();
                                        }
                                    }
                                    break;
                                }
                            }
                        } else {
                            text.append(child.getTextContent());
                        }
                        break;
                    case "kommentar":
                        Kommentar comment = new Kommentar_File_Impl();
                        commentText = child.getTextContent().replace(" ", " ");

                        // Add the text.
                        comment.setInhalt(commentText);

                        // Assemble the Kommentar ID.
                        commentID = new StringBuilder("");
                        commentID.append(redeID);
                        commentID.append("-");
                        commentID.append(commentCounter++); // Check the Kommentar Interface JavaDoc for ID explanations.
                        comment.setID(commentID.toString());

                        // Collect the factions.
                        for (String faction : factions){
                            if (commentText.contains(faction)){
                                comment.addFaction(faction);
                            }
                        }

                        // Collect the commentors, depending on if both their first and last name are in the comment.
                        for (Redner redner : redners){
                            if (commentText.contains(redner.getVorname()) && commentText.contains(redner.getNachname())){
                                comment.addRedner(redner.getID());
                                redner.addKommentar(commentID.toString());
                            }
                        }

                        // Add to the list of comments.
                        kommentare.add(comment);

                        // Add ID to the Tagesordnungspunkt for later reference.
                        rede.addKommentar(comment.getID());
                        break;
                }
            }
        }

        rede.setID(redeID);
        rede.setRedner(rednerID);
        rede.setFaction(rednerFaction);
        rede.setInhalt(text.toString());
        rede.setSessionID(sessionID);
        rede.setContainedInID(containedInID);
        reden.add(rede);

        return redeID;
    }

    /**
     * Iterates over all collected speakers and fixes those which can be identified with an ID by overwriting
     * their information with the information given in the actual Stammdatenblatt.
     */
    private void fixRedner(){
        NodeList nodes = this.StammdatenDocument.getElementsByTagName("ID");
        Node node;
        Node nameNode;
        Node biographyNode;
        Node child;
        Node grandchild;


        for (Redner redner : redners){
            if (!redner.isFixed()){ // Only check Redner that have not already been confirmed.
                for (int i = 0; i < nodes.getLength(); i++){
                    node = nodes.item(i);
                    if (node.getTextContent().equals(redner.getID())){
                        nameNode = node.getNextSibling().getNextSibling(); // This is the NAMEN Node.
                        biographyNode = nameNode.getNextSibling().getNextSibling(); // The is the BIOGPRAFISCHE DATEN Node

                        child = nameNode.getLastChild().getPreviousSibling(); // This is the most recent NAME tag. Risky skip, but it works.
                        // This updates the Names.
                        for (int j = 0; j < child.getChildNodes().getLength(); j++){ // Update the names.
                            grandchild = child.getChildNodes().item(j);
                            if (grandchild.getNodeType() == 1){
                                switch(grandchild.getNodeName()){
                                    case "NACHNAME":
                                        redner.setNachname(grandchild.getTextContent());
                                        break;
                                    case "VORNAME":
                                        redner.setVorname(grandchild.getTextContent());
                                        break;
                                    case "ANREDE_TITEL":
                                        redner.setTitle(grandchild.getTextContent());
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }

                        // Now this gets the party.
                        for (int j = 0; j < biographyNode.getChildNodes().getLength(); j++){
                            child = biographyNode.getChildNodes().item(j);
                            if (child.getNodeType() == 1 && child.getNodeName().equals("PARTEI_KURZ")){
                                redner.setParty(child.getTextContent());
                            }
                        }
                        redner.setFixed(); // Confirm at the end, so no attributes can be changed anymore.
                        // Comments and Speeches can still be added.
                        break;
                    }
                }
            }
        }
    }

    /**
     * Method that gets the index within the redners ArrayList of the speaker of a certain ID. If
     * no speaker of this ID is found, the index -1 is returned.
     *
     * @param id The speaker id that is searched for.
     * @return A string that is the id of the speaker in the redners ArrayList.
     */
    private int findRednerIndex(String id){
        int index = -1;
        for (int i = 0; i < redners.size(); i++) {
            if (redners.get(i).getID().equals(id)){
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public LinkedHashSet<String> getParties() {
        return parties;
    }

    @Override
    public ArrayList<Rede> getReden() {
        return reden;
    }

    @Override
    public ArrayList<Redner> getRedners() {
        return redners;
    }

    @Override
    public ArrayList<Anlage> getAnlagen() {
        return anlagen;
    }

    @Override
    public ArrayList<Tagesordnungspunkt> getTagesordnungspunkte() {
        return tagesordnungspunkte;
    }

    @Override
    public ArrayList<Sitzung> getSitzungen() {
        return sitzungen;
    }

    @Override
    public ArrayList<Kommentar> getKommentare() {
        return kommentare;
    }

    @Override
    public LinkedHashSet<String> getFactions() {
        return factions;
    }

    @Override
    public String[] getSitzungsleiterIDs() {
        return this.sitzungsleiterIDs;
    }
}
