package com.gachon.mp_termproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class signup extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; // 파이어 베이스 인증
    private DatabaseReference mDatabaseRef; // 실시간 데이터 베이스

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        // 로그인 화면으로 돌아가는 버튼(텍스트뷰)
        TextView tv_login = findViewById(R.id.tv_login);
        // 회원가입 완료 버튼
        ImageButton btn_signup = findViewById(R.id.btn_signup);

        // 회원정보 입력받을 edittext
        EditText edit_name = findViewById(R.id.signup_edit_name);
        EditText edit_email = findViewById(R.id.signup_edit_email);
        EditText edit_phone = findViewById(R.id.signup_edit_phone);
        EditText edit_password = findViewById(R.id.signup_edit_password);


        // 파이어베이스 관련
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), login.class);
                startActivity(i);
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 회원가입 처리
                // 입력 받은 이메일 , 패스워드, 닉네임, 폰번호 읽어오기
                String strEmail = edit_email.getText().toString();
                String strPwd = edit_password.getText().toString();
                String strName = edit_name.getText().toString();
                String strPhone = edit_phone.getText().toString();


                // null 처리

                if(strName.equals("")) {
                    customToastView("닉네임을 입력해주세요.");
                    return;
                }
                if(strEmail.equals("")) {
                    customToastView("이메일을 입력해주세요.");
                    return;
                }
                if(strPhone.equals("")) {
                    customToastView("전화번호를 입력해주세요.");
                    return;
                }
                if(strPwd.equals("")) {
                    customToastView("비밀번호를 입력해주세요.");
                    return;
                }

                mDatabaseRef.child("UserAccount").child(strName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            customToastView("닉네임이 중복됩니다.");
                        }else {
                            // Firebase Auth 진행
                            mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                                @Override // 회원가입 성공시
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                        UserAccount account = new UserAccount();
                                        account.setIdToken(firebaseUser.getUid());
                                        account.setEmailId(firebaseUser.getEmail());
                                        account.setPassword(strPwd);
                                        account.setName(strName);
                                        account.setPhone(strPhone);


                                        mDatabaseRef.child("UserAccount").child(strName).setValue(account);
                                        mDatabaseRef.child("UserAccount").child(strName).child("reward").setValue(0); // 상금정보 업데이트
                                        customToastView("회원가입에 성공했습니다.");
                                        finish();
                                    } else{
                                        customToastView("회원가입에 실패했습니다.");
                                    }
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }

    public void customToastView(String text){

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.mytoast_board, (ViewGroup) findViewById(R.id.toast_layout_root));
        TextView textView = layout.findViewById(R.id.textboard);
        textView.setText(text);

        Toast toastView = Toast.makeText(getApplicationContext(),text, Toast.LENGTH_SHORT);
        toastView.setGravity(Gravity.BOTTOM,0,0);
        toastView.setView(layout);
        toastView.show();
    }
}
