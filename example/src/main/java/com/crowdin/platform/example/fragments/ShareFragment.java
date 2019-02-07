package com.crowdin.platform.example.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crowdin.platform.example.R;

public class ShareFragment extends Fragment {

    public static ShareFragment newInstance() {
        return new ShareFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ((TextView) view.findViewById(R.id.textView0))
                .setText(getResources().getQuantityString(R.plurals.test_plurals, 0, 0));
        ((TextView) view.findViewById(R.id.textView1))
                .setText(getResources().getQuantityString(R.plurals.test_plurals, 1, 1));
        ((TextView) view.findViewById(R.id.textView2))
                .setText(getResources().getQuantityString(R.plurals.test_plurals, 2, 2));
        ((TextView) view.findViewById(R.id.textView3))
                .setText(getResources().getQuantityString(R.plurals.test_plurals, 20, 20));
    }

}