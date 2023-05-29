package com.gachon.mp_termproject;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    private List<Post> postList;

    //===== 뷰홀더 클래스 =====================================================
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_writer; // 작성자
        private TextView tv_content; // 내용
        private TextView tv_reward; // 상금정보
        private ImageView image; // 이미지
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_writer = itemView.findViewById(R.id.tv_writer);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_reward = itemView.findViewById(R.id.tv_reward);
            image = itemView.findViewById(R.id.image);
        }

        public void bind(Post post) {
            tv_writer.setText(post.getWriter().toString());
            tv_content.setText(post.getContent().toString());
            tv_reward.setText(post.getReward().get("1").toString());
            Glide.with(post.getContext())
                    .load(post.getimageURI())
                    .into(image);
        }
    }

    //----- 생성자 --------------------------------------
    // 생성자를 통해서 데이터를 전달받도록 함
    public PostAdapter (List<Post> postData) {
        postList = postData;
    }
    //--------------------------------------------------

    @Override   // ViewHolder 객체를 생성하여 리턴한다.
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_content_reward, parent, false);
        PostAdapter.ViewHolder viewHolder = new PostAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override   // ViewHolder안의 내용을 position에 해당되는 데이터로 교체한다.
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.bind(post);
    }

    @Override   // 전체 데이터의 갯수를 리턴한다.
    public int getItemCount() {
        return postList.size();
    }
}
