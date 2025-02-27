package Interfaces.Extensions.File;

import Interfaces.Tagesordnungspunkt;
import org.apache.uima.jcas.JCas;
import org.bson.Document;

import java.util.ArrayList;

/**
 * Implementation of the Tagesordnungspunkt interface to represent a Tagesordnungspunkt in the Bundestags Plenarprotokoll
 * @author Christian Bluemel
 */
public class Tagesordnungspunkt_File_Impl implements Tagesordnungspunkt{

    private String ID;
    private String title;
    private ArrayList<String> reden = new ArrayList<String>();
    private ArrayList<String> kommentare = new ArrayList<String>();

    /**
     * Empty Constructor.
     */
    public Tagesordnungspunkt_File_Impl(){

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
        return this.reden.toArray(new String[this.reden.size()]);
    }

    @Override
    public String[] getKommentare() {
        return this.kommentare.toArray(new String[this.kommentare.size()]);
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
    public void addKommentar(String ID) {
        this.kommentare.add(ID);
    }

    @Override
    public Document toDocument() {
        Document outputDoc = new Document();

        outputDoc.append("_id", this.ID);

        outputDoc.append("title", this.title);

        outputDoc.append("reden", this.reden);
        outputDoc.append("kommentare", this.kommentare);

        return outputDoc;
    }

}
