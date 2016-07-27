package snip.androidapp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by ranreichman on 7/27/16.
 */
public class CollectDataFromInternet extends AsyncTask<String, Void, String>
{
    protected String doInBackground(String... urls)
    {
        return getDataFromWebsite(urls[0]);
    }

    public static String getDataFromWebsite(String websiteURL)
    {
        HttpURLConnection urlConnection = null;

        try
        {
            URL url = new URL(websiteURL);
            urlConnection = (HttpURLConnection)url.openConnection();

            final int READ_TIMEOUT_VALUE_IN_MILLISECONDS = 5000;
            final int CONNECT_TIMEOUT_VALUE_IN_MILLISECONDS = 10000;

            urlConnection.setReadTimeout(READ_TIMEOUT_VALUE_IN_MILLISECONDS);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT_VALUE_IN_MILLISECONDS);
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            urlConnection.setRequestProperty("Authorization", "Token ce53a666b61b6ea2a1950ead117bba3fa27b0f62");
            /*OutputStream outputStream = urlConnection.getOutputStream();
            PrintStream printStream = new PrintStream(outputStream);
            printStream.print("-H 'Authorization: Token ce53a666b61b6ea2a1950ead117bba3fa27b0f62");
            printStream.close();
            outputStream.close();*/

            int responseCode = urlConnection.getResponseCode();

            if (HttpURLConnection.HTTP_OK != responseCode)
            {
                Log.d("Response Code Error: ", Integer.toString(responseCode));
            }
            else
            {
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                return "a";

                // TODO:: get the token from the site
                // TODO:: get string from the stream
                // TODO:: pass the result to some place outside the thread
                // TODO:: make the string a JSON
                // TODO:: turn the JSON into a list of SnipBox
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

        return "";
    }

    public static JSONArray convertJsonStringToJsonDataStructure(String jsonData)
    {
        JSONArray jsonArray = new JSONArray();
        return jsonArray;
    }

    public static LinkedList<ExpandedSnipBox> convertJsonToSnipList(JSONArray jsonArray)
    {
        LinkedList<ExpandedSnipBox> snipBoxes = new LinkedList<ExpandedSnipBox>();
        return snipBoxes;
    }

    public static LinkedList<ExpandedSnipBox> collectSnipsFromBackend()
    {
        String snipRestDomain = "https://test.snip.today/api/snip/";
        String jsonData = getDataFromWebsite(snipRestDomain);
        JSONArray jsonArray = convertJsonStringToJsonDataStructure(jsonData);
        return convertJsonToSnipList(jsonArray);
    }
}
