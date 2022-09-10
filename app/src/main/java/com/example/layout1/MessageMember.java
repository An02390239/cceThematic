package com.example.layout1;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class MessageMember{
    private String AA_userName,AA_userMessage;
    private int AA_imageId;
    private Bitmap AA_messageimage;
    private Drawable AA_headshot;

    public MessageMember(String AA_userName, String AA_userMessage, int imageId){
        this.AA_userName = AA_userName;
        this.AA_userMessage = AA_userMessage;
        this.AA_imageId = imageId;
    }
    public MessageMember(String AA_userName, String AA_userMessage, int imageId, Bitmap messageimage){
        this.AA_userName = AA_userName;
        this.AA_userMessage = AA_userMessage;
        this.AA_imageId = imageId;
        this.AA_messageimage = messageimage;
    }

    public String getAA_userName(){
        return AA_userName;
    }
    public void setAA_userName(String AA_userName){
        this.AA_userName = AA_userName;
    }
    public String getAA_userMessage(){
        return AA_userMessage;
    }
    public void setAA_userMessage(String AA_userMessage){
        this.AA_userMessage = AA_userMessage;
    }
    public int getAA_imageId(){
        return AA_imageId;
    }
    public void setAA_imageId(int AA_imageId){
        this.AA_imageId = AA_imageId;
    }
    public Bitmap getAA_messageimage(){
        return AA_messageimage;
    }
    public void setAA_messageimage(Bitmap messageimage){
        this.AA_messageimage = messageimage;
    }
    public Drawable getAA_headshot(){
        return AA_headshot;
    }
    public void setAA_headshot(Drawable headshot){
        this.AA_headshot = headshot;
    }
}
