package snip.androidapp;

import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created by ranreichman on 8/3/16.
 */
public class SnipConversionUtils
{
    public static String convertInputStreamToString(InputStream inputStream)
    {
        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private static Date convertStringToDate(String dateString, String dateFormatString)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        Date date = new Date();

        try
        {
            date = dateFormat.parse(dateString);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return date;
    }

    public static LinkedList<SnipData> convertJsonArrayToSnipList(JSONArray jsonArray)
    {
        LinkedList<SnipData> snipDataLinkedList = new LinkedList<SnipData>();

        try
        {
            for (int i = 0; i < jsonArray.length(); ++i)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String headline = jsonObject.getString("headline");

                // TODO:: get this from server
                String publisher = "Snip";

                // TODO:: get this from server
                String author = "Guest gal";

                long id = Long.parseLong(jsonObject.getString("id"));
                String dateAsString = jsonObject.getString("date");
                Date date = convertStringToDate(dateAsString, "MM/dd/yyyy hh:mm:ss aa");

                // TODO:: get the image from the path when it's the correct path
                String imageLinkPath = jsonObject.getString("image");
                SerializableBitmap image = new SerializableBitmap();

                String body = jsonObject.getString("body");
                LinkedList<Pair<String,String>> links = new LinkedList<Pair<String, String>>();

                // TODO:: retrieve the comments when comments are implemented

                JSONArray linksJSONArray = jsonObject.getJSONArray("related_links");
                for (int j = 0; j < linksJSONArray.length(); ++j)
                {
                    // TODO:: we need to receive the original article's author here too
                    // This is because the format of the link is "website: websiteURL" (Google: www.google.com)
                    String linkString = linksJSONArray.getString(j);
                    String[] splitLinkString = linkString.split(": ");
                    // TODO:: this is data validation. Should change when source changes
                    if (splitLinkString.length > 1)
                    {
                        links.addLast(new Pair<String, String>(splitLinkString[0], splitLinkString[1]));
                    }
                }

                SnipData snipData = new SnipData(
                        headline, publisher, author, id, date, image, body, links, new SnipComments());

                snipDataLinkedList.addLast(snipData);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return snipDataLinkedList;
    }
}
