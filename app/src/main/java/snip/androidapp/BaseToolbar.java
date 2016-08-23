package snip.androidapp;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by ranihorev on 03/08/2016.
 */
public class BaseToolbar
{
    private void openSearchResultFragment(final AppCompatActivity currentActivity, int param)
    {
        FragmentTransaction fragmentTransaction = currentActivity.getSupportFragmentManager().beginTransaction();
        SearchResultFragment searchResultFragment = new SearchResultFragment();

        Bundle b = new Bundle();
        b.putInt("param", param);
        searchResultFragment.setArguments(b);

        fragmentTransaction.replace(R.id.fragmentPlaceholder, searchResultFragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        updateToolbarAccordingToFragment(currentActivity, param);
    }

    public void setupToolbar(final AppCompatActivity currentActivity)
    {
        android.support.v7.widget.Toolbar myToolbar =
                (android.support.v7.widget.Toolbar) currentActivity.findViewById(R.id.toolbar);

        if (myToolbar != null)
        {
            currentActivity.setSupportActionBar(myToolbar);
            currentActivity.getSupportActionBar().setDisplayUseLogoEnabled(true);
            currentActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            //getSupportActionBar().setHomeButtonEnabled(true);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void restartImageViews(final AppCompatActivity currentActivity)
    {
        ImageView curImage = (ImageView) currentActivity.findViewById(R.id.likeButtonOnToolbar);
        curImage.setImageResource(R.drawable.heart_icon_hollow);
        curImage = (ImageView) currentActivity.findViewById(R.id.snoozeButtonOnToolbar);
        curImage.setImageResource(R.drawable.snooze_black);
    }

    public void updateToolbarAccordingToFragment(
            final AppCompatActivity currentActivity, final int currentFragmentCode)
    {
        restartImageViews(currentActivity);

        if (currentActivity.getResources().getInteger(R.integer.fragmentCodeLiked) == currentFragmentCode)
        {
            ImageView curImage = (ImageView) currentActivity.findViewById(R.id.likeButtonOnToolbar);
            curImage.setImageResource(R.drawable.heart_icon_full);
        }
        else if (currentActivity.getResources().getInteger(R.integer.fragmentCodeSnoozed) == currentFragmentCode)
        {
            ImageView curImage = (ImageView) currentActivity.findViewById(R.id.snoozeButtonOnToolbar);
            curImage.setImageResource(R.drawable.snooze_blue);
        }

        currentActivity.findViewById(R.id.snoozeButtonOnToolbar).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        if (view.getResources().getInteger(R.integer.fragmentCodeSnoozed) != currentFragmentCode)
                        {
                            openSearchResultFragment(
                                    currentActivity,
                                    view.getResources().getInteger(R.integer.fragmentCodeSnoozed));
                        }
                    }
                }
        );

        currentActivity.findViewById(R.id.likeButtonOnToolbar).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (view.getResources().getInteger(R.integer.fragmentCodeLiked) != currentFragmentCode)
                        {
                            openSearchResultFragment(
                                    currentActivity,
                                    view.getResources().getInteger(R.integer.fragmentCodeLiked));
                        }
                    }
                }
        );
    }
}
