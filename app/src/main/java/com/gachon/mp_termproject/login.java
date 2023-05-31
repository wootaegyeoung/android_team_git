package com.gachon.mp_termproject;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class login extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; // 파이어 베이스 인증
    private DatabaseReference mDatabaseRef; // 실시간 데이터 베이스

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

                if(strEmail.equals("")) {
                    Toast.makeText(login.this, "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                    strEmail = "****";
                    return;
                }
                if(strPwd.equals("")) {
                    Toast.makeText(login.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    strPwd = "++++";
                    return;
                }
                mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // 로그인 성공
                            Intent intent = new Intent(login.this, MainActivity.class);
                            startActivity(intent);
                            login_email.setText("");
                            login_password.setText("");
                            finish();
                        }else {
                            Toast.makeText(login.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
}
