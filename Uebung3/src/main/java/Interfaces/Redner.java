package Interfaces;

import org.apache.uima.jcas.JCas;
import org.bson.Document;

/**
 * Interfaces that determines which methods must be implemented for a Redner class object.
 * @author Christian Bluemel
 */
public interface Redner extends Comparable{

    // SETTER

    /**
     * Set the ID of this Redner.
     * @param ID String that is the ID of this Redner.
     */
    void setID(String ID);

    /**
     * Sets the Vorname of this Redner.
     * @param vorname String that is the Vorname of this Redner.
     */
    void setVorname(String vorname);

    /**
     * Sets the Nachname of this Redner.
     * @param nachname String that is the Nachname of this Redner.
     */
    void setNachname(String nachname);

    /**
     * Sets the title of this Redner.
     * @param title String that is the title of this Redner.
     */
    void setTitle(String title);

    /**
     * Sets the faction of this Redner.
     * @param faction String that is the faction of this Redner.
     */
    void setFaction(String faction);

    /**
     * Sets the party of this Redner.
     * @param party String that is the party of this Redner.
     */
    void setParty(String party);

    /**
     * Add the ID of a Kommentar connected to this Redner.
     * The ID of a Kommentar is the ID of the ID of the Tagesordnungspunkt or Rede it is contained in followed by the Index
     * of the Kommentar in relation to the other comments in the Tagesordnungspunkt or Rede starting at 0.
     * Format: TagesordnungspunktID-Index or RedeID-Index
     * Example1: 5-2-TOP-3 is a comment from the 5th session in the 2nd Tagesordnungspunkt that was the first comment
     * in the Tagesordnungspunkt.
     * Example2: 93872ID-2 is the 3rd comment from Rede 93872ID.
     * @param ID String that is the ID of the Kommentar.
     */
    void addKommentar(String ID);

    /**
     * Adds a Rede ID to the speeches connected to this Redner.
     * @param ID String that is the Rede ID.
     */
    void addRede(String ID);

    /**
     * Sets the role of the Redner.
     * @param role String that is the role of this Redner.
     */
    void setRole(String role);

    /**
     * Switches the isFixed boolean to true, to note that this Redners data has been confirmed by the Stammdatenblatt.
     */
    void setFixed();

    // GETTER

    /**
     * Retrieve the Vorname of this Redner.
     * @return String that is the Vorname.
     */
    String getVorname();

    /**
     * Checks if this Redner was already confirmed using the Stammdatenblatt.
     * @return true, if this Redner has already gotten their data confirmed via the Stammdatenblatt.
     */
    boolean isFixed();

    /**
     * Retrieve the Nachname of this Redner
     * @return String that is the Nachname.
     */
    String getNachname();

    /**
     * Retrieve the title of this Redner.
     * @return String that is the title.
     */
    String getTitle();

    /**
     * Retrieve the faction of the Redner.
     * @return String that is the faction.
     */
    String getFaction();

    /**
     * Retrieve the party of the Redner.
     * @return String that is the Party of the Redner.
     */
    String getParty();

    /**
     * Retrieve the IDs of the comments connected to this Redner.
     * The ID of a Kommentar is the ID of the ID of the Tagesordnungspunkt or Rede it is contained in followed by the Index
     * of the Kommentar in relation to the other comments in the Tagesordnungspunkt or Rede starting at 0.
     * Format: TagesordnungspunktID-Index or RedeID-Index
     * Example1: 5-2-TOP-3 is a comment from the 5th session in the 2nd Tagesordnungspunkt that was the first comment
     * in the Tagesordnungspunkt.
     * Example2: 93872ID-2 is the 3rd comment from Rede 93872ID.
     * @return String Arry of IDs representing IDs of Kommentar class objects.
     */
    String[] getKommentare();

    /**
     * Retrieve the IDs of the speeches connected to this Redner.
     * @return String Array that contains the IDs of all speeches connected to this Redner.
     */
    String[] getReden();

    /**
     * Retrieve the role of this Redner.
     * @return String that is the role of this Redner.
     */
    String getRole();

    /**
     * Retrieve the ID of this Redner.
     * @return String that is the ID of this Redner.
     */
    String getID();

    /**
     * Retrieve the number of speeches this speaker held.
     * @return integer that is the number of speeches this speaker held.
     */
    int getSpeechCount();

    /**
     * Turn this object into a bson Document object prepared for upload.
     * @return bson Document that can be uploaded into a MongoDatabase
     */
    Document toDocument();

}
