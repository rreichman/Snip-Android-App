package snip.androidapp;

import android.graphics.Picture;
import android.util.Pair;

import java.util.Date;
import java.util.LinkedList;

/**
 * This holds the data for the content in the app
 *
 * Created by ranreichman on 7/19/16.
 */
public class SnipData
{
    public String mHeadline;
    public String mPublisher;
    public String mAuthor;
    public long mID;
    public Date mDate;
    public Picture mPicture;
    public String mBody;
    public LinkedList<Pair<String,String>> mExternalLinks;
    public SnipComments mComments;

    public SnipData() {}

    public SnipData(
            String headline, String publisher, String author, Long id, Date date, Picture picture,
            String body, LinkedList<Pair<String,String>> externalLinks, SnipComments comments)
    {
        mHeadline = headline;
        mPublisher = publisher;
        mAuthor = author;
        mID = id;
        mDate = date;
        mPicture = picture;
        mBody = body;
        mExternalLinks = new LinkedList<Pair<String,String>>(externalLinks);
        mComments = comments;
    }
}
