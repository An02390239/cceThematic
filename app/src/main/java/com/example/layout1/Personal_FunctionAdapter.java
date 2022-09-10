package com.example.layout1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class Personal_FunctionAdapter extends ArrayAdapter<FunctionItem> {
//個人頁面的list Adapter
    public Personal_FunctionAdapter(@NonNull Context context, int resource, @NonNull List<FunctionItem> objects) {
        super(context, resource, objects);
    }

    public View getView(int position, View converView, ViewGroup parent){
        com.example.layout1.FunctionItem functionItem = getItem(position);
        View oneItem = LayoutInflater.from(getContext()).inflate(R.layout.personal_functionlist_item,parent,false);
        ImageView imageView = oneItem.findViewById(R.id.imageView3);
        TextView textView = oneItem.findViewById(R.id.textView7);

        imageView.setImageResource(functionItem.getImageId());
        textView.setText(functionItem.getText());
        return oneItem;
    }
}
