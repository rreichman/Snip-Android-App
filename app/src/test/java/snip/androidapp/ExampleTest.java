package snip.androidapp;

import android.test.AndroidTestCase;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by ranreichman on 8/14/16.
 */
public class ExampleTest
{
    private int genericFunction(int num)
    {
        return num;
    }

    @Test
    public void testGenericFunction()
    {
        int num = 1;
        assertEquals(num, genericFunction(num));
        assertNotEquals(num + 1, genericFunction(num));
    }
}
