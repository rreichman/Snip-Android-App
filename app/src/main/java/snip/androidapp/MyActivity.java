package snip.androidapp;

//import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.graphics.Picture;
import android.os.Bundle;

import android.util.Log;
import android.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

// TODO:: In the CardView layout - To create a card with a shadow, use the card_view:cardElevation attribute
// TODO:: add Fabric to app

/**
 * Created by ranreichman on 7/19/16.
 */
public class MyActivity extends AppCompatActivity
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
        //Debug.startMethodTracing("trace");
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Log.d("Starting MyActivity", "");

        mRecyclerView = (RecyclerView)findViewById(R.id.snip_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Log.d("Set Layout Manager", "");
        Log.d("Accessing site", "");

        try
        {
            AsyncInternetAccessor accessor = new AsyncInternetAccessor();
            accessor.execute().get();
            Log.d("Finished accessing site", "");
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        Log.d("Now working w adapter", "");

        // specify an adapter
        mAdapter = new MyAdapter(MyActivity.this, SnipCollectionInformation.getInstance().getCollectedSnipsAndCleanList());
        mRecyclerView.setAdapter(mAdapter);

        Log.d("Set adapter works", "");

        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {});

        //Debug.stopMethodTracing();
    }
}