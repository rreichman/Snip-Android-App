package snip.androidapp;

import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

/**
 * Created by ranreichman on 8/4/16.
 */
public class DataCacheManagement
{
    public String mSnipDataCacheFilename;
    public String mSnipQueryCacheFilename;
    public int mActivityCode;

    public DataCacheManagement(String snipDataCacheFilename, String snipQueryCacheFilename, int activityCode)
    {
        mSnipDataCacheFilename = mSnipDataCacheFilename;
        mSnipQueryCacheFilename = mSnipQueryCacheFilename;
        mActivityCode = activityCode;
    }

    public String getFullPathForSnipData(Context context)
    {
        return getFullPathOfFile(context ,mSnipDataCacheFilename);
    }

    public String getFullPathForSnipQuery(Context context)
    {
        return getFullPathOfFile(context, mSnipQueryCacheFilename);
    }

    public static String getFullPathOfFile(Context context, String filename)
    {
        return context.getFilesDir() + "/" + filename;
    }

    // TODO:: this is a bad name for a function
    public static String getSizeString() { return "size"; }

    public static String getQueryString() { return "queryString"; }

    public static void saveObjectToFile(Context context, Object object, String filename)
    {
        try
        {
            File snipDataFile = new File(getFullPathOfFile(context, filename));
            if (snipDataFile.exists())
            {
                snipDataFile.delete();
            }

            FileOutputStream fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(object);
            objectOutputStream.close();
            fileOutputStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // TODO check with Reichman about changing to public
    public static void deleteFileOnDisk(String fullPathOfFile)
    {
        File file = new File(fullPathOfFile);
        if (file.exists())
        {
            file.delete();
        }
    }

    public void deleteActivityInformationFiles(Context context)
    {
        deleteFileOnDisk(getFullPathForSnipData(context));
        deleteFileOnDisk(getFullPathForSnipQuery(context));
    }

    public void saveSnipDataToBundle(Bundle outBundle, LinkedList<SnipData> collectedSnips)
    {
        if (null != collectedSnips)
        {
            outBundle.putInt(DataCacheManagement.getSizeString(), collectedSnips.size());
            for (int i = 0; i < collectedSnips.size(); ++i) {
                outBundle.putParcelable(Integer.toString(i), collectedSnips.get(i));
            }
        }
    }

    public LinkedList<SnipData> retrieveSnipDataFromBundle(Bundle savedInstanceState)
    {
        if (null != savedInstanceState) {
            if (!savedInstanceState.isEmpty()) {
                int size = savedInstanceState.getInt(DataCacheManagement.getSizeString());
                LinkedList<SnipData> collectedSnips = new LinkedList<SnipData>();

                for (int i = 0; i < size; ++i) {
                    SnipData currentSnip = (SnipData) savedInstanceState.getParcelable(Integer.toString(i));
                    collectedSnips.addLast(currentSnip);
                }

                SnipCollectionInformation.getInstance().setLastSnipQuery(
                        mActivityCode, savedInstanceState.getString(DataCacheManagement.getQueryString()));

                return collectedSnips;
            }
        }

        return null;
    }

    public void saveSnipDataToFile(Context context, LinkedList<SnipData> collectedSnips)
    {
        saveObjectToFile(context, collectedSnips, mSnipDataCacheFilename);
    }

    public void saveSnipQueryToFile(Context context)
    {
        saveObjectToFile(context,
                SnipCollectionInformation.getInstance().getLastSnipQueryForActivity(mActivityCode),
                mSnipQueryCacheFilename);
    }

    public void saveAppInformationToFile(Context context, LinkedList<SnipData> collectedSnips)
    {
        saveSnipDataToFile(context, collectedSnips);
        saveSnipQueryToFile(context);
    }

    public static Object retrieveObjectFromFile(Context context, String filename)
    {
        Object retrievedObject = null;
        File dataFile = new File(DataCacheManagement.getFullPathOfFile(context, filename));
        try
        {
            if (dataFile.exists())
            {
                FileInputStream fileInputStream = context.openFileInput(filename);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                retrievedObject = objectInputStream.readObject();
                objectInputStream.close();
                fileInputStream.close();
            }
        }
        catch (IOException e)
        {
            dataFile.delete();
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return retrievedObject;
    }

    public LinkedList<SnipData> retrieveSnipDataFromFile(Context context)
    {
        return (LinkedList<SnipData>)retrieveObjectFromFile(context, mSnipDataCacheFilename);
    }

    public String retrieveSnipQueryFromFile(Context context)
    {
        return (String)retrieveObjectFromFile(context, mSnipQueryCacheFilename);
    }

    public LinkedList<SnipData> retrieveSavedDataFromBundleOrFile(
            Context context, Bundle savedInstanceState)
    {
        LinkedList<SnipData> outputSnips = retrieveSnipDataFromBundle(savedInstanceState);
        if (null == outputSnips)
        {
            LinkedList<SnipData> collectedSnips = retrieveSnipDataFromFile(context);
            String collectedSnipQuery = retrieveSnipQueryFromFile(context);

            if ((null != collectedSnips) && (null != collectedSnipQuery))
            {
                outputSnips = collectedSnips;
                SnipCollectionInformation.getInstance().setLastSnipQuery(
                        mActivityCode, retrieveSnipQueryFromFile(context));
            }
        }
        return outputSnips;
    }
}
