package Interfaces;

import org.apache.uima.jcas.JCas;

/**
 * Interfaces that determines which methods must be implemented for a Tagesordnungspunkt class object.
 * @author Christian Bluemel
 */
public interface Tagesordnungspunkt extends Anlage {

    // SETTER
    /**
     * Adds the ID of this Kommentar to this comments of this Tagesordnungspunkt.
     * The ID of a Kommentar is the ID of the ID of the Tagesordnungspunkt it is contained in followed by the Index
     * of the Kommentar in relation to the other comments in the Tagesordnungspunkt starting at 0. Format: TagesordnungspunktID-Index
     * Example 5-2-TOP-3 is a comment from the 5th session in the 2nd Tagesordnungspunkt that was the first comment
     * in the Tagesordnungspunkt.
     * @param ID String that is the ID of the comment.
     */
    void addKommentar(String ID);

    // GETTER

    /**
     * Get the String Array of all Kommentar class objects connected to this Tagesordnungspunkt.
     * The ID of a Kommentar is the ID of the ID of the Tagesordnungspunkt it is contained in followed by the Index
     * of the Kommentar in relation to the other comments in the Tagesordnungspunkt starting at 0. Format: TagesordnungspunktID-Index
     * Example 5-2-TOP-3 is a comment from the 5th session in the 2nd Tagesordnungspunkt that was the first comment
     * in the Tagesordnungspunkt.
     * @return String that is the ID of the comment.
     */
    String[] getKommentare();


}
