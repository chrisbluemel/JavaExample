package Interfaces.Extensions.MongoDB;

import Interfaces.Extensions.File.Rede_File_Impl;
import org.bson.Document;

import java.util.ArrayList;

import static java.util.Objects.isNull;

/**
 * Extension of the Rede_File_Impl class that represents a speech in the Bundestags plenary protocols
 */
public class Rede_MongoDB_Impl extends Rede_File_Impl {

    /**
     * Special constructor that can take a bson Document class object from a mongoDB that has keys for
     * id, redner, text and kommentare. Can handle null values for any of these, though.
     * @param mongoDBDoc  bson Document class object from a mongoDB that has keys for
     *                    id, redner, text and kommentare. Can handle null values for any of these, though.
     */
    public Rede_MongoDB_Impl(Document mongoDBDoc){
        super();

        if (!isNull(mongoDBDoc.getString("_id"))){
            super.setID(mongoDBDoc.getString("_id"));
        }

        if (!isNull(mongoDBDoc.getString("redner"))){
            super.setRedner(mongoDBDoc.getString("redner"));
        }

        if (!isNull(mongoDBDoc.getString("inhalt"))){
            super.setInhalt(mongoDBDoc.getString("inhalt"));
        }

        if (!isNull(mongoDBDoc.getDouble("score"))){
            super.setScore(mongoDBDoc.getDouble("score"));
        }

        if (!isNull(mongoDBDoc.getString("faction"))){
            super.setFaction(mongoDBDoc.getString("faction"));
        }

        if (!isNull(mongoDBDoc.getString("party"))){
            super.setFaction(mongoDBDoc.getString("party"));
        }

        if (!isNull(mongoDBDoc.getInteger("wordCount"))){
            super.setWordCount(mongoDBDoc.getInteger("wordCount"));
        }

        if (!isNull(mongoDBDoc.getString("sessionID"))){
            super.setSessionID(mongoDBDoc.getString("sessionID"));
        }

        if (!isNull(mongoDBDoc.getString("containedInID"))){
            super.setContainedInID(mongoDBDoc.getString("containedInID"));
        }

        if (!isNull(mongoDBDoc.get("kommentare"))){
            ArrayList<String> kommentarList = (ArrayList<String>) mongoDBDoc.get("kommentare");
            for (String kommentarID : kommentarList){
                super.addKommentar(kommentarID);
            }
        }
    }

    @Override
    public String toString(){
        StringBuilder output = new StringBuilder("");
        output.append("Sitzung ");
        output.append(super.getSessionID());
        output.append("\t");
        if(super.getContainedInID().contains("TOP")){
            output.append("Tagesordnungspunkt ");
            output.append(super.getContainedInID().split("-")[1]);
        } else if (super.getContainedInID().contains("ANL")) {
            output.append("Anlage ");
            output.append(super.getContainedInID().split("-")[1]);
        } else {
            output.append("Not connected to any addendum or topic.");
        }
        output.append("\t");
        output.append("Speech ID: ");
        output.append(super.getID());
        output.append("\t");
        output.append("Speaker ID: ");
        output.append(super.getRedner());
        output.append(" | Metascore: ");
        output.append(super.getScore());
        output.append("\n");
        return output.toString();
    }

}
