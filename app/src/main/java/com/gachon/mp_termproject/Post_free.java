package com.gachon.mp_termproject;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;

public class Post_free implements Parcelable {
    private String title; // 글 제목
    private String writer; // 글 작성자
    private String content; // 글 내용
    private Uri imageURI; // 이미지
    private List<Comment> commentList; // 댓글 모음

    private String send_time; // 올린시간
    private String post_id; // 파이어 스토어 상에서의 문서 id
    private Context context;

    public Post_free(String title, String writer, String content, Uri imageURI, List<Comment> commentList, String send_time, String post_id, Context context) {
        this.title = title;
        this.writer = writer;
        this.content = content;
        this.imageURI = imageURI;
        this.commentList = commentList;
        this.send_time = send_time;
        this.post_id = post_id;
        this.context = context;
    }

    protected Post_free(Parcel in) {
        title = in.readString();
        writer = in.readString();
        content = in.readString();
        imageURI = in.readParcelable(Uri.class.getClassLoader());
        commentList = in.createTypedArrayList(Comment.CREATOR);
        post_id = in.readString();
    }

    public static final Creator<Post_free> CREATOR = new Creator<Post_free>() {
        @Override
        public Post_free createFromParcel(Parcel in) {
            return new Post_free(in);
        }

        @Override
        public Post_free[] newArray(int size) {
            return new Post_free[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getWriter() {
        return writer;
    }

    public String getContent() {
        return content;
    }


    public String getSend_time() {
        return send_time;
    }
    public Uri getImageURI() {
        return imageURI;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }
    public String getPost_id() {
        return post_id;
    }
    public Context getContext() {
        return context;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(writer);
        dest.writeString(content);
        dest.writeParcelable(imageURI, flags);
        dest.writeTypedList(commentList);
        dest.writeString(post_id);
    }

}
