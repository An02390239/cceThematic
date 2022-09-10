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
//訂閱列表的Adapter
public class PeopleSubAdapter extends ArrayAdapter<PeopleSubItem> {
    public PeopleSubAdapter(@NonNull Context context, int resource, @NonNull List<PeopleSubItem> objects) {
        super(context, resource, objects);
    }

    public View getView(int position, View converView, ViewGroup parent){
        PeopleSubItem peopleSubItem = getItem(position);
        View oneItem = LayoutInflater.from(getContext()).inflate(R.layout.personal_sub_item,parent,false);
        ImageView imageView = oneItem.findViewById(R.id.imageView4);
        TextView textView = oneItem.findViewById(R.id.textView8);

        imageView.setImageResource(peopleSubItem.getImageId());
        textView.setText(peopleSubItem.getText());
        return oneItem;
    }
}
