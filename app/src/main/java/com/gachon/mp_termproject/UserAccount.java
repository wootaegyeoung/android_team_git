package com.gachon.mp_termproject;

public class UserAccount {

    public UserAccount() {

    }
    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken){
        this.idToken = idToken;
    }
    private String idToken;

    // 이메일
    public String getEmailId() {
        return emialId;
    }
    public void setEmailId(String emailId) {
        this.emialId = emailId;
    }
    private String emialId;

    // 비번
    public String getPassword(){
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    private String password;

    // 이름
    public String getName(){
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    private String name;

    // 전화번호
    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    private String phone;

    public int getReward(){
        return reward;
    }
    public void setReward(int reward) {
        this.reward = reward;
    }
    private int reward;
}
