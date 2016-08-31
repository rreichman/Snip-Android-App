package snip.androidapp;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ranreichman on 8/31/16.
 */
public class TimeUtils
{
    public static String getDateDiff(Date startDate, Date endDate)
    {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long weeksInMilli = daysInMilli * 7;

        long elapsedWeeks = different / weeksInMilli;
        if (elapsedWeeks > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, ''yy"); // Set your date format
            return sdf.format(startDate);
        }

        long elapsedDays = different / daysInMilli;
        if (elapsedDays > 0) {
            return Long.toString(elapsedDays) + " day" + isTimePlural(elapsedDays);
        }

        long elapsedHours = different / hoursInMilli;
        if (elapsedHours > 0) {
            String prefix = "hr";
            return Long.toString(elapsedHours) + " hr" + isTimePlural(elapsedHours);
        }

        long elapsedMinutes = different / minutesInMilli;
        return Long.toString(elapsedMinutes) + " min" + isTimePlural(elapsedMinutes);
    }

    private static String isTimePlural(long elapsedTime)
    {
        if (elapsedTime > 1) {
            return "s";
        }
        return "";
    }
}
