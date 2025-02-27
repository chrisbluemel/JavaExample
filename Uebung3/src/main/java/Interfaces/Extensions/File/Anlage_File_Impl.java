package Interfaces.Extensions.File;

import Interfaces.Anlage;
import org.bson.Document;

import java.util.ArrayList;

/**
 * Implementation of the Anlage interface to represent an Anlage in the Bundestags Plenarprotokoll
 * @author Christian Bluemel
 */
public class Anlage_File_Impl implements Anlage {

    private String ID;
    private String title;
    private ArrayList<String> reden = new ArrayList<String>();

    /**
     * Empty Constructor.
     */
    public Anlage_File_Impl(){

    }

    @Override
    public String getID() {
        return this.ID;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String[] getReden() {
        // Move the speech ID from the ArrayList into a String[]
        return this.reden.toArray(new String[this.reden.size()]);
    }

    @Override
    public void setID(String ID) {
        this.ID = ID;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void addRede(String ID) {
        this.reden.add(ID);
    }

    @Override
    public Document toDocument() {
        Document outputDoc = new Document();

        outputDoc.append("_id", this.ID);
        outputDoc.append("title", this.title);
        outputDoc.append("reden", this.reden);

        return outputDoc;
    }
}
