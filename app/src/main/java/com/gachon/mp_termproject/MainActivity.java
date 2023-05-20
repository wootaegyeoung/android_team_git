package com.gachon.mp_termproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private BottomNavigationView bottomNavigationView;

    private calendar frag_calendar;
    private billboard frag_billboard;
    private home frag_home;
    private notification frag_noti;
    private profile frag_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home); // 첫 화면 하단바 터치된 애 설정




        // 하단바에서 선택을 했을 때 화면을 전환 시켜줌
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()){
                    case R.id.calendar:
                        setFrag(1);
                        break;
                    case R.id.billboard:
                        setFrag(2);
                        break;
                    case R.id.home:
                        setFrag(3);
                        break;
                    case R.id.notification:
                        setFrag(4);
                        break;
                    case R.id.profile:
                        setFrag(5);
                        break;
                }
                return true;
            }
        });
        frag_calendar = new calendar();
        frag_billboard = new billboard();
        frag_home = new home();
        frag_noti = new notification();
        frag_profile = new profile();
        setFrag(3); // 첫화면은 홈화면으로 설정한다.
    }


    // 메인 화면에 프라그먼트를 전환 시켜준다.
    private void setFrag(int n){
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (n) {
            case 1:
                fragmentTransaction.replace(R.id.main_frame_layout, frag_calendar);
                fragmentTransaction.commit();
                break;
            case 2:
                fragmentTransaction.replace(R.id.main_frame_layout, frag_billboard);
                fragmentTransaction.commit();
                break;
            case 3:
                fragmentTransaction.replace(R.id.main_frame_layout, frag_home);
                fragmentTransaction.commit();
                break;
            case 4:
                fragmentTransaction.replace(R.id.main_frame_layout, frag_noti);
                fragmentTransaction.commit();
                break;
            case 5:
                fragmentTransaction.replace(R.id.main_frame_layout, frag_profile);
                fragmentTransaction.commit();
                break;
        }
    }

}