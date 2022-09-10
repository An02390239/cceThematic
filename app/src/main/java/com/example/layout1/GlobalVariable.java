package com.example.layout1;

import android.app.Application;

import java.util.ArrayList;

public class GlobalVariable extends Application {
    private String AC,category,username,password, postername;
    private String[] arrayList, personaldata;
    private ArrayList<String> ALL_data;
    private boolean refresh = false,search = false;

    public void setAC(String ac){
        this.AC = ac;
    }

    public String getAC(){
        return AC;
    }

    public void setBoardList(String[] s){
        this.arrayList = s;
    }
    public String[] getBoardList(){
        return arrayList;
    }

    public void set_category(String s){
        this.category = s;
    }
    public String get_category(){
        return category;
    }

    public void setUsername(String u){this.username = u;}
    public String getUsername(){return username;}

    public void setPassword(String p){this.password  = p;}
    public String getPassword(){return password;}

    public void setpostername(String s){
        this.postername = s;
    }
    public String getpostername(){
        return postername;
    }

    public void setArrayList(ArrayList<String> s){
        this.ALL_data = s;
    }
    public ArrayList<String> getArrayList(){
        return ALL_data;
    }

    public void setRefresh(boolean r){
        this.refresh = r;
    }
    public boolean getRefresh(){
        return refresh;
    }


    public void setSearch(boolean s){
        this.search = s;
    }
    public boolean getSearch(){
        return search;
    }

    public void setPersonaldata(String[] p){
        this.personaldata = p;
    }
    public String[] getPersonaldata(){

        return personaldata;
    }
}
