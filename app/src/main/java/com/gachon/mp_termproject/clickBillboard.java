package com.gachon.mp_termproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class clickBillboard extends AppCompatActivity {

    List<Comment> commentList;
    CommentAdapter commentAdapter;
    RecyclerView recyclerView;

    FirebaseAuth mAuth = FirebaseAuth.getInstance(); // 로그인정보 갖고오기
    DatabaseReference mDatabaseRef;


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference CW_collectionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clickbillboard);

        RelativeLayout reward_info = findViewById(R.id.cb_rewardInfo); // 상금정보 화면 레이아웃
        TextView tv_topReward = findViewById(R.id.cb_tv_topReward); // 메인에 보일 1등 상금 텍스트뷰

        reward_info.setVisibility(View.INVISIBLE);

        // 글 정보
        TextView tv_writer = findViewById(R.id.cb_writer_name);
        ImageView img = findViewById(R.id.cb_img);
        TextView tv_top = findViewById(R.id.cb_tv_topReward);
        TextView tv_rank1 = findViewById(R.id.cb_tv_rank1);
        TextView tv_rank2 = findViewById(R.id.cb_tv_rank2);
        TextView tv_rank3 = findViewById(R.id.cb_tv_rank3);
        TextView tv_content = findViewById(R.id.cb_content);


        // 댓글 입력
        EditText et_comment = findViewById(R.id.cb_comment_edittext);
        ImageButton ib_send = findViewById(R.id.cb_btn_send);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        // Intent에서 객체를 받음
        Intent intent = getIntent();
        Post post = intent.getParcelableExtra("selectedPost");

        // 받은 객체 사용
        String writer = post.getWriter();
        String content = post.getContent();
        Map<String, Object> reward = post.getReward();
        Uri uri = post.getImageURI();
        String post_id = post.getPost_id();

        // Intent로부터 받은 객체정보를 UI에 적용
        tv_writer.setText(writer);
        Glide.with(this)
                .load(uri)
                .into(img);
        tv_top.setText(reward.get("1").toString());
        tv_rank1.setText(reward.get("1").toString());
        tv_rank2.setText(reward.get("2").toString());
        tv_rank3.setText(reward.get("3").toString());
        tv_content.setText(content);

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
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // 해당 텍스트뷰 터치시 상금화면 보이게
        tv_topReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reward_info.getVisibility() == View.VISIBLE)
                    reward_info.setVisibility(View.INVISIBLE);
                else
                    reward_info.setVisibility(View.VISIBLE);
            }
        });


        // 댓글 갱신해주는 부분
        // RecyclerView와 어댑터 초기화
        commentList = new ArrayList<>();
        recyclerView = findViewById(R.id.cb_recyclerView_comment);
        CommentAdapter adapter = new CommentAdapter(commentList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((Context) this);
        recyclerView.setLayoutManager(linearLayoutManager);

        db.collection("Contest_Writes").document(post_id).addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e("Firestore", "Error reading comments", e);
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                // 댓글 필드 읽어오기
                // 어댑터에 데이터 설정하여 리사이클러뷰에 표시

                // 처음에는 당연히 댓글이 없으니 예외처리
                if (!snapshot.contains("댓글"))
                    return;
                List<Map<String, Comment>> commentMap = (List<Map<String, Comment>>) snapshot.get("댓글");
                List<Comment> newCommentList = new ArrayList<>();
                for (Map<String, Comment> cm : commentMap) {
                    Comment newComment = new Comment();
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
                if (newCommentList != null) {
                    commentList.clear(); // 기존 데이터를 모두 제거
                    commentList.addAll(newCommentList); // 새로운 데이터로 업데이트
                    adapter.notifyDataSetChanged(); // 어댑터에 변경 내용 반영
                    recyclerView.setAdapter(adapter);
                }
            }
        });


        // 댓글 전송 버튼
        ib_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = et_comment.getText().toString();
                Comment newComment = new Comment(current_name[0], comment, post_id);
                CW_collectionRef = db.collection("Contest_Writes");
                CW_collectionRef.document(post_id).update("댓글", FieldValue.arrayUnion(newComment))
                        .addOnSuccessListener(aVoid -> {
                            // 댓글 추가 성공
                            et_comment.setText(""); // 댓글 입력 필드 초기화
                        })
                        .addOnFailureListener(e -> {
                            // 댓글 추가 실패
                            Log.e("Firestore", "Error adding comment", e);
                        });
            }
        });


    }
}