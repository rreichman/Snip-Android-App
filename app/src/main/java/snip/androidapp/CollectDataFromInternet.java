package snip.androidapp;

import android.graphics.Picture;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created by ranreichman on 7/27/16.
 */
public class CollectDataFromInternet extends AsyncTask<Void, Void, String>
{
    protected String doInBackground(Void... params)
    {
        //return getDataFromWebsite(webParams[0], webParams[1].equals("true"), webParams[2], webParams[3]);
        return authenticateAndGetSnipData();
    }

    private static String convertInputStreamToString(InputStream inputStream)
    {
        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private static String authenticateAndGetSnipData()
    {
        final String baseAccessURL = "https://test.snip.today/api";
        final String getTokenURL = "/rest-auth/login";
        final String getDataURL = "/snip";

        final String userEmail = "ran.reichman@gmail.com";
        final String userPassword = "Qwerty123";
        final String emailPasswordString = "email=" + userEmail + "&password=" + userPassword;

        //String unparsedAuthenticationToken = getDataFromWebsite(
        //        baseAccessURL + getTokenURL, "POST", true, "Authorization", "Basic + " + emailPasswordString);

        // TODO:: parse authorization token
        //final String authorizationToken = unparsedAuthenticationToken;
        final String authorizationToken = "";
        return getDataFromWebsite(
                baseAccessURL + getDataURL, "GET", true, "Authorization: Token", authorizationToken);
    }

    public static String getDataFromWebsite(
            String websiteURL, String webRequestString,
            boolean useRequestProperty, String requestPropertyKey, String requestPropertyValue)
    {
        HttpURLConnection urlConnection = null;
        String returnString = "";

        try
        {
            URL url = new URL(websiteURL);
            urlConnection = (HttpURLConnection)url.openConnection();

            final int READ_TIMEOUT_VALUE_IN_MILLISECONDS = 5000;
            final int CONNECT_TIMEOUT_VALUE_IN_MILLISECONDS = 10000;


            urlConnection.setReadTimeout(READ_TIMEOUT_VALUE_IN_MILLISECONDS);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT_VALUE_IN_MILLISECONDS);
            urlConnection.setRequestMethod(webRequestString.toUpperCase());
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            /*if (useRequestProperty)
            {
                urlConnection.setRequestProperty(requestPropertyKey, requestPropertyValue);
            }*/
            urlConnection.setRequestProperty("Authorization", "Token ce53a666b61b6ea2a1950ead117bba3fa27b0f62");

            int responseCode = urlConnection.getResponseCode();

            if (HttpURLConnection.HTTP_OK != responseCode)
            {
                Log.d("Response Code Error: ", Integer.toString(responseCode));
            }
            else
            {
                InputStream inputStream = urlConnection.getInputStream();
                returnString = convertInputStreamToString(inputStream);


                //JSONObject jsonObject = new JSONObject(jsonString);
                //jsonObject.get("")

                // TODO:: get the token from the site
                // TODO:: get string from the stream
                // TODO:: pass the result to some place outside the thread
                // TODO:: make the string a JSON
                // TODO:: turn the JSON into a list of SnipData
                // TODO:: handle read and connect timeouts
                // TODO:: make some of the code generic (not snip related)
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (null != urlConnection)
            {
                urlConnection.disconnect();
            }
        }

        return returnString;
    }

    public static JSONArray convertJsonStringToJsonArray(String jsonData)
    {
        JSONArray jsonArray = new JSONArray();
        try
        {
            JSONObject jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
            return jsonArray;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return jsonArray;
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
        LinkedList<SnipData> SnipDataes = new LinkedList<SnipData>();

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

                long id = Long.getLong(jsonObject.getString("id"));
                String dateAsString = jsonObject.getString("date");
                Date date = convertStringToDate(dateAsString, "MM/dd/yyyy hh:mm:ss aa");

                // TODO:: get the image from the path when it's the correct path
                String imageLinkPath = jsonObject.getString("image_link");
                Picture image = new Picture();

                String body = jsonObject.getString("body");
                LinkedList<Pair<String,String>> links = new LinkedList<Pair<String, String>>();

                // TODO:: retrieve the comments when comments are implemented

                JSONArray linksJSONArray = jsonObject.getJSONArray("related_links");
                for (int j = 0; j < linksJSONArray.length(); ++j)
                {
                    // TODO:: we need to receive the original articel's author here too
                    // This is because the format of the link is "website: websiteURL" (Google: www.google.com)
                    String linkString = linksJSONArray.getString(j);
                    String[] splitLinkString = linkString.split(": ");
                    links.addLast(new Pair<String, String>(splitLinkString[0], splitLinkString[1]));
                }

                SnipData snipData = new SnipData(
                        headline, publisher, author, id, date, image, body, links, new SnipComments());
            }
        }
        catch (JSONException e)
        {

        }

        return SnipDataes;
    }

    public static LinkedList<SnipData> collectSnipsFromBackend()
    {
        String jsonData = "";
        try
        {
            CollectDataFromInternet dataCollector = new CollectDataFromInternet();
            jsonData = dataCollector.execute().get();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        JSONArray jsonArray = convertJsonStringToJsonArray(jsonData);
        return convertJsonArrayToSnipList(jsonArray);
    }
}
