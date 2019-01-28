package com.crowdin.platform.example.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crowdin.platform.example.R;
import com.crowdin.platform.example.adapter.SampleAdapter;

public class GalleryFragment extends Fragment {

    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String[] array = getResources().getStringArray(R.array.string_array);
        RecyclerView.Adapter adapter = new SampleAdapter(array);
        recyclerView.setAdapter(adapter);
    }
}