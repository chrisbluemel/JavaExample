package Interfaces.Extensions.File;

import Interfaces.Redner;
import org.bson.Document;

import java.util.ArrayList;

/**
 * Implementation of the Redner interface to represent a speaker in the Bundestags Plenarprotokoll
 * @author Christian Bluemel
 */
public class Redner_File_Impl implements Redner {
    private String ID;
    private String vorname;
    private String nachname;
    private String title;
    private String faction = "";
    private String party = "";
    private ArrayList<String> kommentare = new ArrayList<String>();
    private ArrayList<String> reden = new ArrayList<String>();
    private String role;
    private int speechCount = 0;

    private boolean isFixed = false;


    /**
     * Empty constructor.
     */
    public Redner_File_Impl(){

    }

    @Override
    public void setID(String ID) {
        if (!this.isFixed){
            this.ID = ID;
        }
    }

    @Override
    public void setVorname(String vorname) {
        if (!this.isFixed){
            this.vorname = vorname;
        }
    }

    @Override
    public void setNachname(String nachname){
        if (!this.isFixed){
            this.nachname = nachname;
        }
    }

    @Override
    public void setTitle(String title) {
        if (!this.isFixed){
            this.title = title;
        }
    }

    @Override
    public void setFaction(String faction) {
        this.faction = faction;
    }

    @Override
    public void setFixed(){
        this.isFixed = true;
    }

    @Override
    public void setParty(String party) {
        this.party = party;
    }

    @Override
    public void addKommentar(String ID) {
        this.kommentare.add(ID);
    }

    @Override
    public void addRede(String ID) {
        this.speechCount++;
        this.reden.add(ID);
    }

    @Override
    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean isFixed(){
        return this.isFixed;
    }

    @Override
    public String getVorname() {
        return this.vorname;
    }

    @Override
    public int getSpeechCount(){
        return this.speechCount;
    }

    @Override
    public String getNachname(){
        return this.nachname;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getFaction() {
        return this.faction;
    }

    @Override
    public String getParty() {
        return this.party;
    }

    @Override
    public String[] getKommentare() {
        return this.kommentare.toArray(new String[this.kommentare.size()]);
    }

    @Override
    public String[] getReden() {
        return this.reden.toArray(new String[this.reden.size()]);
    }

    @Override
    public String getRole() {
        return this.role;
    }

    @Override
    public String getID() {
        return this.ID;
    }

    @Override
    public int compareTo(Object o) {
        return this.toString().compareTo(o.toString());
    }

    @Override
    public String toString(){
        StringBuilder output = new StringBuilder("");
        output.append(this.nachname);
        output.append(", ");
        output.append(this.vorname);
        output.append(" (");
        if (this.faction.equals("LINKE")){
            output.append("DIE LINKE.");
        } else {
            output.append(this.faction);
        }
        output.append(") (");
        if (this.isFixed) {
            if (this.party.equals("LINKE")){
                output.append("DIE LINKE.");
            } else {
                output.append(this.party);
            }
        } else {
            output.append("X");
        }
        output.append(")");
        return output.toString();
    }

    @Override
    public Document toDocument() {
        Document outputDoc = new Document();

        outputDoc.append("_id", this.ID);

        outputDoc.append("title", this.title);
        outputDoc.append("vorname", this.vorname);
        outputDoc.append("nachname", this.nachname);

        outputDoc.append("party", this.party);
        outputDoc.append("faction", this.faction);
        outputDoc.append("role", this.role);

        outputDoc.append("speechCount", this.speechCount);

        if (this.isFixed){
            outputDoc.append("fixed", "1");
        } else {
            outputDoc.append("fixed", "0");
        }

        outputDoc.append("reden", this.reden);
        outputDoc.append("kommentare", this.kommentare);

        return outputDoc;
    }
}
