package com.gachon.mp_termproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class home extends Fragment {

    List<Post> postList;
    PostAdapter postAdapter;
    RecyclerView recyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance(); // 파이어베이스 스토리지
    CollectionReference CW_collectionRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // 오늘의 인기댓글
        Button btn_rank1 = view.findViewById(R.id.comment_rank1);
        Button btn_rank2 = view.findViewById(R.id.comment_rank2);
        Button btn_rank3 = view.findViewById(R.id.comment_rank3);

        // RecyclerView와 어댑터 초기화
        postList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.progress_contest_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        postAdapter = new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);

        // Firestore의 "Contest_Writes" 컬렉션을 참조하는 CollectionReference 생성
        CW_collectionRef = db.collection("Contest_Writes");

        // 데이터 변경을 실시간으로 감지하는 addSnapshotListener 사용
        CW_collectionRef.orderBy("시간", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                        if (error != null) {
                            // 에러 처리
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (value != null) {
                            List<Post> newPosts = new ArrayList<>();
                            for (QueryDocumentSnapshot document : value) {
                                // Firestore에서 각 문서의 필드 데이터 가져오기
                                String str_title = document.getString("제목");
                                String str_content = document.getString("내용");
                                String str_writer = document.getString("작성자");
                                String str_image = document.getString("image");
                                String str_id = document.getString("id");
                                Map<String, Object> map_reward = (Map<String, Object>) document.getData().get("상금");

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
                                        Post newPost = new Post(str_title, str_writer, str_content, map_reward, uri, null, str_id, getContext());
                                        newPosts.add(newPost);

                                        if (newPosts.size() == value.size()) {
                                            // 모든 데이터를 받았을 때 RecyclerView 업데이트
                                            postList.clear();
                                            postList.addAll(newPosts);
                                            postAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        // 이미지 다운로드 실패 시 처리할 작업
                                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }
                });
        // 리사이클러뷰 클릭이벤트
        postAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Post post) {
                // 인텐트를 통해 그 글로 이동
                Intent intent = new Intent(getContext(), clickBillboard.class);
                intent.putExtra("selectedPost", post);
                startActivity(intent);
            }
        });
        return view;
    }
}