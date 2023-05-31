package com.gachon.mp_termproject;

public class Schedule {

    private String month; // 월
    private int day; // 날
    private String contest_title; // 공모전 제목
    private String contest_period; // 공모전 기간
    private Post connected_post; // 연결된 글

    public Schedule(String month, int day, String contest_title, String contest_period, Post connected_post){
        this.month = month;
        this.day = day;
        this.contest_title = contest_title;
        this.contest_period = contest_period;
        this.connected_post = connected_post;
    }


    public void setMonth(String month){
        this.month = month;
    }

    public void setDay(int day){
        this.day = day;
    }

    public void setContest_title(String contest_title){
        this.contest_title = contest_title;
    }

    public void setContest_period(String contest_period){
        this.contest_period = contest_period;
    }

    public void setConnected_post(Post connected_post){
        this.connected_post = connected_post;
    }

    public String getMonth(){
        return month;
    }

    public int getDay(){
        return day;
    }

    public String getContest_title(){
        return contest_title;
    }

    public String getContest_period(){
        return contest_period;
    }
    public Post getConnected_post(){
        return connected_post;
    }
}
