package database.Extensions;

import Interfaces.*;
import Interfaces.Extensions.MongoDB.*;
import ProtocolInterface.ProtocolAssembly;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.*;
import database.MongoDBConnectionHandler;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static java.util.Objects.isNull;

/**
 * Class that manages to download certain parts of the mongoDB database that contains the protocol data on demand.
 * @author Christian Bluemel
 */
public class MongoDBProtocolLoader extends MongoDBConnectionHandler implements ProtocolAssembly {

    /**
     * Constructor that goes through the setup of the connection to the mongoDB.
     *
     * @param config A Properties class config object that contains values for the keys
     *               remote_users, remote_databases, remote_password, remote_host, remote_port.
     */
    public MongoDBProtocolLoader(Properties config) {
        super(config);
    }


    /**
     * Inserts an ArrayList of Rede class objects into the database.
     * @param reden ArrayList of Rede class objects.
     */
    public void uploadReden(ArrayList<Rede> reden){
        MongoCollection<Document> coll = super.getMongoDatabase().getCollection("reden");
        ArrayList<Document> query = new ArrayList<Document>();
        for (Rede rede : reden){
            query.add(rede.toDocument());
        }
        coll.insertMany(query);
        coll.dropIndexes();

        IndexOptions options = new IndexOptions();
        options.name("inhalt");
        options.defaultLanguage("german");
        coll.createIndex(Indexes.text("inhalt"), options);
    }

    /**
     * Inserts an ArrayList of Kommentar class objects into the database.
     * @param kommentare ArrayList of Kommentar class objects.
     */
    public void uploadKommentare(ArrayList<Kommentar> kommentare){
        MongoCollection<Document> coll = super.getMongoDatabase().getCollection("kommentare");
        ArrayList<Document> query = new ArrayList<Document>();
        for (Kommentar kommentar : kommentare){
            query.add(kommentar.toDocument());
        }
        coll.insertMany(query);
    }

    /**
     * Inserts an ArrayList of Sitzung class objects into the database.
     * @param sitzungen ArrayList of Sitzung class objects.
     */
    public void uploadSitzungen(ArrayList<Sitzung> sitzungen){
        MongoCollection<Document> coll = super.getMongoDatabase().getCollection("sitzungen");
        ArrayList<Document> query = new ArrayList<Document>();
        for (Sitzung sitzung : sitzungen){
            query.add(sitzung.toDocument());
        }
        coll.insertMany(query);
    }

    /**
     * Inserts an ArrayList of Anlage class objects into the database.
     * @param anlagen ArrayList of Anlage class objects.
     */
    public void uploadAnlagen(ArrayList<Anlage> anlagen){
        MongoCollection<Document> coll = super.getMongoDatabase().getCollection("anlagen");
        ArrayList<Document> query = new ArrayList<Document>();
        for (Anlage anlage : anlagen){
            query.add(anlage.toDocument());
        }
        coll.insertMany(query);
    }

    /**
     * Inserts an ArrayList of Tagesordnungspunkt class objects into the database.
     * @param tagesordnungspunkte ArrayList of Tagesordnungspunkt class objects.
     */
    public void uploadTagesordnungspunkte(ArrayList<Tagesordnungspunkt> tagesordnungspunkte){
        MongoCollection<Document> coll = super.getMongoDatabase().getCollection("tagesordnungspunkte");
        ArrayList<Document> query = new ArrayList<Document>();
        for (Tagesordnungspunkt tagesordnungspunkt : tagesordnungspunkte){
            query.add(tagesordnungspunkt.toDocument());
        }
        coll.insertMany(query);
    }

    /**
     * Inserts an ArrayList of Redner class objects into the database.
     * @param redners ArrayList of Redner class objects.
     */
    public void uploadRedner(ArrayList<Redner> redners){
        MongoCollection<Document> coll = super.getMongoDatabase().getCollection("redners");
        ArrayList<Document> query = new ArrayList<Document>();
        for (Redner redner : redners){
            query.add(redner.toDocument());
        }
        coll.insertMany(query);
    }

