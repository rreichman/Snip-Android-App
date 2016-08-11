package snip.androidapp;

import android.content.Context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by ranihorev on 11/08/2016.
 */
public class SnipReactionsSingleton {

    private static SnipReactionsSingleton mInstance = null;
    public HashMap<Long, Integer> mSnipsReaction;
    private final static int NO_REACTION = 0;
    private final static int LIKE = 1;
    private final static int DISLIKE = 2;

    protected SnipReactionsSingleton() {
        mSnipsReaction = new HashMap<>();
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
        mSnipsReaction.put(snipID, curState);

    }

    public HashSet<Long> getIdsToRemoveFromDataset()
    {
        HashSet<Long> idsToRemove = new HashSet<Long>();

        for (Map.Entry<Long,Integer> e : mSnipsReaction.entrySet())
        {
            if (e.getValue() != NO_REACTION)
            {
                idsToRemove.add(e.getKey());
            }
        }

        return idsToRemove;
    }

    public boolean isLiked(long snipID) {
        return (mSnipsReaction.get(snipID) == LIKE);
    }

    public boolean isDisliked(long snipID) {
        return (mSnipsReaction.get(snipID) == DISLIKE);
    }

    public void setNoReaction(long snipID) {
        mSnipsReaction.put(snipID, NO_REACTION);
    }

    public void setLiked(long snipID) {
        mSnipsReaction.put(snipID, LIKE);
    }

    public void setDisliked(long snipID) {
        mSnipsReaction.put(snipID, DISLIKE);
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
