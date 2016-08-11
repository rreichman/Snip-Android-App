package snip.androidapp;

import android.graphics.Bitmap;
import android.graphics.Picture;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Pair;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

/**
 * This holds the data for the content in the app. Parceable so that we can serialize it in an Android way
 *
 * Created by ranreichman on 7/19/16.
 */
public class SnipData implements Parcelable, Serializable {
    public String mHeadline;
    public String mPublisher;
    public String mAuthor;
    public long mID;
    public Date mDate;
    public String mThumbnailUrl;
    public transient Bitmap mThumbnail;
    public String mBody;
    public String mReaction;
    public ExternalLinksData mExternalLinks;
    //public SnipComments mComments;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mHeadline);
        dest.writeString(mPublisher);
        dest.writeString(mAuthor);
        dest.writeLong(mID);
        dest.writeSerializable(mDate);
        dest.writeString(mThumbnailUrl);
        dest.writeString(mBody);
        dest.writeString(mReaction);
        dest.writeParcelable(mExternalLinks, flags);
        //dest.writeParcelable(mComments, flags);
    }

    public SnipData(Parcel parcel) {
        mHeadline = parcel.readString();
        mPublisher = parcel.readString();
        mAuthor = parcel.readString();
        mID = parcel.readLong();
        mDate = (Date) parcel.readSerializable();
        mThumbnailUrl = parcel.readString();
        mBody = parcel.readString();
        mReaction = parcel.readString();
        mExternalLinks = parcel.readParcelable(ExternalLinksData.class.getClassLoader());
        //mComments = parcel.readParcelable(SnipComments.class.getClassLoader());
    }

    public SnipData() {
    }

    public static String getSnipDataString() {
        return "snipData";
    }

    public SnipData(
            String headline, String publisher, String author, Long id, Date date, String thumbnailUrl,
            String body, ExternalLinksData externalLinks, SnipComments comments, String reaction) {
        mHeadline = headline;
        mPublisher = publisher;
        mAuthor = author;
        mID = id;
        mDate = date;
        mThumbnailUrl = thumbnailUrl;
        mThumbnail = getBitmapFromUrl(thumbnailUrl);
        mBody = body;
        mExternalLinks = externalLinks;
        mReaction = reaction;

        //mComments = comments;
    }

    private static Bitmap getBitmapFromUrlInSeperateThread(String thumbnailUrl)
    {
        // TODO:: think if counting on the cache here is smart. Maybe we should receive the Bitmap
        try {
            ImageInternetAccessor accessor = new ImageInternetAccessor();
            return accessor.execute(thumbnailUrl).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap getBitmapFromUrl(String thumbnailUrl) {
        if (Looper.getMainLooper().getThread() == Thread.currentThread())
        {
            return getBitmapFromUrlInSeperateThread(thumbnailUrl);
        }
        else
        {
            return ImageLoader.getInstance().loadImageSync(thumbnailUrl);
        }
    }

    public static final Parcelable.Creator<SnipData> CREATOR = new Parcelable.Creator<SnipData>()
    {
        @Override
        public SnipData createFromParcel(Parcel parcel)
        {
            SnipData snipData = new SnipData(parcel);
            return snipData;
        }

        @Override
        public SnipData[] newArray(int size) {
            return new SnipData[size];
        }
    };
}
