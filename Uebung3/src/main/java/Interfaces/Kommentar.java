package Interfaces;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.bson.Document;

/**
 * Interfaces that determines which methods must be implemented for a Kommentar class object.
 * @author Christian Bluemel
 */
public interface Kommentar {

    // SETTER

    /**
     * Set the ID of this Kommentar.
     * The ID of a Kommentar is the ID of the ID of the Tagesordnungspunkt or Rede it is contained in followed by the Index
     * of the Kommentar in relation to the other comments in the Tagesordnungspunkt or Rede starting at 0.
     * Format: TagesordnungspunktID-Index or RedeID-Index
     * Example1: 5-2-TOP-3 is a comment from the 5th session in the 2nd Tagesordnungspunkt that was the first comment
     * in the Tagesordnungspunkt.
     * Example2: 93872ID-2 is the 3rd comment from Rede 93872ID.
     * @param ID String that is the ID of this Kommentar
     */
    void setID(String ID);

    /**
     * Set the Inhalt of this Kommentar.
     * @param inhalt String that is the Inhalt of this Kommentar.
     */
    void setInhalt(String inhalt);

    /**
     * Add the ID of a Redner to this Kommentar.
     * @param ID String that is the ID of a Redner connected to this Kommentar.
     */
    void addRedner(String ID);

    /**
     * Add the name of a faction to this Kommentar.
     * @param faction String that is the name of a faction connected to this Kommentar.
     */
    void addFaction(String faction);

    // GETTER

    /**
     * Retrieve the ID of this Kommentar.
     * The ID of a Kommentar is the ID of the ID of the Tagesordnungspunkt or Rede it is contained in followed by the Index
     * of the Kommentar in relation to the other comments in the Tagesordnungspunkt or Rede starting at 0.
     * Format: TagesordnungspunktID-Index or RedeID-Index
     * Example1: 5-2-TOP-3 is a comment from the 5th session in the 2nd Tagesordnungspunkt that was the first comment
     * in the Tagesordnungspunkt.
     * Example2: 93872ID-2 is the 3rd comment from Rede 93872ID.
     * @return String that is the ID of this Kommentar.
     */
    String getID();

    /**
     * Retrieve the Inhalt of this Kommentar
     * @return String that is the Inhalt of this Kommentar.
     */
    String getInhalt();

    /**
     * Retrieve the list of IDs of speakers connected to this Kommentar.
     * @return String Array that is the list of IDs connected to this Kommentar.
     */
    String[] getRedner();

    /**
     * Retrieve the list of names of factions connected to this Kommentar.
     * @return String Array that is the list of IDs connected to this Kommentar.
     */
    String[] getFactions();

    /**
     * Turn this object into a bson Document object prepared for upload.
     * @return bson Document that can be uploaded into a MongoDatabase
     */
    Document toDocument();

    /**
     * Turns this comment into a JCas object.
     * @return JCas instance that represents this comment.
     * @throws UIMAException
     */
    JCas toCAS() throws UIMAException;

}
