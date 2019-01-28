package com.crowdin.platform.example.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crowdin.platform.example.R;

public class CameraFragment extends Fragment {

    public static CameraFragment newInstance() {
        return new CameraFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }
}