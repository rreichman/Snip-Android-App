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
    public static void deleteAllInformationFiles(Context context)
    {
        deleteFragmentInformationFiles(context, context.getResources().getInteger(R.integer.fragmentCodeMain));
        deleteFragmentInformationFiles(context, context.getResources().getInteger(R.integer.fragmentCodeLiked));
        deleteFragmentInformationFiles(context, context.getResources().getInteger(R.integer.fragmentCodeSnoozed));
    }

    public static String getFullPathForSnipData(Context context, int activityCode)
    {
        return getFullPathOfFile(context ,getSnipDataCacheFilename(activityCode));
    }

    public static String getFullPathForSnipQuery(Context context, int activityCode)
    {
        return getFullPathOfFile(context, getSnipQueryCacheFilename(activityCode));
    }

    public static String getFullPathOfFile(Context context, String filename)
    {
        String filesDir = context.getFilesDir().toString();
        return filesDir + "/" + filename;
    }

    public static String getSnipDataCacheFilename(int activityCode)
    {
        return "savedSnipData" + Integer.toString(activityCode) + ".dat";
    }

    public static String getSnipQueryCacheFilename(int activityCode)
    {
        return "savedQueryData" + Integer.toString(activityCode) + ".dat";
    }

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

    public static void deleteFileOnDisk(String fullPathOfFile)
    {
        File file = new File(fullPathOfFile);
        if (file.exists())
        {
            file.delete();
        }
    }

//    // TODO:: delete this
//    public static void deleteActivityInformationFiles(Context context, int activityCode)
//    {
//        deleteFileOnDisk(getFullPathForSnipData(context, activityCode));
//        deleteFileOnDisk(getFullPathForSnipQuery(context, activityCode));
//    }

    public static void deleteFragmentInformationFiles(Context context, int fragmentCode)
    {
        deleteFileOnDisk(getFullPathForSnipData(context, fragmentCode));
        deleteFileOnDisk(getFullPathForSnipQuery(context, fragmentCode));
    }

    public static void saveSnipDataToBundle(Context context, Bundle outBundle, LinkedList<SnipData> collectedSnips)
    {
        if (null != collectedSnips)
        {
            outBundle.putInt(
                    context.getResources().getString(R.string.SnipQueryStringInBundle),
                    collectedSnips.size());
            for (int i = 0; i < collectedSnips.size(); ++i) {
                outBundle.putParcelable(Integer.toString(i), collectedSnips.get(i));
            }
        }
    }

    public static LinkedList<SnipData> retrieveSnipDataFromBundle(
            Context context, Bundle savedInstanceState, int activityCode)
    {
        if (null != savedInstanceState) {
            if (!savedInstanceState.isEmpty()) {
                int size = savedInstanceState.getInt(
                        context.getResources().getString(R.string.SnipDataSizeInCacheString));
                LinkedList<SnipData> collectedSnips = new LinkedList<SnipData>();

                for (int i = 0; i < size; ++i) {
                    SnipData currentSnip = (SnipData) savedInstanceState.getParcelable(Integer.toString(i));
                    collectedSnips.addLast(currentSnip);
                }

                SnipCollectionInformation.getInstance(context).setLastSnipQuery(activityCode,
                        savedInstanceState.getString(
                                context.getResources().getString(R.string.SnipQueryStringInBundle)));

                return collectedSnips;
            }
        }

        return null;
    }

    public static void saveSnipDataToFile(
            Context context, LinkedList<SnipData> collectedSnips, int activityCode)
    {
        if (null != collectedSnips)
        {
            saveObjectToFile(context, collectedSnips, getSnipDataCacheFilename(activityCode));
        }
    }

    public static void saveSnipQueryToFile(Context context, int activityCode)
    {
        saveObjectToFile(context,
                SnipCollectionInformation.getInstance(context).getLastSnipQueryForFragment(activityCode),
                getSnipQueryCacheFilename(activityCode));
    }

    public static void saveAppInformationToFile(Context context, LinkedList<SnipData> collectedSnips, int activityCode)
    {
        saveSnipDataToFile(context, collectedSnips, activityCode);
        saveSnipQueryToFile(context, activityCode);
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

    public static LinkedList<SnipData> retrieveSnipDataFromFile(Context context, int activityCode)
    {
        return (LinkedList<SnipData>)retrieveObjectFromFile(
                context, getSnipDataCacheFilename(activityCode));
    }

    public static String retrieveSnipQueryFromFile(Context context, int activityCode)
    {

        return (String)retrieveObjectFromFile(
                context, getSnipQueryCacheFilename(activityCode));
    }

    public static LinkedList<SnipData> retrieveSavedDataFromBundleOrFile(
            Context context, Bundle savedInstanceState, int activityCode)
    {
        LinkedList<SnipData> outputSnips = retrieveSnipDataFromBundle(context, savedInstanceState, activityCode);
        if (null == outputSnips)
        {
            LinkedList<SnipData> collectedSnips = retrieveSnipDataFromFile(context, activityCode);
            String collectedSnipQuery = retrieveSnipQueryFromFile(context, activityCode);

            if ((null != collectedSnips) && (null != collectedSnipQuery))
            {
                outputSnips = collectedSnips;
                SnipCollectionInformation.getInstance(context).setLastSnipQuery(
                        activityCode, retrieveSnipQueryFromFile(context, activityCode));
            }
        }
        return outputSnips;
    }
}
