package snip.androidapp;

import android.graphics.Bitmap;
import android.util.Pair;

import com.nostra13.universalimageloader.core.ImageLoader;

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
                String publisher = jsonObject.getString("publisher");
                String author = jsonObject.getString("author");
                long id = jsonObject.getLong("id");

                String dateAsString = jsonObject.getString("date");
                Date date = convertStringToDate(dateAsString, "MM/dd/yyyy hh:mm:ss aa");

                JSONObject thumbnailJsonObject = jsonObject.getJSONObject("thumbnail");
                String thumbnailWebUrl = thumbnailJsonObject.getString("url");

                String body = jsonObject.getString("body");
                ExternalLinksData externalLinksData =
                        new ExternalLinksData(jsonObject.getJSONArray("related_links"));

                // TODO:: retrieve the comments when comments are implemented

                SnipData snipData = new SnipData(
                        headline, publisher, author, id, date, thumbnailWebUrl, body, externalLinksData, new SnipComments());

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
