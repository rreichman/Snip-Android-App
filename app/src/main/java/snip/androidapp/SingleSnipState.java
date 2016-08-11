package snip.androidapp;

import android.content.Context;

/**
 * Created by ranihorev on 11/08/2016.
 */
public class SingleSnipState {

    private static SingleSnipState mInstance = null;
    public long mSnipId;
    public boolean mIsLiked;
    public boolean mIsDisliked;
    public String mReaction;
    private String mDislikeServerText;
    private String mLikeServerText;


    protected SingleSnipState(Context context) {
        mIsDisliked = false;
        mIsLiked = false;
        mLikeServerText = context.getResources().getString(R.string.ReactionLike);
        mDislikeServerText = context.getResources().getString(R.string.ReactionDislike);
    }

    public void setReaction(String reaction) {
        mIsLiked = (mLikeServerText.equals(reaction));
        mIsDisliked = (mDislikeServerText.equals(reaction));
        mReaction = reaction;
    }

    public void setLiked() {
        mIsLiked = true;
        mIsDisliked = false;
        mReaction = mLikeServerText;
    }

    public void setDisliked() {
        mIsLiked = false;
        mIsDisliked = true;
        mReaction = mDislikeServerText;
    }

    public static synchronized SingleSnipState getInstance(Context context)
    {
        if (null == mInstance)
        {
            mInstance = new SingleSnipState(context);
        }
        return mInstance;
    }

    public static synchronized SingleSnipState getInstance()
    {
        return mInstance;
    }
}
