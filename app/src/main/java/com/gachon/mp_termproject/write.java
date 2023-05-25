package com.gachon.mp_termproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

public class write extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        EditText et_title = findViewById(R.id.write_title);
        EditText et_content = findViewById(R.id.write_content);

        Button btn_finish = findViewById(R.id.btn_finish);

        CollectionReference CW_collectionRef = db.collection("Contest_Writes");
        Map<String, Object> write_content = new HashMap<>();

        // 글 작성 완료 버튼 이벤트
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_title = et_title.getText().toString();
                String str_content = et_content.getText().toString();

                // 글 올린 시간
                LocalDateTime currentTime = LocalDateTime.now();
                String ct = currentTime.getYear() + "-" + currentTime.getMonth() + "-" + currentTime.getDayOfMonth() + " " + currentTime.getHour() + ":" + currentTime.getMinute() + ":" + currentTime.getSecond();

                write_content.put("제목", str_title);
                write_content.put("내용", str_content);
                write_content.put("시간", ct);

                // 파이어 스토어에 글 정보 등록
                CW_collectionRef.add(write_content).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "저장 성공", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        // 데이터의 추가를 감지하는 리스너(-->> 가장 최근에 올라간 글의 정보를 갖고 오게 설정)
        CW_collectionRef.orderBy("시간", Query.Direction.DESCENDING)
                .limit(1)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // 에러 처리
                            return;
                        }

                        if (snapshot != null && !snapshot.isEmpty()) {
                            // 마지막에 추가된 문서 가져오기
                            DocumentSnapshot lastDocument = snapshot.getDocuments().get(0);
                            String str_title = lastDocument.getString("제목");
                            String str_content = lastDocument.getString("내용");

                            System.out.println("제목: " + str_title + " / " + "내용: " + str_content); // 테스트용
                        }
                    }
                });


    }
}