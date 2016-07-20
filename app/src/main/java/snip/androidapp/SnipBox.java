package snip.androidapp;

import android.media.Image;

import java.util.HashMap;

/**
 * This holds the data for the content in the app
 *
 * Created by ranreichman on 7/19/16.
 */
public class SnipBox
{
    public String mSnipTitle;
    public Image mSnipThumbnail;
    public String mSnipText;
    public String mSnipAuthor;
    //public String mSnipComments;
    public HashMap<String,String> mExternalLinks;

    public SnipBox() {}

    public SnipBox(String snipTitle, String snipText, String snipAuthor, HashMap<String,String> externalLinks)
    {
        mSnipTitle = snipTitle;
        mSnipText = snipText;
        mSnipAuthor = snipAuthor;
        mExternalLinks = new HashMap<String,String>(externalLinks);
    }
}
