package Statistics;

import ChrisTime.ChrisDate;
import Interfaces.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import database.Extensions.MongoDBProtocolLoader;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import static java.util.Objects.isNull;

/**
 * Class to generate basic statistics about parsed Bundestag plenary protocols from remote mongoDB data.
 *
 * @author Christian Bluemel
 */
public class StatsFromRemote{

    private MongoDBProtocolLoader loader;

    /**
     * Constructor that saves a MongoDBProtocolLoader for access to the mongoDB.
     * @param protocolLoader A MongoDBProtocolLoader class object that has already established access to the mongoDB.
     */
    public StatsFromRemote(MongoDBProtocolLoader protocolLoader) {
        loader = protocolLoader;
    }

    /**
     * Generates a multiline String that contains String representations of all Redner class objects
     * found in the mongoDB. The String representations are sorted and filtered.
     * @param filter String Array that defines inclusion filters for four attributes of the Redner class objects:
     *               Nachname, Vorname, Faction and Party.
     * @return multiline String that contains String representations of all Redner class objects
     *          found in the mongoDB. The String representations are sorted and filtered.
     */
    public String toStringFilteredSortedRedners(String[] filter){
        StringBuilder output = new StringBuilder("+-------------------------+\n");
        output.append("| Redner List:       \t  |\n");
        output.append("+-------------------------+\n");
        output.append("Nachname, Vorname (Fraktion) (Partei)\n");
        output.append("|----Start----<\n");

        ArrayList<Redner> redners = loader.getFilteredRednerSortedBy(filter, "nachname");

       for (Redner redner : redners){
           output.append(redner.toString());
           output.append("\n");
       }

        output.append(">----End----|\n");
        return output.toString();
    }

    /**
     * Generates a multiline String that contains String representations of all Tagesordnungspunkt class objects related
     * to the Sitzung class object identified by the given ID.
     * @param sitzungID String that is the ID of a Sitzung class object.
     * @return multiline String that contains String representations of all Tagesordnungspunkt class objects related
     *          to the Sitzung class object identified by the given ID.
     */
    public String listAllRednerOfSitzungPerTagesordnungspunkt(String sitzungID){
        StringBuilder output = new StringBuilder("+-------------------------+\n");
        Sitzung sitzung = loader.findSitzung(sitzungID);
        if (isNull(sitzung)){
            return null;
        } else {
            // Get stuff.
            String[] topIDs = sitzung.getTagesordnungspunkte();

            // Build string.
            output.append("| Redner list by TOPs:\t|\n");
            output.append("+-------------------------+\n");
            output.append(sitzung.getTitle());
            output.append("\t");
            output.append(sitzung.getDate());
            output.append("\n|----Start----<\n");


            for (int i = 0; i < topIDs.length; i++){
                Tagesordnungspunkt top = loader.findTagesordnungspunkt(topIDs[i]);

                // Build String
                if (top.getID().contains("Z")) {
                    output.append("Zusatzpunkt ");
                } else {
                    output.append("Tagesordnungspunkt ");
                }
                output.append(top.getID().split("-")[1].replace("Z", ""));
                output.append(" (");
                output.append(top.getID());
                output.append(")\n\t");
                output.append(top.getTitle());
                output.append("\n");

                // Get stuff.
                ArrayList<Redner> redners = loader.findListOfRednerOfRedenIDs(top.getReden());
                for (Redner redner : redners){
                    output.append("\t\t");
                    output.append(redner.toString());
                    output.append("\n");
                }
            }
            output.append(">----End----|\n");
        }
        return output.toString();
    }

    /**
     * For all speakers that lead a session in the plenary protocols this method calculates how often they did so and
     * outputs it as a well formatted multiline String.
     * @return multiline String containing the information about which speaker lead how many sessions.
     */
    public String listSitzungsleiterAndLeadSessionCount(){
        // Build String
        StringBuilder output = new StringBuilder("+-------------------------+\n");
        output.append("| Sitzungsleiter List:\t |\n");
        output.append("+-------------------------+\n");
        output.append("Nachname, Vorname (Fraktion) (Partei) | Leitungs-Count\n");
        output.append("|----Start----<\n");


        // Get stuff
        String[] sitzungsleiterIDs = loader.getSitzungsleiterIDs();
        ArrayList<Redner> sitzungsleiter = loader.findListOfRedners(sitzungsleiterIDs);

        int[] sessionsLeadCounts = loader.countOccurencesOfListInFieldOfColl(
                sitzungsleiterIDs,
                "sitzungsleiter",
                "sitzungen"
        );

        // Further build the String
        for (int i = 0; i < sitzungsleiter.size(); i++){
            output.append(sitzungsleiter.get(i));
            output.append(" \t|\t ");
            for (int j = 0; j < sitzungsleiterIDs.length; j++){
                if (sitzungsleiter.get(i).getID().equals(sitzungsleiterIDs[j])){
                    output.append(sessionsLeadCounts[j]);
                }
            }
            output.append("\n");
        }
        output.append(">----End----|\n");
        return output.toString();
    }

