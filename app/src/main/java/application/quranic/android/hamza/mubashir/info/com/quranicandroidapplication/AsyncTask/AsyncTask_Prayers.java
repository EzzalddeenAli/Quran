package application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.AsyncTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Model.PrayObjects;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.R;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Service.PrayersInterface;

public class AsyncTask_Prayers extends AsyncTask<String, String, String> {

    ProgressDialog spinner = null;
    Context context;
    Context getContext;
    String getPackageName;
    boolean mError = false;
    double longitude;
    double latitude;
    LayoutInflater layInflate = null;
    Typeface font_custom = null;
    private AlertDialog alertDialog;
    HttpURLConnection urlConnection;
    HttpURLConnection urlConnectionPray;
    private String mCuurentAddress;
    private boolean name = true;
    View view = null;
    PrayObjects[] prayObjects;
    private LinearLayout data_layout;
    private LinearLayout[] tv_linear;
    private TextView[] tv_day;
    private TextView[] tv_fajr;
    private TextView[] tv_dhuhr;
    private TextView[] tv_asr;
    private TextView[] tv_maghrib;
    private TextView[] tv_isha;
    private int TEXTSIZE;
    int z = 0;

    public PrayersInterface delegate = null;

    @Override
    protected void onPreExecute() {
        spinner = new ProgressDialog(getContext, R.style.MyCustomSpinnerTheme);
        spinner.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        spinner.setCancelable(false);
        spinner.show();

        data_layout = (LinearLayout) view.findViewById(R.id.layout_data);
    }

    @Override
    protected void onPostExecute(String s) {
        font_custom = Typeface.createFromAsset(getContext.getAssets(), "list_textviews.otf");

        if (!mError) {

            delegate.addName(mCuurentAddress);
            delegate.useOrNo(name);
            delegate.startFun(true);

            Handler delayHandler = new Handler();
            delayHandler.postDelayed(executeAfterComplete, 5);
        }
        else if (mError) {
            spinner.dismiss();

            final Animation pusher = AnimationUtils.loadAnimation(getContext, R.anim.buttons_anim_push_in);

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
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            getPrayerTimersArray();
            usersCityName();
        }
        catch (Exception e) {
            mError = true;
            name = false;
        }

        return null;
    }

    private void usersCityName() {

        JSONObject ret = getLocationInfo(latitude, longitude);

        JSONObject location;

        try {
            location = ret.getJSONArray("results").getJSONObject(2);
            mCuurentAddress = location.getString("formatted_address");
            //publishProgress(mCuurentAddress);
        }
        catch (JSONException e1) {
            name = false;
            delegate.useOrNo(false);
        }

    }

    public JSONObject getLocationInfo(double lat, double lng) {

        StringBuilder stringBuilder = new StringBuilder();

        try {
            URL url = new URL("http://maps.google.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&sensor=true");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            int line;
            while ((line = reader.read()) != -1) {
                stringBuilder.append((char) line);
            }

        } catch (MalformedURLException e) {
            name = false;
        } catch (IOException e) {
            name = false;
        } finally {
            urlConnection.disconnect();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        }
        catch (JSONException e) {
            name = false;
        }
        return jsonObject;
    }

