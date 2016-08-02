package snip.androidapp;

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
            LinkedList<Pair<String, String>> links = new LinkedList<Pair<String, String>>();
            links.addLast(new Pair<String, String>(snipSource, snipWebsite));
            SerializableBitmap fakePicture = new SerializableBitmap();
            Date fakeDate = new Date();
            // TODO:: obviously important to change this later to real snip ID.
            long snipID = i + 1;
            SnipData currentSnipInformation =
                    new SnipData(snipHeadline, snipPublisher, snipAuthor, snipID, fakeDate, fakePicture,
                            snipBody, links, new SnipComments());

            myDataset.addLast(currentSnipInformation);
        }

        return myDataset;
    }

    private static LinkedList<SnipData> getListOfSnips() {
        return createRandomSnipDataset(15, 1);
    }
}