    /**
     * Inserts an LinkedHashSet of String class objects into the database.
     * The Strings are supposed to represent party names.
     * @param parteien LinkedHashSet of String class objects.
     */
    public void uploadParteien(LinkedHashSet<String> parteien){

        MongoCollection<Document> coll = super.getMongoDatabase().getCollection("parteien");
        ArrayList<Document> query = new ArrayList<Document>();
        for (String partei : parteien){
            Document factionDoc = new Document();
            factionDoc.append("name", partei);
            query.add(factionDoc);
        }
        coll.insertMany(query);
    }

    /**
     * Inserts an LinkedHashSet of String class objects into the database.
     * The Strings are supposed to represent faction names.
     * @param fraktionen LinkedHashSet of String class objects.
     */
    public void uploadFraktionen(LinkedHashSet<String> fraktionen){

        MongoCollection<Document> coll = super.getMongoDatabase().getCollection("fraktionen");
        ArrayList<Document> query = new ArrayList<Document>();
        for (String fraktion : fraktionen){
            Document factionDoc = new Document();
            factionDoc.append("name", fraktion);
            query.add(factionDoc);
        }
        coll.insertMany(query);
    }

    /**
     * Uploads the IDs of session leaders into the mongoDB databse.
     * @param sitzungsleiterIDs String Array of IDs.
     */
    public void uploadSitzungsleiterIDs(String[] sitzungsleiterIDs){
        MongoCollection<Document> coll = super.getMongoDatabase().getCollection("sitzungsleiterIDs");
        ArrayList<Document> query = new ArrayList<Document>();
        for (String id : sitzungsleiterIDs){
            Document leaderDoc = new Document();
            leaderDoc.append("id", id);
            query.add(leaderDoc);
        }
        coll.insertMany(query);
    }

    /**
     * This method combines all the singular upload methods of this class to upload an
     * entire ProtocolAssembly in one call.
     * @param protocol ProtocolAssembly class object that contains information of parsed Bundestags plenary protocols.
     */
    public void uploadProtocolAssembly(ProtocolAssembly protocol){
        System.out.println("Uploading:");
        System.out.println("0/9 Uploading Addenda");
        this.uploadAnlagen(protocol.getAnlagen());
        System.out.println("1/9 Uploading Factions");
        this.uploadFraktionen(protocol.getFactions());
        System.out.println("2/9 Uploading Comments");
        this.uploadKommentare(protocol.getKommentare());
        System.out.println("3/9 Uploading Speeches");
        this.uploadReden(protocol.getReden());
        System.out.println("4/9 Uploading Speakers");
        this.uploadRedner(protocol.getRedners());
        System.out.println("5/9 Uploading Topics");
        this.uploadTagesordnungspunkte(protocol.getTagesordnungspunkte());
        System.out.println("6/9 Uploading Sessions");
        this.uploadSitzungen(protocol.getSitzungen());
        System.out.println("7/9 Uploading session leaders.");
        this.uploadSitzungsleiterIDs(protocol.getSitzungsleiterIDs());
        System.out.println("8/9 Uploading Parties");
        this.uploadParteien(protocol.getParties());
        System.out.println("9/9 Done!");
    }

    @Override
    public LinkedHashSet<String> getFactions() {
        // Download the collection
        MongoCollection<Document> coll = super.getMongoDatabase().getCollection("fraktionen");
        FindIterable<Document> docs = coll.find();

        // Fill the container.
        LinkedHashSet<String> output = new LinkedHashSet<>();
        for ( Document doc : docs){
            output.add(doc.getString("name"));
        }
        return output;
    }

    @Override
    public LinkedHashSet<String> getParties() {
        // Download the collection
        MongoCollection<Document> coll = super.getMongoDatabase().getCollection("parteien");
        FindIterable<Document> docs = coll.find();

        // Fill the container.
        LinkedHashSet<String> output = new LinkedHashSet<>();
        for ( Document doc : docs){
            output.add(doc.getString("name"));
        }
        return output;
    }

    @Override
    public ArrayList<Redner> getRedners() {
        // Download the collection
        MongoCollection<Document> coll = super.getMongoDatabase().getCollection("redners");
        FindIterable<Document> docs = coll.find();

        // Fill the container using the constructor.
        ArrayList<Redner> output = new ArrayList<Redner>();
        for ( Document doc : docs){
            output.add(new Redner_MongoDB_Impl(doc));
        }
        return output;
    }

