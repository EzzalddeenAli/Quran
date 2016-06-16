package application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.R;

public class Fragment_Search_Tips extends Fragment {

    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_tips, container, false);

        final Typeface text_view_font = Typeface.createFromAsset(getActivity().getAssets(), "list_textviews.otf");

        TextView[] texters = new TextView[4];
        int[] ids = {R.id.tips_heading, R.id.tips_one, R.id.tips_two, R.id.tips_three};

        for (int x = 0; x < texters.length; x++) {
            texters[x] = (TextView) view.findViewById(ids[x]);
            texters[x].setTypeface(text_view_font);
        }

        return view;
    }
}
