package snip.androidapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by ranreichman on 7/24/16.
 */
public class SnipComments implements Parcelable, Serializable
{
    // TODO:: implement class
    public SnipComments()
    {

    }

    public SnipComments(Parcel parcel)
    {
        // FUTURE:: implement? do i even need to implement this?
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString("");
    }

    public static final Parcelable.Creator<SnipComments> CREATOR = new Parcelable.Creator<SnipComments>()
    {
        @Override
        public SnipComments createFromParcel(Parcel parcel)
        {
            return new SnipComments(parcel);
        }

        @Override
        public SnipComments[] newArray(int size) {
            return new SnipComments[size];
        }
    };
}
