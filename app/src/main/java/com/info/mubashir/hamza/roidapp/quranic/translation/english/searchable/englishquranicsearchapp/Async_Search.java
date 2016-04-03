package com.info.mubashir.hamza.roidapp.quranic.translation.english.searchable.englishquranicsearchapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Async_Search extends AsyncTask<String, String, String> {

    List<String> searchList;
    ProgressDialog spinner = null;
    Context context;
    Context getContext;
    boolean mError = false;
    boolean determine_get_method; // true = bookmarks, false = chapters
    int start, end;
    LayoutInflater layInflate;
    Typeface font_custom;
    private AlertDialog alertDialog;
    SearchListObjects[] searchListObjects;
    String dbArray[][];

    public Search_delegates search_delegates = null;

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
            search_delegates.setEndResult(true);

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
    protected String doInBackground(String... params) {

        try {
            SQLHelper_DataBase_Creation_Query_Deletion dbHelp = new SQLHelper_DataBase_Creation_Query_Deletion(context);
            dbHelp.setSearching(searchList);
            dbHelp.openDataBase();
            dbHelp.getSearchPatterns();
            dbArray = dbHelp.getDbArray();
            dbHelp.close();

            searchListObjects = new SearchListObjects[dbArray.length];

            for (int x = 0; x < dbArray.length; x++) {
                int s = getContext.getResources().getIdentifier(dbArray[x][3], "drawable", getContext.getPackageName());

                String eng = dbArray[x][2];

                for (String exp : searchList) {
                    Pattern pattern = Pattern.compile("\\b" + exp + "(er|ing|ed|s|'s)?\\b", Pattern.CASE_INSENSITIVE);
                    Matcher match = pattern.matcher(eng);

                    while (match.find()) {
                        eng = match.replaceAll("<b><i>" + match.group() + "</i></b>");
                    }
                }

                Spanned exp = Html.fromHtml(eng);

                searchListObjects[x] = new SearchListObjects(Integer.parseInt(dbArray[x][0]), dbArray[x][1], exp, dbArray[x][3],
                        Integer.parseInt(dbArray[x][4]), s);
            }

            search_delegates.setSearchList(searchListObjects);
        } catch (Exception e) {
            mError = true;
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {

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

    public class SearchListObjects {
        public int rowid_number;
        public String verse_chapter;
        public Spanned english_chapter;
        public String arabic_chapter;
        public int bookmarks_chapter;
        public int arabic_resource;

        SearchListObjects() {
            super();
        }

        public SearchListObjects(int rowid, String verse_chapter, Spanned english_chapter, String arabic_chapter,
                                 int bookmarks_chapter, int arabic_resource) {
            super();
            this.rowid_number = rowid;
            this.verse_chapter = verse_chapter;
            this.english_chapter = english_chapter;
            this.arabic_chapter = arabic_chapter;
            this.bookmarks_chapter = bookmarks_chapter;
            this.arabic_resource = arabic_resource;
        }
    }

    public void setSearchList(List<String> searchList) {
        this.searchList = searchList;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setContextSpinner(Context contexts) {
        this.getContext = contexts;
    }

    public void setLayInflate(LayoutInflater layInflate) {
        this.layInflate = layInflate;
    }

}
