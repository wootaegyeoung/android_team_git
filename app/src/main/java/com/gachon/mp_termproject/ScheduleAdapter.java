package com.gachon.mp_termproject;

import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private List<Schedule> scheduleList;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView s_month;
        private TextView s_day;
        private TextView s_title;
        private TextView s_period;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            s_month = itemView.findViewById(R.id.month);
            s_day = itemView.findViewById(R.id.day);
            s_title = itemView.findViewById(R.id.contestTitle);
            s_period = itemView.findViewById(R.id.contestPeriod);
            // 공모전 타이틀 터치시 밑줄 & 떼면 사라짐
            s_title.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int status = motionEvent.getAction();
                    s_title.setPaintFlags(s_title.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    if (status == MotionEvent.ACTION_UP) {
                        s_title.setPaintFlags(0);
                    }
                    return false;
                }
            });
        }

        public void bind(Schedule schedule) {
            s_month.setText(schedule.getMonth()); // int형이면 오류
            s_day.setText(Integer.toString(schedule.getDay()));
            s_title.setText(schedule.getContest_title());
            s_period.setText(schedule.getContest_period());
        }
    }


    public ScheduleAdapter(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public interface OnItemClickListener {
        void onItemClick(Schedule schedule);
    }
    private ScheduleAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_calendar_schedule, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ScheduleAdapter.ViewHolder holder, int position) {
        Schedule schedule = scheduleList.get(position);
        holder.bind(schedule);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(schedule);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

}
