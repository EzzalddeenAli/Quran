package application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.R;
import application.quranic.android.hamza.mubashir.info.com.quranicandroidapplication.Service.KeyboardGone;

public class Fragment_Empty extends Fragment {
    View view = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_empty_layout, container, false);

        return view;
    }

    @Override
    public void onPause() {
        KeyboardGone removeKey = new KeyboardGone(getActivity());
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        super.onPause();
    }
}
