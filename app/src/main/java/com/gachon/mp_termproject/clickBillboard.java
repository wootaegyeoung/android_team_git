package com.gachon.mp_termproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class clickBillboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clickbillboard);

        RelativeLayout reward_info = findViewById(R.id.cb_rewardInfo); // 상금정보 화면 레이아웃
        TextView tv_topReward = findViewById(R.id.cb_tv_topReward); // 메인에 보일 1등 상금 텍스트뷰

        reward_info.setVisibility(View.INVISIBLE);

        // 해당 텍스트뷰 터치시 상금화면 보이게
        tv_topReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reward_info.getVisibility() == View.VISIBLE)
                    reward_info.setVisibility(View.INVISIBLE);
                else
                    reward_info.setVisibility(View.VISIBLE);
            }
        });

    }
}