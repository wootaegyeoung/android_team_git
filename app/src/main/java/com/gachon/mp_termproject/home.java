package com.gachon.mp_termproject;

import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class home extends Fragment {
    private LinearLayout frame;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // 오늘의 인기댓글
        Button btn_rank1 = view.findViewById(R.id.comment_rank1);
        Button btn_rank2 = view.findViewById(R.id.comment_rank2);
        Button btn_rank3 = view.findViewById(R.id.comment_rank3);

        // 진행 중인 공모전
        TextView tv_content = view.findViewById(R.id.contest_content);
        TextView tv_viewmore = view.findViewById(R.id.view_more);
        TextView tv_viewless = view.findViewById(R.id.view_less);


        // 오늘의 인기댓글 - 터치시 밑줄 & 떼면 사라짐
        btn_rank1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int status = motionEvent.getAction();
                btn_rank1.setPaintFlags(btn_rank1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                if (status == MotionEvent.ACTION_UP) {
                    btn_rank1.setPaintFlags(0);
                }
                return false;
            }
        });
        btn_rank2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int status = motionEvent.getAction();
                btn_rank2.setPaintFlags(btn_rank2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                if (status == MotionEvent.ACTION_UP) {
                    btn_rank2.setPaintFlags(0);
                }
                return false;
            }
        });
        btn_rank3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int status = motionEvent.getAction();
                btn_rank3.setPaintFlags(btn_rank3.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                if (status == MotionEvent.ACTION_UP) {
                    btn_rank3.setPaintFlags(0);
                }
                return false;
            }
        });


        // 진행 중인 공모전 - 더보기 & 줄이기
        tv_viewmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_viewmore.setVisibility(View.GONE);
                tv_content.setMaxLines(Integer.MAX_VALUE);
                tv_viewless.setVisibility(View.VISIBLE);
            }
        });
        tv_viewless.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_viewmore.setVisibility(View.VISIBLE);
                tv_content.setMaxLines(3);
                tv_viewless.setVisibility(View.GONE);
            }
        });
        return view;
    }
}