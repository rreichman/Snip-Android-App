//package snip.androidapp;
//
//import android.content.Context;
//import com.android.volley.Request;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.LinkedList;
//
//
//import butterknife.BindInt;
//import butterknife.BindString;
//
///**
// * Created by ranreichman on 7/27/16.
// */
//public class CollectDataFromInternet
//{
//
//    LinkedList<SnipData> mSnipsFromBackend = new LinkedList<SnipData>();
//    int mSnipsCollectedSoFar = 0;
//    int mLastSnipsCollection = -1;
//    String mBaseAccessURL;
//    String mTokenField;
//    String mGetSnipsBaseURL;
//
//    public CollectDataFromInternet(String baseAccessURL, String getSnipsBaseURL, String tokenField) {
//        mBaseAccessURL = baseAccessURL;
//        mGetSnipsBaseURL = getSnipsBaseURL;
//        mTokenField = tokenField;
//    }
//
//    public LinkedList<SnipData> retrieveSnipsFromInternet(Context context)
//    {
//        myCollectSnip(context, null);
//        return SnipCollectionInformation.getInstance().getCollectedSnipsAndCleanList();
//    }
//
//    private void handleResponse(JSONObject response, JSONObject params) {
//        try {
//            JSONArray jsonArray = response.getJSONArray("results");
//            mSnipsFromBackend.addAll(SnipConversionUtils.convertJsonArrayToSnipList(jsonArray));
//
//            mLastSnipsCollection = jsonArray.length();
//            mSnipsCollectedSoFar += mLastSnipsCollection;
//
//            String fullNextRequest = response.getString("next");
//            if (fullNextRequest.equals("null")) {
//                SnipCollectionInformation.getInstance().setLastSnipQuery(fullNextRequest);
//                return;
//            }
//
//            String[] splittedFullNextRequest = fullNextRequest.split("/");
//            String nextQueryString = "/" + splittedFullNextRequest[splittedFullNextRequest.length - 1];
//
//            SnipCollectionInformation.getInstance().setLastSnipQuery(nextQueryString);
//
//        } catch (JSONException e){
//            e.printStackTrace();
//        }
//    }
//
//    private String getSnipsQuery() {
//        final String baseQuery = "?im_width=600&im_height=600";
//        String lastRequestURL = SnipCollectionInformation.getInstance().getLastSnipQuery();
//        String fullRequestURL = mBaseAccessURL + mGetSnipsBaseURL;
//        if (lastRequestURL.isEmpty()) {
//            fullRequestURL += baseQuery;
//        }
//        else {
//            fullRequestURL += lastRequestURL;
//        }
//        return fullRequestURL;
//    }
//
//    private void myCollectSnip(Context context, JSONObject tokenParams) {
//        int numSnipsToCollect = context.getResources().getInteger(R.integer.numSnipsPerLoading);
//        JSONObject loginJsonParams = new JSONObject();
//        try {
//            loginJsonParams.put("Authorization", "Token ce53a666b61b6ea2a1950ead117bba3fa27b0f62");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        ServerRequests signInReq = new ServerRequests(mBaseAccessURL);
//
//        ServerRequests.responseFuncInterface reqSuccessFun = new ServerRequests.responseFuncInterface() {
//            @Override
//            public void apply(JSONObject response, JSONObject params) {
//                handleResponse(response, params);
//            }
//        };
//        ServerRequests.responseFuncInterface reqFailedFun = new ServerRequests.responseFuncInterface() {
//            @Override
//            public void apply(JSONObject response, JSONObject params) {
//                String test = "a";
//            }
//        };
//        while ((mLastSnipsCollection != 0) && (mSnipsCollectedSoFar <= numSnipsToCollect)) {
//            String queryURL = getSnipsQuery();
//            signInReq.sendRequest(context, Request.Method.GET, queryURL, loginJsonParams, reqSuccessFun, reqFailedFun);
//        }
//        SnipCollectionInformation.getInstance().setCollectedSnips(mSnipsFromBackend);
//
//    }
//}