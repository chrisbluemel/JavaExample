package Interfaces.Extensions.MongoDB;

import Interfaces.Extensions.File.Tagesordnungspunkt_File_Impl;
import org.bson.Document;

import java.util.ArrayList;

import static java.util.Objects.isNull;

/**
 * Extension of the Tagesordnungspunkt_File_Impl class that represents a topic in the Bundestags plenary protocols
 */
public class Tagesordnungspunkt_MongoDB_Impl extends Tagesordnungspunkt_File_Impl {

    /**
     * Special constructor that can take a bson Document class object from a mongoDB that has keys for
     * id, title, reden, kommentare. Can handle null values for any of these, though.
     * @param mongoDBDoc  bson Document class object from a mongoDB that has keys for
     *                    id, title, reden, kommentare. Can handle null values for any of these, though.
     */
    public Tagesordnungspunkt_MongoDB_Impl(Document mongoDBDoc){
        super();


        if (!isNull(mongoDBDoc.getString("_id"))){
            super.setID(mongoDBDoc.getString("_id"));
        }

        if (!isNull(mongoDBDoc.getString("title"))){
            super.setTitle(mongoDBDoc.getString("title"));
        }

        if (!isNull(mongoDBDoc.get("reden"))){
            ArrayList<String> redenList = (ArrayList<String>) mongoDBDoc.get("reden");
            for (String redeID : redenList){
                super.addRede(redeID);
            }
        }

        if (!isNull(mongoDBDoc.get("kommentare"))){
            ArrayList<String> kommentareList = (ArrayList<String>) mongoDBDoc.get("kommentare");
            for (String kommentareID : kommentareList){
                super.addKommentar(kommentareID);
            }
        }
    }
}