    @Override
    public ArrayList<Rede> getReden() {
        // Download the collection
        MongoCollection<Document> coll = super.getMongoDatabase().getCollection("reden");
        FindIterable<Document> docs = coll.find();

        // Fill the container using the constructor.
        ArrayList<Rede> output = new ArrayList<Rede>();
        for ( Document doc : docs){
            output.add(new Rede_MongoDB_Impl(doc));
        }
        return output;
    }

    @Override
    public ArrayList<Sitzung> getSitzungen() {
        // Download the collection
        MongoCollection<Document> coll = super.getMongoDatabase().getCollection("sitzungen");
        FindIterable<Document> docs = coll.find();

        // Fill the container using the constructor.
        ArrayList<Sitzung> output = new ArrayList<Sitzung>();
        for ( Document doc : docs){
            output.add(new Sitzung_MongoDB_Impl(doc));
        }
        return output;
    }

    @Override
    public ArrayList<Tagesordnungspunkt> getTagesordnungspunkte() {
        // Download the collection
        MongoCollection<Document> coll = super.getMongoDatabase().getCollection("tagesordnungspunkte");
        FindIterable<Document> docs = coll.find();

        // Fill the container using the constructor.
        ArrayList<Tagesordnungspunkt> output = new ArrayList<Tagesordnungspunkt>();
        for ( Document doc : docs){
            output.add(new Tagesordnungspunkt_MongoDB_Impl(doc));
        }
        return output;
    }

    @Override
    public ArrayList<Anlage> getAnlagen() {
        // Download the collection
        MongoCollection<Document> coll = super.getMongoDatabase().getCollection("anlagen");
        FindIterable<Document> docs = coll.find();

        // Fill the container using the constructor.
        ArrayList<Anlage> output = new ArrayList<Anlage>();
        for ( Document doc : docs){
            output.add(new Anlage_MongoDB_Impl(doc));
        }
        return output;
    }

    @Override
    public ArrayList<Kommentar> getKommentare() {
        // Download the collection
        MongoCollection<Document> coll = super.getMongoDatabase().getCollection("kommentare");
        FindIterable<Document> docs = coll.find();

        // Fill the container using the constructor.
        ArrayList<Kommentar> output = new ArrayList<Kommentar>();
        for ( Document doc : docs){
            output.add(new Kommentar_MongoDB_Impl(doc));
        }
        return output;
    }

    @Override
    public String[] getSitzungsleiterIDs() {
        // Download the collection
        MongoCollection<Document> coll = super.getMongoDatabase().getCollection("sitzungsleiterIDs");
        FindIterable<Document> docs = coll.find();

        // Fill the intermediate container.
        ArrayList<String> inter = new ArrayList<String>();
        for ( Document doc : docs){
            inter.add(doc.getString("id"));
        }

        // Fill the final container. This is kind of a smooth brain version of FindIterable to String Array conversion.
        String[] output = new String[inter.size()];
        for (int i = 0; i < inter.size(); i++){
            output[i] = inter.get(i);
        }

        return output;
    }

