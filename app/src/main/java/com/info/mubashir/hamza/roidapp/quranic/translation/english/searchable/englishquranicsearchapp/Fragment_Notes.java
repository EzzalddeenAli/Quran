package com.info.mubashir.hamza.roidapp.quranic.translation.english.searchable.englishquranicsearchapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class Fragment_Notes extends Fragment {

    private View view;
    SharedPreferences firstAppRun = null;
    String data = null;
    EditText edit_text = null;
    InputMethodManager imm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.notes_fragment_layout, container, false);

        final Typeface font_style = Typeface.createFromAsset(getActivity().getAssets(), "list_textviews.otf");

        TextView header = (TextView) view.findViewById(R.id.heading_name);
        Spanned name_bold = Html.fromHtml("<b><i>Notes</i></b>");
        header.setText(name_bold);
        header.setTypeface(font_style);

        edit_text = (EditText) view.findViewById(R.id.notesTextInput);
        edit_text.setTypeface(font_style);

        firstAppRun = PreferenceManager.getDefaultSharedPreferences(getContext());

        data = firstAppRun.getString("notes", null);

        edit_text.setText(data);
        edit_text.requestFocus();

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return view;
    }

    @Override
    public void onPause() {
        data = edit_text.getText().toString();

        SharedPreferences.Editor editor = firstAppRun.edit();
        editor.putString("notes", data);
        editor.commit();

        KeyboardGone removeKey = new KeyboardGone(getActivity());
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        super.onPause();
    }

    @Override
    public void onResume() {
        firstAppRun = PreferenceManager.getDefaultSharedPreferences(getActivity());
        data = firstAppRun.getString("notes", null);

        edit_text.requestFocus();

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);

        super.onResume();
    }

}
