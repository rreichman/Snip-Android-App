package snip.androidapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by ranreichman on 8/2/16.
 */
public class ExternalLinksData implements Parcelable, Serializable
{
    public LinkedList<Pair<String,String>> mExternalLinks;

    public ExternalLinksData(LinkedList<Pair<String,String>> externalLinks)
    {
        mExternalLinks = externalLinks;
    }

    public ExternalLinksData(Parcel parcle)
    {
        // TODO:: implement
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        ListIterator<Pair<String,String>> listIterator = mExternalLinks.listIterator();
        while (listIterator.hasNext())
        {
            Pair<String,String> pairInList = listIterator.next();
            dest.writeString(pairInList.first);
            dest.writeString(pairInList.second);
        }
    }

    public static final Parcelable.Creator<ExternalLinksData> CREATOR = new Parcelable.Creator<ExternalLinksData>()
    {
        @Override
        public ExternalLinksData createFromParcel(Parcel parcel)
        {
            return new ExternalLinksData(parcel);
        }

        @Override
        public ExternalLinksData[] newArray(int size) {
            return new ExternalLinksData[size];
        }
    };
}