    /**
     * Returns the speeches from the DB that fulfill a given filter.
     * @param filter Bson filter that will be checked for.
     * @return ArrayList of the Rede class objects.
     */
    public ArrayList<Rede> findRedenFilteredBy(Bson filter) {
        MongoCollection<Document> coll = this.getMongoDatabase().getCollection("reden");
        ArrayList<Rede> output = new ArrayList<Rede>();


        MongoCursor<Document> cursor = coll
                .find(filter)
                .iterator();

        try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                output.add(new Rede_MongoDB_Impl(doc));
            }
        } finally {
            cursor.close();
        }
        return output;
    }

    /**
     * Gets the title, the date and tagesordnungspunkte of a Sitzung class objects. Returns null, if the Sitzung ID does
     * not exist in the database.
     * @param sitzungID String that is the ID of a session. These IDs are an integer between one and three digits long.
     * @return Sitzung class object if the ID was found in the database or null if not.
     */
    public Sitzung findSitzung(String sitzungID){
        MongoCollection<Document> coll = this.getMongoDatabase().getCollection("sitzungen");
        Sitzung output = null;


        MongoCursor<Document> cursor = coll
                .find(eq("_id", sitzungID))
                .projection(include("title", "date", "tagesordnungspunkte"))
                .iterator();
        try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                output = new Sitzung_MongoDB_Impl(doc);
            }
        } finally {
            cursor.close();
        }

        return output;
    }

    /**
     * Gets the _id, title and speeches of a tagesordnungspunkt class object. Returns null, if the Tagesordnungspunkt ID does
     * not exist in the database.
     * @param topID String that is the ID of a topic. These IDs are formatted like this: sessionID-TagesordnungspunktCount-TOP
     * @return Tagesordnungspunkt class object if the ID was found in the database or null if not.
     */
    public Tagesordnungspunkt findTagesordnungspunkt(String topID){
        MongoCollection<Document> coll = this.getMongoDatabase().getCollection("tagesordnungspunkte");
        Tagesordnungspunkt output = null;


        MongoCursor<Document> cursor = coll
                .find(eq("_id", topID))
                .projection(include("_id", "title", "reden"))
                .iterator();

        try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                output = new Tagesordnungspunkt_MongoDB_Impl(doc);
            }
        } finally {
            cursor.close();
        }

        return output;
    }

    /**
     * Gets the vorname, nachname, party, faction and fixed attributes of speakers that held speeches identified by
     * the input IDs. Returns an empty list, if no speech IDs are in the input String or none of the speakers do
     * not exist in the database.
     * @param redenIDs String Array that contains the IDs of speeches whose speakers are to be found.
     * @return ArrayList of Redner class objects that held the speeches of the given speech IDs.
     */
    public ArrayList<Redner> findListOfRednerOfRedenIDs(String[] redenIDs){
        MongoCollection<Document> redenColl = this.getMongoDatabase().getCollection("reden");
        MongoCollection<Document> rednersColl = this.getMongoDatabase().getCollection("redners");

        String[] rednerIDs = new String[redenIDs.length];


        ArrayList<Redner> output = new ArrayList<Redner>();

        // Get the IDs of the speakers.
        MongoCursor<Document> cursorReden = redenColl
                .find(in("_id", redenIDs))
                .projection(include("redner"))
                .iterator();

        int indexCount = 0;
        try {
            while (cursorReden.hasNext()) {
                Document doc = cursorReden.next();
                rednerIDs[indexCount++] = doc.getString("redner");
            }
        } finally {
            cursorReden.close();
        }
        output = findListOfRedners(rednerIDs);
        return output;
    }

    /**
     * Retrieves a list of speakers identified by the IDs given in a String Array.
     * @param rednerIDs String Array for IDs of the searched for speaker.
     * @return ArrayList of Redner class objects that contain the information about "vorname", "nachname", "party",
     *          "faction" and "fixed" attributes.
     */
    public ArrayList<Redner> findListOfRedners(String[] rednerIDs) {
        MongoCollection<Document> rednersColl = this.getMongoDatabase().getCollection("redners");
        ArrayList<Redner> output = new ArrayList<Redner>();

        // Get the speakers.
        MongoCursor<Document> cursorRedners = rednersColl
                .find(in("_id", rednerIDs))
                .projection(include("_id", "vorname", "nachname", "party", "faction", "fixed"))
                .iterator();

        try {
            while (cursorRedners.hasNext()) {
                Document doc = cursorRedners.next();
                output.add(new Redner_MongoDB_Impl(doc));
            }
        } finally {
            cursorRedners.close();
        }
        return output;
    }

    /**
     * For a given list of ids look through the given field of the given collection and count how often each of
     * the given ids occurred.
     * @param ids List of ids that are supposed to be counted.
     * @param fieldName field name present in the given mongoDB collection that contains a String ArrayList.
     * @param collName collection that exists in the present mongoDB.
     * @return Array of integer containing the occurrance counts for each id in the ids parameter.
     */
    public int[] countOccurencesOfListInFieldOfColl(String[] ids, String fieldName, String collName){
        int[] output = new int[ids.length];
        for (int i = 0; i < output.length; i++){
            output[i] = 0;
        }
        MongoCollection<Document> coll = this.getMongoDatabase().getCollection(collName);

        // Get the speakers.
        MongoCursor<Document> cursorRedners = coll
                .find()
                .projection(include(fieldName))
                .iterator();

        try {
            while (cursorRedners.hasNext()) {
                Document doc = cursorRedners.next();
                for (String id : (ArrayList<String>) doc.get(fieldName)) {
                    for (int i = 0; i < ids.length; i++){
                        if (id.equals(ids[i])){
                            output[i]++;
                        }
                    }
                }
            }
        } finally {
            cursorRedners.close();
        }
        return output;
    }

    /**
     * Sums the entries of the sumField in a collection filtered by if the entry of the filterField contains the filterRegex.
     *
     * @param sumField String tht is the name of the field that will get summed up.
     * @param collName String that is the name of the counted collection.
     * @param filterFieldName String that is the name of the field that the regex filter is applied to.
     * @param filterRegex Regex expression that will be checked for in the filterField.
     * @return integer that is the sum of sumFields in the collection complying with the given filters.
     */
    public int sumFieldsOfCollectionIfRegex(String sumField, String collName, String filterFieldName, String filterRegex) {
        MongoCollection<Document> coll = this.getMongoDatabase().getCollection(collName);
        AggregateIterable<Document> aggregate = coll.aggregate(
                Arrays.asList(
                        Aggregates.match(regex(filterFieldName,
                                filterRegex)
                        ),
                        Aggregates.group(null,
                                Accumulators.sum("_sum",
                                        "$" + sumField
                                )
                        )
                )
        );
        if (isNull(aggregate.first())){
            return 0;
        } else {
            return aggregate.first().getInteger("_sum");
        }
    }


    /**
     * Counts the Documents in a collection filtered by if the entry of the filterField contains the filterRegex.
     *
     * @param collName String that is the name of the counted collection.
     * @param filterFieldName String that is the name of the field that the regex filter is applied to.
     * @param filterRegex Regex expression that will be checked for in the filterField.
     * @return integer that is the number of documents in the collection complying with the given filters.
     */
    public int countDocsOfCollectionIfRegex(String collName, String filterFieldName, String filterRegex) {
        MongoCollection<Document> coll = this.getMongoDatabase().getCollection(collName);
        int output = (int) coll.count(regex(filterFieldName, filterRegex));
        return output;
    }

    /**
     * Counts how many rows of a collection each entry of the groupBy field occupies.
     * @param collName String that is the name of the collection that shall be counted in.
     * @param groupBy String that is the field of the collection which shall be used to group.
     * @return ArrayList of bson documents with the groupBy parameter as the _id key and _count as the key for the count.
     */
    public ArrayList<Document> countRowsOfCollectionGroupBy(String collName, String groupBy) {
        ArrayList<Document> output = new ArrayList<Document>();
        MongoCollection<Document> coll = this.getMongoDatabase().getCollection(collName);

        AggregateIterable<Document> aggregate = coll.aggregate(
                Arrays.asList(
                        Aggregates.match(regex("_id", "")),
                        Aggregates.group("$" + groupBy,
                                Accumulators.sum("_count",
                                        1
                                )
                        )
                )
        );
        for (Document doc : aggregate){
            output.add(doc);
        }
        return output;
    }

    /**
     * For each distinct attribute in the groupBy field of the collection, will sum all sumFields.
     *
     * @param sumField String that is the name of the field that shall be summed for each group.
     * @param collName String that is the name of the collection that shall be summed in.
     * @param groupBy String that is the field of the collection which shall be used to group.
     * @return ArrayList of bson documents with the groupBy parameter as the _id key and _sum as the key for the sum.
     */
    public ArrayList<Document> sumFieldsOfCollectionGroupBy(String sumField, String collName, String groupBy) {
        ArrayList<Document> output = new ArrayList<Document>();
        MongoCollection<Document> coll = this.getMongoDatabase().getCollection(collName);

        AggregateIterable<Document> aggregate = coll.aggregate(
                Arrays.asList(
                        Aggregates.match(regex("_id", "")),
                        Aggregates.group("$" + groupBy,
                                Accumulators.sum("_sum",
                                        "$" + sumField
                                )
                        )
                )
        );
        for (Document doc : aggregate){
            output.add(doc);
        }
        return output;
    }

    /**
     * Collects a filtered and sorted list of Redner class objects from the mongo DB.
     * @param filter String Array containing filter criteria for (0) first name, (1) last name, (2) faction, (3) party.
     * @param sortedBy String which is the name of the field that should be used to sort the speakers.
     * @return ArrayList of Redner class objects that were fetched from the server and fit the filter criteria.
     */
    public ArrayList<Redner> getFilteredRednerSortedBy(String[] filter, String sortedBy) {
        // Download the collection
        MongoCollection<Document> coll = this.getMongoDatabase().getCollection("redners");

        MongoCursor<Document> cursor = coll
                .find(and(
                        regex("vorname", filter[0]),
                        regex("nachname", filter[1]),
                        regex("faction", filter[2]),
                        regex("party", filter[3])))
                .projection(include("vorname", "nachname", "faction", "party", "fixed", "reden"))
                .sort(Sorts.ascending(sortedBy)).iterator();

        ArrayList<Redner> redners = new ArrayList<Redner>();

        try {
            while(cursor.hasNext()) {
                Document doc = cursor.next();
                redners.add(new Redner_MongoDB_Impl(doc));
            }
        } finally {
            cursor.close();
        }
        return redners;
    }

    /**
     * Output all speeches that contain the words in the textQuery sorted by how often the words of the textWuery are contained in them.
     * @param textQuery String that is the full text query that is searched for. No exact match required to get scored.
     * @return Array List of Rede class objects, sorted  by how often the query was found in them.
     */
    public ArrayList<Rede> getSpeechesWithQuerySortedByQueryCount(String textQuery) {
        ArrayList<Rede> output = new ArrayList<Rede>();
        MongoCollection<Document> coll = this.getMongoDatabase().getCollection("reden");

        Bson projection = Projections.fields(exclude("kommentare", "inhalt"), Projections.metaTextScore("score"));

        AggregateIterable<Document> results = coll.aggregate(Arrays.asList(
                Aggregates.match(text(textQuery))
                ,
                Aggregates.project(projection)
                ,
                Aggregates.sort(Sorts.ascending("score"))
                )
        ).allowDiskUse(true);


        for (Document doc : results){
            output.add(new Rede_MongoDB_Impl(doc));
        }

        return output;
    }

    /*
    /**
     * Outputs the Sessions sorted by a given parameter.
     * @param sortedBy String that is the name of the filed that will be sorted by.
     * @return Array List of Sitzung class objects.
     */
    /*
    public ArrayList<Sitzung> getSitzungenSortedBy(String sortedBy) {
        // Download the collection
        MongoCollection<Document> coll = this.getMongoDatabase().getCollection("sitzungen");
        MongoCursor<Document> cursor = coll
                .find()
                .projection(include("title", "date", "duration"))
                .sort(Sorts.descending(sortedBy)).iterator();

        ArrayList<Sitzung> sitzungen = new ArrayList<Sitzung>();

        try {
            while(cursor.hasNext()) {
                Document doc = cursor.next();
                sitzungen.add(new Sitzung_MongoDB_Impl(doc));
            }
        } finally {
            cursor.close();
        }
        return sitzungen;
    }
    */

    /**
     * Gets all sessions from the mongoDB that fit the given filter and sorts them descending by the given field.
     * @param sortedBy String that is the name of the field that should be sorted by (descending)
     * @param filter Bson Filter that is used to filter the query.
     * @return sorted ArrayList of all sessions that fit the filter.
     */
    public ArrayList<Sitzung> getSitzungenSortedByFilteredBy(String sortedBy, Bson filter) {
        // Download the collection
        MongoCollection<Document> coll = this.getMongoDatabase().getCollection("sitzungen");
        MongoCursor<Document> cursor = coll
                .find(filter)
                .projection(include("title", "date", "duration"))
                .sort(Sorts.descending(sortedBy)).iterator();

        ArrayList<Sitzung> sitzungen = new ArrayList<Sitzung>();

        try {
            while(cursor.hasNext()) {
                Document doc = cursor.next();
                sitzungen.add(new Sitzung_MongoDB_Impl(doc));
            }
        } finally {
            cursor.close();
        }
        return sitzungen;
    }
}
