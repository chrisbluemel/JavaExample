package Interfaces;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.bson.Document;

/**
 * Interfaces that determines which methods must be implemented for a Rede class object.
 * @author Christian Bluemel
 */
public interface Rede {

    // SETTER

    /**
     * Set the ID of the Rede.
     * @param ID String that is the ID of the Rede.
     */
    void setID(String ID);

    /**
     * Set the faction that is connected to this speech.
     * @param faction String that is the name of the faction connected to this speech.
     */
    void setFaction(String faction);

    /**
     * Set the inhalt of the Rede.
     * @param inhalt The inhalt of the Rede
     */
    void setInhalt(String inhalt);

    /**
     * Set the party that is connected to this speech.
     * @param party String that is the party connected to this speech.
     */
    void setParty(String party);

    /**
     * Set the Redner of the Rede, by putting in their ID.
     * @param ID The ID that represents the speaker that did this speech.
     */
    void setRedner(String ID);

    /**
     * Set the word count of this speech.
     * @param count integer that is the count of words of this speech.
     */
    void setWordCount(int count);

    /**
     * Add the ID of a Kommentar that is connected to this speech.
     * The ID of a Kommentar is the ID of the ID of the Rede it is contained in followed by the Index
     * of the Kommentar in relation to the other comments in the Rede starting at 0.
     * Format: RedeID-Index
     * Example 1938273ID-3 is the 4th comment from Rede 1938273ID
     * @param ID String that is the ID of the Rede
     */
    void addKommentar(String ID);

    /**
     * Set the ID of the session this speech is contained in.
     * @param ID String that is the ID of the session this speech is contained in.
     */
    void setSessionID(String ID);

    /**
     * Set the ID of the addendum or topic this speech is contained in.
     * @param ID String that is the ID of the addendum or topic this speech is contained in.
     */
    void setContainedInID(String ID);

    // GETTER
    /**
     * Retrieve the ID of this Rede class object.
     * @return String that is the ID of this Rede.
     */
    String getID();

    /**
     * Retrieve the text of this Rede.
     * @return String that is the text of this Rede.
     */
    String getInhalt();

    /**
     * Retrieve the ID of the Redner that is connected to this Rede.
     * @return String that is the ID of the Redner that is connected to this Rede.
     */
    String getRedner();

    /**
     * Retrieve all IDs of all comments connected to this Rede.
     * @return String Array of the IDs of all Kommentar class objects connected to this Rede.
     */
    String[] getKommentare();

    /**
     * Retrieve the ID of the session this speech is contained in.
     * @return String that is the ID of the session this speech is contained in.
     */
    String getSessionID();

    /**
     * Retrive the ID of the addendum or topic this speech is contained in.
     * @return String that is the ID of the addendum or topic this speech is contained in.
     */
    String getContainedInID();


    /**
     * Retrieve the faction that is connected to this speech.
     * @return String that represents the faction that is connected to this speech.
     */
    String getFaction();

    /**
     * Retrieve the party that is connected to this speech.
     * @return String that is the party connected to this speech.
     */
    String getParty();

    /**
     * Retrieve the word count of the speech.
     * @return integer that is the word count of the speech.
     */
    int getWordCount();

    /**
     * Turn this object into a bson Document object prepared for upload.
     * @return bson Document that can be uploaded into a MongoDatabase
     */
    Document toDocument();


    /**
     * Set the text metascore of this speech.
     * @param text Double that is the metascore that was derived from searching a certain text in this speech.
     */
    void setScore(Double text);

    /**
     * Get the text metascore of this speech..
     * @return Double that is the metascore that was derived from searching a certain text in this speech.
     */
    Double getScore();

    /**
     * Turns this speech into a JCas object.
     * @return JCas instance that represents this speech.
     * @throws UIMAException
     */
    JCas toCAS() throws UIMAException;

}
