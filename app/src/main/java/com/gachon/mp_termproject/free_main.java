package com.gachon.mp_termproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class free_main extends AppCompatActivity {

    List<Post_free> post_freeList;
    Post_freeAdapter postAdapter_free;
    RecyclerView recyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance(); // 파이어베이스 스토리지
    CollectionReference CW_collectionRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_content_free_main);
        Button plusContent = findViewById(R.id.write_btn);

        // RecyclerView와 어댑터 초기화
        post_freeList = new ArrayList<>();
        recyclerView = findViewById(R.id.free_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((Context) this);
        recyclerView.setLayoutManager(linearLayoutManager);
        postAdapter_free = new Post_freeAdapter(post_freeList);
        recyclerView.setAdapter(postAdapter_free);

        // Firestore의 "Contest_Writes" 컬렉션을 참조하는 CollectionReference 생성
        CW_collectionRef = db.collection("Free_Writes");

        // 데이터 변경을 실시간으로 감지하는 addSnapshotListener 사용
        CW_collectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                if (error != null) {
                    // 에러 처리
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                if (value != null) {
                    List<Post_free> newPosts_free = new ArrayList<>();
                    for (QueryDocumentSnapshot document : value) {
                        // Firestore에서 각 문서의 필드 데이터 가져오기
                        String str_title = document.getString("제목");
                        String str_content = document.getString("내용");
                        String str_writer = document.getString("작성자");
                        String str_image = document.getString("image");
                        String send_time = document.getString("시간");
                        String str_id = document.getString("id");
                        if (str_image == null)
                            return;

                        // 이미지 불러오기
                        StorageReference storageRef = storage.getReference();
                        StorageReference imageRef = storageRef.child(str_image);
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // 이미지 다운로드 성공 시 처리할 작업
                                // uri를 사용하여 이미지를 표시할 수 있음
                                Post_free newPost_free = new Post_free(str_title, str_writer, str_content, uri, null, send_time, str_id, getApplicationContext());
                                newPosts_free.add(newPost_free);

                                if (newPosts_free.size() == value.size()) {
                                    // 모든 데이터를 받았을 때 RecyclerView 업데이트
                                    Collections.sort(newPosts_free, new Comparator<Post_free>() {
                                        @Override
                                        public int compare(Post_free o1, Post_free o2) {
                                            return o2.getSend_time().compareTo(o1.getSend_time());
                                        }
                                    });
                                    post_freeList.clear();
                                    post_freeList.addAll(newPosts_free);
                                    postAdapter_free.notifyDataSetChanged();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                // 이미지 다운로드 실패 시 처리할 작업
                                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                }
            }
        });

        plusContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), write_free.class);
                startActivity(i);
            }
        });

        // 리사이클러뷰 클릭이벤트
        postAdapter_free.setOnItemClickListener(new Post_freeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Post_free post_free) {
                // 인텐트를 통해 그 글로 이동
                Intent intent = new Intent(getApplicationContext(), clickBillboard_free.class);
                intent.putExtra("selectedPost_free", post_free);
                startActivity(intent);
            }
        });
    }


}