package com.info.mubashir.hamza.roidapp.quranic.translation.english.searchable.englishquranicsearchapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.vending.expansion.zipfile.APKExpansionSupport;
import com.android.vending.expansion.zipfile.ZipResourceFile;
import com.google.android.vending.expansion.downloader.Helpers;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;

public class Fragment_Bookmarks extends Fragment implements Chapters_Array_Creation {

    private View view;

    private ListView chapters_list;
    private boolean SD_STORAGE_AVAILABLE_OR_NOT;
    private AlertDialog alertDialog;
    int EXP_NUM;
    Bookmarks_ListView chap_listview = null;
    Async_task_fragment_chapters_bookmarks.ChaptersListObjects[] chaptersList;
    int lengther;
    TextView results_textview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences firstAppRunEXPNum = PreferenceManager.getDefaultSharedPreferences(getActivity());
        EXP_NUM = firstAppRunEXPNum.getInt("EXP_NUM", 4);

        String obbFileArray[] = CheckOBBFile.getAPKExpansionFiles(getActivity(), EXP_NUM, 0);

        if (obbFileArray.length > 0) {
            //File read correctly
            SD_STORAGE_AVAILABLE_OR_NOT = true;
            final Async_task_fragment_chapters_bookmarks grab_arrays = new Async_task_fragment_chapters_bookmarks();
            grab_arrays.setContextSpinner(getActivity());
            grab_arrays.setContext(getActivity());
            grab_arrays.setDetermine_get_method(true);
            grab_arrays.setLayInflate(getActivity().getLayoutInflater());
            grab_arrays.chapters_delegates = this;
            grab_arrays.execute("Start array creation");

        }
        else if (obbFileArray.length == 0) {
            //Unable to read file do not proceed until sd-card file is read
            SD_STORAGE_AVAILABLE_OR_NOT = false;
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bookmark_search_chapters_layout, container, false);

        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        if (!SD_STORAGE_AVAILABLE_OR_NOT) {
            storageERROR();
        }

        return view;
    }

    private void createViewsNow() {
        final Typeface text_view_font = Typeface.createFromAsset(getActivity().getAssets(), "list_textviews.otf");

        TextView not_needed_textview = (TextView) view.findViewById(R.id.searched_words);
        not_needed_textview.setVisibility(View.GONE);

        TextView name_menu = (TextView) view.findViewById(R.id.view_name);
        Spanned name_bold = Html.fromHtml("<b><i>Bookmarks</i></b>");
        name_menu.setText(name_bold);
        name_menu.setTypeface(text_view_font);

        results_textview = (TextView) view.findViewById(R.id.number_of_results);
        Spanned results_bold = Html.fromHtml("Total saved verses: <b>" + chaptersList.length + "</b>");
        results_textview.setText(results_bold);
        results_textview.setTypeface(text_view_font);

        if (chaptersList.length != 0) {

            ArrayList<Async_task_fragment_chapters_bookmarks.ChaptersListObjects> aryList = new ArrayList<Async_task_fragment_chapters_bookmarks.ChaptersListObjects>();

            for (int x =0; x < chaptersList.length; x++) {
                aryList.add(chaptersList[x]);
            }

            chap_listview = new Bookmarks_ListView(getActivity(), getActivity().getLayoutInflater(), aryList, EXP_NUM, view);

            ListView chap_list = (ListView) view.findViewById(R.id.listView_chapters_bookmarks_search);

            chap_list.setAdapter(chap_listview);
        }
        else {
            TextView empty_text = (TextView) view.findViewById(R.id.empty_bookmarks);
            empty_text.setTypeface(text_view_font);
            empty_text.setText("Bookmarks empty...");
            empty_text.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void setChaptersList(Async_task_fragment_chapters_bookmarks.ChaptersListObjects[] chaptersList) {
        this.chaptersList = chaptersList;
    }


    @Override
    public void setEndResult(boolean value) {
        createViewsNow();
    }

    private void storageERROR() {
        final Animation pusher = AnimationUtils.loadAnimation(getActivity(), R.anim.buttons_anim_push_in);
        Typeface font_custom = Typeface.createFromAsset(getActivity().getAssets(), "list_textviews.otf");

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());

        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_custom_demo, null);
        alertBuilder.setView(dialogView);
        alertBuilder.setCancelable(false);

        TextView demo_text = (TextView) dialogView.findViewById(R.id.demo_text_view);
        final Button demo_button = (Button) dialogView.findViewById(R.id.demo_button_view);

        demo_text.setText("Unable to read storage, insert SD-Card and restart App!");
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

    @Override
    public void onPause() {

        KeyboardGone removeKey = new KeyboardGone(getActivity());
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        super.onPause();
    }

    @Override
    public void onDestroy() {

        if (chap_listview != null) {
            chap_listview.turnMediaOFF();
        }

        super.onDestroy();
    }
}
