package Interfaces;

import org.apache.uima.jcas.JCas;
import org.bson.Document;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Interfaces that determines which methods must be implemented for a Sitzung class object.
 * @author Christian Bluemel
 */
public interface Sitzung extends Comparable{

    // GETTER
    /**
     * Retrieve the ID of the Sitzung.
     * @return A String of the ID of the Sitzung.
     */
    String getID();

    /**
     * Retrieve the date in String format.
     * @return The date in an unspecified String format. No guarantees.
     */
    String getDate();

    /**
     * Retrieves the begin time of the Sitzung.
     * @return The time in an unspecified String format. No guarantees.
     */
    String getBegin();

    /**
     * Retrieves the end time of the Sitzung.
     * @return The time in an unspecified String format. No guarantees.
     */
    String getEnd();

    /**
     * The duration of the Sitzung in minutes.
     * @return The integer that represents the amount of minutes the session lasted.
     */
    int getDuration();

    /**
     * Retrieves the Legislaturperiode of the Sitzung.
     * @return String that is the Legislaturperiode.
     */
    String getLegislaturperiode();

    /**
     * Retrieves the title of the Sitzung.
     * @return String that is the title of the Sitzung.
     */
    String getTitle();



    /**
     * Retrieves the IDs of all persons that lead this Sitzung. Can be 0 and up to n many people.
     * The ID refers to a Redner class object.
     * @return String Array that contains the IDs of the Sitzungsleiter.
     */
    String[] getSitzungsleiter();

    /**
     * Retrieves the IDs of all Tagesordnungspunkte. The ID consists of the ID of the Sitzung and then the Nr of the
     * Tagesordnungspunkt, concluded by the three letter code TOP to differentiate them from the Anlage class objects.
     * Format: SitzungID-TagesordnungspunktNr-TOP (example: 5-2-TOP for 5th Sitzung, 2nd Tagesorndungspunkt)
     * @return String that is the ID of the Tagesordnungspunkt.
     */
    String[] getTagesordnungspunkte();

    /**
     * Retrieves the IDs of all Anlage class objects. The ID consists of the ID of the Sitzung and then the Nr of the
     * Anlage, concluded by the three letter code ANL to differentiate them from the Tagesordnungspunkt class objects.
     * Format: SitzungID-AnlageNr-ANL (example: 5-2-ANL for 5th Sitzung, 2nd Anlage)
     * @return String that is the ID of the Anlage.
     */
    String[] getAnlagen();

    // SETTER

    /**
     * Set the ID of the Sitzung.
     * @param ID String of the ID of the Sitzung.
     */
    void setID(String ID);

    /**
     * Set the date
     * @param date Date in unspecified format. Go wild.
     */
    void setDate(LocalDate date);

    /**
     * Set the begin time.
     * @param time Time in unspecified format. Go wild.
     */
    void setBegin(String time);

    /**
     * Set the end time.
     * @param time Time in unspecified format. Go wild.
     */
    void setEnd(String time);

    /**
     * Set the Legislaturperiode.
     * @param ID The ID of the Legislaturperiode.
     */
    void setLegislaturperiode(String ID);

    /**
     * Set the title.
     * @param title The title of the Sitzung.
     */
    void setTitle(String title);

    /**
     * Set the duration of the session.
     * @param duration integer that indicates how many minutes the session lasted.
     */
    void setDuration(int duration);

    /**
     * Add an ID of a Sitzungsleiter.
     * @param ID The ID of a Redner identified as Sitzungsleiter.
     */
    void addSitzungsleiter(String ID);

    /**
     * Add an ID of a Tagesordnungspunkt.
     * @param ID The ID of the Tagesordnungspunkt.
     *           Format: SitzungID-TagesordnungspunktNr-TOP (example: 5-2-TOP for 5th Sitzung, 2nd Tagesorndungspunkt)
     */
    void addTagesordnungspunkt(String ID);

    /**
     * Add an ID of an Anlage.
     * @param ID The ID of the Anlage.
     *           Format: SitzungID-AnlageNr-ANL (example: 5-2-ANL for 5th Sitzung, 2nd Anlage)
     */
    void addAnlage(String ID);

    // Methods

    /**
     * Method that calculates the duration in minutes by parsing the end and the start time.
     * @return integer that is the duration of the session in minutes.
     */
    int calcDuration();

    // Converter

    /**
     * Turn this object into a bson Document object prepared for upload.
     * @return bson Document that can be uploaded into a MongoDatabase
     */
    Document toDocument();

    /**
     * Special toString method that is used for actual conversion to String. The other version without parameters only returns
     * the duration.
     * @param flag boolean that has no influence on the method, but only his there to change the signature.
     * @return String representation of this session containing the session number and the date of the session.
     */
    String toString(boolean flag);

}
