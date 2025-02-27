package Interfaces.Extensions.File;

import ChrisTime.ChrisDate;
import Interfaces.Sitzung;
import org.bson.Document;

import java.time.LocalDate;
import java.util.ArrayList;


/**
 * Implementation of the Sitzung interface to represent a session in the Bundestags Plenarprotokoll
 * @author Christian Bluemel
 */
public class Sitzung_File_Impl implements Sitzung {
    private String ID;
    private ChrisDate date;
    private String begin;
    private String end;
    private int duration;
    private String legislaturperiode;
    private String title;
    private ArrayList<String> sitzungsleiter = new ArrayList<String>();
    private ArrayList<String> tagesordnungspunkte = new ArrayList<String>();
    private ArrayList<String> anlagen = new ArrayList<String>();


    /**
     * Empty Constructor.
     */
    public Sitzung_File_Impl(){

    }

    @Override
    public String getID() {
        return this.ID;
    }

    @Override
    public String getDate() {
        return this.date.toString();
    }

    @Override
    public String getBegin() {
        return this.begin;
    }

    @Override
    public String getEnd() {
        return this.end;
    }

    @Override
    public int getDuration() {
        return this.duration;
    }

    @Override
    public String getLegislaturperiode() {
        return this.legislaturperiode;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String[] getSitzungsleiter() {
        return this.sitzungsleiter.toArray(new String[this.sitzungsleiter.size()]);
    }

    @Override
    public String[] getTagesordnungspunkte() {
        return this.tagesordnungspunkte.toArray(new String[this.tagesordnungspunkte.size()]);
    }

    @Override
    public String[] getAnlagen() {
        return this.anlagen.toArray(new String[this.anlagen.size()]);
    }

    @Override
    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * Alternative set method that takes a ChrisDate object directly.
     * @param date ChrisDate object that contains a date.
     */
    public void setDate(ChrisDate date) {
        this.date = date;
    }

    @Override
    public void setDate(LocalDate date) {
        this.date = new ChrisDate(date);
    }

    @Override
    public void setBegin(String time) {
        this.begin = time;
    }

    @Override
    public void setEnd(String time) {
        this.end = time;
    }

    @Override
    public void setLegislaturperiode(String WP) {
        this.legislaturperiode = WP;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public void addSitzungsleiter(String ID) {
        this.sitzungsleiter.add(ID);
    }

    @Override
    public void addTagesordnungspunkt(String ID) {
        this.tagesordnungspunkte.add(ID);
    }

    @Override
    public void addAnlage(String ID) {
        this.anlagen.add(ID);
    }

    @Override
    public int compareTo(Object o) {
        return Integer.parseInt(this.toString()) - Integer.parseInt(o.toString());
    }

    @Override
    public String toString(){
        return this.getDuration() + "";
    }

    @Override
    public String toString(boolean flag){
        String output = "Sitzung " +
                this.getID() +
                " " +
                this.getDate();
        return output;
    }

    // Methods
    @Override
    public int calcDuration(){
        int output;
        String beginString = this.begin;
        String endString = this.end;
        // One time is known that contains x as a symbol instead of numbers. Set the time to an easily recognizable value.
        if (beginString.contains("x") || endString.contains("x")) {
            output = 10000;
        } else {
            if (beginString.contains("Uhr")) {
                beginString = beginString.split(" ")[0];
            }
            if (endString.contains("Uhr")) {
                endString = endString.split(" ")[0];
            }
            int startTime = this.parseTime(beginString);
            int endTime = this.parseTime(endString);
            if (endTime < startTime) {
                output = endTime + 1440 - startTime;
            } else {
                output = endTime - startTime;
            }
        }
        return output;
    }

    /**
     * Parses a single String representing a time. Can parse the following formats: h.mm, hh.mm, hh:mm, h:mm.
     * @param timeString A string representing a time of day either in format: h.mm, hh.mm, hh:mm or h:mm.
     * @return int that represents which minute of the day the daytime String represents.
     */
    private int parseTime(String timeString){
        String hourString;
        String minuteString;
        if (timeString.contains(".") && timeString.length() == 5) { // For hh.mm
            hourString = timeString.substring(0, 2);
            minuteString = timeString.substring(3);
        } else if (timeString.contains(":")) { // For h:mm and hh:mm
            String[] timeList = timeString.split(":");
            hourString = timeList[0];
            minuteString = timeList[1];
        } else if (timeString.contains(".") && timeString.length() == 4) { // For h.mm
            hourString = timeString.substring(0, 1);
            minuteString = timeString.substring(2);
        } else { // Never happens.
            System.out.println(timeString);
            hourString = "0";
            minuteString = "0";
        }
        int hour = Integer.parseInt(hourString);
        int minutes = Integer.parseInt(minuteString);
        int time = (hour * 60) + minutes;
        return time;
    }

    @Override
    public Document toDocument() {
        Document outputDoc = new Document();

        outputDoc.append("_id", this.ID);

        outputDoc.append("title", this.title);

        outputDoc.append("legislaturperiode", this.legislaturperiode);

        outputDoc.append("date", date.getValue()); // Using integer represntation for dates to have an easy time comparing.
        outputDoc.append("begin", this.begin);
        outputDoc.append("end", this.end);

        outputDoc.append("duration", this.duration);

        outputDoc.append("anlagen", this.anlagen);
        outputDoc.append("tagesordnungspunkte", this.tagesordnungspunkte);
        outputDoc.append("sitzungsleiter", this.sitzungsleiter);

        return outputDoc;
    }
}
