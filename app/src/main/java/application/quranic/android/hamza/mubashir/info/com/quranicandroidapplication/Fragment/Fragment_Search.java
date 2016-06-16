package application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Fragment;

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

import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Adapter.Search_ListView;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.AsyncTask.Async_Search;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Model.SearchListObjects;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.R;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Service.KeyboardGone;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Service.Search_delegates;

public class Fragment_Search extends Fragment implements Search_delegates {

    private View view;
    private AlertDialog alertDialog;
    int option;
    String searching;
    Search_ListView search_listView = null;
    List<String> searchList;
    SearchListObjects[] searchListObjectses = null;
    Spanned search_bold;;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        searching = bundle.getString("search", null);
        option = bundle.getInt("option", 0);

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bookmark_search_chapters_layout, container, false);

        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

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
            search_listView = new Search_ListView(getActivity(), getActivity().getLayoutInflater(), searchListObjectses);

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
    public void setSearchList(SearchListObjects[] searchList) {
        this.searchListObjectses = searchList;
    }

    @Override
    public void setEndResult(boolean value) {
        createViewsNow();
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
