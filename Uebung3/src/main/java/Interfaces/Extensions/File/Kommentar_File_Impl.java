package Interfaces.Extensions.File;

import Interfaces.Kommentar;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.bson.Document;

import java.util.ArrayList;

/**
 * Implementation of the Kommentar interface to represent a comment in the Bundestags Plenarprotokoll
 * @author Christian Bluemel
 */
public class Kommentar_File_Impl implements Kommentar {

    private String ID;
    private String inhalt;
    private ArrayList<String> redner = new ArrayList<String>();
    private ArrayList<String> factions = new ArrayList<String>();

    /**
     * Empty Constructor.
     */
    public Kommentar_File_Impl(){

    }

    @Override
    public void setID(String ID) {
        this.ID = ID;
    }

    @Override
    public void setInhalt(String inhalt) {
        this.inhalt = inhalt;
    }

    @Override
    public void addRedner(String ID) {
        this.redner.add(ID);
    }

    @Override
    public void addFaction(String faction) {
        this.factions.add(faction);
    }

    @Override
    public String getID() {
        return this.ID;
    }

    @Override
    public String getInhalt() {
        return this.inhalt;
    }

    @Override
    public String[] getRedner() {
        return this.redner.toArray(new String[this.redner.size()]);
    }

    @Override
    public String[] getFactions() {
        return this.factions.toArray(new String[this.factions.size()]);
    }

    @Override
    public String toString(){
        StringBuilder output = new StringBuilder("");
        for (String faction : factions){
            output.append(faction);
            output.append(", ");
        }
        output.append(this.ID);
        output.append("\n");
        output.append(this.inhalt);
        return output.toString() ;
    }

    @Override
    public Document toDocument() {
        Document outputDoc = new Document();

        outputDoc.append("_id", this.ID);
        outputDoc.append("inhalt", this.inhalt);
        outputDoc.append("redners", this.redner);
        outputDoc.append("factions", this.factions);

        return outputDoc;
    }

    @Override
    public JCas toCAS() throws UIMAException {
        JCas jCas = JCasFactory.createJCas();
        jCas.setDocumentText(this.inhalt);
        jCas.setDocumentLanguage("de");
        return jCas;
    }
}


