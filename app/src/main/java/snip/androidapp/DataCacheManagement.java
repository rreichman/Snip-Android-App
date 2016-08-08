package snip.androidapp;

import android.content.Context;
import android.os.Bundle;

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
    public static String getFileNameForSnipData() { return "savedSnipData.dat"; }

    public static String getFileNameForSnipQuery()
    {
        return "savedSnipQuery.dat";
    }

    public static String getFullPathForSnipData(Context context)
    {
        return getFullPathOfFile(context ,getFileNameForSnipData());
    }

    public static String getFullPathForSnipQuery(Context context)
    {
        return getFullPathOfFile(context, getFileNameForSnipQuery());
    }

    public static String getFullPathOfFile(Context context, String filename) {
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

    private static void deleteFileOnDisk(String fullPathOfFile)
    {
        File file = new File(fullPathOfFile);
        if (file.exists())
        {
            file.delete();
        }
    }

    public static void deleteAppInformationFiles(Context context)
    {
        deleteFileOnDisk(getFullPathForSnipData(context));
        deleteFileOnDisk(getFullPathForSnipQuery(context));
    }

    public static void saveSnipDataToBundle(Bundle outBundle, LinkedList<SnipData> collectedSnips)
    {
        if (null != collectedSnips)
        {
            outBundle.putInt(DataCacheManagement.getSizeString(), collectedSnips.size());
            for (int i = 0; i < collectedSnips.size(); ++i) {
                outBundle.putParcelable(Integer.toString(i), collectedSnips.get(i));
            }
        }
    }

    public static LinkedList<SnipData> retrieveSnipDataFromBundle(Bundle savedInstanceState)
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
                        savedInstanceState.getString(DataCacheManagement.getQueryString()));

                return collectedSnips;
            }
        }

        return null;
    }

    public static void saveSnipDataToFile(Context context, LinkedList<SnipData> collectedSnips)
    {
        saveObjectToFile(context, collectedSnips, DataCacheManagement.getFileNameForSnipData());
    }

    public static void saveSnipQueryToFile(Context context)
    {
        saveObjectToFile(context,
                SnipCollectionInformation.getInstance().getLastSnipQuery(),
                DataCacheManagement.getFileNameForSnipQuery());
    }

    public static void saveAppInformationToFile(Context context, LinkedList<SnipData> collectedSnips)
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

    public static LinkedList<SnipData> retrieveSnipDataFromFile(Context context)
    {
        return (LinkedList<SnipData>)retrieveObjectFromFile(context, getFileNameForSnipData());
    }

    public static String retrieveSnipQueryFromFile(Context context)
    {
        return (String)retrieveObjectFromFile(context, getFileNameForSnipQuery());
    }

    public static LinkedList<SnipData> retrieveSavedDataFromBundleOrFile(
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
                SnipCollectionInformation.getInstance().setLastSnipQuery(retrieveSnipQueryFromFile(context));
            }
        }
        return outputSnips;
    }
}
