package com.example.layout1;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class CardMember {

    //首頁的文章格式

    private String article,title,theme;
    private int like,collect,subscription;
    private Bitmap articlepic;

    public CardMember(){
        super();
    }

    public CardMember(String article, String title, String theme, Bitmap pic, int like, int collect, int subscription){
        super();
        this.article = article;
        this.title = title;
        this.theme = theme;
        this.like = like;
        this.collect = collect;
        this.subscription = subscription;
        this.articlepic = pic;
    }

    public String getArticle(){
        return article;
    }

    public void setArticle(String article){
        this.article = article;
    }


    public Bitmap getPic(){
        return articlepic;
    }

    public void setPic(Bitmap articlepic){
        this.articlepic = articlepic;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTheme(){
        return theme;
    }

    public void setTheme(String theme){
        this.title = theme;
    }

    public int getLike(){ return like; }

    public void setLike(int like){ this.like = like; }

    public int getCollect(){ return collect; }

    public void setCollect(int collect){ this.collect = collect; }

    public int getSubscription(){ return subscription; }

    public void setSubscription(int subscription){ this.subscription = subscription; }

}
