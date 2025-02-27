package Interfaces.Extensions.MongoDB;

import Interfaces.Extensions.File.Kommentar_File_Impl;
import org.bson.Document;

import java.util.ArrayList;

import static java.util.Objects.isNull;

/**
 * Extension of the Kommentar_File_Impl class that represents a comment in the Bundestags plenary protocols
 */
public class Kommentar_MongoDB_Impl extends Kommentar_File_Impl {

    /**
     * Special constructor that can take a bson Document class object from a mongoDB that has keys for
     * id, inhalt, redners, factions
     * @param mongoDBDoc  bson Document class object from a mongoDB that has keys for
     *                    id, inhalt, redners, factions
     */
    public Kommentar_MongoDB_Impl(Document mongoDBDoc){
        super();

        if (!isNull(mongoDBDoc.getString("_id"))){
            super.setID(mongoDBDoc.getString("_id"));
        }

        if (!isNull(mongoDBDoc.getString("inhalt"))){
            super.setInhalt(mongoDBDoc.getString("inhalt"));
        }

        if (!isNull(mongoDBDoc.get("redners"))){
            ArrayList<String> rednerList = (ArrayList<String>) mongoDBDoc.get("redners");
            for (String rednerID : rednerList){
                super.addRedner(rednerID);
            }
        }

        if (!isNull(mongoDBDoc.get("factions"))){
            ArrayList<String> factionList = (ArrayList<String>) mongoDBDoc.get("factions");
            for (String faction : factionList){
                super.addFaction(faction);
            }
        }
    }

}
