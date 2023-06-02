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

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class home extends Fragment {

    List<Post> postList;
    PostAdapter postAdapter;
    RecyclerView recyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance(); // 파이어베이스 스토리지
    CollectionReference CW_collectionRef;
    List<Post> posts; // 인기글에 올릴 글 목록들

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // 인기 공모전
        Button btn_rank1 = view.findViewById(R.id.rank1_title);
        Button btn_rank2 = view.findViewById(R.id.rank2_title);
        Button btn_rank3 = view.findViewById(R.id.rank3_title);
        TextView tv_com1 = view.findViewById(R.id.rank1_com_num);
        TextView tv_com2 = view.findViewById(R.id.rank2_com_num);
        TextView tv_com3 = view.findViewById(R.id.rank3_com_num);

        // 초기화
        btn_rank1.setText("");
        btn_rank2.setText("");
        btn_rank3.setText("");
        tv_com1.setText("");
        tv_com2.setText("");
        tv_com3.setText("");

        btn_rank1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (posts.size() >= 1) {
                    Intent intent = new Intent(getContext(), clickBillboard.class);
                    intent.putExtra("selectedPost", posts.get(0));
                    startActivity(intent);
                }
            }
        });
        btn_rank2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (posts.size() >= 2) {
                    Intent intent = new Intent(getContext(), clickBillboard.class);
                    intent.putExtra("selectedPost", posts.get(1));
                    startActivity(intent);
                }
            }
        });
        btn_rank3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (posts.size() >= 3) {
                    Intent intent = new Intent(getContext(), clickBillboard.class);
                    intent.putExtra("selectedPost", posts.get(2));
                    startActivity(intent);
                }
            }
        });


        // Firestore의 "Contest_Writes" 컬렉션을 참조하는 CollectionReference 생성
        CW_collectionRef = db.collection("Contest_Writes");

        CW_collectionRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    posts = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {


                        // Firestore에서 각 문서의 필드 데이터 가져오기
                        String str_title = document.getString("제목");
                        String str_content = document.getString("내용");
                        String str_writer = document.getString("작성자");
                        String str_image = document.getString("image");
                        String send_time = document.getString("시간");
                        String str_id = document.getString("id");
                        Map<String, Object> map_reward = (Map<String, Object>) document.getData().get("상금");
                        List<Map<String, Comment>> commentMap;
                        if (document.contains("댓글"))
                            commentMap = (List<Map<String, Comment>>) document.get("댓글");
                        else
                            commentMap = new ArrayList<>(); // 아직 댓글이 없으면 0크기의 리스트를 새롭게 선언해줌


                        List<Comment> newCommentList = new ArrayList<>();
                        for (Map<String, Comment> cm : commentMap) {
                            Comment newComment = new Comment(2);
                            for (String key : cm.keySet()) {
                                Object value = cm.get(key);
                                if (key.equalsIgnoreCase("cnt_recommend")) {
                                    if (value instanceof Number) {
                                        newComment.setCnt_recommend(((Long) value).intValue());
                                    }
                                } else if (key.equalsIgnoreCase("comment_content")) {
                                    if (value instanceof String) {
                                        newComment.setComment_content((String) value);
                                    }
                                } else if (key.equalsIgnoreCase("writer")) {
                                    if (value instanceof String) {
                                        newComment.setWriter((String) value);
                                    }
                                } else if (key.equalsIgnoreCase("parentPostId")) {
                                    if (value instanceof String) {
                                        newComment.setParentPostId((String) value);
                                    }
                                }
                            }
                            newCommentList.add(newComment);
                        }
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
                                Post newPost = new Post(str_title, str_writer, str_content, map_reward, uri, newCommentList, send_time, str_id, getContext());
                                posts.add(newPost);
                                if (queryDocumentSnapshots.size() == posts.size()) {
                                    Collections.sort(posts, new Comparator<Post>() {
                                        @Override
                                        public int compare(Post o1, Post o2) {
                                            return Integer.compare(o2.getCommentList().size(), o1.getCommentList().size()); // 댓글 갯수를 기준으로 내림차순 정렬
                                        }
                                    });
                                    if (posts.size() >= 3) {
                                        // 글 제목 표시
                                        btn_rank1.setText(posts.get(0).getTitle());
                                        btn_rank2.setText(posts.get(1).getTitle());
                                        btn_rank3.setText(posts.get(2).getTitle());
                                        // 댓글 수 표시
                                        tv_com1.setText(Integer.toString(posts.get(0).getCommentList().size()));
                                        tv_com2.setText(Integer.toString(posts.get(1).getCommentList().size()));
                                        tv_com3.setText(Integer.toString(posts.get(2).getCommentList().size()));
                                    } else if (posts.size() == 2) {
                                        btn_rank1.setText(posts.get(0).getTitle());
                                        btn_rank2.setText(posts.get(1).getTitle());

                                        tv_com1.setText(Integer.toString(posts.get(0).getCommentList().size()));
                                        tv_com2.setText(Integer.toString(posts.get(1).getCommentList().size()));
                                    } else if (posts.size() == 1) {
                                        btn_rank1.setText(posts.get(0).getTitle());

                                        tv_com1.setText(Integer.toString(posts.get(0).getCommentList().size()));
                                    }
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

                })
                .addOnFailureListener(e -> {
                });


        // RecyclerView와 어댑터 초기화
        postList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.progress_contest_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        postAdapter = new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);
        // 데이터 변경을 실시간으로 감지하는 addSnapshotListener 사용
        CW_collectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        String send_time = document.getString("시간");
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
                                Post newPost = new Post(str_title, str_writer, str_content, map_reward, uri, null, send_time, str_id, getContext());
                                newPosts.add(newPost);

                                if (newPosts.size() == value.size()) {
                                    // 모든 데이터를 받았을 때 RecyclerView 업데이트
                                    Collections.sort(newPosts, new Comparator<Post>() {
                                        @Override
                                        public int compare(Post o1, Post o2) {
                                            return o2.getSend_time().compareTo(o1.getSend_time());
                                        }
                                    });
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