package application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;

import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Adapter.ListViewAdapter_Right;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.AsyncTask.Async_task_Main_List;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Fragment.Fragment_Bookmarks;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Fragment.Fragment_Chapters;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Fragment.Fragment_Empty;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Fragment.Fragment_Notes;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Fragment.Fragment_Prayers;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Fragment.Fragment_Search;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Fragment.Fragment_Search_Tips;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Model.RightObjects;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.R;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Service.Array_Creation_List_Views;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Service.FlipAnimation;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Service.KeyboardGone;

public class Activity_Main extends AppCompatActivity implements Array_Creation_List_Views {

    private DrawerLayout mDrawerlayout;
    private ListView mDrawerList_Right;
    private ListViewAdapter_Right right_adapter;
    private LinearLayout mDrawer_left_linear_layout, mDrawer_right_linear_layout;

    private MediaPlayer mp;
    private Animation pushed_in;
    private View v_inflate = null;

    private Typeface chapters_info_font;

    private boolean demoListener;
    private AlertDialog alertDialog = null;
    private RelativeLayout back_dim_layout = null;
    private FrameLayout frame_frag_layout_front = null;
    private FrameLayout frame_frag_layout_back = null;

    private ActionBarDrawerToggle mDrawerToggle;
    private float lastTranslate = 0.0f;

    private boolean fliperView = true; //true = front, false = back
    private LinearLayout wholeView = null;
    private int frag_pos = 0;
    private int chapter_select_position = -1;

    private RightObjects[] rightObjectses;
    private Async_task_Main_List grab_arrays;

    private ImageButton imgLeftMenu, search;
    private TextView appNameHeader;
    private TextView hadith;
    private LinearLayout bookmarks_button, chapters_button, notes_button, prayers_button;
    private RelativeLayout search_button_and_app_name, search_bar_and_toggle_button;

    private String hadiths = null;
    private EditText editText;
    private ToggleButton toggleButton;
    private int opt = 0; //0 = words 1 = sentence
    private String searching = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v_inflate = inflator.inflate(R.layout.header, null);

        KeyboardGone removeKey = new KeyboardGone(Activity_Main.this);
        Activity_Main.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        chapters_info_font = Typeface.createFromAsset(getAssets(), "list_textviews.otf");

        pushed_in = AnimationUtils.loadAnimation(Activity_Main.this, R.anim.buttons_anim_push_in);
        mDrawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        final Typeface chapters_title_font = Typeface.createFromAsset(getAssets(), "chapters_title.otf");
        final TextView chap_tv = (TextView) findViewById(R.id.chap_title_textview);
        chap_tv.setTypeface(chapters_title_font);

        hadith = (TextView) findViewById(R.id.hadiths);
        hadith.setTypeface(chapters_info_font);

        frame_frag_layout_front = (FrameLayout) findViewById(R.id.Frame_Layout_front);
        frame_frag_layout_back = (FrameLayout) findViewById(R.id.Frame_Layout_back);

        mDrawerList_Right = (ListView) findViewById(R.id.drawer_list_right);

        mDrawerList_Right.setSelector(R.drawable.chapters_list_background_colors);

        bookmarks_button = (LinearLayout) findViewById(R.id.bookmarks_btn);
        chapters_button = (LinearLayout) findViewById(R.id.chapters_btn);
        bookmarks_button.setSelected(true);
        bookmarks_button.setPressed(true);
        bookmarks_button.setActivated(true);
        notes_button = (LinearLayout) findViewById(R.id.notes_btn);
        prayers_button = (LinearLayout) findViewById(R.id.prayers_btn);

        bookmarks_button.setOnClickListener(btnsClick);
        chapters_button.setOnClickListener(btnsClick);
        notes_button.setOnClickListener(btnsClick);
        prayers_button.setOnClickListener(btnsClick);

        wholeView = (LinearLayout) findViewById(R.id.wholeFrameView);

        mDrawer_left_linear_layout = (LinearLayout) findViewById(R.id.drawer_list_left_linear_layout);
        mDrawer_right_linear_layout = (LinearLayout) findViewById(R.id.drawer_list_right_linear_layout);

        imgLeftMenu = (ImageButton) v_inflate.findViewById(R.id.imgLeftMenu);
        search = (ImageButton) v_inflate.findViewById(R.id.search_icon);

