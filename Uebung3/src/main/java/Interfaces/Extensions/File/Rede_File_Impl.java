package Interfaces.Extensions.File;

import Interfaces.Rede;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.bson.Document;

import java.util.ArrayList;

/**
 * Implementation of the Rede interface to represent a speech in the Bundestags Plenarprotokoll
 * @author Christian Bluemel
 */
public class Rede_File_Impl implements Rede {
    private String ID = "";
    private String inhalt = "";
    private String redner = "";
    private String faction = "";
    private String party = "";
    private String sessionID = "";
    private String containedInID = "";
    private int wordCount;
    private ArrayList<String> kommentare = new ArrayList<String>();
    private double score;


    /**
     * Empty Constructor.
     */
    public Rede_File_Impl(){

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
    public void setRedner(String ID) {
        this.redner = ID;
    }

    @Override
    public void setFaction(String faction){
        this.faction = faction;
    }

    @Override
    public void setParty(String party){
        this.party = party;
    }

    @Override
    public void setWordCount(int count){
        this.wordCount = count;
    }

    @Override
    public void addKommentar(String ID) {
        this.kommentare.add(ID);
    }

    @Override
    public void setSessionID(String ID) {
        this.sessionID = ID;
    }

    @Override
    public void setContainedInID(String ID) {
        this.containedInID = ID;
    }

    @Override
    public String getID() {
        return this.ID;
    }

    @Override
    public String getFaction(){
        return this.faction;
    }

    @Override
    public String getParty(){
        return this.party;
    }

    @Override
    public String getInhalt() {
        return this.inhalt;
    }

    @Override
    public String getRedner() {
        return this.redner;
    }

    @Override
    public String[] getKommentare() {
        return this.kommentare.toArray(new String[this.kommentare.size()]);
    }

    @Override
    public String getSessionID() {
        return this.sessionID;
    }

    @Override
    public String getContainedInID() {
        return this.containedInID;
    }

    @Override
    public int getWordCount(){
        return this.wordCount;
    }

    @Override
    public String toString(){
        StringBuilder output = new StringBuilder("");
        output.append("Sitzung ");
        output.append(this.sessionID);
        output.append("\t");
        if(this.containedInID.contains("TOP")){
            output.append("Tagesordnungspunkt ");
            output.append(this.containedInID.split("-")[1]);
        } else if (this.containedInID.contains("ANL")) {
            output.append("Anlage ");
            output.append(this.containedInID.split("-")[1]);
        } else {
            output.append("Not connected to any addendum or topic.");
        }
        output.append("\t");
        output.append("Speech ID:");
        output.append(this.ID);
        output.append("\t");
        output.append("Speaker ID:");
        output.append(this.redner);
        return output.toString();
    }

    @Override
    public Document toDocument() {
        Document outputDoc = new Document();

        outputDoc.append("_id", this.ID);
        outputDoc.append("redner", this.redner);
        outputDoc.append("inhalt", this.inhalt);
        outputDoc.append("kommentare", this.kommentare);
        outputDoc.append("faction", this.faction);
        outputDoc.append("party", this.party);
        outputDoc.append("wordCount", this.wordCount);
        outputDoc.append("sessionID", this.sessionID);
        outputDoc.append("containedInID", this.containedInID);

        return outputDoc;
    }

    /*
    /**
     * Turns a one line speech into a readable multiline String.
     * @param text A single line speech.
     * @return String that is the same speech but spread over several lines, seperated at every 18th word.
     */
    /*
    protected String formatSpeech(String text){
        StringBuilder output = new StringBuilder();
        char[] charText = text.toCharArray();
        int spaceCounter = 0;
        for (char c : charText){
            if (c == ' '){
                spaceCounter++;
            } else if (c == '\n') {
                spaceCounter = 0;
            }

            if (spaceCounter == 18){
                output.append("\n");
                spaceCounter = 0;
            } else {
                output.append(c);
            }
        }
        return output.toString();
    }
    */

    @Override
    public void setScore(Double text) {
        this.score = text;
    }

    @Override
    public Double getScore(){
        return this.score;
    }

    @Override
    public JCas toCAS() throws UIMAException {
        JCas jCas = JCasFactory.createJCas();
        jCas.setDocumentText(this.inhalt);
        jCas.setDocumentLanguage("de");
        return jCas;
    }
}
