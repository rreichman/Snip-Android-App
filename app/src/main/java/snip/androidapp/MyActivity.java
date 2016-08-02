package snip.androidapp;

//import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.os.Bundle;

import android.os.Environment;
import android.os.Parcel;
import android.util.Log;
import android.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.ListIterator;
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
    // TODO:: what do i do with this when i load more snips and they aren't here? populate the list?
    // TODO:: Currently doesn't seem to be a bug. think why not! is it auto-populated?
    private LinkedList<SnipData> mCollectedSnips;

    // Referenced this: http://stackoverflow.com/questions/26422948/how-to-do-swipe-to-delete-cardview-in-android-using-support-library
    private SwipeableRecyclerViewTouchListener getDefaultTouchListener() {
        return new SwipeableRecyclerViewTouchListener(mRecyclerView,
                new SwipeableRecyclerViewTouchListener.SwipeListener() {
                    @Override
                    public boolean canSwipeRight(int position) {
                        return true;
                    }

                    @Override
                    public boolean canSwipeLeft(int position) {
                        return true;
                    }

                    @Override
                    public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            // TODO:: mark as unlike
                            mCollectedSnips.remove(position);
                            mAdapter.notifyItemRemoved(position);
                        }
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            // TODO:: mark as snooze
                            mCollectedSnips.remove(position);
                            mAdapter.notifyItemRemoved(position);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    public static LinkedList<SnipData> createRandomSnipDataset(int size, int numberToStartCountingAt) {
        LinkedList<SnipData> myDataset = new LinkedList<SnipData>();
        for (int i = 0; i < size; ++i) {
            int printedNumber = i + numberToStartCountingAt;
            String snipHeadline = "Headline" + printedNumber;
            String snipPublisher = "Snip" + printedNumber;
            String snipAuthor = "Author" + printedNumber;
            String snipSource = "Ynet" + printedNumber;

            String snipBody = "Body" + printedNumber;
            String snipWebsite = "www.ynet" + printedNumber + ".co.il";
            LinkedList<Pair<String, String>> links = new LinkedList<Pair<String, String>>();
            links.addLast(new Pair<String, String>(snipSource, snipWebsite));
            SerializableBitmap fakePicture = new SerializableBitmap();
            Date fakeDate = new Date();
            // TODO:: obviously important to change this later to real snip ID.
            long snipID = i + 1;
            SnipData currentSnipInformation =
                    new SnipData(snipHeadline, snipPublisher, snipAuthor, snipID, fakeDate, fakePicture,
                            snipBody, links, new SnipComments());

            myDataset.addLast(currentSnipInformation);
        }

        return myDataset;
    }

    private LinkedList<SnipData> getListOfSnips() {
        return createRandomSnipDataset(15, 1);
    }

    private LinkedList<SnipData> retrieveSnipsFromInternet() {
        try {
            AsyncInternetAccessor accessor = new AsyncInternetAccessor();
            accessor.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        return SnipCollectionInformation.getInstance().getCollectedSnipsAndCleanList();
    }

    private void startActivityOperation() {
        setContentView(R.layout.my_activity);
        mRecyclerView = (RecyclerView) findViewById(R.id.snip_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // TODO:: is there a scenario where it's not null but empty and i still want to retrieve?
        if (null == mCollectedSnips) {
            mCollectedSnips = retrieveSnipsFromInternet();
        }
        // specify an adapter
        mAdapter = new MyAdapter(MyActivity.this, mRecyclerView, mCollectedSnips);
        mRecyclerView.setAdapter(mAdapter);

        SwipeableRecyclerViewTouchListener swipeTouchListener = getDefaultTouchListener();

        mRecyclerView.addOnItemTouchListener(swipeTouchListener);

        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
        });
    }

    private String getFileNameForSnipData()
    {
        return "savedSnipData.dat";
    }

    private String getFileNameForSnipQuery()
    {
        return "savedSnipQuery.dat";
    }

    private String getFullPathOfFile(String filename) {
        return getFilesDir() + "/" + filename;
    }

    @Override
    public void onStop() {
        saveSnipDataToFile();
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private String getSizeString() {
        return "size";
    }

    private String getQueryString() {
        return "queryString";
    }

    @Override
    public void onSaveInstanceState(Bundle outBundle) {
        saveSnipDataToBundle(outBundle);
        super.onSaveInstanceState(outBundle);
    }

    private void saveSnipDataToBundle(Bundle outBundle) {
        // TODO:: this could possibly be optimized
        ListIterator<SnipData> listIterator = mCollectedSnips.listIterator();
        outBundle.putInt(getSizeString(), mCollectedSnips.size());
        int i = 0;
        while (listIterator.hasNext()) {
            SnipData snipData = listIterator.next();
            outBundle.putParcelable(Integer.toString(i), snipData);
            i++;
        }

        outBundle.putString(getQueryString(), SnipCollectionInformation.getInstance().getLastSnipQuery());
    }

    private void retrieveSnipDataFromBundle(Bundle savedInstanceState) {
        if (null != savedInstanceState) {
            if (!savedInstanceState.isEmpty()) {
                int size = savedInstanceState.getInt(getSizeString());
                mCollectedSnips = new LinkedList<SnipData>();

                for (int i = 0; i < size; ++i) {
                    SnipData currentSnip = (SnipData) savedInstanceState.getParcelable(Integer.toString(i));
                    mCollectedSnips.addLast(currentSnip);
                }

                SnipCollectionInformation.getInstance().setLastSnipQuery(savedInstanceState.getString(getQueryString()));
            }
        }
    }

    private void saveObjectToFile(Object object, String filename)
    {
        try
        {
            File snipDataFile = new File(getFullPathOfFile(filename));
            if (snipDataFile.exists())
            {
                snipDataFile.delete();
            }

            FileOutputStream fileOutputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(mCollectedSnips);
            objectOutputStream.close();
            fileOutputStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void saveSnipDataToFile()
    {
        saveObjectToFile(mCollectedSnips, getFileNameForSnipData());
    }

    private void saveSnipQueryToFile()
    {
        saveObjectToFile(SnipCollectionInformation.getInstance().getLastSnipQuery(), getFileNameForSnipQuery());
    }

    private void saveAppInformationToFile()
    {
        saveSnipDataToFile();
        saveSnipQueryToFile();
    }

    private Object retrieveObjectFromFile(String filename)
    {
        Object retrievedObject = null;
        try
        {
            File dataFile = new File(getFullPathOfFile(filename));
            if (dataFile.exists())
            {
                FileInputStream fileInputStream = openFileInput(filename);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                retrievedObject = objectInputStream.readObject();
                objectInputStream.close();
                fileInputStream.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return retrievedObject;
    }

    private LinkedList<SnipData> retrieveSnipDataFromFile()
    {
        return (LinkedList<SnipData>)retrieveObjectFromFile(getFileNameForSnipData());
    }

    private String retrieveSnipQueryFromFile()
    {
        return (String)retrieveObjectFromFile(getFileNameForSnipQuery());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        // TODO:: The retrieve code is duplicate. fix this.
        retrieveSnipDataFromBundle(savedInstanceState);
        if (null == mCollectedSnips)
        {
            mCollectedSnips = retrieveSnipDataFromFile();
            SnipCollectionInformation.getInstance().setLastSnipQuery(retrieveSnipQueryFromFile());
        }
        startActivityOperation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        retrieveSnipDataFromBundle(savedInstanceState);
        if (null == mCollectedSnips)
        {
            mCollectedSnips = retrieveSnipDataFromFile();
            SnipCollectionInformation.getInstance().setLastSnipQuery(retrieveSnipQueryFromFile());
        }
        startActivityOperation();
    }
}