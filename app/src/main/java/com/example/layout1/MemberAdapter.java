package com.example.layout1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder>{

    //這個不用理他 先留著 我在想能不能優化程式碼

    private Context context;
    private List<CardMember> memberList;

    MemberAdapter(Context context, List<CardMember> memberList){
        this.context = context;
        this.memberList = memberList;
    }

    @NonNull
    @Override
    public MemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.articlecard,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberAdapter.ViewHolder holder, int position) {
        final CardMember member = memberList.get(position);
        //holder.imageId.setImageResource(member.getImage());
        holder.textArticle.setText(member.getArticle());
        holder.textTitle.setText(member.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast toast =Toast.makeText(context,"test",Toast.LENGTH_SHORT);
                toast.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textTitle,textArticle,textMessage,textCollect,textSubscription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textArticle = itemView.findViewById(R.id.textArticle);
            textMessage = itemView.findViewById(R.id.textMessage);
            textSubscription = itemView.findViewById(R.id.textSubscription);
            //imageId = itemView.findViewById(R.id.imageView);

        }
    }
}
