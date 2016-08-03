package snip.androidapp;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.content.Context;
import android.os.Bundle;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.ExecutionException;

// TODO:: In the CardView layout - To create a card with a shadow, use the card_view:cardElevation attribute
// TODO:: add Fabric to app
// TODO:: this file looks terrible. divide it to two or three files.

/**
 * Created by ranreichman on 7/19/16.
 */
public class MyActivity extends AppCompatActivity
{
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter<ViewHolder> mAdapter;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeContainer;
    // TODO:: what do i do with this when i load more snips and they aren't here? populate the list?
    // TODO:: Currently doesn't seem to be a bug. think why not! is it auto-populated?
    private LinkedList<SnipData> mCollectedSnips;

    private LinkedList<SnipData> retrieveSnipsFromInternet() {
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

    private void collectDataAndPopulateActivity()
    {
        // TODO:: is there a scenario where it's not null but empty and i still want to retrieve?
        if (null == mCollectedSnips)
        {
            mCollectedSnips = retrieveSnipsFromInternet();
        }
        // specify an adapter
        mAdapter = new MyAdapter(MyActivity.this, mRecyclerView, mCollectedSnips);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
                {
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDirection)
                    {
                        int currentPositionInDataset = viewHolder.getAdapterPosition();
                        if (ItemTouchHelper.LEFT == swipeDirection)
                        {
                            // TODO:: mark as unlike
                            mCollectedSnips.remove(currentPositionInDataset);
                            mAdapter.notifyItemRemoved(currentPositionInDataset);
                        }

                        if (ItemTouchHelper.RIGHT == swipeDirection)
                        {
                            // TODO:: mark as like
                            mCollectedSnips.remove(currentPositionInDataset);
                            mAdapter.notifyItemRemoved(currentPositionInDataset);
                        }

                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public boolean onMove(
                            RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder1, RecyclerView.ViewHolder viewHolder2)
                    {
                        return false;
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {});
    }

    private void startActivityOperation()
    {
        startUI();
        mRecyclerView = (RecyclerView) findViewById(R.id.snip_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        mSwipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                cleanAppCacheAndStartOver();
                collectDataAndPopulateActivity();
                Log.d("Refreshed!", "So refreshing!");
            }
        });

        collectDataAndPopulateActivity();
    }

    private String getFileNameForSnipData() { return "savedSnipData.dat"; }

    private String getFileNameForSnipQuery()
    {
        return "savedSnipQuery.dat";
    }

    private String getFullPathForSnipData()
    {
        return getFullPathOfFile(getFileNameForSnipData());
    }

    private String getFullPathForSnipQuery()
    {
        return getFullPathOfFile(getFileNameForSnipQuery());
    }

    private String getFullPathOfFile(String filename) {
        return getFilesDir() + "/" + filename;
    }

    @Override
    public void onStop() {
        saveAppInformationToFile();
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

            objectOutputStream.writeObject(object);
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

    private void deleteFileOnDisk(String fullPathOfFile)
    {
        File file = new File(fullPathOfFile);
        if (file.exists())
        {
            file.delete();
        }
    }

    private void cleanAppCacheAndStartOver()
    {
        mCollectedSnips = null;
        SnipCollectionInformation.getInstance().cleanLastSnipQuery();
        deleteAppInformationFiles();
        startActivityOperation();
    }

    private void deleteAppInformationFiles()
    {
        deleteFileOnDisk(getFullPathForSnipData());
        deleteFileOnDisk(getFullPathForSnipQuery());
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

    private void retrieveSavedDataFromBundleOrFile(Bundle savedInstanceState)
    {
        retrieveSnipDataFromBundle(savedInstanceState);
        if (null == mCollectedSnips)
        {
            mCollectedSnips = retrieveSnipDataFromFile();
            SnipCollectionInformation.getInstance().setLastSnipQuery(retrieveSnipQueryFromFile());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        retrieveSavedDataFromBundleOrFile(savedInstanceState);
        startActivityOperation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        deleteAppInformationFiles();
        try
        {
            retrieveSavedDataFromBundleOrFile(savedInstanceState);
            startActivityOperation();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        mAdapter.resetAdapter();
//         Checks the orientation of the screen
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            SquareImageView imgView = (SquareImageView) findViewById(R.id.thumbnail);
//            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) imgView.getLayoutParams();
//            lp.weight = 0.1f;
//        }
    }



    private void startUI() {
        setContentView(R.layout.my_activity);
        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (myToolbar != null) {
            setSupportActionBar(myToolbar);
            getSupportActionBar().setLogo(R.drawable.sniplogosmall);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
//            getSupportActionBar().setHomeButtonEnabled(true);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
}