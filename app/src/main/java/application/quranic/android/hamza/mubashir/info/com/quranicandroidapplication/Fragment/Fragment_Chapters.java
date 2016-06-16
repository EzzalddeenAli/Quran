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

import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Adapter.Chapters_ListView;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.AsyncTask.Async_task_fragment_chapters_bookmarks;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Model.ChaptersListObjects;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.R;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Service.Chapters_Array_Creation;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Service.KeyboardGone;

public class Fragment_Chapters extends Fragment implements Chapters_Array_Creation {

    private View view;
    private int start, end;
    private String chapter_name;

    private ListView chapters_list;
    private AlertDialog alertDialog;
    Chapters_ListView chap_listview = null;
    ChaptersListObjects[] chaptersList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        chapter_name = bundle.getString("chapter", null);
        start = bundle.getInt("start", 0);
        end = bundle.getInt("end", 0);

        final Async_task_fragment_chapters_bookmarks grab_arrays = new Async_task_fragment_chapters_bookmarks();
        grab_arrays.setContextSpinner(getActivity());
        grab_arrays.setContext(getActivity());
        grab_arrays.setDetermine_get_method(false);
        grab_arrays.setStart(start);
        grab_arrays.setEnd(end);
        grab_arrays.setLayInflate(getActivity().getLayoutInflater());
        grab_arrays.chapters_delegates = this;
        grab_arrays.execute("Start array creation");
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

        TextView not_needed_textview = (TextView) view.findViewById(R.id.searched_words);
        not_needed_textview.setVisibility(View.GONE);

        TextView name_menu = (TextView) view.findViewById(R.id.view_name);
        Spanned name_bold = Html.fromHtml("<b><i>Chapters</i></b>");
        name_menu.setText(name_bold);
        name_menu.setTypeface(text_view_font);

        TextView results_textview = (TextView) view.findViewById(R.id.number_of_results);
        Spanned results_bold = Html.fromHtml("Total # of verses: <b>" + chaptersList.length + "</b>");
        results_textview.setText(results_bold);
        results_textview.setTypeface(text_view_font);

        chap_listview = new Chapters_ListView(getActivity(), getActivity().getLayoutInflater(), chaptersList);

        ListView chap_list = (ListView) view.findViewById(R.id.listView_chapters_bookmarks_search);

        chap_list.setAdapter(chap_listview);

    }

    @Override
    public void setChaptersList(ChaptersListObjects[] chaptersList) {
        this.chaptersList = chaptersList;
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

        if (chap_listview != null) {
            chap_listview.turnMediaOFF();
        }

        super.onDestroy();
    }
}


