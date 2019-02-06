package com.crowdin.platform.example.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crowdin.platform.example.R;

public class SendFragment extends Fragment {

    public static SendFragment newInstance() {
        return new SendFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_send, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView textView0 = view.findViewById(R.id.textView0);
        TextView textView1 = view.findViewById(R.id.textView1);
        TextView textView2 = view.findViewById(R.id.textView2);
        TextView textView3 = view.findViewById(R.id.textView3);

        textView0.setText(getString(R.string.formatting_test0, 3));
        textView1.setText(getString(R.string.formatting_test1, "Test"));
        textView2.setText(getString(R.string.formatting_test2, 3, "Test"));

        String text = getString(R.string.formatting_test3);
        Spanned styledText = Html.fromHtml(text);
        textView3.setText(styledText);
    }
}