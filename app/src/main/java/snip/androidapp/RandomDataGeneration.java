package snip.androidapp;

import android.graphics.Bitmap;
import android.util.Pair;

import java.util.Date;
import java.util.LinkedList;

/**
 * Created by ranreichman on 8/2/16.
 */
public class RandomDataGeneration
{
    public static LinkedList<SnipData> createRandomSnipDataset(int size, int numberToStartCountingAt) {
        LinkedList<SnipData> myDataset = new LinkedList<SnipData>();

        for (int i = 0; i < size; ++i) {
            int printedNumber = i + numberToStartCountingAt;
            String snipHeadline = "Headline" + printedNumber;
            String snipPublisher = "Snip" + printedNumber;
            String snipAuthor = "Author" + printedNumber;
            String snipSource = "Ynet" + printedNumber;

            String snipBody = "Body" + printedNumber;
            String snipWebsite = "www.ynet" + printedNumber + ".co.il";
            String bitmapURL = "fakeURL";
            Date fakeDate = new Date();
            long snipID = i + 1;

            SnipData currentSnipInformation =
                    new SnipData(snipHeadline, snipPublisher, snipAuthor, snipID, fakeDate,
                            bitmapURL, snipBody, null, new SnipComments(), "");

            myDataset.addLast(currentSnipInformation);
        }

        return myDataset;
    }

    private static LinkedList<SnipData> getListOfSnips() {
        return createRandomSnipDataset(15, 1);
    }
}
