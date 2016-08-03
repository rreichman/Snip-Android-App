package snip.androidapp;

import android.graphics.Bitmap;
import android.graphics.Picture;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

/**
 * This holds the data for the content in the app. Parceable so that we can serialize it in an Android way
 *
 * Created by ranreichman on 7/19/16.
 */
public class SnipData implements Parcelable, Serializable
{
    public String mHeadline;
    public String mPublisher;
    public String mAuthor;
    public long mID;
    public Date mDate;
    //public SerializableBitmap mPicture;
    public String mBody;
    public ExternalLinksData mExternalLinks;
    public SnipComments mComments;

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mHeadline);
        dest.writeString(mPublisher);
        dest.writeString(mAuthor);
        dest.writeLong(mID);
        dest.writeSerializable(mDate);
        //dest.writeParcelable(mPicture, flags);
        dest.writeString(mBody);
        dest.writeParcelable(mExternalLinks, flags);
        dest.writeParcelable(mComments, flags);
    }

    public SnipData(Parcel parcel)
    {
        // FUTURE:: implement? is this even necessary?
    }

    public SnipData() {}

    public SnipData(
            String headline, String publisher, String author, Long id, Date date, SerializableBitmap picture,
            String body, LinkedList<Pair<String,String>> externalLinks, SnipComments comments)
    {
        mHeadline = headline;
        mPublisher = publisher;
        mAuthor = author;
        mID = id;
        mDate = date;
        //mPicture = picture;
        mBody = body;
        mExternalLinks = new ExternalLinksData(externalLinks);
        mComments = comments;
    }

    public static final Parcelable.Creator<SnipData> CREATOR = new Parcelable.Creator<SnipData>()
    {
        @Override
        public SnipData createFromParcel(Parcel parcel)
        {
            return new SnipData(parcel);
        }

        @Override
        public SnipData[] newArray(int size) {
            return new SnipData[size];
        }
    };
}
