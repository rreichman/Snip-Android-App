package snip.androidapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.ExecutionException;

import io.fabric.sdk.android.Fabric;

/**
 * Created by ranreichman on 8/3/16.
 */
public class ActivityOperations
{
    private static ActivityOperations mInstance = null;

    private Activity mMainActivity;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter<ViewHolder> mAdapter;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeContainer;
    // TODO:: what do i do with this when i load more snips and they aren't here? populate the list?
    // TODO:: Currently doesn't seem to be a bug. think why not! is it auto-populated?
    private LinkedList<SnipData> mCollectedSnips;

    public ActivityOperations(Activity activity)
    {
        mMainActivity = activity;
    }

    public static synchronized ActivityOperations getInstance(Activity activity)
    {
        if (null == mInstance)
        {
            mInstance = new ActivityOperations(activity);
        }
        return mInstance;
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

        return SnipCollectionInformation.getInstance().getCollectedSnipsAndCleanList();
    }

    private ItemTouchHelper.SimpleCallback getSwipeTouchHelperCallback()
    {
        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
        {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDirection)
            {
                int currentPositionInDataset = viewHolder.getAdapterPosition();
                if (ItemTouchHelper.LEFT == swipeDirection)
                {
                    ReactionManager.userDislikedSnip(mCollectedSnips.get(currentPositionInDataset).mID);
                    mCollectedSnips.remove(currentPositionInDataset);
                    mAdapter.notifyItemRemoved(currentPositionInDataset);
                }

                if (ItemTouchHelper.RIGHT == swipeDirection)
                {
                    ReactionManager.userSnoozedSnip(mCollectedSnips.get(currentPositionInDataset).mID);
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
    }

    private void collectDataAndPopulateActivity()
    {
        // TODO:: is there a scenario where it's not null but empty and i still want to retrieve?
        if (null == mCollectedSnips)
        {
            mCollectedSnips = retrieveSnipsFromInternet();
        }
        // specify an adapter
        mAdapter = new MyAdapter(mRecyclerView, mCollectedSnips);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback swipeTouchHelperCallback = getSwipeTouchHelperCallback();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {});
    }

    public void startActivityOperation()
    {
        startUI();
        mRecyclerView = (RecyclerView) mMainActivity.findViewById(R.id.snip_recycler_view);
        mLayoutManager = new LinearLayoutManager(mMainActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(mMainActivity));
        mSwipeContainer = (SwipeRefreshLayout)mMainActivity.findViewById(R.id.swipeContainer);

        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                deleteAppCacheAndStartOver();
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
        return mMainActivity.getFilesDir() + "/" + filename;
    }

    private String getSizeString() {
        return "size";
    }

    private String getQueryString() {
        return "queryString";
    }

    public void saveSnipDataToBundle(Bundle outBundle) {
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

            FileOutputStream fileOutputStream = mMainActivity.openFileOutput(filename, Context.MODE_PRIVATE);
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

    public void saveAppInformationToFile()
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

    public void initalizeImportantStuff()
    {
        Fabric.with(mMainActivity, new Crashlytics());
        // TODO:: make these do something real
        logUserIntoCrashlytics();
        LogUserActions.logContentView("Tweet", "Video", "1234");

        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        // TODO:: think if you want the caches to depend on the device..
        ImageLoaderConfiguration imageLoaderConfiguration =
                new ImageLoaderConfiguration.Builder(mMainActivity)
                .diskCacheSize(104857600)
                .memoryCacheSize(41943040)
                .threadPoolSize(10)
                .defaultDisplayImageOptions(displayImageOptions)
                .build();

        ImageLoader.getInstance().init(imageLoaderConfiguration);
    }

    private void logUserIntoCrashlytics()
    {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier("12345");
        Crashlytics.setUserEmail("testuser@snip.today");
        Crashlytics.setUserName("Test user");
    }

    public void startUI()
    {
        mMainActivity.setContentView(R.layout.my_activity);
        setupToolbar();
    }

    public void setupToolbar()
    {
        Toolbar myToolbar = (Toolbar) mMainActivity.findViewById(R.id.toolbar);
        if (myToolbar != null) {
            ((AppCompatActivity)mMainActivity).setSupportActionBar(myToolbar);
            ((AppCompatActivity)mMainActivity).getSupportActionBar().
                    setTitle(mMainActivity.getResources().getString(R.string.app_name));
//            getSupportActionBar().setHomeButtonEnabled(true);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
    }

    private void deleteAppCacheAndStartOver()
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
        File dataFile = new File(getFullPathOfFile(filename));
        try
        {
            if (dataFile.exists())
            {
                FileInputStream fileInputStream = mMainActivity.openFileInput(filename);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                retrievedObject = objectInputStream.readObject();
                objectInputStream.close();
                fileInputStream.close();
            }
        }
        catch (IOException e)
        {
            dataFile.delete();
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

    public void retrieveSavedDataFromBundleOrFile(Bundle savedInstanceState)
    {
        retrieveSnipDataFromBundle(savedInstanceState);
        if (null == mCollectedSnips)
        {
            mCollectedSnips = retrieveSnipDataFromFile();
            SnipCollectionInformation.getInstance().setLastSnipQuery(retrieveSnipQueryFromFile());
        }
    }
}
