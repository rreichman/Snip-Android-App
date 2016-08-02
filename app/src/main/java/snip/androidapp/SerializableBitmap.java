package snip.androidapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Used this: http://stackoverflow.com/questions/6002800/android-serializable-problem
 * need to make sure it works
 *
 * Created by ranreichman on 8/2/16.
 */
public class SerializableBitmap implements Parcelable, Serializable
{
    public Bitmap mBitmap;

    // TODO: Finish this constructor
    SerializableBitmap() {
        // Take your existing call to BitmapFactory and put it here
        //bitmap = BitmapFactory.decodeSomething(<some params>);
    }

    // Converts the Bitmap into a byte array for serialization
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG, 0, byteStream);
        byte bitmapBytes[] = byteStream.toByteArray();
        out.write(bitmapBytes, 0, bitmapBytes.length);
    }

    // Deserializes a byte array representing the Bitmap and decodes it
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        int b;
        while((b = in.read()) != -1)
            byteStream.write(b);
        byte bitmapBytes[] = byteStream.toByteArray();
        mBitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
    }


    public SerializableBitmap(Parcel parcel)
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
        dest.writeParcelable(mBitmap, flags);
    }

    public static final Parcelable.Creator<SerializableBitmap> CREATOR = new Parcelable.Creator<SerializableBitmap>()
    {
        @Override
        public SerializableBitmap createFromParcel(Parcel parcel)
        {
            return new SerializableBitmap(parcel);
        }

        @Override
        public SerializableBitmap[] newArray(int size) {
            return new SerializableBitmap[size];
        }
    };
}
