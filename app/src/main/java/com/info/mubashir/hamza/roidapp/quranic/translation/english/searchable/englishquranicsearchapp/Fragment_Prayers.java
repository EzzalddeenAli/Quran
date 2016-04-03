package com.info.mubashir.hamza.roidapp.quranic.translation.english.searchable.englishquranicsearchapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.novoda.merlin.Merlin;
import com.novoda.merlin.MerlinsBeard;
import com.novoda.merlin.NetworkStatus;
import com.novoda.merlin.registerable.bind.Bindable;
import com.novoda.merlin.registerable.connection.Connectable;
import com.novoda.merlin.registerable.disconnection.Disconnectable;

import java.util.Calendar;
import java.util.Locale;

public class Fragment_Prayers extends Fragment implements PrayersInterface {

    private View view;
    private Typeface font_custom;
    private Merlin merlin;
    private MerlinsBeard merlinsBeard;
    double longitude;
    double latitude;
    private AlertDialog alertDialog;
    private String mCuurentAddress;
    private boolean name = true;
    private int thisYear;
    private String nameMonth;
    private boolean reRun = false;
    private int x;
    AsyncTask_Prayers grabPrayers_records = null;
    private int TEXTSIZE;
    Animation pushed_in;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                thisYear = calendar.get(Calendar.YEAR);
                nameMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
            }
        }).start();

        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            TEXTSIZE = 11;
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            TEXTSIZE = 14;
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            TEXTSIZE = 16;
        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            TEXTSIZE = 18;
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.prayers_fragment_layout, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        new Thread(new Runnable() {
            @Override
            public void run() {

                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                font_custom = Typeface.createFromAsset(getActivity().getAssets(), "list_textviews.otf");

                pushed_in = AnimationUtils.loadAnimation(getActivity(), R.anim.buttons_anim_push_in);

                final TextView header_name = (TextView) view.findViewById(R.id.heading_name);
                final Spanned name_bold = Html.fromHtml("<b><i>Prayer Timings</i></b>");
                header_name.setTypeface(font_custom);
                final TextView date_text_view = (TextView) view.findViewById(R.id.date_prayers);
                final Spanned date_bold = Html.fromHtml("<b>" + nameMonth + " - " + thisYear + "</b>");
                date_text_view.setTypeface(font_custom);

                int[] heading_text = new int[] {R.id.prayers_date, R.id.prayers_fajr, R.id.prayers_dhuhr, R.id.prayers_asr,
                        R.id.prayers_maghrib, R.id.prayers_isha};

                TextView[] headings_text_view = new TextView[5];

                for (int x = 0; x < 5; x++) {
                    headings_text_view[x] = (TextView) view.findViewById(heading_text[x]);
                    headings_text_view[x].setTypeface(font_custom);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        header_name.setText(name_bold);
                        date_text_view.setText(date_bold);

                    }
                });
            }
        }).start();


        //check for wifi connection here now
        merlin = new Merlin.Builder().withAllCallbacks().build(getActivity());

        merlinsBeard = MerlinsBeard.from(getActivity());

        merlin.registerConnectable(new Connectable() {
            @Override
            public void onConnect() {

                if (!reRun) {
                    GPSTracker gpsTracker = new GPSTracker(getActivity());

                    if (gpsTracker.isGPSEnabled) {
                        longitude = gpsTracker.getLongitude();
                        latitude = gpsTracker.getLatitude();
                        createAsyncData();
                    } else if (gpsTracker.isNetworkEnabled) {
                        longitude = gpsTracker.getLongitude();
                        latitude = gpsTracker.getLatitude();
                        createAsyncData();
                    } else if (gpsTracker.canGetLocation) {
                        longitude = gpsTracker.getLongitude();
                        latitude = gpsTracker.getLatitude();
                        createAsyncData();
                    }
                }
            }
        });

        merlin.registerDisconnectable(new Disconnectable() {
            @Override
            public void onDisconnect() {
                final Animation pusher = AnimationUtils.loadAnimation(getActivity(), R.anim.buttons_anim_push_in);

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());

                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                View dialogView = layoutInflater.inflate(R.layout.dialog_custom_demo, null);
                alertBuilder.setView(dialogView);
                alertBuilder.setCancelable(false);

                TextView demo_text = (TextView) dialogView.findViewById(R.id.demo_text_view);
                final Button demo_button = (Button) dialogView.findViewById(R.id.demo_button_view);

                demo_text.setText("Opps, no internet connection!");
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
        });

        merlin.registerBindable(new Bindable() {
            @Override
            public void onBind(NetworkStatus networkStatus) {

                if (merlinsBeard.isConnected() || merlinsBeard.isConnectedToWifi() || merlinsBeard.isConnectedToMobileNetwork()) {
                    GPSTracker gpsTracker = new GPSTracker(getActivity());

                    if (gpsTracker.isGPSEnabled) {
                        longitude = gpsTracker.getLongitude();
                        latitude = gpsTracker.getLatitude();
                        createAsyncData();
                    } else if (gpsTracker.isNetworkEnabled) {
                        longitude = gpsTracker.getLongitude();
                        latitude = gpsTracker.getLatitude();
                        createAsyncData();
                    } else if (gpsTracker.canGetLocation) {
                        longitude = gpsTracker.getLongitude();
                        latitude = gpsTracker.getLatitude();
                        createAsyncData();
                    }

                }
            }
        });

        return view;
    }

    private void createAsyncData() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                grabPrayers_records = new AsyncTask_Prayers();
                grabPrayers_records.setContextSpinner(getActivity());
                grabPrayers_records.setContext(getContext());
                grabPrayers_records.setLatitude(latitude);
                grabPrayers_records.setLongitude(longitude);
                grabPrayers_records.setView(view);
                grabPrayers_records.delegate = Fragment_Prayers.this;
                grabPrayers_records.setTEXTSIZE(TEXTSIZE);
                grabPrayers_records.setLayInflate(getActivity().getLayoutInflater());
                grabPrayers_records.setGetPackageName(getActivity().getPackageName());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        grabPrayers_records.execute("Start pray execution");
                    }
                });
            }
        }).start();
    }

    private void createResultViews() {
        grabPrayers_records = null;

        TextView location_text_view = (TextView) view.findViewById(R.id.city_name);

        if (name) {
            location_text_view.setText(mCuurentAddress);
            location_text_view.setTypeface(font_custom);
        }
        else if (!name) {
            location_text_view.setVisibility(View.GONE);
        }

    }

    @Override
    public void onResume() {
        merlin.bind();
        super.onResume();
    }

    @Override
    public void onPause() {

        KeyboardGone removeKey = new KeyboardGone(getActivity());
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        merlin.unbind();

        super.onPause();
    }

    @Override
    public void addName(String name) {
        this.mCuurentAddress = name;
    }

    @Override
    public void useOrNo(boolean value) {
        this.name = value;
    }

    @Override
    public void startFun(boolean value_second) {
        this.reRun = value_second;
        createResultViews();
    }
}
