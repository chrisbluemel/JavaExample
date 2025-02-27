package Interfaces.Extensions.MongoDB;

import ChrisTime.ChrisDate;
import Interfaces.Extensions.File.Sitzung_File_Impl;
import org.bson.Document;

import java.util.ArrayList;

import static java.util.Objects.isNull;

/**
 * Extension of the Sitzung_File_Impl class that represents a session in the Bundestags plenary protocols
 */
public class Sitzung_MongoDB_Impl extends Sitzung_File_Impl {


    /**
     * Special constructor that can take a bson Document class object from a mongoDB that has keys for
     * id, title, legislaturperiode, begin, end, date, anlagen, sitzungsleiter and tagesordnungspunkte.
     * Can handle null values for any of these, though.
     * @param mongoDBDoc  bson Document class object from a mongoDB that has keys for
     *                    id, title, legislaturperiode, begin, end, date, anlagen,
     *                    sitzungsleiter and tagesordnungspunkte.
     *                    Can handle null values for any of these, though.
     */
    public Sitzung_MongoDB_Impl(Document mongoDBDoc){
        super();

        if (!isNull(mongoDBDoc.getString("_id"))){
            super.setID(mongoDBDoc.getString("_id"));
        }

        if (!isNull(mongoDBDoc.getString("title"))){
            super.setTitle(mongoDBDoc.getString("title"));
        }

        if (!isNull(mongoDBDoc.getString("legislaturperiode"))){
            super.setLegislaturperiode(mongoDBDoc.getString("legislaturperiode"));
        }

        if (!isNull(mongoDBDoc.getString("begin"))){
            super.setBegin(mongoDBDoc.getString("begin"));
        }

        if (!isNull(mongoDBDoc.getString("end"))){
            super.setEnd(mongoDBDoc.getString("end"));
        }



        if (!isNull(mongoDBDoc.getInteger("date"))){
            super.setDate(new ChrisDate(mongoDBDoc.getInteger("date")));
        }

        if (!isNull(mongoDBDoc.getInteger("duration"))){
            super.setDuration(mongoDBDoc.getInteger("duration"));
        }

        if (!isNull(mongoDBDoc.get("anlagen"))){
            ArrayList<String> anlagenList = (ArrayList<String>) mongoDBDoc.get("anlagen");
            for (String anlageID : anlagenList) super.addAnlage(anlageID);
        }

        if (!isNull(mongoDBDoc.get("sitzungsleiter"))){
            ArrayList<String> sitzungsleiterList = (ArrayList<String>) mongoDBDoc.get("sitzungsleiter");
            for (String sitzungsleiterID : sitzungsleiterList) super.addSitzungsleiter(sitzungsleiterID);
        }

        if (!isNull(mongoDBDoc.get("tagesordnungspunkte"))){
            ArrayList<String> tagesordnungspunkteList = (ArrayList<String>) mongoDBDoc.get("tagesordnungspunkte");
            for (String tagesordnungspunktID : tagesordnungspunkteList)
                super.addTagesordnungspunkt(tagesordnungspunktID);
        }
    }
}
