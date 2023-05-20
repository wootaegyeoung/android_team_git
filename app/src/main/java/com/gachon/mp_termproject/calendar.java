package com.gachon.mp_termproject;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

public class calendar extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // 공모전 정보 view 들
        Button btn_contestTitle1 = view.findViewById(R.id.contestTitle1);
        TextView tv_day1 = view.findViewById(R.id.day1);
        TextView tv_month1 = view.findViewById(R.id.month1);
        Button tv_title1 = view.findViewById(R.id.contestTitle1);
        TextView tv_period = view.findViewById(R.id.contestPeriod1);

        CalendarView cv = view.findViewById(R.id.calendarView);

        // 날짜를 선택하면 아래 공모전 정보 란의 날짜가 해당 날짜로 바뀐다.
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                tv_day1.setText(Integer.toString(dayOfMonth));
                tv_month1.setText(convertMonth (month));
                tv_period.setText(Integer.toString(dayOfMonth) + " " + convertMonth(month) + " - " + Integer.toString(dayOfMonth + 2) + " " + convertMonth(month)); // DB에서 공모전 일정 받아오기
            }
        });

        tv_title1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
            }
        });

        // 공모전 타이틀 터치시 밑줄 & 떼면 사라짐
        btn_contestTitle1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int status = motionEvent.getAction();
                btn_contestTitle1.setPaintFlags(btn_contestTitle1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                if (status == MotionEvent.ACTION_UP) {
                    btn_contestTitle1.setPaintFlags(0);
                }
                return false;
            }
        });

        return view;
    }

    // 날짜: 숫자를 문자열로 변환
    private String convertMonth(int n) {
        n = (n + 1) % 12;
        String month = "";
        switch (n) {
            case 1:
                month = "JAN";
                break;
            case 2:
                month = "FEB";
                break;
            case 3:
                month = "MAR";
                break;
            case 4:
                month = "APR";
                break;
            case 5:
                month = "MAY";
                break;
            case 6:
                month = "JUNE";
                break;
            case 7:
                month = "JULY";
                break;
            case 8:
                month = "AUG";
                break;
            case 9:
                month = "SEP";
                break;
            case 10:
                month = "OCT";
                break;
            case 11:
                month = "NOV";
                break;
            case 12:
                month = "DEC";
                break;
        }

        return month;
    }

}