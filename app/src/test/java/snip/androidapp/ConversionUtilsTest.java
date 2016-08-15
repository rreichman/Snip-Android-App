package snip.androidapp;

import android.icu.util.Calendar;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

/**
 * Created by ranreichman on 8/15/16.
 */
public class ConversionUtilsTest
{
    @BeforeClass
    public static void setupClass()
    {
        throw new RuntimeException("Sorry dude, you won't find any test!");
    }

    @Test
    public void testConvertStringToDate() throws Exception
    {
        Date date = SnipConversionUtils.convertStringToDate("05-05-2016", "MM-DD-YYYY");

        Assert.assertEquals(date.getDay(), 5);
        Assert.assertEquals(date.getMonth(), 5);
        Assert.assertEquals(date.getYear(), 2016);
    }
}
