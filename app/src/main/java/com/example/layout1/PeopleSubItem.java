package com.example.layout1;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PeopleSubItem {
//列表包含大頭貼跟名稱
    private String text;
    private int imageId;

    PeopleSubItem(String text,int imageId){
        this.text = text;
        this.imageId = imageId;

    }
    public List<PeopleSubItem> getAllPeople(){
        List<PeopleSubItem> peopleItems = new ArrayList<PeopleSubItem>();
        PeopleSubItem p = new PeopleSubItem(this.text,this.imageId);
        peopleItems.add(p);

        return peopleItems;
    }

    public String getText(){return text;}

    public void setText(String text){this.text = text;}

    public int getImageId(){return imageId;}

    public void setImageId(int imageId){this.imageId= imageId;}



}
