package snip.androidapp;

import android.app.Activity;

import android.graphics.Picture;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONArray;

import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

// TODO:: In the CardView layout - To create a card with a shadow, use the card_view:cardElevation attribute
// TODO:: add Fabric to app

/**
 * Created by ranreichman on 7/19/16.
 */
public class MyActivity extends Activity
{
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter<ViewHolder> mAdapter;
    private LinearLayoutManager mLayoutManager;

    public static LinkedList<SnipData> createRandomSnipDataset(int size, int numberToStartCountingAt)
    {
        LinkedList<SnipData> myDataset = new LinkedList<SnipData>();
        for (int i = 0; i < size; ++i)
        {
            int printedNumber = i + numberToStartCountingAt;
            String snipHeadline = "Headline" + printedNumber;
            String snipPublisher = "Snip" + printedNumber;
            String snipAuthor = "Author" + printedNumber;
            String snipSource = "Ynet" + printedNumber;

            String snipBody = "Body" + printedNumber;
            String snipWebsite = "www.ynet" + printedNumber + ".co.il";
            LinkedList<Pair<String,String>> links = new LinkedList<Pair<String,String>>();
            links.addLast(new Pair<String, String>(snipSource, snipWebsite));
            Picture fakePicture = new Picture();
            Date fakeDate = new Date();
            // TODO:: obviously important to change this later to real snip ID.
            long snipID = i + 1;
            SnipData currentSnipInformation =
                    new SnipData(snipHeadline, snipPublisher, snipAuthor, snipID, fakeDate, fakePicture,
                            snipBody,links, new SnipComments());

            myDataset.addLast(currentSnipInformation);
        }

        return myDataset;
    }

    private LinkedList<SnipData> getListOfSnips()
    {
        return createRandomSnipDataset(15, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_activity);

        mRecyclerView = (RecyclerView)findViewById(R.id.snip_recycler_view);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        try
        {
            AsyncInternetAccessor accessor = new AsyncInternetAccessor();
            accessor.execute().get();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }

        // specify an adapter
        mAdapter = new MyAdapter(MyActivity.this, SnipCollectionInformation.getInstance().mSnipsCollectedByNonUIThread);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {});

        // TODO:: move this from here

        /*try
        {
            CollectDataFromInternet dataCollector = new CollectDataFromInternet();
            String resultData = dataCollector.execute().get();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/
        //LinkedList<ExpandedSnipData> snipsData = CollectDataFromInternet.collectSnipsFromBackend();
        //JSONArray jsonArray = CollectDataFromInternet.convertJsonStringToJsonArray("{\"count\":4,\"next\":\"http://test.snip.today/api/snip/?limit=3&offset=3\",\"previous\":null,\"results\":[{\"id\":3,\"headline\":\"Headline of Snip 1\",\"date\":\"2016-07-25T19:43:57.513000Z\",\"image_link\":\"original_images/markCuban.jpg\",\"body\":\"<p>Body stuff</p>\",\"comments\":[],\"related_links\":[\"Google: http://www.google.com\"]},{\"id\":4,\"headline\":\"Headline of snip 2\",\"date\":\"2016-07-27T07:48:01.545000Z\",\"image_link\":\"original_images/thumbnail_theGuardian.png\",\"body\":\"<p>Body of snip 2</p>\",\"comments\":[],\"related_links\":[\"Facebook: http://www.facebook.com\"]},{\"id\":5,\"headline\":\"Headline of snip 3\",\"date\":\"2016-07-27T07:49:14.571000Z\",\"image_link\":\"original_images/thumbnail_ynet.jpg\",\"body\":\"<p>Body of snip 3</p><p><embed alt=\\\"Another image 3\\\" embedtype=\\\"image\\\" format=\\\"left\\\" id=\\\"4\\\"/><br/></p>\",\"comments\":[],\"related_links\":[\"Ynet: http://www.ynet.co.il\"]}]}");
        //LinkedList<SnipData> SnipDataLinkedList = CollectDataFromInternet.convertJsonArrayToSnipList(jsonArray);
    }
}