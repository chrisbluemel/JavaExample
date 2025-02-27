package Interfaces.Extensions.MongoDB;


import Interfaces.Extensions.File.Anlage_File_Impl;
import org.bson.Document;

import java.util.ArrayList;

import static java.util.Objects.isNull;

/**
 * Extension of the Anlage_File_Impl class that represents an addendum in the Bundestags plenary protocols
 */
public class Anlage_MongoDB_Impl extends Anlage_File_Impl {

    /**
     * Special constructor that can take a bson Document class object from a mongoDB that has keys for
     * id, title, reden. Can handle null values for any of these, though.
     * @param mongoDBDoc  bson Document class object from a mongoDB that has keys for
     *                    id, title, reden. Can handle null values for any of these, though.
     */
    public Anlage_MongoDB_Impl(Document mongoDBDoc){
        super();
        if (!isNull(mongoDBDoc.getString("_id"))){
            super.setID(mongoDBDoc.getString("_id"));
        } else {
            super.setID("");
        }

        if (!isNull(mongoDBDoc.getString("title"))){
            super.setTitle(mongoDBDoc.getString("title"));
        } else {
            super.setTitle("");
        }


        if (!isNull(mongoDBDoc.get("reden"))){
            ArrayList<String> redenList = (ArrayList<String>) mongoDBDoc.get("reden");
            for (String redeID : redenList){
                super.addRede(redeID);
            }
        }
    }
}
