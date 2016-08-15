package snip.androidapp;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @BeforeClass
    public static void setupClass()
    {
        throw new RuntimeException("Sorry dude, you won't find any test!");
    }

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_addition_isCorrectAgain() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testConvertStringToDatee() throws Exception
    {
        Date date = SnipConversionUtils.convertStringToDate("05-05-2016", "MM-DD-YYYY");

        Assert.assertEquals(date.getDay(), 5);
        Assert.assertEquals(date.getMonth(), 5);
        Assert.assertEquals(date.getYear(), 2016);
    }
}