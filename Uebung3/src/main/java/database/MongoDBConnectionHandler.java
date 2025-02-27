package database;

import com.mongodb.MongoClientOptions;
import com.mongodb.*;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Class that manages the connection to a mongoDB database given a credentials.
 * @author Guiseppe Abrami, mongoDB documentation (<a href="https://www.mongodb.com/docs/">...</a>), Christian Blümel
 */
public class MongoDBConnectionHandler {

    final static String[] collectionNames = {
            "sitzungen", "reden", "redners",
            "tagesordnungspunkte", "fraktionen", "anlagen",
            "kommentare", "sitzungsleiterIDs"
    };

    private MongoClient mongoClient = null;
    private MongoDatabase mongoDatabase = null;

    /**
     * Constructor that goes through the setup of the connection to the mongoDB.
     * @param config A Properties class config object that contains values for the keys
     *               remote_users, remote_databases, remote_password, remote_host, remote_port.
     */
    public MongoDBConnectionHandler(Properties config){
        // -------------------------------------------------------
        // ALL BELOW INSPIRED BY MongoDatabaseConnector.java in example3.zip by GUISEPPE ABRAMI

        MongoCredential credential = MongoCredential.createScramSha1Credential(
                config.getProperty("remote_user"),
                config.getProperty("remote_database"),
                config.getProperty("remote_password").toCharArray());
        ArrayList<MongoCredential> credentials = new ArrayList<MongoCredential>();
        credentials.add(credential);
        // defining Hostname and Port
        ServerAddress seed = new ServerAddress(config.getProperty("remote_host"), Integer.parseInt(config.getProperty("remote_port")) );
        List<ServerAddress> seeds = new ArrayList(0);
        seeds.add(seed);
        // defining some Options
        MongoClientOptions options = MongoClientOptions.builder()
                .connectionsPerHost(2)
                .socketTimeout(300000)
                .maxWaitTime(10000)
                .connectTimeout(1000)
                .sslEnabled(false)
                .build();

        // connect to MongoDB
        mongoClient = new MongoClient(seeds, credentials, options);
        // ALL ABOVE INSPIRED BY MongoDatabaseConnector.java IN example3.zip BY GUISEPPE ABRAMI
        // -------------------------------------------------------

        this.mongoDatabase = mongoClient.getDatabase(config.getProperty("remote_database"));

        // -------------------------------------------------------
        // Below taken from the mongoDB documentation as a test if the connection is up.
        try {
            Bson command = new BsonDocument("ping", new BsonInt64(1));
            Document commandResult = this.mongoDatabase.runCommand(command);
            System.out.println("Connected successfully to server.");
        } catch (MongoException me) {
            System.err.println("An error occurred while attempting to run a command: " + me);
        }
        // Above taken from the mongoDB documentation as a test if the connection is up.
        // -------------------------------------------------------
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    /**
     * Drops all collections currently in the database and generates the base collections anew, but empty.
     */
    public void resetCollections(){
        for ( String collectionName : this.getMongoDatabase().listCollectionNames()) {
            this.getMongoDatabase().getCollection(collectionName).drop();
        }
        this.setupCollections();
    }

    /**
     * Initiates all the basic collections "sitzungen", "reden", "redners",
     *             "tagesordnungspunkte", "fraktionen", "anlagen",
     *             "kommentare" "sitzungsleiterIDs
     */
    protected void setupCollections(){
        for (String collectionName : collectionNames){
            this.getMongoDatabase().createCollection(collectionName);
        }
    }

    /**
     * Finds documents in a collection identified by the given filter and reduced by the given projection.
     * @param collName String that is the name of the collection.
     * @param filter Bson expression used to filter the collection.
     * @param projection Bson expression used to reduce the documents to wanted keys.
     * @return FindIterable filled with the found Documents.
     */
    public FindIterable<Document> findDocumentsInColl(String collName, Bson filter, Bson projection ){
        MongoCollection<Document> collection = this.getMongoDatabase().getCollection(collName);
        return collection.find(filter)
                .projection(projection);
    }

    /**
     * Deletes a single document defined by the given filter.
     * @param collName String that is the Name of the collection that shall be deleted from.
     * @param filter Bson filter expression that is used to identify the object that is to be deleted.
     */
    public void deleteOneDocument(String collName, Bson filter){
        MongoCollection<Document> collection = this.getMongoDatabase().getCollection(collName);

        try {
            DeleteResult result = collection.deleteOne(filter);
            System.out.println("Delete count: " + result.getDeletedCount());
        } catch (MongoException e) {
            System.err.println("Something went wrong. Check this: " + e);
        }
    }

    /**
     * Generates a document and inserts it into the given collection.
     * @param collName String that is the name of the collection to be inserted into.
     * @param keyArray String Array that contains keys of fields that the document contains.
     * @param valueArray String Array that is the values of the respective fields from the keyArray.
     */
    public void insertOneDocument(String collName, String[] keyArray, Object[] valueArray){
        MongoCollection<Document> collection = this.getMongoDatabase().getCollection(collName);

        boolean idFlag = true;

        Document insertee = new Document();
        for (int i = 0; i < keyArray.length; i++){
            if (keyArray[i].equals("_id")){
                idFlag = false;
            }
            insertee.append(keyArray[i], valueArray[i]);
        }
        if (idFlag){
            insertee.append("_id", new ObjectId());
        }
        try {
            collection.insertOne(insertee);
            System.out.println("Inserted " + insertee.toJson() + ".");
        } catch (MongoException e) {
            System.err.println("Something went wrong. Check this: " + e);
        }
    }

    /**
     * Method that counts documents in the given collection that fit the given Bson filter.
     * @param collName String that is the name of the collection.
     * @param filter Bson object that is the filter for the collections documents.
     * @return long that is the count. -1 if error occurred.
     */
    public long countColl(String collName, Bson filter){
        MongoCollection<Document> collection = this.getMongoDatabase().getCollection(collName);
        long count;
        try {
            count = collection.count(filter);
        } catch (MongoException e) {
            System.err.println("Can't count. Returning -1. Check this: " + e);
            count = -1;
        }
        return count;
    }


    /**
     * Method that aggregates a collection according to the given input.
     * @param collName String that is the name of the collection that shall be aggregated.
     * @param groupBy String that is the field the documents shall be grouped by.
     * @param acc   BsonField that defines the expression of how the documents shall be accumulated.
     * @param filter Bson Filter that defines how the Aggregatees get reduced.
     * @return  An Iterable Aggregate of Documents generate by the Aggregation.
     */
    public AggregateIterable<Document> aggregateColl(String collName, String groupBy, BsonField acc, Bson filter) {
        MongoCollection<Document> coll = this.getMongoDatabase().getCollection(collName);

        return coll.aggregate(
                Arrays.asList(
                        Aggregates.match(filter),
                        Aggregates.group("$" + groupBy, acc)
                )
        );
    }

    /**
     * Updates a single field in a single document in the given collection.
     * @param collName  String that is the collection name.
     * @param identifierField String that identifies the Field which will contain out identifierValue
     * @param identifierValue Object that is the identifier value.
     * @param updateField String that identifies the Field which will be updated.
     * @param updateValue Object that is the value that will be entered into the field.
     * @param upsertFlag If this is true, when the document is not found, it will instead be inserted into the collection.
     */
    public void updateOneDocument(String collName,
                                    String identifierField,
                                    Object identifierValue,
                                    String updateField,
                                    Object updateValue,
                                    boolean upsertFlag){
        MongoCollection<Document> collection = this.getMongoDatabase().getCollection(collName);
        Document updateQuery = new Document().append(identifierField,  identifierValue);
        Bson updates = Updates.set(updateField, updateValue);
        UpdateOptions options = new UpdateOptions().upsert(upsertFlag);
        try {
            UpdateResult result = collection.updateOne(updateQuery, updates, options);
            System.out.println("Update count: " + result.getModifiedCount());
            System.out.println("Upsert? " + result.getUpsertedId()); // only contains a value when an upsert is performed
        } catch (MongoException e) {
            System.err.println("Can't update. Check this: " + e);
        }
    }

}
