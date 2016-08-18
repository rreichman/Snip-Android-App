package snip.androidapp;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by ranihorev on 11/08/2016.
 */
public class SnipReactionsSingleton {

    private static SnipReactionsSingleton mInstance = null;
    private HashMap<Long, Integer> mSnipsReactions;
    private final static int NO_REACTION = 0;
    private final static int LIKE = 1;
    private final static int DISLIKE = 2;

    protected SnipReactionsSingleton()
    {
        mSnipsReactions = new HashMap<Long, Integer>();
    }

    public void setReaction(Context context, long snipID, String reaction) {
        final String likeServerText = context.getResources().getString(R.string.ReactionLike);
        final String dislikeServerText = context.getResources().getString(R.string.ReactionDislike);
        int curState = NO_REACTION;
        if (likeServerText.equals(reaction)) {
            curState = LIKE;
        }
        else if (dislikeServerText.equals(reaction)) {
            curState = DISLIKE;
        }
        mSnipsReactions.put(snipID, curState);
    }

    public HashSet<Long> getIdsToRemoveFromDataset()
    {
        HashSet<Long> idsToRemove = new HashSet<Long>();

        for (Map.Entry<Long,Integer> e : mSnipsReactions.entrySet())
        {
            if (e.getValue() != NO_REACTION)
            {
                idsToRemove.add(e.getKey());
            }
        }

        return idsToRemove;
    }

    public boolean isLiked(long snipID)
    {
        Log.d("snipID", Long.toString(snipID));
        Log.d("mSnipsReactions", mSnipsReactions.keySet().toString());
        if (mSnipsReactions.containsKey(snipID))
        {
            return (mSnipsReactions.get(snipID) == LIKE);
        }
        else
        {
            return false;
        }
    }

    public boolean isDisliked(long snipID)
    {
        Log.d("snipID", Long.toString(snipID));
        Log.d("mSnipsReactions", mSnipsReactions.keySet().toString());
        if (mSnipsReactions.containsKey(snipID))
        {
            return (mSnipsReactions.get(snipID) == DISLIKE);

        }
        else
        {
            return false;
        }
    }

    public void setNoReaction(long snipID) {
        mSnipsReactions.put(snipID, NO_REACTION);
    }

    public void setLiked(long snipID) {
        mSnipsReactions.put(snipID, LIKE);
    }

    public void setDisliked(long snipID) {
        mSnipsReactions.put(snipID, DISLIKE);
    }

    public static synchronized SnipReactionsSingleton getInstance()
    {
        if (null == mInstance)
        {
            mInstance = new SnipReactionsSingleton();
        }
        return mInstance;
    }

}
