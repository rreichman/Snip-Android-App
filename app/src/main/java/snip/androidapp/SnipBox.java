package snip.androidapp;

import android.graphics.Picture;
import android.media.Image;

/**
 * This holds the data for the content in the app
 *
 * Created by ranreichman on 7/19/16.
 */
public class SnipBox
{
    public String mSnipTitle;
    public Picture mSnipThumbnail;
    public String mSnipSource;
    public String mSnipAuthor;

    public SnipBox() {}

    public SnipBox(String snipTitle, String snipSource, String snipAuthor, Picture snipThumbnail)
    {
        mSnipTitle = snipTitle;
        mSnipSource = snipSource;
        mSnipAuthor = snipAuthor;
        mSnipThumbnail = snipThumbnail;
    }
}
