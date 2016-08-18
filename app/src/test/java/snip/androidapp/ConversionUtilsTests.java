package snip.androidapp;

import android.util.Log;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by ranreichman on 8/15/16.
 */
public class ConversionUtilsTests extends TestCase
{
    @Test
    public void testConvertStringToDate()
    {
        Random rand = new Random();
        for (int i = 0; i < 30; i++)
        {
            int day = rand.nextInt(28) + 1;
            int month = rand.nextInt(12) + 1;
            int year = rand.nextInt(30) + 2000;
            int hour = rand.nextInt(6) + 3;
            int minute = rand.nextInt(60);
            int second = rand.nextInt(60);
            String dateStringToConvert =
                    String.format("%04d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, minute, second);

            //Date date = SnipConversionUtils.convertStringToDate("2016-07-03 17:00:00", "yyyy-MM-dd hh:mm:ss");
            Date date = SnipConversionUtils.convertStringToDate(dateStringToConvert, "yyyy-MM-dd hh:mm:ss");
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            Assert.assertEquals("Day compare", day, cal.get(Calendar.DAY_OF_MONTH));
            Assert.assertEquals("Month compare",  month, cal.get(Calendar.MONTH) + 1);
            int year2 = cal.get(Calendar.YEAR);
            Assert.assertEquals("Year compare", year, cal.get(Calendar.YEAR));
            // This 3 and 2 is GMT stuff
            boolean isHourOK =
                    ((hour == cal.get(Calendar.HOUR_OF_DAY) - 3) || (hour == cal.get(Calendar.HOUR_OF_DAY) - 2));
            Assert.assertTrue("Hour compare", isHourOK);
            Assert.assertEquals("Minute compare", minute, cal.get(Calendar.MINUTE));
            Assert.assertEquals("Second compare", second, cal.get(Calendar.SECOND));
        }
    }
}
