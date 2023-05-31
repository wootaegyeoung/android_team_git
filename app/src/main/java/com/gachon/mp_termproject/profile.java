package com.gachon.mp_termproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class profile extends Fragment {

    TextView userNameTextView;
    TextView emailTextView;
    TextView phoneNumTextView;

    TextView rewardTextView;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_profile1, container, false);
        Button profile_1=view.findViewById(R.id.profile1);

        Intent intent = getActivity().getIntent();
        String userEmail = intent.getStringExtra("email");
        String userName = intent.getStringExtra("name");
        String phoneNum=intent.getStringExtra("phone");
        int reward=intent.getIntExtra("reward", 13);

        emailTextView = view.findViewById(R.id.mail);
        userNameTextView=view.findViewById(R.id.user_name);
        phoneNumTextView=view.findViewById(R.id.phone_number);
        rewardTextView=view.findViewById(R.id.p);


        readUserInfo(userEmail, userName, phoneNum, reward);


        profile_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), myprofile.class);
                startActivity(i);
            }
        });

        return view;


    }
    private void readUserInfo(String email, String name, String phone, int reward) {
        // 사용자 정보를 읽어오는 코드 작성
        // 예시: 사용자 이메일을 파라미터로 받아와서 해당 사용자 정보를 읽어옴
        // 사용자 정보를 읽어와서 UI에 설정하는 로직을 작성
        // 예시 코드에서는 이메일을 받아온 후 UI에 설정하는 부분을 추가하도록 되어 있음
        emailTextView.setText(email);
        phoneNumTextView.setText(phone);
        userNameTextView.setText(name);
        rewardTextView.setText(String.valueOf(reward));
    }

}