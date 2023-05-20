package com.gachon.mp_termproject;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Content extends LinearLayout {

    public Content(Context context) {
        super(context);

        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.contest_content, this, true);
        TextView tv_viewmore = findViewById(R.id.view_more);
        TextView tv_content = findViewById(R.id.contest_content);
        TextView tv_viewless = findViewById(R.id.view_less);
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
                tv_viewless.setVisibility(View.GONE);
                tv_content.setMaxLines(3);
                tv_viewmore.setVisibility(View.VISIBLE);
            }
        });
    }
}
