package Interfaces;

import org.apache.uima.jcas.JCas;
import org.bson.Document;

/**
 * Interfaces that determines which methods must be implemented for an Anlage class object.
 * @author Christian Bluemel
 */
public interface Anlage {

    // GETTER
    /**
     * Retrieves the ID of the Tagesordnungspunkt/Anlage. Consists
     * @return String that is the ID. Format: SitzungID-AnlageNr-ANL or SitzungID-TagesordnungspunktNr-TOP
     */
    String getID();

    /**
     * Retrieves the title of the Tagesordnungspunkt/Anlage
     * @return String of the title of the Tagesordnungspunkt/ANlag
     */
    String getTitle();

    /**
     * Retrieves the IDs of all Reden done during this Anlage or Tagesordnungspunkt.
     * @return String Array of all IDs of all Reden connected to this Anlage or Tagesordnungspunkt.
     */
    String[] getReden();

    // SETTER

    /**
     * Set the ID of the Anlage.
     * @param ID String that is the ID of the Anlage/Tagesordnungspunkt
     */
    void setID(String ID);

    /**
     * Set the Title of the Anlage/Tagesordnungspunkt
     * @param title String that is the title of the Anlage/Tagesordnungspunkt
     */
    void setTitle(String title);

    /**
     * Add a Rede to this Anlage/Tagesordnungspunkt by adding the ID
     * @param ID The ID of the added Rede.
     */
    void addRede(String ID);

    /**
     * Turn this object into a bson Document object prepared for upload.
     * @return bson Document that can be uploaded into a MongoDatabase
     */
    Document toDocument();

}
