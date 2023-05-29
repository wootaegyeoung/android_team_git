package com.gachon.mp_termproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.mp_termproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class reward_main extends AppCompatActivity {

    List<Post> postList;
    PostAdapter postAdapter;
    RecyclerView recyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance(); // 파이어베이스 스토리지
    CollectionReference CW_collectionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_content_reward_main);
        Button plusContent = findViewById(R.id.write_btn);


        plusContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), write.class);
                startActivity(i);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);

        recyclerView = findViewById(R.id.contest_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(reward_main.this,LinearLayoutManager.VERTICAL, false));

        CW_collectionRef = db.collection("Contest_Writes");

        CW_collectionRef.orderBy("시간", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String str_title = document.getString("제목");
                                String str_content = document.getString("내용");
                                String str_writer = document.getString("작성자");
                                String str_image = document.getString("image");
                                Map<String, Object> map_reward = (Map<String, Object>) document.getData().get("상금");
                                // 이미지 불러오기
                                StorageReference storageRef = storage.getReference();
                                StorageReference imageRef = storageRef.child(str_image);
                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // 이미지 다운로드 성공 시 처리할 작업
                                        // uri를 사용하여 이미지를 표시할 수 있음

                                        Post newPost = new Post(str_title, str_writer, str_content, map_reward, uri, null, getApplicationContext());
                                        postList.add(newPost);
                                        postAdapter.notifyDataSetChanged();
                                        recyclerView.setAdapter(postAdapter);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        // 이미지 다운로드 실패 시 처리할 작업
                                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        } else {
                            // 작업 실패 시 처리 로직을 구현합니다.
                        }
                    }

                });
    }
}