package snip.androidapp;

import java.io.Serializable;

/**
 * Created by ranreichman on 8/3/16.
 */
public class ExternalLinkData implements Serializable
{
    public String mAuthor;
    public String mLink;
    public String mTitle;

    public ExternalLinkData(String author, String link, String title)
    {
        mAuthor = author;
        mLink = link;
        mTitle = title;
    }
}
