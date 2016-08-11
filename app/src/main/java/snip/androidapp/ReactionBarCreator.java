package snip.androidapp;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by ranihorev on 10/08/2016.
 */
public class ReactionBarCreator {


    // TODO should I change the mReaction value?
    public static void addReactionBarToLayout(Context context, LinearLayout snipLayout) {
        final long snipId = SingleSnipState.getInstance().mSnipId;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        LinearLayout reactionLayout = (LinearLayout) inflater.inflate(R.layout.reaction_bar, snipLayout, false);

        RelativeLayout dislikeLayout = (RelativeLayout) reactionLayout.findViewById(R.id.ReactionButtonDislike);
        View.OnClickListener dislikeListener = new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (SingleSnipState.getInstance().mIsDisliked) { // user already disliked it  and now undoing
                    ReactionManager.userUnDislikedSnip(snipId);
                    setImageResource(view, R.id.ReactionButtonDislikeImage, R.drawable.thumb_down_hollow);
                    SingleSnipState.getInstance().setReaction("null");

                }
                else {
                    ReactionManager.userDislikedSnip(snipId);
                    setImageResource(view, R.id.ReactionButtonDislikeImage, R.drawable.thumb_down_full);
                    setImageResource((LinearLayout) view.getParent(), R.id.ReactionButtonLikeImage, R.drawable.heart_icon_hollow);
                    SingleSnipState.getInstance().setDisliked();
                }
            }
        };
        dislikeLayout.setOnClickListener(dislikeListener);
        setImageReaction(reactionLayout, SingleSnipState.getInstance().mIsDisliked, R.id.ReactionButtonDislikeImage, R.drawable.thumb_down_full, R.drawable.thumb_down_hollow);

        RelativeLayout likeLayout = (RelativeLayout) reactionLayout.findViewById(R.id.ReactionButtonLike);
        View.OnClickListener likeListener = new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (SingleSnipState.getInstance().mIsLiked) { // user already liked it and now undoing
                    ReactionManager.userUnLikedSnip(snipId);
                    setImageResource(view, R.id.ReactionButtonLikeImage, R.drawable.heart_icon_hollow);
                    SingleSnipState.getInstance().setReaction("null");
                }
                else {
                    ReactionManager.userLikedSnip(snipId);
                    setImageResource(view, R.id.ReactionButtonLikeImage, R.drawable.heart_icon_full);
                    setImageResource((LinearLayout) view.getParent(), R.id.ReactionButtonDislikeImage, R.drawable.thumb_down_hollow);
                    SingleSnipState.getInstance().setLiked();
                }
            }
        };
        likeLayout.setOnClickListener(likeListener);
        setImageReaction(reactionLayout, SingleSnipState.getInstance().mIsLiked, R.id.ReactionButtonLikeImage, R.drawable.heart_icon_full, R.drawable.heart_icon_hollow);

        snipLayout.addView(reactionLayout);
    }

    private static void setImageReaction(View parentView, boolean currentState, int imageHolderId, int full_image_id, int hollow_image_id) {
        int imageResId = hollow_image_id;
        if (currentState) {
            imageResId = full_image_id;
        }
        setImageResource(parentView, imageHolderId, imageResId);
    }

    private static void setImageResource(View parentView, int imageHolderId, int imageResId) {
        ImageView curImage = (ImageView) parentView.findViewById(imageHolderId);
        curImage.setImageResource(imageResId);
    }

}

