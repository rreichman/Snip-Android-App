package snip.androidapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

/**
 * Created by ranreichman on 7/27/16.
 */
public class CollectDataFromInternet
{
    public static LinkedList<SnipData> retrieveSnipsFromInternet()
    {
        try {
            AsyncInternetAccessor accessor = new AsyncInternetAccessor();
            accessor.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return SnipCollectionInformation.getInstance().getCollectedSnipsAndCleanList();
    }

    private static String authenticateAndGetSnipData()
    {
        final String baseAccessURL = "https://test.snip.today/api";
        final String getTokenURL = "/rest-auth/login";
        final String getDataURL = "/snip";
        final String baseQuery = "/?im_width=600&im_height=600";
        final String userEmail = "ran.reichman@gmail.com";
        final String userPassword = "Qwerty123";
        final String emailPasswordString = "email=" + userEmail + "&password=" + userPassword;

        String lastRequestURL = SnipCollectionInformation.getInstance().getLastSnipQuery();
        String fullRequestURL = baseAccessURL + getDataURL;
        if (lastRequestURL != "") {
            fullRequestURL += lastRequestURL;
        }
        else {
            fullRequestURL += baseQuery;
        }


        //String unparsedAuthenticationToken = getDataFromWebsite(
        //        baseAccessURL + getTokenURL, "POST", true, "Authorization", "Basic + " + emailPasswordString);

        // TODO:: parse authorization token
        //final String authorizationToken = unparsedAuthenticationToken;
        final String authorizationToken = "";
        return getDataFromWebsite(
                fullRequestURL, "GET", true, "Authorization: Token", authorizationToken);
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

            /*if (useRequestProperty)
            {
                urlConnection.setRequestProperty(requestPropertyKey, requestPropertyValue);
            }*/
            urlConnection.setRequestProperty("Authorization", "Token ce53a666b61b6ea2a1950ead117bba3fa27b0f62");

            int responseCode = urlConnection.getResponseCode();

            if (HttpURLConnection.HTTP_OK != responseCode)
            {
                Log.d("Response Code Error: ", Integer.toString(responseCode));
                InputStream errorStream = urlConnection.getErrorStream();
                returnString = SnipConversionUtils.convertInputStreamToString(errorStream);
                Log.d("error is: ", returnString);
            }
            else
            {
                InputStream inputStream = urlConnection.getInputStream();
                returnString = SnipConversionUtils.convertInputStreamToString(inputStream);

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

    public static LinkedList<SnipData> collectSnipsFromBackend(int amountToCollect)
    {
        String jsonDataAsString = "";
        LinkedList<SnipData> snipsFromBackend = new LinkedList<SnipData>();
        int snipsCollectedSoFar = 0;

        while (snipsCollectedSoFar < amountToCollect)
        {
            try
            {
                CollectDataFromInternet dataCollector = new CollectDataFromInternet();
                jsonDataAsString = authenticateAndGetSnipData();
                JSONObject jsonObject = new JSONObject(jsonDataAsString);

                JSONArray jsonArray = jsonObject.getJSONArray("results");
                snipsFromBackend.addAll(SnipConversionUtils.convertJsonArrayToSnipList(jsonArray));

                String fullNextRequest = jsonObject.getString("next");
                if (fullNextRequest.equals("null"))
                {
                    SnipCollectionInformation.getInstance().setLastSnipQuery(fullNextRequest);
                    break;
                }

                String[] splittedFullNextRequest = fullNextRequest.split("/");
                String nextQueryString = "/" + splittedFullNextRequest[splittedFullNextRequest.length - 1];

                SnipCollectionInformation.getInstance().setLastSnipQuery(nextQueryString);

                if (0 != jsonArray.length())
                {
                    snipsCollectedSoFar += jsonArray.length();
                }
                else
                {
                    break;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return snipsFromBackend;
    }
}