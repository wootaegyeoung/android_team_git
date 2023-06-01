package com.gachon.mp_termproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class Post_freeAdapter extends RecyclerView.Adapter<Post_freeAdapter.ViewHolder> {

    private List<Post_free> post_freeList;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tv_writer;
            private TextView tv_content;
            private ImageView image;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_writer = itemView.findViewById(R.id.tv_writer_free);
                tv_content = itemView.findViewById(R.id.tv_content);
                image = itemView.findViewById(R.id.image_free);
            }

            public void bind(Post_free post_free) {
                tv_writer.setText(post_free.getWriter());
                tv_content.setText(post_free.getContent());
                Glide.with(post_free.getContext())
                        .load(post_free.getImageURI())
                        .into(image);
            }
        }

    public Post_freeAdapter(List<Post_free> post_freeData) {
        post_freeList = post_freeData;
    }

    public interface OnItemClickListener {
        void onItemClick(Post_free post_free);
    }
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
    @NonNull
    @Override
    public Post_freeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_content_free, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post_free post_free = post_freeList.get(position);
        holder.bind(post_free);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(post_free);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return post_freeList.size();
    }
}
