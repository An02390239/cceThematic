package com.example.layout1;

import java.util.ArrayList;
import java.util.List;

class FunctionItem {
//個人頁面的list item 到時候會放圖示
    private String text;
    private int imageId;

    FunctionItem(String text,int imageId){
        this.text = text;
        this.imageId = imageId;

    }
    static List<com.example.layout1.FunctionItem> getAllfunction(){
        List<com.example.layout1.FunctionItem> functionItems = new ArrayList<com.example.layout1.FunctionItem>();
        functionItems.add(new com.example.layout1.FunctionItem("訂閱列表",R.drawable.ic_subject));
        functionItems.add(new com.example.layout1.FunctionItem("瀏覽紀錄",R.drawable.ic_time_ccw));
        functionItems.add(new com.example.layout1.FunctionItem("更改密碼",R.drawable.ic_password));
        functionItems.add(new com.example.layout1.FunctionItem("登出",R.drawable.ic_logout_fill));
        return functionItems;
    }

    public String getText(){return text;}

    public void setText(String text){this.text = text;}

    public int getImageId(){return imageId;}

    public void setImageId(int imageId){this.imageId= imageId;}

}