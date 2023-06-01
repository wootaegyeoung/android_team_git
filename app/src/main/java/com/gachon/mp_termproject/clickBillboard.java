package com.gachon.mp_termproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class clickBillboard extends AppCompatActivity {


    List<Comment> commentList;
    CommentAdapter commentAdapter;
    RecyclerView recyclerView;

    FirebaseAuth mAuth = FirebaseAuth.getInstance(); // 로그인정보 갖고오기
    DatabaseReference mDatabaseRef;

    Post post; // 현재 글
    EditText et_comment;
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
        et_comment = findViewById(R.id.cb_comment_edittext);
        ImageButton ib_send = findViewById(R.id.cb_btn_send);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        LinearLayout layout_deadline = findViewById(R.id.layout_deadline);


        layout_deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuDialog();
            }
        });


        // Intent에서 객체를 받음
        Intent intent = getIntent();
        post = intent.getParcelableExtra("selectedPost");

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
                        // 현재 유저와 글의 작성자와 다른 경우 정산하기 버튼 비활성화
                        if (!current_name[0].equals(post.getWriter())) {
                            layout_deadline.setVisibility(View.INVISIBLE);
                        }
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
        commentAdapter = new CommentAdapter(commentList);
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
                    commentAdapter.notifyDataSetChanged(); // 어댑터에 변경 내용 반영
                    recyclerView.setAdapter(commentAdapter);
                }
            }
        });


        // 댓글 전송 버튼
        ib_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = et_comment.getText().toString();
                Comment newComment = new Comment(current_name[0], comment, post_id, 2);
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

    private void showMenuDialog() {
        final String[] selectedWriter = new String[1];

        // 댓글 중 1위 2위 3위 선정하는 다이얼로그
        Dialog dialog1 = new Dialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialog1.setContentView(R.layout.choose_comment);

        TextView tv_first_nick = dialog1.findViewById(R.id.first_nick);
        TextView tv_second_nick = dialog1.findViewById(R.id.second_nick);
        TextView tv_third_nick = dialog1.findViewById(R.id.third_nick);
        Button btn_choose_fin = dialog1.findViewById(R.id.choose_fin);
        Button btn_reset = dialog1.findViewById(R.id.reset);

        // 필드 초기화
        tv_first_nick.setText("");
        tv_second_nick.setText("");
        tv_third_nick.setText("");


        // 특정 댓글을 클릭하고 그 클릭한 댓글을 몇등으로 설정할건지
        Dialog dialog2 = new Dialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialog2.setContentView(R.layout.choose_comment_sub);

        Button btn_setOne = dialog2.findViewById(R.id.btn_setOne);
        Button btn_setTwo = dialog2.findViewById(R.id.btn_setTwo);
        Button btn_setThree = dialog2.findViewById(R.id.btn_setThree);

        btn_setOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이미 선택을 했으면 선택 불가
                String str_first = tv_first_nick.getText().toString();
                String str_second = tv_second_nick.getText().toString();
                String str_third = tv_third_nick.getText().toString();
                // 해당 등수에 이미 선택이 되었거나, 이미 선택된 사람이 중복 선택되는 경우
                if (!str_first.equals("")) {
                    customToastView("이미 선택되었습니다.");
                    return;
                } else if (!str_first.equals("") && (str_second.equals(str_first) || str_third.equals(str_first))) {
                    customToastView("작성자가 중복됩니다.");
                    return;
                }
                tv_first_nick.setText(selectedWriter[0]);
                dialog2.dismiss();
            }
        });
        btn_setTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_first = tv_first_nick.getText().toString();
                String str_second = tv_second_nick.getText().toString();
                String str_third = tv_third_nick.getText().toString();
                // 해당 등수에 이미 선택이 되었거나, 이미 선택된 사람이 중복 선택되는 경우
                if (!str_second.equals("")) {
                    customToastView("이미 선택되었습니다.");
                    return;
                } else if (!str_second.equals("") && (str_first.equals(str_second) || str_third.equals(str_second))) {
                    customToastView("작성자가 중복됩니다.");
                    return;
                }
                tv_second_nick.setText(selectedWriter[0]);
                dialog2.dismiss();
            }
        });
        btn_setThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_first = tv_first_nick.getText().toString();
                String str_second = tv_second_nick.getText().toString();
                String str_third = tv_third_nick.getText().toString();
                // 해당 등수에 이미 선택이 되었거나, 이미 선택된 사람이 중복 선택되는 경우
                if (!str_third.equals("")) {
                    customToastView("이미 선택되었습니다.");
                    return;
                } else if (!str_third.equals("") && (str_first.equals(str_third) || str_second.equals(str_third))) {
                    customToastView("작성자가 중복됩니다.");
                    return;
                }
                tv_third_nick.setText(selectedWriter[0]);
                dialog2.dismiss();
            }
        });


        // 완료 버튼을 누르면 해당 댓글을 쓴 사람의 계정에 reward 값이 갱신됨
        btn_choose_fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_first_nick.getText().toString().equals("") || tv_second_nick.getText().toString().equals("") || tv_third_nick.getText().toString().equals("")) {
                    customToastView("모든 정보를 입력해주세요");
                    return;
                }
                mDatabaseRef = FirebaseDatabase.getInstance().getReference(); // 리얼타임 디비에서 회원정보
                mDatabaseRef.child("UserAccount").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // 데이터 중복갱신 방지
                        int flag_first = 0;
                        int flag_second = 0;
                        int flag_third = 0;

                        Map<String, Long> rewardM = new HashMap<>();
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {

                            // 회원정보 디비에서 name 값과 상금정보를 갖고와 map에 임시저장
                            if (childSnapshot.exists()) {
                                rewardM.put((String) childSnapshot.child("name").getValue(), (Long) childSnapshot.child("reward").getValue());
                            }
                        }
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {

                            // 회원정보 디비에서 name 값을 읽어와 선정된 사람과 대조하여 같은 경우, 그 사람의 상금 정보를 해당 상금 만큼 추가해 갱신
                            if (childSnapshot.exists()) {
                                if (childSnapshot.child("name").getValue().equals(tv_first_nick.getText().toString())) {
                                    flag_first = 1;
                                    for (String s : rewardM.keySet()) {
                                        if (s.equals(tv_first_nick.getText().toString())) {
                                            Long current_reward = rewardM.get(s);
                                            rewardM.replace(s, current_reward + Integer.parseInt((String) post.getReward().get("1")));
                                        }
                                    }
                                }
                                if (childSnapshot.child("name").getValue().equals(tv_second_nick.getText().toString())) {
                                    flag_second = 1;
                                    for (String s : rewardM.keySet()) {
                                        if (s.equals(tv_second_nick.getText().toString())) {
                                            Long current_reward = rewardM.get(s);
                                            rewardM.replace(s, current_reward + Integer.parseInt((String) post.getReward().get("2")));
                                        }
                                    }
                                }
                                if (childSnapshot.child("name").getValue().equals(tv_third_nick.getText().toString())) {
                                    flag_third = 1;
                                    for (String s : rewardM.keySet()) {
                                        if (s.equals(tv_third_nick.getText().toString())) {
                                            Long current_reward = rewardM.get(s);
                                            rewardM.replace(s, current_reward + Integer.parseInt((String) post.getReward().get("3")));
                                        }
                                    }
                                }
                            }
                        }
                        for (String s : rewardM.keySet()) {
                            mDatabaseRef.child("UserAccount").child(s).child("reward").setValue(rewardM.get(s));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_first_nick.setText("");
                tv_second_nick.setText("");
                tv_third_nick.setText("");
            }
        });


        // 메뉴 화면의 레이아웃 설정
        // 댓글 갱신해주는 부분
        // RecyclerView와 어댑터 초기화
        List<Comment> dia_commentList = new ArrayList<>();
        RecyclerView dia_recyclerView = dialog1.findViewById(R.id.recyclerview_select_comment);
        CommentAdapter dia_commentAdapter = new CommentAdapter(dia_commentList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager((Context) this);
        dia_recyclerView.setLayoutManager(linearLayoutManager);
        dia_recyclerView.setAdapter(dia_commentAdapter);


        //리사이클러뷰 아이템 선택시
        dia_commentAdapter.setOnItemClickListener(new CommentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Comment comment) {
                dialog2.show();
                selectedWriter[0] = comment.getWriter();
            }
        });

        db.collection("Contest_Writes").document(post.getPost_id()).addSnapshotListener((snapshot, e) -> {
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
                    newComment.setFlag(1);
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
                Collections.sort(newCommentList, new Comparator<Comment>() {
                    @Override
                    public int compare(Comment o1, Comment o2) {
                        return Integer.compare(o2.getCnt_recommend(), o1.getCnt_recommend());
                    }
                });
                if (newCommentList != null) {
                    dia_commentList.clear(); // 기존 데이터를 모두 제거
                    dia_commentList.addAll(newCommentList); // 새로운 데이터로 업데이트
                    dia_commentAdapter.notifyDataSetChanged(); // 어댑터에 변경 내용 반영
                }
            }
        });


        // 팝업 창 보여주기
        dialog1.show();
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