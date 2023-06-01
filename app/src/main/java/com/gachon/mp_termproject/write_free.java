package com.gachon.mp_termproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

import androidx.core.util.Pair;

public class write_free extends AppCompatActivity {

    // 파이어스토어 짝꿍
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference CW_collectionRef;


    Map<String, Object> write_content = new HashMap<>();

    // 파베 스토리지 짝꿍
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    FirebaseAuth mAuth = FirebaseAuth.getInstance(); // 로그인정보 갖고오기
    DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_free);

        // 글 제목, 내용
        EditText et_title = findViewById(R.id.et_title_free);
        EditText et_content = findViewById(R.id.et_content_free);

        // 버튼
        Button btn_finish = findViewById(R.id.btn_finish_free);
        ImageButton btn_camera = findViewById(R.id.btn_camera_free);


        mDatabaseRef = FirebaseDatabase.getInstance().getReference(); // 리얼타임 디비에서 회원정보
        String email = mAuth.getCurrentUser().getEmail();
        final String[] current_name = new String[1]; // 작성자 정보
        mDatabaseRef.child("UserAccount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    // 각 child 데이터에 대한 처리
                    // 특정 데이터를 찾는 조건을 확인하고 원하는 작업 수행
                    if (childSnapshot.exists() && childSnapshot.child("emailId").getValue().equals(email)) {
                        current_name[0] = childSnapshot.child("name").getValue().toString(); // 현재 사용자의 닉네임 받아오기
                        write_content.put("작성자", current_name[0]);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // 글 작성 완료 버튼 이벤트
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_title = et_title.getText().toString();
                String str_content = et_content.getText().toString();


                // NULL 예외처리
                if (str_title.equals("")) {
                    customToastView("제목을 입력해주세요");
                    return;
                }
                if (str_content.equals("")) {
                    customToastView("내용을 입력해주세요");
                    return;
                }
                if (!write_content.containsKey("image")) {
                    customToastView("사진을 선택해주세요");
                    return;
                }


                // 글 올린 시간
                LocalDateTime currentTime = LocalDateTime.now();
                String ct = currentTime.getYear() + "-" + currentTime.getMonthValue() + "-" + currentTime.getDayOfMonth() + " " + currentTime.getHour() + ":" + currentTime.getMinute() + ":" + currentTime.getSecond();

                write_content.put("제목", str_title);
                write_content.put("내용", str_content);
                write_content.put("시간", ct);

                CW_collectionRef = db.collection("Free_Writes");

                // 파이어 스토어에 글 정보 등록
                CW_collectionRef.add(write_content).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        write_content.put("id", documentReference.getId());
                        CW_collectionRef.document(documentReference.getId()).set(write_content);
                        finish();
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



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
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
                        write_content.put("image", filename);
                        // 업로드된 이미지의 다운로드 URL을 사용하여 원하는 작업 수행
                        // 예: 데이터베이스에 URL 저장, 이미지를 표시하는 등
                    });
                })
                .addOnFailureListener(e -> {
                    // 업로드 실패 시 처리할 내용
                    // 오류 메시지 출력 등
                });
    }


    public void customToastView(String text) {

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.mytoast_board, (ViewGroup) findViewById(R.id.toast_layout_root));
        TextView textView = layout.findViewById(R.id.textboard);
        textView.setText(text);

        Toast toastView = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toastView.setGravity(Gravity.BOTTOM, 0, 0);
        toastView.setView(layout);
        toastView.show();
    }
}

