package com.gachon.mp_termproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.UUID;


public class write extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    CollectionReference CW_collectionRef;
    Map<String, Object> write_content = new HashMap<>();

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        EditText et_title = findViewById(R.id.write_title);
        EditText et_content = findViewById(R.id.write_content);

        Button btn_finish = findViewById(R.id.btn_finish);
        ImageButton btn_camera = findViewById(R.id.btn_camera);
        ImageButton btn_time = findViewById(R.id.btn_time);


        CW_collectionRef = db.collection("Contest_Writes");

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


        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }


        });

        btn_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuDialog();
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

                        }
                    }
                });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    uploadImageToFirebaseStorage(uri);
                }
                break;
        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        // Storage에 업로드할 파일의 이름 생성 (예: "images/my_image.jpg")
        String filename = "images/" + UUID.randomUUID().toString() + ".jpg";

        // Storage 레퍼런스 생성
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // 업로드할 파일의 레퍼런스 생성
        StorageReference imageRef = storageRef.child(filename);

        // 이미지 업로드
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // 업로드 성공 시 처리할 내용
                    // 업로드된 이미지의 다운로드 URL 가져오기
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        write_content.put("image", downloadUrl);
                        // 업로드된 이미지의 다운로드 URL을 사용하여 원하는 작업 수행
                        // 예: 데이터베이스에 URL 저장, 이미지를 표시하는 등
                    });
                })
                .addOnFailureListener(e -> {
                    // 업로드 실패 시 처리할 내용
                    // 오류 메시지 출력 등
                });
    }

    private void showMenuDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.timeset); // 메뉴 화면의 레이아웃 설정

        // 메뉴 화면에서 필요한 뷰 요소들 가져오기
        DatePicker startDatePicker = dialog.findViewById(R.id.startDatePicker);
        TimePicker startTimePicker = dialog.findViewById(R.id.startTimePicker);
        DatePicker endDatePicker = dialog.findViewById(R.id.endDatePicker);
        TimePicker endTimePicker = dialog.findViewById(R.id.endTimePicker);
        Button confirmButton = dialog.findViewById(R.id.confirmButton);

        // 확인 버튼 클릭 이벤트 처리
        confirmButton.setOnClickListener(v -> {
            // 선택된 날짜와 시간 가져오기
            int startYear = startDatePicker.getYear();
            int startMonth = startDatePicker.getMonth() + 1; // 월은 0부터 시작하므로 1을 더함
            int startDay = startDatePicker.getDayOfMonth();
            int startHour = startTimePicker.getHour();
            int startMinute = startTimePicker.getMinute();

            int endYear = endDatePicker.getYear();
            int endMonth = endDatePicker.getMonth() + 1;
            int endDay = endDatePicker.getDayOfMonth();
            int endHour = endTimePicker.getHour();
            int endMinute = endTimePicker.getMinute();

            // 선택된 날짜와 시간 활용 예시: Firebase Firestore에 저장 등
            // ...

            // 팝업 창 닫기
            dialog.dismiss();
        });

        // 팝업 창 보여주기
        dialog.show();
    }

}