    private void getPrayerTimersArray() {

        final Typeface font_tv = Typeface.createFromAsset(getContext.getAssets(), "list_textviews.otf");

        String fullTimeZone = TimeZone.getDefault().getID().trim();
        String[] timeZone = fullTimeZone.split("/");

        Calendar calendar = Calendar.getInstance();
        int thisYear = calendar.get(Calendar.YEAR);
        int thisMonth = calendar.get(Calendar.MONTH) + 1;

        JSONObject pray = getPrayerInfo(latitude, longitude, timeZone[0], timeZone[1], thisMonth, thisYear);

        try {

            int jsonArraySize = pray.getJSONArray("data").length();

            prayObjects = new PrayObjects[jsonArraySize];

            tv_linear = new LinearLayout[prayObjects.length];
            tv_day = new TextView[prayObjects.length];
            tv_fajr = new TextView[prayObjects.length];
            tv_dhuhr = new TextView[prayObjects.length];
            tv_asr = new TextView[prayObjects.length];
            tv_maghrib = new TextView[prayObjects.length];
            tv_isha = new TextView[prayObjects.length];

            for (int y = 0; y < jsonArraySize; y++) {
                String date = null;

                JSONObject timings = pray.getJSONArray("data").getJSONObject(y);
                JSONObject smallObj = timings.getJSONObject("timings");

                String[] splitFajr = smallObj.getString("Fajr").split("\\(");
                String[] splitDhuhr = smallObj.getString("Dhuhr").split("\\(");
                String[] splitAsr = smallObj.getString("Asr").split("\\(");
                String[] splitMaghrib = smallObj.getString("Maghrib").split("\\(");
                String[] splitIsha = smallObj.getString("Isha").split("\\(");

                if ((y + 1) < 10) {
                    date = "" + (y + 1);
                    date = String.format("%2s", date).replace(' ', '0');
                }
                else {
                    date = "" + (y + 1);
                }

                prayObjects[y] = new PrayObjects(date, splitFajr[0].trim(), splitDhuhr[0].trim(), splitAsr[0].trim(),
                        splitMaghrib[0].trim(), splitIsha[0].trim());

                tv_linear[y] = new LinearLayout(getContext);
                tv_day[y] = new TextView(getContext);
                tv_fajr[y] = new TextView(getContext);
                tv_dhuhr[y] = new TextView(getContext);
                tv_asr[y] = new TextView(getContext);
                tv_maghrib[y] = new TextView(getContext);
                tv_isha[y] = new TextView(getContext);

                linearLayoutFormat(tv_linear[y]);
                textViewFormat(tv_day[y], font_tv);
                textViewFormat(tv_fajr[y], font_tv);
                textViewFormat(tv_dhuhr[y], font_tv);
                textViewFormat(tv_asr[y], font_tv);
                textViewFormat(tv_maghrib[y], font_tv);
                textViewFormat(tv_isha[y], font_tv);

                tv_linear[y].addView(tv_day[y], 0);
                tv_linear[y].addView(tv_fajr[y], 1);
                tv_linear[y].addView(tv_dhuhr[y], 2);
                tv_linear[y].addView(tv_asr[y], 3);
                tv_linear[y].addView(tv_maghrib[y], 4);
                tv_linear[y].addView(tv_isha[y], 5);

            }

        }
        catch (Exception e) {
            mError = true;
        }


    }

    public JSONObject getPrayerInfo(double lat, double lng, String zone_one, String zone_two, int month, int year) {

        StringBuilder stringBuilder = new StringBuilder();

        try {
            URL url = new URL("http://api.aladhan.com/calendar?latitude=" + lat + "&longitude=" + lng + "&timezonestring=" + zone_one + "%2F" + zone_two + "&method=2&month=" + month + "&year=" + year);
            urlConnectionPray = (HttpURLConnection) url.openConnection();
            urlConnectionPray.connect();
            InputStream in = new BufferedInputStream(urlConnectionPray.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            int line;
            while ((line = reader.read()) != -1) {
                stringBuilder.append((char) line);
            }

        } catch (MalformedURLException e) {
            mError = true;
        } catch (IOException e) {
            mError = true;
        } finally {
            urlConnectionPray.disconnect();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        }
        catch (JSONException e) {
            mError = true;
        }
        return jsonObject;
    }

    private Runnable executeAfterComplete = new Runnable() {
        @Override
        public void run() {
            slowlyFillViews();
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

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLayInflate(LayoutInflater layInflate) {
        this.layInflate = layInflate;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void setTEXTSIZE(int TEXTSIZE) {
        this.TEXTSIZE = TEXTSIZE;
    }

    private LinearLayout linearLayoutFormat(LinearLayout lay_linear) {
        LinearLayout.LayoutParams layoutparms = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lay_linear.setLayoutParams(layoutparms);
        lay_linear.setOrientation(LinearLayout.HORIZONTAL);
        lay_linear.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);

        return lay_linear;
    }

    private TextView textViewFormat(TextView tv, Typeface font) {
        LinearLayout.LayoutParams layoutparm = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        tv.setLayoutParams(layoutparm);
        tv.setTypeface(font);
        tv.setTextSize(TEXTSIZE);
        tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        tv.setTextColor(ContextCompat.getColor(getContext, R.color.black));

        return tv;
    }

    private void slowlyFillViews() {

        if (z < prayObjects.length) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tv_day[z].setText(prayObjects[z].getPrayerDay());
                    tv_fajr[z].setText(prayObjects[z].getPrayerFajr());
                    tv_dhuhr[z].setText(prayObjects[z].getPrayerDhuhr());
                    tv_asr[z].setText(prayObjects[z].getPrayerAsr());
                    tv_maghrib[z].setText(prayObjects[z].getPrayerMaghrib());
                    tv_isha[z].setText(prayObjects[z].getPrayerIsha());

                    data_layout.addView(tv_linear[z], z);

                    ++z;

                    slowlyFillViews();
                }
            }, 10);
        }
        else {
            try {
                spinner.dismiss();
            }
            catch (Exception e) {
                spinner.cancel();
            }
        }
    }

}
