package snip.androidapp;

/**
 * Created by ranreichman on 8/23/16.
 */
public class MainFragment extends SnipHoldingFragment
{
    public String getBaseSnipsQueryForFragment()
    {
        String lastRequestURL =
                SnipCollectionInformation.getInstance(getActivity()).getLastSnipQueryForFragment(getFragmentCode());
        String baseAccessUrl = getResources().getString(R.string.baseAccessURL);
        String snipsBaseUrl = getResources().getString(R.string.snipsBaseURL);
        String newSnipsBaseUrl = getResources().getString(R.string.newSnipsBaseURL);
        String fullRequestURL = baseAccessUrl + snipsBaseUrl + newSnipsBaseUrl;
        /*if (lastRequestURL.isEmpty() || lastRequestURL.equals("null")) {
            fullRequestURL += SnipCollectionInformation.getInstance().getDimensionsQuery();
        }
        else {
            fullRequestURL += lastRequestURL;
        }*/
        return fullRequestURL;
    }

    public int getFragmentCode()
    {
        return getResources().getInteger(R.integer.fragmentCodeMain);
    }
}