package snip.androidapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by ranreichman on 8/2/16.
 */
public class ExternalLinksData implements Parcelable, Serializable
{
    public LinkedList<ExternalLinkData> mExternalLinks;

    public ExternalLinksData(JSONArray externalLinksAsJson)
    {
        try
        {
            mExternalLinks = new LinkedList<ExternalLinkData>();
            for (int i = 0; i < externalLinksAsJson.length(); i++) {
                JSONObject currentExternalLinkAsJson = externalLinksAsJson.getJSONObject(i);
                mExternalLinks.addLast(
                        new ExternalLinkData(
                                currentExternalLinkAsJson.getString("author"),
                                currentExternalLinkAsJson.getString("link"),
                                currentExternalLinkAsJson.getString("title")));
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public ExternalLinksData(Parcel parcle)
    {
        mExternalLinks = new LinkedList<ExternalLinkData>();
        int amountOfLinks = parcle.readInt();
        for (int i = 0; i < amountOfLinks; ++i)
        {
            String author = parcle.readString();
            String link = parcle.readString();
            String title = parcle.readString();
            ExternalLinkData data = new ExternalLinkData(author, link, title);
            mExternalLinks.addLast(data);
        }
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(mExternalLinks.size());
        ListIterator<ExternalLinkData> listIterator = mExternalLinks.listIterator();
        while (listIterator.hasNext())
        {
            ExternalLinkData singleLinkData = listIterator.next();
            dest.writeString(singleLinkData.mAuthor);
            dest.writeString(singleLinkData.mLink);
            dest.writeString(singleLinkData.mTitle);
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
