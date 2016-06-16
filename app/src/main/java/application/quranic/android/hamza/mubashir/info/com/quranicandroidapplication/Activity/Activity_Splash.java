package application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.DOA.SQLHelper_DataBase_Creation_Query_Deletion;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.R;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Service.KeyboardGone;

public class Activity_Splash extends Activity {

    private static String DB_PATH = "data/data/application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication/databases/";
    private static final String DATABASE_NAME = "quranDB.sqlite";
    ImageView iv;
    Animation zoom_in;
    Animation zoom_out;
    ProgressDialog spinner = null;
    boolean pause_for_database_creation = false;
    SharedPreferences firstAppRun = null;
    LayoutInflater layInflate;
    Typeface font_custom;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_layout);

        KeyboardGone removeKey = new KeyboardGone(Activity_Splash.this);
        Activity_Splash.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        layInflate = this.getLayoutInflater();

        firstAppRun = PreferenceManager.getDefaultSharedPreferences(this);

        String file = DB_PATH + DATABASE_NAME;
        File dbFile = new File(file);

        if (!dbFile.exists()) {
            executeError_from_database_creation();
        }

        if (!firstAppRun.getBoolean("firstTime", false)) {

            Thread thread = new Thread() {

                @Override
                public void run() {
                    try {
                        SQLHelper_DataBase_Creation_Query_Deletion dbHelper = new SQLHelper_DataBase_Creation_Query_Deletion(getBaseContext());
                        dbHelper.createDataBase();
                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                executeError_from_database_creation();

                                final Animation pusher = AnimationUtils.loadAnimation(Activity_Splash.this, R.anim.buttons_anim_push_in);

                                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Activity_Splash.this);

                                View dialogView = layInflate.inflate(R.layout.dialog_custom_demo, null);
                                alertBuilder.setView(dialogView);
                                alertBuilder.setCancelable(false);

                                TextView demo_text = (TextView) dialogView.findViewById(R.id.demo_text_view);
                                final Button demo_button = (Button) dialogView.findViewById(R.id.demo_button_view);

                                demo_text.setText(R.string.database_creation_error_message);
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
                                        System.exit(0);

                                    }
                                });
                            }
                        });
                    }
                }
            };

            thread.start();

            pause_for_database_creation = true;

            SharedPreferences.Editor editor = firstAppRun.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();

        }

        iv = (ImageView) findViewById(R.id.splash_logo_image);
        zoom_in = AnimationUtils.loadAnimation(getBaseContext(), R.anim.splash_zoom_in);
        zoom_out = AnimationUtils.loadAnimation(getBaseContext(), R.anim.splash_zoom_out);

        iv.startAnimation(zoom_in);
        zoom_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                try {
                    Thread.sleep(1200);
                    iv.startAnimation(zoom_out);
                } catch (Exception e) {
                    iv.startAnimation(zoom_out);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        zoom_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                iv.setVisibility(View.GONE);

                if (pause_for_database_creation) {

                    spinner = new ProgressDialog(Activity_Splash.this, R.style.MyCustomSpinnerTheme);
                    spinner.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                    spinner.setCancelable(false);
                    spinner.show();

                    Handler delayProcess = new Handler();
                    delayProcess.postDelayed(createNextActvity, 3000);


                } else if (!pause_for_database_creation) {
                    Intent mainActivity = new Intent(getBaseContext(), Activity_Main.class);
                    startActivity(mainActivity);
                    finish();
                    overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void executeError_from_database_creation() {
        pause_for_database_creation = false;

        SharedPreferences.Editor editor = firstAppRun.edit();
        editor.putBoolean("firstTime", false);
        editor.commit();
    }

    private Runnable createNextActvity = new Runnable() {
        @Override
        public void run() {
            spinner.dismiss();
            Intent mainActivity = new Intent(getBaseContext(), Activity_Main.class);
            startActivity(mainActivity);
            finish();
            overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
        }
    };
}
