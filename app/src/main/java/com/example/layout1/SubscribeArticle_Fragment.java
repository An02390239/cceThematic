package com.example.layout1;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class SubscribeArticle_Fragment extends Fragment {

    private final String My_PostURL = Login_Fragment.My_PostURL;
    private String SAF_splitword = Login_Fragment.splitword;
    private String article_context, SAF_postresult,SAF_username,SAF_entrytime,SAF_downlink = Login_Fragment.Downlink;
    private ArrayList<String> article_get_dataSource, article_put_dataSource,
            SAF_article_id, SAF_poster, SAF_allarticlepic,SAF_arttitle,SAF_arttheme,SAF_artcontext;
    private String[] article_data, article_data_send,SAF_imagelinks,SAF_persondata_aftersplit;
    private ThreadHandler threadHandler = new ThreadHandler();
    private RecyclerView recyclerView;
    private ArrayList<CardMember> SAF_memberList;
    private GlobalVariable SAF_getusername, SAF_refresh,SAF_setpersonaldata;
    private int SAF_countbit = 0,SAF_choosearticle;
    private ArrayList<Bitmap> SAF_allbitmap;
    public SubscribeArticle_Fragment() { }

    //訂閱公告的首頁

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_2,container,false);

        init();



        SAF_username = SAF_getusername.getUsername();
        recyclerView = view.findViewById(R.id.recycleView);
        SubBoardListRunnable sub = new SubBoardListRunnable();
        Thread subpost = new Thread(sub);
        subpost.start();


        if(container == null) return null;

        return view;
    }

    private void init(){
        SAF_getusername = (GlobalVariable)requireActivity().getApplicationContext();
        SAF_setpersonaldata = (GlobalVariable)requireActivity().getApplicationContext();
        article_get_dataSource = new ArrayList<>();
        article_put_dataSource = new ArrayList<>();
        SAF_article_id = new ArrayList<>();
        SAF_poster = new ArrayList<>();
        SAF_allarticlepic = new ArrayList<>();
        SAF_arttheme = new ArrayList<>();
        SAF_arttitle = new ArrayList<>();
        SAF_artcontext = new ArrayList<>();
        SAF_allbitmap = new ArrayList<>();
        SAF_memberList = new ArrayList<>();
    }
    //藉由Post從資料庫拿出該使用者的個人資料
    private class PersonalInformationRunnable implements Runnable{
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("function","personalInformation"));
            params.add(new BasicNameValuePair("username",SAF_poster.get(SAF_choosearticle)));
            //將參數加入params這個List中等待post
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //帶上post參數且將參數設置為UTF-8的格式
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //執行
                SAF_postresult = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                //把結果(php的echo內容)取出轉換成String型態且為UTF-8的格式
                SAF_persondata_aftersplit = SAF_postresult.split("u,4t;4e04");
            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }
            SAF_setpersonaldata.setPersonaldata(SAF_persondata_aftersplit);

        }// run()
    }


    private class GetImage extends AsyncTask<String , Integer , Bitmap> {
        private ProgressDialog progressBar;

        @Override
        protected void onPreExecute() {
            //執行前 設定可以在這邊設定
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... imageUrl) {
            //執行中 在背景做事情
            //因為不會用到但不能為空，所以隨便找一張塞進去
            Bitmap nullbit = BitmapFactory.decodeResource(getResources(),R.drawable.a);
            for (String url : imageUrl){
                if (url.equals("沒有圖片")){
                    SAF_allbitmap.add(nullbit);
                    SAF_countbit += 1;
                }else {
                    try
                    {
                        URL imagelink = new URL(SAF_downlink+url);
                        HttpURLConnection connection = (HttpURLConnection) imagelink.openConnection();
                        connection.setDoInput(true);
                        InputStream input = connection.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(input);
                        SAF_allbitmap.add(bitmap);

                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    SAF_countbit += 1;
                }

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //執行中 可以在這邊告知使用者進度
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //執行後 完成背景任務
            super.onPostExecute(bitmap);
            //創建公告

            if (SAF_arttitle != null && SAF_arttheme != null && SAF_artcontext != null){
                for (int i = 0;i < SAF_arttitle.size();i++){
                    if (SAF_allarticlepic.get(i).equals("沒有圖片")){

                        //如果沒有圖片就只顯示文字的部分
                        SAF_memberList.add(new CardMember(SAF_artcontext.get(i),
                                SAF_arttitle.get(i),
                                SAF_arttheme.get(i),
                                null,
                                10,10,10));

                    }else {
                        SAF_memberList.add(new CardMember(SAF_artcontext.get(i),
                                SAF_arttitle.get(i),
                                SAF_arttheme.get(i),
                                SAF_allbitmap.get(i),
                                10, 10, 10));

                    }

                    //將設訂好的公告加入recyclerView中顯示出來
                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
                    recyclerView.setAdapter(new MemberAdapter(requireActivity(), SAF_memberList));
                }
            }
        }
    }


    //訂閱公告處理
    public class ThreadHandler extends Handler {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            article_data_send = bundle.getStringArray("article_data");
            article_get_dataSource.addAll(Arrays.asList(article_data_send));
            //announcement_get_dataSource.remove(announcement_get_dataSource.size()-1);//因為不需要顯示出公告數量所以刪除他
            for (int i = 0; i< article_get_dataSource.size()-1; i+=7) {
                /*article_context = article_get_dataSource.get(i + 3);
                if (article_context.length() > 18) {
                    article_context = article_context.substring(0, 18) + ".....";
                }//限制文章字數*/
                article_put_dataSource.add(article_get_dataSource.get(i) + SAF_splitword +
                        article_get_dataSource.get(i + 1) + SAF_splitword +
                        article_get_dataSource.get(i + 2) + SAF_splitword +
                        article_get_dataSource.get(i + 3) + SAF_splitword +
                        article_get_dataSource.get(i + 4) + SAF_splitword +
                        article_get_dataSource.get(i + 5) + SAF_splitword +
                        article_get_dataSource.get(i + 6));
            /*announcement_get_dataSource.get(i)為論壇id,announcement_get_dataSource.get(i+1)為文章標題,
            announcement_get_dataSource.get(i+2)為主題,announcement_get_dataSource.get(i+3)為標題圖片,
            announcement_get_dataSource.get(i+4)為文章內容,announcement_get_dataSource.get(i+5)為時間,
            announcement_get_dataSource.get(i+6)為發文者帳號*/
                SAF_article_id.add(article_get_dataSource.get(i));//記錄標題id
                SAF_arttitle.add(article_get_dataSource.get(i + 1));//記錄文章標題
                SAF_arttheme.add(article_get_dataSource.get(i + 2));//記錄主題
                SAF_allarticlepic.add(article_get_dataSource.get(i + 3));//記錄標題圖片
                SAF_artcontext.add(article_get_dataSource.get(i + 4));//記錄文章內容
                SAF_poster.add(article_get_dataSource.get(i + 6));//記錄發文者

            }
            SAF_imagelinks = new String[SAF_allarticlepic.size()];
            SAF_imagelinks = SAF_allarticlepic.toArray(SAF_imagelinks);

            new GetImage().execute(SAF_imagelinks);



        }

    }
    //藉由Post從資料庫拿出所有訂閱公告
    public class SubBoardListRunnable implements Runnable{
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("function","subscribeB"));
            params.add(new BasicNameValuePair("username",SAF_username));
            //將參數加入params這個List中等待post
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //帶上post參數且將參數設置為UTF-8的格式
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //執行
                SAF_postresult = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                //把結果(php的echo內容)取出轉換成String型態且為UTF-8的格式
                article_data = SAF_postresult.split("u,4t;4e04");
            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }
            Message msg =
                    threadHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putStringArray("article_data", article_data);
            msg.setData(bundle);
            threadHandler.sendMessage(msg);
        }// run()
    }// class TimerRunnable

    //訂閱公告內容處理
    public class MemberAdapter extends RecyclerView.Adapter<SubscribeArticle_Fragment.MemberAdapter.ViewHolder> {

        private Context context;
        private List<CardMember> memberList;

        MemberAdapter(Context context, List<CardMember> memberList){
            this.context = context;
            this.memberList = memberList;
        }

        @NonNull
        @Override
        public SubscribeArticle_Fragment.MemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.articlecard,parent,false);
            return new SubscribeArticle_Fragment.MemberAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            CardMember member = memberList.get(position);
            //holder.imageId.setImageResource(member.getImage());
            holder.textArticle.setText(member.getArticle());
            holder.textTitle.setText(member.getTitle());
            holder.texttheme.setText(member.getTheme());

            holder.articlepic.setImageBitmap(member.getPic());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //記錄點擊的是第幾個公告
                    SAF_choosearticle = position;
                    //執行獲取個人資訊的動作
                    PersonalInformationRunnable personalInformationRunnable = new PersonalInformationRunnable();
                    Thread GetPersonalInformation = new Thread(personalInformationRunnable);
                    GetPersonalInformation.start();
                    getTime();
                    //AA = Article_Activity
                    Intent toAA = new Intent(requireActivity(),Article_Activity.class);
                    toAA.putExtra("context_toAA", article_put_dataSource.get(position));
                    toAA.putExtra("entrytime",SAF_entrytime);
                    startActivity(toAA);
                }
            });
        }
        private void getTime(){
            SimpleDateFormat nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            nowDate.setTimeZone(TimeZone.getTimeZone("GMT+08"));//台灣為GMT+8時區
            Date curDate = new Date(System.currentTimeMillis()) ;
            SAF_entrytime = nowDate.format(curDate);
        }

        @Override
        public int getItemCount() {
            return memberList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            TextView textTitle,textArticle,texttheme,textMessage,textCollect,textSubscription;
            ImageView articlepic;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textTitle = itemView.findViewById(R.id.textTitle);
                textArticle = itemView.findViewById(R.id.textArticle);
                textMessage = itemView.findViewById(R.id.textMessage);
                textSubscription = itemView.findViewById(R.id.textSubscription);
                texttheme = itemView.findViewById(R.id.texttheme);
                articlepic = itemView.findViewById(R.id.articlepic);
                //imageId = itemView.findViewById(R.id.imageView);

            }
        }
    }

}