package ChrisTime;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * My very own very restricitve class to reprsent Dates. It's main use comes from simultanously having the LocalDate
 * representation, but also an 8 digit integer representation of a date. Can do literally no operation, but is useful
 * in the mongoDB.
 *
 * @author Christian Bluemel
 */
public class ChrisDate {

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private int value;
    private LocalDate date;

    /**
     * Constructs from an LocalDate object a ChrisDate object.
     * @param date integer that has 8 digits.
     */
    public ChrisDate(LocalDate date){
        this.date = date;
        this.value = date.getYear() * 10000;
        this.value += date.getMonthValue() * 100;
        this.value += date.getDayOfMonth();
    }

    /**
     * Constructs from an 8 digit integer a ChrisDate object.
     * @param value integer that has 8 digits.
     */
    public ChrisDate(int value){
        this.value = value;
        int year = value / 10000;
        int month = (value - year*10000) / 100;
        int day = (value - (year*10000) - (month*100));

        String monthStr = month + "";
        String dayStr = day + "";

        if (monthStr.length() == 1) {
            monthStr = "0" + monthStr;
        }

        if (dayStr.length() == 1) {
            dayStr = "0" + dayStr;
        }
        this.date = LocalDate.parse(dayStr + "." + monthStr + "." + year, formatter);
    }

    /**
     * Puts the ChrisDate object out as a String representation in the format dd.MM.yyyy
     * @return String represntation of the ChrisDate object.
     */
    public String toString(){
        return date.format(formatter);
    }

    /**
     * Retrieves the 8 digit integer representation of the ChrisDate Object.
     * @return integer consisting of 8 digits that represents the ChrisDate object.
     */
    public int getValue(){
        return this.value;
    }
}
