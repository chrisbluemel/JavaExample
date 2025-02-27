package Interfaces.Extensions.MongoDB;

import Interfaces.Extensions.File.Redner_File_Impl;
import org.bson.Document;

import java.util.ArrayList;

import static java.util.Objects.isNull;

/**
 * Extension of the Redner_File_Impl class that represents a speaker in the Bundestags plenary protocols
 */
public class Redner_MongoDB_Impl extends Redner_File_Impl {

    /**
     * Special constructor that can take a bson Document class object from a mongoDB that has keys for
     * id, title, vorname, nachname, party, faction, role. Can handle null values for any of these, though.
     * @param mongoDBDoc  bson Document class object from a mongoDB that has keys for
     *                    id, title, vorname, nachname, party, faction, role. Can handle null values for any of these, though.
     */
    public Redner_MongoDB_Impl(Document mongoDBDoc){
        super();

        if (!isNull(mongoDBDoc.getString("_id"))){
            super.setID(mongoDBDoc.getString("_id"));
        }

        if (!isNull(mongoDBDoc.getString("title"))){
            super.setTitle(mongoDBDoc.getString("title"));
        }

        if (!isNull(mongoDBDoc.getString("vorname"))){
            super.setVorname(mongoDBDoc.getString("vorname"));
        }

        if (!isNull(mongoDBDoc.getString("nachname"))){
            super.setNachname(mongoDBDoc.getString("nachname"));
        }

        if (!isNull(mongoDBDoc.getString("party"))){
            super.setParty(mongoDBDoc.getString("party"));
        }

        if (!isNull(mongoDBDoc.getString("faction"))){
            super.setFaction(mongoDBDoc.getString("faction"));
        }

        if (!isNull(mongoDBDoc.getString("role"))){
            super.setRole(mongoDBDoc.getString("role"));
        }

        if (!isNull(mongoDBDoc.get("kommentare"))){
            ArrayList<String> kommentarList = (ArrayList<String>) mongoDBDoc.get("kommentare");
            for (String kommentarID : kommentarList) {
                super.addKommentar(kommentarID);
            }
        }

        if (!isNull(mongoDBDoc.get("reden"))){
            ArrayList<String> redeList = (ArrayList<String>) mongoDBDoc.get("reden");
            for (String redenID : redeList){
                super.addRede(redenID);
            }
        }

        if (!isNull(mongoDBDoc.getString("fixed"))){
            if (mongoDBDoc.getString("fixed").equals("1")){
                super.setFixed();
            }
        }
    }
}
