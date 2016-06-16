package com.info.mubashir.hamza.roidapp.quranic.translation.english.searchable.englishquranicsearchapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Fragment_Search extends Fragment implements Search_delegates {

    private View view;
    private boolean SD_STORAGE_AVAILABLE_OR_NOT;
    private AlertDialog alertDialog;
    int EXP_NUM;
    int option;
    String searching;
    Search_ListView search_listView = null;
    List<String> searchList;
    Async_Search.SearchListObjects[] searchListObjectses = null;
    Spanned search_bold;;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        searching = bundle.getString("search", null);
        option = bundle.getInt("option", 0);

        SharedPreferences firstAppRunEXPNum = PreferenceManager.getDefaultSharedPreferences(getActivity());
        EXP_NUM = firstAppRunEXPNum.getInt("EXP_NUM", 4);

        String obbFileArray[] = CheckOBBFile.getAPKExpansionFiles(getActivity(), EXP_NUM, 0);

        if (obbFileArray.length > 0) {
            //File read correctly
            SD_STORAGE_AVAILABLE_OR_NOT = true;

            if (option == 0) {
                String[] searchTrim = searching.trim().split(" ");
                searchList = new ArrayList<String>(Arrays.asList(searchTrim));
            }
            else if (option == 1) {
                searchList = new ArrayList<String>(Arrays.asList(searching.trim()));
            }

            final Async_Search async_search = new Async_Search();
            async_search.setSearchList(searchList);
            async_search.setContextSpinner(getActivity());
            async_search.setContext(getActivity());
            async_search.search_delegates = this;
            async_search.setLayInflate(getActivity().getLayoutInflater());
            async_search.execute("Start array creation");

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

        TextView name_menu = (TextView) view.findViewById(R.id.view_name);
        Spanned name_bold = Html.fromHtml("<b><i>Search</i></b>");
        name_menu.setText(name_bold);
        name_menu.setTypeface(text_view_font);

        TextView results_textview = (TextView) view.findViewById(R.id.number_of_results);
        Spanned results_bold = Html.fromHtml("Total # of verses: <b>" + searchListObjectses.length + "</b>");
        results_textview.setText(results_bold);
        results_textview.setTypeface(text_view_font);

        TextView search_word = (TextView) view.findViewById(R.id.searched_words);

        if (option == 0) {
            search_bold = Html.fromHtml("Searched Words: <b><i>" + searching + "</i></b>");
        }
        else if (option == 1) {
            search_bold = Html.fromHtml("Searched Sentence: <b><i>" + searching + "</i></b>");
        }

        search_word.setText(search_bold);
        search_word.setTypeface(text_view_font);
        search_word.setVisibility(View.VISIBLE);

        if (searchListObjectses.length != 0) {
            search_listView = new Search_ListView(getActivity(), getActivity().getLayoutInflater(), searchListObjectses, EXP_NUM);

            ListView search_list = (ListView) view.findViewById(R.id.listView_chapters_bookmarks_search);

            search_list.setAdapter(search_listView);
        }
        else {
            TextView empty_text = (TextView) view.findViewById(R.id.empty_bookmarks);
            empty_text.setTypeface(text_view_font);
            empty_text.setText("Searched matched no verse in the Quran.");
            empty_text.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void setSearchList(Async_Search.SearchListObjects[] searchList) {
        this.searchListObjectses = searchList;
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

        if (search_listView != null) {
            search_listView.turnMediaOFF();
        }

        super.onDestroy();
    }
}
