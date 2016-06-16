package application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.DOA.SQLHelper_DataBase_Creation_Query_Deletion;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Model.RightObjects;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.R;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Service.Array_Creation_List_Views;

public class Async_task_Main_List extends AsyncTask<String, String, String> {

    ProgressDialog spinner = null;
    Context context;
    Context getContext;
    String getPackageName;
    private static String[][] listChaptersDatabase;
    boolean mError = false;
    LayoutInflater layInflate;
    Typeface font_custom;
    private AlertDialog alertDialog;
    RightObjects[] rightObjectses;
    private String hadiths;

    public Array_Creation_List_Views delegates = null;

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
            delegates.startListCreationFun(true);

            Handler delayHandler = new Handler();
            delayHandler.postDelayed(executeAfterComplete, 800);
        }
        else if (mError) {
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

        try {
            SQLHelper_DataBase_Creation_Query_Deletion dbHelp = new SQLHelper_DataBase_Creation_Query_Deletion(context);
            dbHelp.openDataBase();
            dbHelp.createChapterList();
            listChaptersDatabase = dbHelp.getChapters_names_for_list_view_main_activity();
            hadiths = dbHelp.getHadiths();
            dbHelp.close();
        }
        catch (Exception e) {
            mError = true;
        }

        delegates.hadithsString(hadiths);

        rightObjectses = new RightObjects[listChaptersDatabase.length];

        for (int x = 0; x < listChaptersDatabase.length; x++) {
            rightObjectses[x] = new RightObjects(Async_task_Main_List.listChaptersDatabase[x][0],
                    Async_task_Main_List.listChaptersDatabase[x][1], Async_task_Main_List.listChaptersDatabase[x][2]);
        }

        delegates.rightObjects(rightObjectses);

        return null;
    }

    private Runnable executeAfterComplete = new Runnable() {
        @Override
        public void run() {

            try {
                spinner.dismiss();
            }
            catch (Exception e) {
                spinner.cancel();
            }
        }
    };

    public void setGetPackageName(String getPackageName) {
        this.getPackageName = getPackageName;
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

