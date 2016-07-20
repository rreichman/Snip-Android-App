package snip.androidapp;

import android.app.Activity;

import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

// TODO:: In the CardView layout - To create a card with a shadow, use the card_view:cardElevation attribute

/**
 * Created by ranreichman on 7/19/16.
 */
public class MyActivity extends Activity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter<ViewHolder> mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private LinkedList<SnipBox> createRandomSnipDataset(int size)
    {
        LinkedList<SnipBox> myDataset = new LinkedList<SnipBox>();
        for (int i = 0; i < size; ++i)
        {
            String snipTitle = "Title" + i;
            String snipText = "Text" + i;
            String snipAuthor = "Author" + i;
            String snipSource = "Ynet" + i;
            String snipWebsite = "www.ynet" + i + ".co.il";
            HashMap<String,String> links = new HashMap<String,String>();
            links.put(snipSource, snipWebsite);
            SnipBox currentSnipInformation = new SnipBox(
                snipTitle, snipText, snipAuthor, links
            );
            myDataset.addLast(currentSnipInformation);
        }

        return myDataset;
    }

    private LinkedList<SnipBox> getListOfSnips()
    {
        return createRandomSnipDataset(10);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_activity);
        mRecyclerView = (RecyclerView)findViewById(R.id.snip_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        // Currently marked out because i think the size will change
        //mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        mAdapter = new MyAdapter(getListOfSnips());
        mRecyclerView.setAdapter(mAdapter);
    }
}