package snip.androidapp;

import java.net.InetAddress;

/**
 * Created by ranreichman on 8/31/16.
 */
public class InternetUtils
{
    public static boolean isInternetAvailable()
    {
        try
        {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //TODO: You can replace it with your name
            InetAddress ipAddr2 = InetAddress.getByName("twitter.com");
            return (!ipAddr.equals("") && !ipAddr2.equals(""));
        }
        catch (Exception e) {
            return false;
        }
    }
}
