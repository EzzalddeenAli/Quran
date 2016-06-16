package application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.DOA.SQLHelper_DataBase_Creation_Query_Deletion;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Model.ChaptersListObjects;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.R;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Service.Chapters_Array_Creation;

public class Async_task_fragment_chapters_bookmarks extends AsyncTask<String, String, String> {

    ProgressDialog spinner = null;
    Context context;
    Context getContext;
    boolean mError = false;
    boolean determine_get_method; // true = bookmarks, false = chapters
    int start, end;
    private ArrayList<Integer> rowid;
    private ArrayList<String> verse_name;
    private ArrayList<String> english;
    private ArrayList<String> arabic;
    private ArrayList<Integer> bookmarks;
    LayoutInflater layInflate;
    Typeface font_custom;
    private AlertDialog alertDialog;
    ChaptersListObjects[] chaptersListObjects;

    public Chapters_Array_Creation chapters_delegates = null;

    @Override
    protected void onPreExecute() {
        spinner = new ProgressDialog(getContext, R.style.MyCustomSpinnerTheme);
        spinner.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        spinner.setCancelable(false);
        spinner.show();
    }

    @Override
    protected void onPostExecute(String s) {

        if (!mError) {
            chapters_delegates.setEndResult(true);

            Handler delayHandler = new Handler();
            delayHandler.postDelayed(executeAfterComplete, 800);


        } else if (mError) {
            spinner.dismiss();

            final Animation pusher = AnimationUtils.loadAnimation(getContext, R.anim.buttons_anim_push_in);
            font_custom = Typeface.createFromAsset(getContext.getAssets(), "list_textviews.otf");

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext);

            View dialogView = layInflate.inflate(R.layout.dialog_custom_demo, null);
            alertBuilder.setView(dialogView);
            alertBuilder.setCancelable(false);

            TextView demo_text = (TextView) dialogView.findViewById(R.id.demo_text_view);
            final Button demo_button = (Button) dialogView.findViewById(R.id.demo_button_view);

            demo_text.setText("Error occurred!");
            demo_button.setText("OK");
            demo_button.setTypeface(font_custom);
            demo_text.setTypeface(font_custom);

            alertDialog = alertBuilder.create();
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            alertDialog.show();

            demo_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    demo_button.startAnimation(pusher);
                    v.startAnimation(pusher);
                    alertDialog.dismiss();
                }
            });
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {

    }

    @Override
    protected String doInBackground(String... params) {

        if (!determine_get_method) {
            try {
                SQLHelper_DataBase_Creation_Query_Deletion dbHelp = new SQLHelper_DataBase_Creation_Query_Deletion(context);
                dbHelp.setStart(start);
                dbHelp.setEnd(end);
                dbHelp.openDataBase();
                dbHelp.grabChapterViews();
                dbHelp.close();

                rowid = dbHelp.getRowid();
                verse_name = dbHelp.getVerse_name();
                english = dbHelp.getEnglish();
                arabic = dbHelp.getArabic();
                bookmarks = dbHelp.getBookmarks();

                chaptersListObjects = new ChaptersListObjects[rowid.size()];

                for (int x = 0; x < rowid.size(); x++) {
                    int s = getContext.getResources().getIdentifier(arabic.get(x), "drawable", getContext.getPackageName());
                    chaptersListObjects[x] = new ChaptersListObjects(rowid.get(x), verse_name.get(x), english.get(x),
                            arabic.get(x), bookmarks.get(x), s);
                }

                chapters_delegates.setChaptersList(chaptersListObjects);
            } catch (Exception e) {
               mError = true;
            }
        }
        else if (determine_get_method) {
            try {
                SQLHelper_DataBase_Creation_Query_Deletion dbHelp = new SQLHelper_DataBase_Creation_Query_Deletion(context);
                dbHelp.openDataBase();
                dbHelp.grabBookMarksFragmentView();
                dbHelp.close();

                rowid = dbHelp.getRowid();
                verse_name = dbHelp.getVerse_name();
                english = dbHelp.getEnglish();
                arabic = dbHelp.getArabic();
                bookmarks = dbHelp.getBookmarks();

                chaptersListObjects = new ChaptersListObjects[rowid.size()];

                for (int x = 0; x < rowid.size(); x++) {
                    int s = getContext.getResources().getIdentifier(arabic.get(x), "drawable", getContext.getPackageName());
                    chaptersListObjects[x] = new ChaptersListObjects(rowid.get(x), verse_name.get(x), english.get(x),
                            arabic.get(x), bookmarks.get(x), s);
                }

                chapters_delegates.setChaptersList(chaptersListObjects);
            } catch (Exception e) {
                mError = true;
            }
        }

        return null;
    }

    private Runnable executeAfterComplete = new Runnable() {
        @Override
        public void run() {

            try {
                spinner.dismiss();
            } catch (Exception e) {
                spinner.cancel();
            }
        }
    };

    public void setContext(Context context) {
        this.context = context;
    }

    public void setContextSpinner(Context contexts) {
        this.getContext = contexts;
    }

    public void setDetermine_get_method(boolean method) {
        this.determine_get_method = method;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setLayInflate(LayoutInflater layInflate) {
        this.layInflate = layInflate;
    }

}