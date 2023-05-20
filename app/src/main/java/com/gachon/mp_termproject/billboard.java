package com.gachon.mp_termproject;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

public class billboard extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_billboard, container, false);

        ImageButton free_contest=view.findViewById(R.id.contest_board1);
        free_contest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), contest_main.class);
                startActivity(i);
            }
        });

        ImageButton reward_contest=view.findViewById(R.id.contest_board2);
        reward_contest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), reward_main.class);
                startActivity(i);
            }
        });



        return view;
    }
}