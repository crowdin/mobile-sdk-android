package com.crowdin.platform.example.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crowdin.platform.example.R;

public class ShareFragment extends Fragment {

    public static ShareFragment newInstance() {
        return new ShareFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share, container, false);
    }
}