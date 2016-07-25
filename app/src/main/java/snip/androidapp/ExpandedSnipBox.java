package snip.androidapp;

import android.graphics.Picture;

import java.util.HashMap;

/**
 * Created by ranreichman on 7/24/16.
 */
public class ExpandedSnipBox extends SnipBox
{
    public String mFullText;
    public Picture mFullSizePicture;
    public HashMap<String,String> mExternalLinks;
    public SnipComments mComments;

    ExpandedSnipBox(String fullText, Picture fullSizePicture, HashMap<String,String> externalLinks, SnipComments comments)
    {
        mFullText = fullText;
        mFullSizePicture = fullSizePicture;
        mExternalLinks = new HashMap<String,String>(externalLinks);
        mComments = comments;
    }
}
