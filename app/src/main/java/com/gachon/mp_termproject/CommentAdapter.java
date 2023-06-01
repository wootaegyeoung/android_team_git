package com.gachon.mp_termproject;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private List<Comment> commentList;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView cm_writer;
        private TextView cm_content;
        private TextView cm_cnt_recommend;
        private ImageButton ib_like;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cm_writer = itemView.findViewById(R.id.cm_writer);
            cm_content = itemView.findViewById(R.id.cm_comment);
            cm_cnt_recommend = itemView.findViewById(R.id.cm_cnt_recomm);
            ib_like = itemView.findViewById(R.id.btn_like);
        }

        public void bind(Comment comment, int index, int flag, int commentType) {
            cm_writer.setText(comment.getWriter());
            cm_content.setText(comment.getComment_content());
            cm_cnt_recommend.setText(Integer.toString(comment.getCnt_recommend()));
            if(flag == 0)
                ib_like.setEnabled(true);
            else if(flag == 1)
                ib_like.setEnabled(false);
            ib_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int cnt_recommend = comment.getCnt_recommend();
                    cnt_recommend++; // 추천수 증가
                    comment.setCnt_recommend(cnt_recommend); // 추천수 갱신



                    // 갱신된 추천 수를 디비에 갱신하기
                    String collection = "";

                    if(commentType == 1)
                        collection = "Free_Writes";
                    else if(commentType == 2)
                        collection = "Contest_Writes";
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference documentRef = FirebaseFirestore.getInstance().collection(collection).document(comment.getParentPostId());

                    documentRef.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        List<Comment> arrayField = (List<Comment>) documentSnapshot.get("댓글");
                                        if (arrayField != null) {
                                            int indexToChange = index; // 변경할 값의 인덱스
                                            Comment newComment = comment; // 변경할 새로운 값

                                            arrayField.set(indexToChange, newComment); // 특정 인덱스의 값을 변경

                                            Map<String, Object> updateData = new HashMap<>();
                                            updateData.put("댓글", arrayField); // 변경된 배열 필드를 업데이트할 데이터에 추가

                                            documentRef.update(updateData)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // 업데이트 성공시 실행되는 작업
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // 업데이트 실패시 실행되는 작업
                                                        }
                                                    });
                                        }
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // 문서 가져오기 실패시 실행되는 작업
                                }
                            });

                }
            });
        }
    }
    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }


    // 클릭이벤트 처리 부분
    public interface OnItemClickListener {
        void onItemClick(Comment comment);
    }
    private CommentAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(CommentAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_comment, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.bind(comment, position, comment.getFlag(), comment.getCommentType());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(comment);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
}