    public String AvgSpeechLength() {
        // Build String
        StringBuilder output = new StringBuilder("+-------------------------+\n");
        output.append("| Speech Length Avg.      |\n");
        output.append("| (word count)            |\n");
        output.append("+-------------------------+\n|\t");

        // Initiate Counter
        int speechCount = loader.countDocsOfCollectionIfRegex("reden", "faction", "");
        int lengthSum = loader.sumFieldsOfCollectionIfRegex("wordCount", "reden", "faction", "");

        output.append(lengthSum/speechCount);
        output.append("\n+-------------------------+\n");


        return output.toString();
    }

    /**
     * Calculates the average speech length for each speaker represented in the Bundestags plenary protocols and
     * outputs the results as a formatted multiline String
     * @return multiline String containing the String representations for each speaker and also their avergae
     *          speech length.
     */
    public String listAllSpeakerWithAvgSpeechLength() {
        // Build String.
        StringBuilder output = new StringBuilder("+-------------------------+\n");
        output.append("| Speaker list            |\n");
        output.append("| with avg.               |\n");
        output.append("| speech length.          |\n");
        output.append("+-------------------------+\n");
        output.append("Nachname, Vorname (Fraktion) (Partei) | Avg. speech length\n");
        output.append("|----Start----<\n");

        ArrayList<Redner> redners = loader.getFilteredRednerSortedBy(new String[] {"", "", "", ""}, "nachname");

        ArrayList<Document> speechCounts = loader.countRowsOfCollectionGroupBy("reden", "redner");
        ArrayList<Document> wordSums = loader.sumFieldsOfCollectionGroupBy("wordCount", "reden", "redner");

        for (Redner redner : redners) {
            boolean foundFlag = false;
            Document countDoc = null;
            // Collect the count.
            for (Document doc : speechCounts){
                if (doc.getString("_id").equals(redner.getID())) {
                    foundFlag = true;
                    countDoc = doc;
                    break;
                }
            }

            if (foundFlag) {
                Document sumDoc = null;
                // Collect the sum.
                for (Document doc : wordSums){
                    if (doc.getString("_id").equals(redner.getID())) {
                        sumDoc = doc;
                        break;
                    }
                }

                output.append(redner.toString());
                output.append(" ");
                output.append(sumDoc.getInteger("_sum") / countDoc.getInteger("_count"));
            } else {
                output.append(redner.toString());
                output.append(" ");
                output.append(0);
            }
            output.append("\n");
        }
        output.append(">----End----|\n");
        return output.toString();
    }

    /**
     * Calculates the average speech length for each faction represented in the Bundestags plenary protocols and
     * outputs the results as a formatted multiline String
     * @return multiline String containing the name of each faction and also their avergae
     *          speech length.
     */
    public String listAllFactionWithAvgSpeechLength() {
        // Build String.
        StringBuilder output = new StringBuilder("+-------------------------+\n");
        output.append("| Avg. speech length per  |\n");
        output.append("| faction: (word count)   |\n");
        output.append("+-------------------------+\n");

        // Get stuff.
        LinkedHashSet<String> factions = loader.getFactions();

        ArrayList<Document> speechCounts = loader.countRowsOfCollectionGroupBy("reden", "faction");
        ArrayList<Document> wordSums = loader.sumFieldsOfCollectionGroupBy("wordCount", "reden", "faction");

        // Iterate over all factions.
        for (String faction : factions) {
            Document countDoc = null;
            Document sumDoc = null;

            for (Document doc : speechCounts){
                if (doc.getString("_id").equals(faction)) {
                    countDoc = doc;
                    break;
                }
            }

            for (Document doc : wordSums){
                if (doc.getString("_id").equals(faction)) {
                    sumDoc = doc;
                    break;
                }
            }

            output.append("\n");
            // Build String
            output.append(faction);
            output.append(" | ");
            output.append("Avg. speech length: ");
            output.append(sumDoc.getInteger("_sum")/countDoc.getInteger("_count"));
        }
        return output.toString();
    }

    /**
     * Outputs a list of all speakers that is filtered by names, faction and party
     * @param filter String Array that contains filters for forename, surname, party and faction respectively.
     * @return multiline String of all speakers String representations with the speech count at the end.
     */
    public String listAllRednerSortedBySpeechCount(String[] filter) {
        StringBuilder output = new StringBuilder("+-------------------------+\n");
        output.append("| Speaker List            |\n");
        output.append("| sorted by speech count: |\n");
        output.append("+-------------------------+\n");
        output.append("Nachname, Vorname (Fraktion) (Partei) | speech count\n");
        output.append("|----Start----<\n");

        ArrayList<Redner> redners = loader.getFilteredRednerSortedBy(filter, "speechCount");

        for (Redner redner : redners){
            output.append(redner.toString());
            output.append(" | ");
            output.append(redner.getSpeechCount());
            output.append("\n");
        }

        output.append(">----End----|\n");
        return output.toString();
    }

