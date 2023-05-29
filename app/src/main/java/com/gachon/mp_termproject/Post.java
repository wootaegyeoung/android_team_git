package com.gachon.mp_termproject;

import android.content.Context;
import android.net.Uri;

import java.util.List;
import java.util.Map;

public class Post {
    private String title; // 글 제목
    private String writer; // 글 작성자
    private String content; // 글 내용
    private Map<String, Object> reward; // 상금정보
    private Uri imageURI; // 이미지
    private List<Comment> commentList; // 댓글 모음

    private Context context;



    public Post(String title, String writer, String content, Map<String, Object> reward, Uri imageURI, List<Comment> commentList, Context context) {
        this.title = title;
        this.writer = writer;
        this.content = content;
        this.reward = reward;
        this.imageURI = imageURI;
        this.commentList = commentList;
        this.context = context;
    }

    public String getTitle(){
        return title;
    }
    public String getWriter(){
        return writer;
    }
    public String getContent(){
        return content;
    }
    public Map<String, Object> getReward(){
        return reward;
    }
    public Uri getimageURI(){
        return imageURI;
    }

    public List<Comment> getCommentList(){
        return commentList;
    }
    public Context getContext(){
        return context;
    }

}
