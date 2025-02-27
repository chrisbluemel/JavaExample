package ProtocolInterface;

import Interfaces.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * Interface that demands a set of Getters for a protocol assembly.
 * @author Christian Bluemel
 */
public interface ProtocolAssembly {


    /**
     * Returns the names of parties of this protocol assembly.
     * @return LinkedHashSet of Strings that are the names of the parties within this protocol assembly.
     */
    LinkedHashSet<String> getParties();

    /**
     * Returns the names of factions of this protocol assembly.
     * @return LinkedHashSet of Strings that are the names of the factions within this protocol assembly.
     */
    LinkedHashSet<String> getFactions();

    /**
     * Returns the Redner class objects of this protocol assembly.
     * @return ArrayList of Redner class objects.
     */
    ArrayList<Redner> getRedners();

    /**
     * Returns the Reden class objects of this protocol assembly.
     * @return ArrayList of Reden class objects.
     */
    ArrayList<Rede> getReden();

    /**
     * Returns the Sitzung class objects of this protocol assembly.
     * @return ArrayList of Sitzung class objects.
     */
    ArrayList<Sitzung> getSitzungen();

    /**
     * Returns the Tagesordnungspunkt class objects of this protocol assembly.
     * @return ArrayList of Tagesordnungspunkt class objects.
     */
    ArrayList<Tagesordnungspunkt> getTagesordnungspunkte();

    /**
     * Returns the Anlage class objects of this protocol assembly.
     * @return ArrayList of Anlage class objects.
     */
    ArrayList<Anlage> getAnlagen();

    /**
     * Returns the Kommentar class objects of this protocol assembly.
     * @return ArrayList of Kommentar class objects.
     */
    ArrayList<Kommentar> getKommentare();

    /**
     * Returns the IDs of all session leaders represented in this protocol assembly.
     * @return Array of String that is the IDs of the session leaders.
     */
    String[] getSitzungsleiterIDs();

}
