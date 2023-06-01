package com.gachon.mp_termproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        ImageButton btnLogin = findViewById(R.id.btn_login);
        TextView signUp = findViewById(R.id.tv_signup);

        EditText login_email = findViewById(R.id.login_edit_email);
        EditText login_password = findViewById(R.id.login_edit_password);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), signup.class);
                startActivity(i);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEmail = login_email.getText().toString();
                String strPwd = login_password.getText().toString();

                if (strEmail.equals("")) {
                    Toast.makeText(login.this, "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                    strEmail = "****";
                    return;
                }
                if (strPwd.equals("")) {
                    Toast.makeText(login.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    strPwd = "++++";
                    return;
                }
                String finalStrEmail = strEmail;

                mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                            login.this.finish(); // 현재 액티비티 종료
                            Intent intent = new Intent(login.this, MainActivity.class);
                            intent.putExtra("email", finalStrEmail);

                            // 사용자 추가 정보 가져오기
                            DatabaseReference userRef = mDatabaseRef.child("UserAccount");
                            userRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    boolean foundUser = false;
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            UserAccount userAccount = snapshot.getValue(UserAccount.class);
                                            if (userAccount != null && userAccount.getEmailId() != null && userAccount.getEmailId().equals(finalStrEmail)) {
                                                String name = userAccount.getName();
                                                String phone = userAccount.getPhone();
                                                int reward = userAccount.getReward();

                                                intent.putExtra("name", name);
                                                intent.putExtra("phone", phone);
                                                intent.putExtra("reward", reward);
                                                Log.d("Login", "Name: " + name + ", Phone: " + phone);
                                                foundUser = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (!foundUser) {
                                        // 사용자 정보가 없을 때의 처리
                                    }
                                    startActivity(intent);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Toast.makeText(login.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
}
