package snip.androidapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ranreichman on 7/25/16.
 */
public class ReadSnipActivity extends AppCompatActivity
{
    private static Spanned fromHtml(String htmlString)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            return Html.fromHtml(htmlString,Html.FROM_HTML_MODE_LEGACY);
        }
        else
        {
            return Html.fromHtml(htmlString);
        }
    }


    private void addTextDynamicallyToLayout(LinearLayout layout, String text, boolean isLink, int styleId)
    {

        SnipTextView textView = new SnipTextView(new ContextThemeWrapper(this, styleId), styleId);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        textView.setGravity(Gravity.RIGHT);

        int margin_horz = (int) getResources().getDimension(R.dimen.snip_text_margin_horz);
        int margin_vert = (int) getResources().getDimension(R.dimen.snip_text_margin_vert);
        params.setMargins(margin_horz, margin_vert, margin_horz, margin_vert);

        Typeface fontType = Typeface.createFromAsset(getAssets(),"fonts/Heebo-Regular.ttf");
        textView.setTypeface(fontType);

        if (isLink)
        {
            Spanned spanned = fromHtml(text);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(spanned);
        }
        else {
            textView.setText(text);
        }
        textView.setLayoutParams(params);
        layout.addView(textView, params);
    }

    private void addPictureDynamicallyToLayout(LinearLayout layout, int pictureId, int styleId)
    {
        ImageView imageView = new ImageView(this, null, styleId);
        imageView.setImageResource(pictureId);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

//         Setting the height to be the same as the width.
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, displayMetrics.widthPixels);

        layout.addView(imageView, params);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clean_scrollable_activity);
        BaseToolbar activityToolbar = new BaseToolbar();
        activityToolbar.setupToolbar(this);



        LinearLayout layout = (LinearLayout)findViewById(R.id.clean_layout);
        // TextSize, and then Margins are left, top, right, down, in dpi
        addTextDynamicallyToLayout(layout, "תמצית: מלא תוכן מעניין", false, R.style.SingleSnip_Text_Headline);
        addTextDynamicallyToLayout(layout, "סניפ, נכתב ע״י אלון נבון", false, R.style.SingleSnip_Text_Author);
        addTextDynamicallyToLayout(layout, "8 שעות", false, R.style.SingleSnip_Text_Author);
        addPictureDynamicallyToLayout(layout, R.drawable.alonsnipthailand, R.style.SingleSnip_Image);
        addTextDynamicallyToLayout(layout, "קבלת פנים למלך באחת מעיירות תאילנד", false, R.style.SingleSnip_Text_ImageDesc);

        String firstParagraphInSnip = "מלך תאילנד ראמה התשיעי הוא מנהיג המדינה הוותיק ביותר בעולם. הוא הוכתר ב-9 ביוני 1946, ומכהן באופן רשמי מאז 1950! עם זאת, בגיל 88 הוא כבר אינו בקו הבריאות. הוא לא נראה בציבור כבר חודשים, וכנראה נמצא על ערש דווי. מזה שנתיים ארצו נשלטת בידי חונטה צבאית, לכאורה בשמו, והצבא מנצל חוקים דרקוניים האוסרים ביקורת נגד המלך ומשפחתו (והכלב שלו). התקשורת הושתקה, ופוליטיקאים שהעזו להביע התנגדות נלקחו ל״חינוך מחדש״.";
        String secondParagraphInSnip = "אולם כעת הצבא עומד בפני בעיה. היורש המיועד, נסיך הכתר, הוא פלייבוי גרוש בשלישית, שהעניק לפודל שלו דרגת מרשל בחיל האוויר ושנוא ע״י הציבור. העברת השלטון אל הבן עלולה לגרום לפגיעה קשה במעמד המלוכה, מקור הלגיטימציה של החונטה הצבאית.";
        String thirdParagraphInSnip = "מאז שנות ה-30 ראתה תאילנד לא פחות מתריסר הפיכות מוצלחות, וחוקה חדשה כל ארבע שנים בממוצע. לאורך השנים ראשי הצבא השתמשו במוסד המלוכה כדי להלהיט רגשות לאומיים ולזכות בלגיטימציה להפיכותיהם, ומנגד אפשרו לבית המלוכה לצבור כוח פוליטי וכלכלי. המלך מחזיק כיום נכסים בשווי של 50 מיליארד דולר, אותם הוא ניצל למטרות צדקה (שחיזקו את תדמיתו) וכן להקמת רשת פטרונות רחבה.";
        String fourthParagraphInSnip = "ההפיכה האחרונה במדינה התרחשה לאחר עימותים אלימים בין ״החולצות האדומות״, הכפריים ואנשי המעמד הנמוך במדינה, לבין ״החולצות הצהובות״, העירוניים ואנשי המעמד הבינוני-גבוה. עימותים אלה מבטאים את הקרע העמוק בחברה התאילנדית, בה אי השוויון קיצוני אף ביחס למדינות מתפתחות אחרות. על רקע הפערים החברתיים ב-2001 נבחר לראשות הממשלה תקסין שנאווטרה, מוגול תקשורת פופוליסט שהבטיח בריאות חינם וסובסידיות לחקלאים, ואיים על האליטות המסורתיות. הפיכה צבאית ב-2006 הותירה אותו בגלות, אך מפלגתו המשיכה לזכות בכל מערכת בחירות, ולימים הפכה אחותו לראשת הממשלה (אך הודחה בעצמה, בהפיכה נוספת).";
        String fifthParagraphInSnip = "בעוד שהדיכוי הצבאי עשוי לאפשר העברה מסודרת של הכתר, נראה שיציבות אמיתית תושג בתאילנד רק ע״י איחוי הקרעים בחברה. התמיכה הנמשכת בתקסין מבטאת את תסכולם האמיתי של תושבי הפריפריה, ורק פשרה בין ״הצהובים״ ל״אדומים״ תאפשר לתאילנד לצעוד קדימה.";


        addTextDynamicallyToLayout(layout, firstParagraphInSnip, false, R.style.SingleSnip_Text);
        addTextDynamicallyToLayout(layout, secondParagraphInSnip, false, R.style.SingleSnip_Text);
        addTextDynamicallyToLayout(layout, thirdParagraphInSnip, false, R.style.SingleSnip_Text);
        addTextDynamicallyToLayout(layout, fourthParagraphInSnip, false, R.style.SingleSnip_Text);
        addTextDynamicallyToLayout(layout, fifthParagraphInSnip, false, R.style.SingleSnip_Text);


        addTextDynamicallyToLayout(layout, "מקורות:", false, R.style.SingleSnip_Text);


        for (int i = 0; i < 3; ++i)
        {
            String textInLink = "הכתבה באקונומיסט" + i;
            String pathOfWebsite = "www.economist.com";
            String htmlLinkString = "<a href=\"" + pathOfWebsite + "\">" + textInLink + "</a>";
            addTextDynamicallyToLayout(layout, htmlLinkString, true, R.style.SingleSnip_Text);
        }

        Button button = new Button(this);
        button.setText("LIKE");
        layout.addView(button);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }
}