    /**
     * Searches the speeches in the mongoDB for a textQuery.
     * @param textQuery Query of words and phrases to search for.
     * @return multiline String that contains all String represntations of speeches that contain the searched for phrases
     * and words sorted by their metaScore (high, if the words and phrases were found often)
     */
    public String searchForFullText(String textQuery) {
        // Build String
        StringBuilder output = new StringBuilder("+-------------------------+\n");
        output.append("| Full text search of:    |\n");
        output.append("| ");
        output.append(textQuery);
        output.append("\n+-------------------------+\n");

        // Initiate Counter
        ArrayList<Rede> reden = loader.getSpeechesWithQuerySortedByQueryCount(textQuery);


        for (Rede rede : reden){
            output.append(rede.toString());
            output.append("+-------------------------+\n");
        }
        return output.toString();
    }

    /**
     * Lists all parties found in the Bundestags plenary protocols and their avergae speech length (word count)
     * @return Multiline String containing all parties and their average speech length.
     */
    public String listAllPartiesWithAvgSpeechLength() {
        // Build String.
        StringBuilder output = new StringBuilder("+-------------------------+\n");
        output.append("| Avg. speech length per  |\n");
        output.append("| party: (word count)   |\n");
        output.append("+-------------------------+\n");

        // Get stuff.
        LinkedHashSet<String> parties = loader.getParties();

        ArrayList<Document> speechCounts = loader.countRowsOfCollectionGroupBy("reden", "party");
        ArrayList<Document> wordSums = loader.sumFieldsOfCollectionGroupBy("wordCount", "reden", "party");

        // Iterate over all parties
        for (String party : parties) {
            Document countDoc = null;
            Document sumDoc = null;


            for (Document doc : speechCounts){
                if (!isNull(doc.getString("_id"))){ // Skip the group up speeches that did not get a party.
                    if (doc.getString("_id").equals(party)) {
                        countDoc = doc;
                        break;
                    }
                }
            }

            for (Document doc : wordSums){
                if (!isNull(doc.getString("_id"))){ // Skip the group up speeches that did not get a party.
                    if (doc.getString("_id").equals(party)) {
                    sumDoc = doc;
                    break;
                    }
                }
            }

            if (!isNull(sumDoc)) {
                output.append("\n");
                // Build String
                output.append(party);
                output.append(" | ");
                output.append("Avg. speech length: ");
                System.out.println(sumDoc.toJson());
                System.out.println(countDoc.toJson());
                output.append(sumDoc.getInteger("_sum")/countDoc.getInteger("_count"));
            }
        }
        return output.toString();
    }


    /**
     * Test function to check the indexes of the Reden collection.
     */
    public void checkIndexes() {
        MongoCollection<Document> coll =  this.loader.getMongoDatabase().getCollection("reden");
        for (Document doc : coll.listIndexes()){
            System.out.println(doc.toJson());
        }
    }

    /**
     * Method that generates a multiline String of all sessions sorted by Duration and filtered by the given Date filters,
     * but only if the filter flags have been set to true.
     * @param dateFilter LocalDate that is the  specific date the sessions must have to be loaded.
     * @param dateRangeFilter LocalDate Array that set the date range the sessions must be in to be loaded.
     * @param dateRangeFlag boolean that must be true to use the dateRangeFilter
     * @param dateFlag boolean that must be true to use the dateFilter
     * @return Returns a multiline String of the sessions.
     */
    public String listAllSessionsSortedByDurationFilteredBy(ChrisDate dateFilter, ChrisDate[] dateRangeFilter, boolean dateRangeFlag, boolean dateFlag) {
        StringBuilder output = new StringBuilder("+-------------------------+\n");
        output.append("| Session List            |\n");
        output.append("| sorted by duration.     |\n");
        output.append("+-------------------------+\n");

        ArrayList<Sitzung> sitzungen;
        if (dateFlag) {
            output.append("Only sessions on date:");
            output.append(dateFilter.toString());
            output.append("\n+-------------------------+\n");

            Bson filter = Filters.eq("date", dateFilter.getValue());
            sitzungen = loader.getSitzungenSortedByFilteredBy("duration", filter);

        } else if (dateRangeFlag) {
            output.append("Only sessions in this date range:");
            output.append("\n\tFrom: " + dateRangeFilter[0].toString());
            output.append("\n\tTo: " + dateRangeFilter[1].toString());
            output.append("\n+-------------------------+\n");

            Bson filter = Filters.and(Filters.gte("date", dateRangeFilter[0].getValue()), Filters.lte("date", dateRangeFilter[1].getValue()));
            sitzungen = loader.getSitzungenSortedByFilteredBy("duration", filter);

        } else {
            Bson filter = Filters.regex("_id", "");
            sitzungen = loader.getSitzungenSortedByFilteredBy("duration", filter);
        }
        output.append("Sitzungsnummer Datum | duration (in minutes)\n");
        output.append("|----Start----<\n");

        for (Sitzung sitzung : sitzungen){
            output.append(sitzung.toString(true));
            output.append(" | ");
            if (sitzung.getDuration() == 10000){
                output.append("not available");
            } else {
                output.append(sitzung.getDuration());
            }
            output.append("\n");
        }

        output.append(">----End----|\n");
        return output.toString();
    }
}