        search_button_and_app_name = (RelativeLayout) v_inflate.findViewById(R.id.menu_with_app_name);
        search_bar_and_toggle_button = (RelativeLayout) v_inflate.findViewById(R.id.layout_search_gone_visible);

        appNameHeader = (TextView) v_inflate.findViewById(R.id.txtLeftMenuAppName);
        appNameHeader.setTypeface(chapters_info_font);

        editText = (EditText) v_inflate.findViewById(R.id.searchTextInput);
        toggleButton = (ToggleButton) v_inflate.findViewById(R.id.toggle_button);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(v_inflate);

        mDrawerToggle = new ActionBarDrawerToggle(Activity_Main.this, mDrawerlayout, R.drawable.background, R.string.acc_drawer_open, R.string.acc_drawer_close) {

            @SuppressLint("NewApi")
            public void onDrawerSlide(View drawerView, float slideOffset) {

                InputMethodManager inputMethodManager = (InputMethodManager) Activity_Main.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(Activity_Main.this.getCurrentFocus().getWindowToken(), 0);

                if (search_bar_and_toggle_button.getVisibility() == View.VISIBLE) {
                    animationComingName();
                }

                if (drawerView.getId() == R.id.drawer_list_left_linear_layout) {

                    float moveFactor = (mDrawer_left_linear_layout.getWidth() * slideOffset);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

                        wholeView.setTranslationX(moveFactor);

                    } else {
                        TranslateAnimation anim = new TranslateAnimation(lastTranslate, moveFactor, 0.0f, 0.0f);
                        anim.setDuration(0);
                        anim.setFillAfter(true);
                        wholeView.startAnimation(anim);

                        lastTranslate = moveFactor;
                    }
                }

            }
        };

        mDrawerlayout.setDrawerListener(mDrawerToggle);

        mp = MediaPlayer.create(this, R.raw.button);

        grab_arrays = new Async_task_Main_List();
        grab_arrays.setContextSpinner(Activity_Main.this);
        grab_arrays.setContext(getApplicationContext());
        grab_arrays.setGetPackageName(getPackageName());
        grab_arrays.setLayInflate(getLayoutInflater());
        grab_arrays.delegates = Activity_Main.this;
        grab_arrays.execute("Start array creation");

        final SharedPreferences firstAppRun = PreferenceManager.getDefaultSharedPreferences(Activity_Main.this);
        demoListener = firstAppRun.getBoolean("firstTimeDemo", false);

        if (!firstAppRun.getBoolean("firstTimeDemo", false)) {

            back_dim_layout = (RelativeLayout) findViewById(R.id.bac_dim_layout);
            back_dim_layout.setVisibility(View.VISIBLE);
            int back = getResources().getIdentifier("frame_dim_background", "drawable", getPackageName());
            frame_frag_layout_front.setBackgroundResource(back);
            frame_frag_layout_back.setBackgroundResource(back);

            toggleButton.setEnabled(false);
            editText.setEnabled(false);
            search.setEnabled(false);

            toggleButton.setSelected(false);
            editText.setSelected(false);
            search.setSelected(false);

            toggleButton.setPressed(false);
            editText.setPressed(false);
            search.setPressed(false);

            toggleButton.setActivated(false);
            editText.setActivated(false);
            search.setActivated(false);

            mDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, mDrawer_left_linear_layout);
            mDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, mDrawer_right_linear_layout);

            showArrowsDemo();
        }

        if (demoListener) {

            imgLeftMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    KeyboardGone removeKey = new KeyboardGone(Activity_Main.this);
                    Activity_Main.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                    if (mDrawerlayout.isDrawerOpen(mDrawer_right_linear_layout)) {

                        mDrawerlayout.closeDrawer(mDrawer_right_linear_layout);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mDrawerlayout.openDrawer(mDrawer_left_linear_layout);
                            }
                        }, 500);

                    } else {
                        if (mDrawerlayout.isDrawerOpen(mDrawer_left_linear_layout)) {
                            mDrawerlayout.closeDrawer(mDrawer_left_linear_layout);
                        } else if (!mDrawerlayout.isDrawerOpen(mDrawer_left_linear_layout)) {
                            mDrawerlayout.openDrawer(mDrawer_left_linear_layout);
                        }
                    }
                }
            });

            toggleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    v.startAnimation(pushed_in);
                    startPlayingButtonSound();

                    if (toggleButton.isChecked()){
                        //Button is ON
                        opt = 1;
                    }
                    else {
                        //Button is OFF
                        opt = 0;
                    }

                }
            });

            editText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                        searching = editText.getText().toString().trim();

                        if (!searching.equals("")) {

                            final View view = v;

                            frag_pos = 2;

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    onCardFlip(view);
                                }
                            }, 250);

                        }
                    }
                    return false;
                }
            });


            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(pushed_in);
                    startPlayingButtonSound();
                    final View view = v;

                    LinearLayout[] layAry = {prayers_button, notes_button, chapters_button, bookmarks_button};
                    for (int x = 0; x < layAry.length; x++) {
                        layAry[x].setSelected(false);
                        layAry[x].setPressed(false);
                        layAry[x].setActivated(false);
                    }

                    mDrawerList_Right.clearChoices();
                    mDrawerList_Right.clearFocus();

                    KeyboardGone removeKey = new KeyboardGone(Activity_Main.this);
                    Activity_Main.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                    if (mDrawerlayout.isDrawerOpen(mDrawer_left_linear_layout)) {
                        mDrawerlayout.closeDrawer(mDrawer_left_linear_layout);
                    } else if (mDrawerlayout.isDrawerOpen(mDrawer_right_linear_layout)) {
                        mDrawerlayout.closeDrawer(mDrawer_right_linear_layout);
                    }

                    animationLeavingName();

                    frag_pos = 5;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onCardFlip(view);
                        }
                    }, 250);
                }
            });

            mDrawerList_Right.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final View v = view;
                    view.startAnimation(pushed_in);
                    startPlayingButtonSound();
                    view.setSelected(true);
                    mDrawerlayout.closeDrawer(mDrawer_right_linear_layout);

                    LinearLayout[] layAry = {prayers_button, notes_button, chapters_button, bookmarks_button};
                    for (int x = 0; x < layAry.length; x++) {
                        layAry[x].setSelected(false);
                        layAry[x].setPressed(false);
                        layAry[x].setActivated(false);
                    }

                    layAry[2].setSelected(true);
                    layAry[2].setPressed(true);
                    layAry[2].setActivated(true);

                    KeyboardGone removeKey = new KeyboardGone(Activity_Main.this);
                    Activity_Main.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                    frag_pos = 1;

                    chapter_select_position = position;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onCardFlip(v);
                        }
                    }, 250);
                }
            });
        }
    }

    private View.OnClickListener btnsClick = new View.OnClickListener() {
        public void onClick(View v) {

            final View view = v;
            view.startAnimation(pushed_in);
            startPlayingButtonSound();

            LinearLayout[] layAry = {prayers_button, notes_button, chapters_button, bookmarks_button};
            for (int x = 0; x < layAry.length; x++) {
                layAry[x].setSelected(false);
                layAry[x].setPressed(false);
                layAry[x].setActivated(false);
            }

            v.setSelected(true);
            v.setPressed(true);
            v.setActivated(true);

            mDrawerlayout.closeDrawer(mDrawer_left_linear_layout);

            KeyboardGone removeKey = new KeyboardGone(Activity_Main.this);
            Activity_Main.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            switch (v.getId()) {
                case R.id.bookmarks_btn:

                    frag_pos = 0;

                    mDrawerList_Right.clearChoices();
                    mDrawerList_Right.clearFocus();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onCardFlip(view);
                        }
                    }, 250);

                    break;
                case R.id.chapters_btn:

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mDrawerlayout.openDrawer(mDrawer_right_linear_layout);
                        }
                    }, 300);

                    break;
                case R.id.notes_btn:

                    frag_pos = 4;

                    mDrawerList_Right.clearChoices();
                    mDrawerList_Right.clearFocus();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onCardFlip(view);
                        }
                    }, 250);

                    break;
                case R.id.prayers_btn:

                    frag_pos = 3;

                    mDrawerList_Right.clearChoices();
                    mDrawerList_Right.clearFocus();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onCardFlip(view);
                        }
                    }, 250);

                    break;
                default:
                    break;
            }
            }
        };

    private void animationLeavingName() {
        final Animation fade_out = AnimationUtils.loadAnimation(getBaseContext(), R.anim.button_fade_out);
        final Animation fade_in = AnimationUtils.loadAnimation(getBaseContext(), R.anim.button_fade_in);

        search_button_and_app_name.startAnimation(fade_out);
        fade_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                search_button_and_app_name.setVisibility(View.GONE);
                search_bar_and_toggle_button.startAnimation(fade_in);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fade_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                search_bar_and_toggle_button.setVisibility(View.GONE);
                editText.setFocusableInTouchMode(true);
                editText.setFocusable(true);
                editText.setEnabled(true);
                editText.setClickable(true);
                editText.setActivated(true);
                toggleButton.setEnabled(true);
                toggleButton.setActivated(true);
                toggleButton.setFocusable(true);
                toggleButton.setFocusableInTouchMode(true);
                toggleButton.setEnabled(true);
                toggleButton.setClickable(true);
                search_bar_and_toggle_button.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                search_bar_and_toggle_button.setVisibility(View.GONE);
                editText.setFocusableInTouchMode(true);
                editText.setFocusable(true);
                editText.setEnabled(true);
                editText.setClickable(true);
                editText.setActivated(true);
                toggleButton.setEnabled(true);
                toggleButton.setActivated(true);
                toggleButton.setFocusable(true);
                toggleButton.setFocusableInTouchMode(true);
                toggleButton.setEnabled(true);
                toggleButton.setClickable(true);
                search_bar_and_toggle_button.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void animationComingName() {
        final Animation fade_out = AnimationUtils.loadAnimation(getBaseContext(), R.anim.button_fade_out);
        final Animation fade_in = AnimationUtils.loadAnimation(getBaseContext(), R.anim.button_fade_in);

        search_bar_and_toggle_button.startAnimation(fade_out);
        fade_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                search_bar_and_toggle_button.setVisibility(View.GONE);
                editText.setFocusableInTouchMode(false);
                editText.setFocusable(false);
                editText.setEnabled(false);
                editText.setClickable(false);
                editText.setActivated(false);
                toggleButton.setEnabled(false);
                toggleButton.setActivated(false);
                toggleButton.setFocusable(false);
                toggleButton.setFocusableInTouchMode(false);
                toggleButton.setEnabled(false);
                toggleButton.setClickable(false);
                search_button_and_app_name.startAnimation(fade_in);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fade_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                search_button_and_app_name.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                search_button_and_app_name.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void startPlayingButtonSound() {
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (mediaPlayer == mp) {
                    mp.start();
                }
            }
        });

    }

    private void CreateListView() {

        grab_arrays = null;

        int color_font = ContextCompat.getColor(this, R.color.white_off);

        right_adapter = new ListViewAdapter_Right(getBaseContext(), getLayoutInflater(), rightObjectses, chapters_info_font, color_font);
        mDrawerList_Right.setAdapter(right_adapter);

        hadith.setText(hadiths);

    }

    @Override
    public void rightObjects(RightObjects[] r_obj) {
        this.rightObjectses = r_obj;
    }

    @Override
    public void hadithsString(String hadiths) {
        this.hadiths = hadiths;
    }

    @Override
    public void startListCreationFun(boolean value) {
        CreateListView();
        startFragmentViews();
    }

    private void showArrowsDemo() {

        //First the dialog will display itself
        final Animation pusher = AnimationUtils.loadAnimation(this, R.anim.buttons_anim_push_in);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Activity_Main.this);
        LayoutInflater layInflate = this.getLayoutInflater();

        View dialogView = layInflate.inflate(R.layout.dialog_custom_demo, null);
        alertBuilder.setView(dialogView);
        alertBuilder.setCancelable(false);

        TextView demo_text = (TextView) dialogView.findViewById(R.id.demo_text_view);
        final Button demo_button = (Button) dialogView.findViewById(R.id.demo_button_view);

        demo_text.setText(R.string.quick_demo_string);
        demo_button.setText("BEGIN");
        demo_button.setTypeface(chapters_info_font);
        demo_text.setTypeface(chapters_info_font);

        alertDialog = alertBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        alertDialog.show();

        demo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demo_button.startAnimation(pusher);
                v.startAnimation(pusher);
                startPlayingButtonSound();
                alertDialog.dismiss();

                addDemoDialogBookMarks();
            }
        });

    }

    private void addDemoDialogBookMarks() {
        ToolTipRelativeLayout toolTipRelativeLayout = (ToolTipRelativeLayout) findViewById(R.id.activity_main_tooltipRelativeLayout);
        toolTipRelativeLayout.setVisibility(View.VISIBLE);

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view_demo_book = layoutInflater.inflate(R.layout.demo_bookmarks_tab, null);

        TextView text_book_demo = (TextView) view_demo_book.findViewById(R.id.demo_string_replace_bookmarks_tab);
        text_book_demo.setTypeface(chapters_info_font);

        ToolTip toolTip = new ToolTip()
                .withColor(ContextCompat.getColor(this, R.color.dim_color))
                .withContentView(view_demo_book)
                .withShadow()
                .withAnimationType(ToolTip.AnimationType.FROM_TOP);
        ToolTipView myToolTipView = toolTipRelativeLayout.showToolTipForView(toolTip, findViewById(R.id.search_icon));
        myToolTipView.setClickable(false);

        myToolTipView.animate()
                .alpha(0.0f)
                .setDuration(2000)
                .setStartDelay(2500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animationLeavingName();
            }
        }, 2000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addDemoDialogMenuTab();
            }
        }, 4300);
    }

    private void addDemoDialogMenuTab() {
        ToolTipRelativeLayout toolTipRelativeLayout = (ToolTipRelativeLayout) findViewById(R.id.activity_main_tooltipRelativeLayout);
        toolTipRelativeLayout.setVisibility(View.VISIBLE);

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view_demo_book = layoutInflater.inflate(R.layout.demo_menu_tab, null);

        TextView text_menu_demo = (TextView) view_demo_book.findViewById(R.id.demo_string_replace_menu_tab);
        text_menu_demo.setTypeface(chapters_info_font);

        ToolTip toolTip = new ToolTip()
                .withColor(ContextCompat.getColor(this, R.color.dim_color))
                .withContentView(view_demo_book)
                .withShadow()
                .withAnimationType(ToolTip.AnimationType.FROM_TOP);
        ToolTipView myToolTipView = toolTipRelativeLayout.showToolTipForView(toolTip, findViewById(R.id.imgLeftMenu));
        myToolTipView.setClickable(false);

        myToolTipView.animate()
                .alpha(0.0f)
                .setDuration(2000)
                .setStartDelay(2500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addDemoRightMenuList();
            }
        }, 4300);
    }

    private void addDemoRightMenuList() {

        demoToastCustom(R.string.demo_menu_chapters_list, 1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN, mDrawer_right_linear_layout);
            }
        }, 4300);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mDrawerList_Right.smoothScrollToPosition(114);
            }
        }, 5000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, mDrawer_right_linear_layout);
            }
        }, 9200);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                demoToastCustom(R.string.demo_menu_main_list, 1);
            }
        }, 10700);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN, mDrawer_left_linear_layout);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //toggleButton.setChecked(true);
                    }
                }, 12500);
            }
        }, 15700);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                demoToastCustom(R.string.demo_search_input, 2);
            }
        }, 17500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, mDrawer_left_linear_layout);
            }
        }, 26500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                demoEndMessage();
            }
        }, 28000);

    }

    private void demoToastCustom(int ID_STRING, int length_toaster) {
        LayoutInflater demoToastInflate = this.getLayoutInflater();
        View toastView = demoToastInflate.inflate(R.layout.demo_layout_toast, null);

        TextView toastText = (TextView) toastView.findViewById(R.id.demo_string_replace);
        toastText.setText(ID_STRING);
        toastText.setTypeface(chapters_info_font);

        for (int x = 0; x < length_toaster; x++) {
            Toast toast = new Toast(Activity_Main.this);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(toastView);
            toast.show();
        }
    }

    private void demoEndMessage() {
        LayoutInflater demoToastInflate = this.getLayoutInflater();
        View toastView = demoToastInflate.inflate(R.layout.demo_layout_end, null);

        TextView toastText = (TextView) toastView.findViewById(R.id.demo_string_replace_ending);
        toastText.setTypeface(chapters_info_font);

        Toast toast = new Toast(Activity_Main.this);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastView);
        toast.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences firstAppRunEnding = PreferenceManager.getDefaultSharedPreferences(Activity_Main.this);
                SharedPreferences.Editor editor = firstAppRunEnding.edit();
                editor.putBoolean("firstTimeDemo", true);
                editor.commit();

                Intent intent = getIntent();
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
            }
        }, 4500);

    }

    public void onCardFlip(View view) {
        if (fliperView) {
            fliperView = false;
            frame_frag_layout_front.removeAllViews();
        }
        else if (!fliperView) {
            fliperView = true;
            frame_frag_layout_back.removeAllViews();
        }

        flipCard();
    }

    private void flipCard() {

        View rootLayout = findViewById(R.id.main_activity_root);
        View cardFace = findViewById(R.id.main_activity_card_front);
        View cardBack = findViewById(R.id.main_activity_card_back);

        FlipAnimation flipAnimation = new FlipAnimation(cardFace, cardBack);

        if (cardFace.getVisibility() == View.GONE) {
            flipAnimation.reverse();
        }

        rootLayout.startAnimation(flipAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startFragmentViews();
            }
        }, 400);
    }

    private void startFragmentViews() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Fragment fragment = null;

                Bundle fragData = new Bundle();

                if (frag_pos == 1) {
                    fragData.putBoolean("back_or_front", fliperView);
                    fragData.putString("chapter", rightObjectses[chapter_select_position].getChap());
                    fragData.putInt("start", Integer.parseInt(rightObjectses[chapter_select_position].getStart()));
                    fragData.putInt("end", Integer.parseInt(rightObjectses[chapter_select_position].getEnd()));
                }

                if (frag_pos == 2) {
                    fragData.putString("search", searching);
                    fragData.putInt("option", opt);
                }

                switch (frag_pos) {
                    case 0:
                        fragment = new Fragment_Bookmarks();
                        break;
                    case 1:
                        fragment = new Fragment_Chapters();
                        break;
                    case 2:
                        fragment = new Fragment_Search();
                        break;
                    case 3:
                        fragment = new Fragment_Prayers();
                        break;
                    case 4:
                        fragment = new Fragment_Notes();
                        break;
                    case 5:
                        fragment = new Fragment_Search_Tips();
                        break;
                    default:
                        fragment = new Fragment_Bookmarks();
                        break;
                }

                if (fragment != null) {

                    if (frag_pos == 1) {
                        fragment.setArguments(fragData);
                    }

                    if (frag_pos == 2) {
                        fragment.setArguments(fragData);
                    }

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    if (fliperView) {
                        fragmentTransaction.replace(R.id.Frame_Layout_front, fragment);

                        Fragment fragEmpty = new Fragment_Empty();
                        FragmentManager fmEmpty = getSupportFragmentManager();
                        FragmentTransaction ftEMPTY = fmEmpty.beginTransaction();
                        ftEMPTY.replace(R.id.Frame_Layout_back, fragEmpty);
                        ftEMPTY.commit();

                    } else if (!fliperView) {
                        fragmentTransaction.replace(R.id.Frame_Layout_back, fragment);

                        Fragment fragEmpty = new Fragment_Empty();
                        FragmentManager fmEmpty = getSupportFragmentManager();
                        FragmentTransaction ftEMPTY = fmEmpty.beginTransaction();
                        ftEMPTY.replace(R.id.Frame_Layout_front, fragEmpty);
                        ftEMPTY.commit();

                    }

                    fragmentTransaction.commit();

                }
            }
        }, 500);
    }

    public void onBackPressed(){

        final Animation pusher = AnimationUtils.loadAnimation(getBaseContext(), R.anim.buttons_anim_push_in);
        Typeface font_custom = Typeface.createFromAsset(getAssets(), "list_textviews.otf");

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Activity_Main.this);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_exit_app, null);
        alertBuilder.setView(dialogView);
        alertBuilder.setCancelable(false);

        TextView demo_text = (TextView) dialogView.findViewById(R.id.exit_text_view);
        final Button demo_button = (Button) dialogView.findViewById(R.id.exit_button_view);
        final Button demo_button_cancel = (Button) dialogView.findViewById(R.id.cancel_button_view);

        demo_text.setText("EXIT the App?");
        demo_button.setText("OK");
        demo_button_cancel.setText("Cancel");
        demo_button.setTypeface(font_custom);
        demo_button_cancel.setTypeface(font_custom);
        demo_text.setTypeface(font_custom);

        alertDialog = alertBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        alertDialog.show();

        demo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demo_button.startAnimation(pusher);
                v.startAnimation(pusher);
                finish();
            }
        });

        demo_button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demo_button.startAnimation(pusher);
                v.startAnimation(pusher);
                alertDialog.dismiss();
            }
        });

    }
}
