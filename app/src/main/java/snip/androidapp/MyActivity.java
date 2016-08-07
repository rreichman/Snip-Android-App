package snip.androidapp;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.support.v4.app.ShareCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.support.v7.widget.helper.ItemTouchHelper;

import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.os.Bundle;
import android.content.res.Configuration;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import butterknife.BindString;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;


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

    @BindString(R.string.baseAccessURL) String baseAccessURL;
    @BindString(R.string.tokenField) String tokenField;
    @BindString(R.string.getSnipsBaseURL) String getSnipsBaseURL;


    @Override
    public void onStop()
    {
        DataCacheManagement.saveAppInformationToFile(this, mCollectedSnips);
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

    @Override
    public void onSaveInstanceState(Bundle outBundle)
    {
        DataCacheManagement.saveSnipDataToBundle(outBundle, mCollectedSnips);
        super.onSaveInstanceState(outBundle);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        ButterKnife.bind(this);
        initalizeImportantStuff();

//        DataCacheManagement.retrieveObjectFromFile(this, )
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);

        try
        {
            mCollectedSnips = DataCacheManagement.retrieveSavedDataFromBundleOrFile(this, savedInstanceState);
            startActivityOperation();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    // TODO decide if need to change the images size on rotation
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void startUI() {
        setContentView(R.layout.my_activity);
        BaseToolbar activityToolbar = new BaseToolbar();
        activityToolbar.setupToolbar(this);
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

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder)
            {
                getDefaultUIUtil().clearView(((ViewHolder) viewHolder).mForeground);
            }

            @Override
            public void onChildDraw(
                    Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                    float dX, float dY, int actionState, boolean isCurrentlyActive)
            {
                final View foregroundView = ((ViewHolder)viewHolder).mForeground;
                drawBackground(viewHolder, dX, actionState);
                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
            }

            private void drawBackground(RecyclerView.ViewHolder viewHolder, float dX, int actionState)
            {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE)
                {
                    Display display = getWindowManager().getDefaultDisplay();
                    Point screenSize = new Point();
                    display.getSize(screenSize);

                    final View backgroundViewRight = ((ViewHolder) viewHolder).mSwipeBackgroundRight;
                    final View backgroundViewLeft = ((ViewHolder) viewHolder).mSwipeBackgroundLeft;

                    // Swiping Right
                    if (dX > 0)
                    {
                        Log.d("right is", Integer.toString((int)Math.max(dX, 0)));
                        backgroundViewRight.setRight((int) Math.max(dX, 0));
                        backgroundViewRight.setLeft(0);

                        backgroundViewLeft.setRight(0);
                        backgroundViewLeft.setLeft(0);
                    }
                    // Swiping Left
                    else
                    {
                        final View backgroundView = ((ViewHolder) viewHolder).mSwipeBackgroundLeft;
                        // screenSize.x is the width of the screen
                        Log.d("left is", Integer.toString((int)(screenSize.x + dX)));
                        backgroundView.setRight(screenSize.x);
                        backgroundView.setLeft((int)(screenSize.x + dX));

                        backgroundViewRight.setRight(0);
                        backgroundViewRight.setLeft(0);
                    }
                }
            }
        };
    }

    private void addPicturesToSnips()
    {
        for (int i = 0; i < mCollectedSnips.size(); i++)
        {
            if (null == mCollectedSnips.get(i).mThumbnail)
            {
                mCollectedSnips.get(i).mThumbnail =
                        SnipData.getBitmapFromUrl(mCollectedSnips.get(i).mThumbnailUrl);
            }
        }
    }

    private void actualCollectDataAndPopulateActivity()
    {
        // TODO:: is there a scenario where it's not null but empty and i still want to retrieve?
        if (null == mCollectedSnips)
        {
            CollectSnipsFromInternet snipCollector = new CollectSnipsFromInternet(baseAccessURL, getSnipsBaseURL, "");
            mCollectedSnips = snipCollector.retrieveSnipsFromInternet(this);
        }
        else
        {
            addPicturesToSnips();
        }
        // specify an adapter
        mAdapter = new MyAdapter(mRecyclerView, mCollectedSnips);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback swipeTouchHelperCallback = getSwipeTouchHelperCallback();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {});
    }

    public void responseFunctionImplementation(JSONObject response, JSONObject params)
    {
        Log.d("response", "function");
    }

    public void errorFunctionImplementation(VolleyError error, JSONObject params)
    {
        Log.d("error", "function");
    }

    private void collectDataAndPopulateActivity()
    {
        //actualCollectDataAndPopulateActivity();
        String url = "https://test.snip.today/api/snip";
        //String url = "https://test.snip.today/api/rest-auth/login/";

        JSONObject loginJsonParams = new JSONObject();
        /*try
        {
            loginJsonParams.put("email", "ran.reichman@gmail.com");
            loginJsonParams.put("password", "Qwerty123");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }*/

        //int requestMethod = Request.Method.POST;
        int requestMethod = Request.Method.GET;

        HashMap<String,String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Token ce53a666b61b6ea2a1950ead117bba3fa27b0f62");

        VolleyInternetOperator.responseFunctionInterface responseFunction =
                new VolleyInternetOperator.responseFunctionInterface() {
            @Override
            public void apply(JSONObject response, JSONObject params)
            {
                responseFunctionImplementation(response, params);
            }
        };
        VolleyInternetOperator.errorFunctionInterface errorFunction =
                new VolleyInternetOperator.errorFunctionInterface() {
            @Override
            public void apply(VolleyError error, JSONObject params)
            {
                errorFunctionImplementation(error, params);
            }
        };

        VolleyInternetOperator.accessWebsiteWithVolley(
                getApplicationContext(), url, requestMethod, loginJsonParams, headers,
                responseFunction, errorFunction);
    }

    public void startActivityOperation()
    {
        startUI();
        mRecyclerView = (RecyclerView) this.findViewById(R.id.snip_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        mSwipeContainer = (SwipeRefreshLayout)this.findViewById(R.id.swipeContainer);

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

    public void initalizeImportantStuff()
    {
        Fabric.with(this, new Crashlytics());
        // TODO:: make these do something real
        logUserIntoCrashlytics();
        LogUserActions.logContentView("Tweet", "Video", "1234");

        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        // TODO:: think if you want the caches to depend on the device..
        ImageLoaderConfiguration imageLoaderConfiguration =
                new ImageLoaderConfiguration.Builder(this)
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

    public void deleteAppCacheAndStartOver()
    {
        mCollectedSnips = null;
        SnipCollectionInformation.getInstance().cleanLastSnipQuery();
        DataCacheManagement.deleteAppInformationFiles(this);
        startActivityOperation();
    }
}