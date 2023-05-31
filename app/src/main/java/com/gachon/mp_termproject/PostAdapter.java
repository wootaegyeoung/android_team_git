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

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private List<Post> postList;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tv_writer;
            private TextView tv_content;
            private TextView tv_reward;
            private ImageView image;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_writer = itemView.findViewById(R.id.tv_writer);
                tv_content = itemView.findViewById(R.id.tv_content);
                tv_reward = itemView.findViewById(R.id.tv_reward);
                image = itemView.findViewById(R.id.image);
            }

            public void bind(Post post) {
                tv_writer.setText(post.getWriter());
                tv_content.setText(post.getContent());
                tv_reward.setText(post.getReward().get("1").toString());
                Glide.with(post.getContext())
                        .load(post.getImageURI())
                        .into(image);
            }
        }

    public PostAdapter(List<Post> postData) {
        postList = postData;
    }

    public interface OnItemClickListener {
        void onItemClick(Post post);
    }
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_content_reward, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.bind(post);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(post);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
}
