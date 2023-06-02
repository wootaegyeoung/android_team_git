package com.gachon.mp_termproject;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Comment implements Parcelable {

    private String writer; // 작성자
    private String comment_content; // 댓글 내용
    private int cnt_recommend; // 추천 수
    private String parentPostId; // 댓글이 속한 글의 id
    private int commentType; // 1: 자유 / 2: 공모전


    public Comment(int commentType) {
        this.parentPostId = "";
        this.writer = "";
        this.comment_content = "";
        this.cnt_recommend = 0;
        this.commentType = commentType;
    }

    public Comment(String writer, String comment, String parentPostId, int commentType) {
        this.writer = writer;
        this.comment_content = comment;
        this.parentPostId = parentPostId;
        this.cnt_recommend = 0; // 첫 추천 수는 0
        this.commentType = commentType;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public void setComment_content(String comment) {
        this.comment_content = comment;
    }

    public void setCnt_recommend(int cnt_recommend) {
        this.cnt_recommend = cnt_recommend;
    }

    public void setParentPostId(String parentPostId) {
        this.parentPostId = parentPostId;
    }


    public void setCommentType(int type) {
        this.commentType = type;
    }

    public String getWriter() {
        return writer;
    }

    public String getComment_content() {
        return comment_content;
    }

    public int getCnt_recommend() {
        return cnt_recommend;
    }

    public String getParentPostId() {
        return parentPostId;
    }


    public int getCommentType() {
        return commentType;
    }

    protected Comment(Parcel in) {
        writer = in.readString();
        comment_content = in.readString();
        cnt_recommend = in.readInt();
        parentPostId = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(writer);
        dest.writeString(comment_content);
        dest.writeInt(cnt_recommend);
        dest.writeString(parentPostId);
    }
}
