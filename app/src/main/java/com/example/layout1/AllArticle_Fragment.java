package com.example.layout1;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
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
import java.util.Locale;
import java.util.TimeZone;


//全部文章首頁
public class AllArticle_Fragment extends Fragment {

    private final String My_PostURL = Login_Fragment.My_PostURL;
    private String AAF_splitword = Login_Fragment.splitword,AAF_downlink = Login_Fragment.Downlink;
    private String article_context, AAF_postresult,AAF_entrytime,AAF_departtime,AAF_articleid,AAF_picname;
    private ArrayList<String> article_get_dataSource, article_all_dataSource, article_put_dataSource, AAF_article_id
            , article_all1_dataSource,AAF_poster, AAF_allarticlepic;
    private ArrayList<String> AAF_artcontext,AAF_arttitle,AAF_arttheme,AAF_searchlist;
    private ArrayList<Bitmap> AAF_allbitmap;
    private String[] AAF_article_data, AAF_article_data_send,AAF_imagelinks, AAF_article_data_search
            ,AAF_persondata_aftersplit;
    private BoardListHandler threadHandler = new BoardListHandler();
    private RecyclerView AAF_recyclerView;
    private ArrayList<CardMember> AAF_memberList;
    private GlobalVariable AAF_alldata , AAF_refresh,AAF_search,AAF_searchdatafromAFPA,AAF_setpersonaldata;
    private Bitmap[] AAF_allimage;
    private Bitmap AAF_nullbit;
    private ProgressDialog AAF_progressBar;
    private int AAF_countbit = 0,AAF_choosearticle;


    public AllArticle_Fragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.fragment_1,container,false);
        AAF_recyclerView = view.findViewById(R.id.AllrecyclerView);
        AAF_search = (GlobalVariable)requireActivity().getApplicationContext();
        AAF_refresh = (GlobalVariable) requireActivity().getApplicationContext();
        AAF_searchdatafromAFPA = (GlobalVariable) requireActivity().getApplicationContext();
        AAF_setpersonaldata = (GlobalVariable) requireActivity().getApplicationContext();
        ArrayListInit();


        AAF_progressBar = new ProgressDialog(requireActivity());
        AAF_progressBar.setMessage("Loading...");
        AAF_progressBar.setCancelable(false);
        AAF_progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        AAF_progressBar.show();

        //判斷是否有搜尋
        if (!AAF_search.getSearch()){
            //無搜尋，顯示全部公告
            BoardListRunnable postRunnable = new BoardListRunnable();
            Thread post = new Thread(postRunnable);
            post.start();
            return view;
        }else {
            //有搜尋，只顯示有搜尋到的公告
            AAF_searchlist = AAF_searchdatafromAFPA.getArrayList();

            if (AAF_searchlist!=null){
                for (int i = 0;i < AAF_searchlist.size()-1;i+=7){

                    //紀錄所有公告資料，進入AA之後用來顯示  AA=Article_Activity
                    article_put_dataSource.add(AAF_searchlist.get(i) + AAF_splitword +
                            AAF_searchlist.get(i + 1) + AAF_splitword +
                            AAF_searchlist.get(i + 2) + AAF_splitword +
                            AAF_searchlist.get(i + 3) + AAF_splitword +
                            AAF_searchlist.get(i + 4) + AAF_splitword +
                            AAF_searchlist.get(i + 5) + AAF_splitword +
                            AAF_searchlist.get(i + 6));
            /*announcement_get_dataSource.get(i)為論壇id,announcement_get_dataSource.get(i+1)為文章標題,
            announcement_get_dataSource.get(i+2)為主題,announcement_get_dataSource.get(i+3)為標題圖片,
            announcement_get_dataSource.get(i+4)為文章內容,announcement_get_dataSource.get(i+5)為時間,
            announcement_get_dataSource.get(i+6)為發文者帳號*/

                    AAF_article_id.add(AAF_searchlist.get(i));//記錄標題id
                    AAF_arttitle.add(AAF_searchlist.get(i + 1));//記錄文章標題
                    AAF_arttheme.add(AAF_searchlist.get(i + 2));//記錄主題
                    AAF_allarticlepic.add(AAF_searchlist.get(i + 3));//記錄標題圖片
                    AAF_artcontext.add(AAF_searchlist.get(i + 4));//記錄文章內容
                    AAF_poster.add(AAF_searchlist.get(i + 6));//記錄發文者
                }
                AAF_imagelinks = new String[AAF_allarticlepic.size()];

                AAF_imagelinks = AAF_allarticlepic.toArray(AAF_imagelinks);
                new GetImage().execute(AAF_imagelinks);
            }



            AAF_refresh.setSearch(false);
        }



        if(container == null) return null;

        return view;
    }
    private void ArrayListInit(){
        article_get_dataSource = new ArrayList<>();
        article_all_dataSource = new ArrayList<>();
        article_all1_dataSource = new ArrayList<>();
        article_put_dataSource = new ArrayList<>();
        AAF_article_id = new ArrayList<>();
        AAF_poster = new ArrayList<>();
        AAF_allarticlepic = new ArrayList<>();
        AAF_artcontext = new ArrayList<>();
        AAF_arttheme = new ArrayList<>();
        AAF_arttitle = new ArrayList<>();
        AAF_allbitmap = new ArrayList<>();
        AAF_memberList = new ArrayList<>();
        AAF_searchlist = new ArrayList<>();

    }

    //藉由Post從資料庫拿出該使用者的個人資料
    private class PersonalInformationRunnable implements Runnable{
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("function","personalInformation"));
            params.add(new BasicNameValuePair("username",AAF_poster.get(AAF_choosearticle)));
            //將參數加入params這個List中等待post
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //帶上post參數且將參數設置為UTF-8的格式
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //執行
                AAF_postresult = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                //把結果(php的echo內容)取出轉換成String型態且為UTF-8的格式
                AAF_persondata_aftersplit = AAF_postresult.split("u,4t;4e04");
            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }
            AAF_setpersonaldata.setPersonaldata(AAF_persondata_aftersplit);

        }// run()
    }

    //BoardListRunnable的Handler
    private class BoardListHandler extends Handler {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            AAF_article_data_send = bundle.getStringArray("article_data");
            article_get_dataSource.addAll(Arrays.asList(AAF_article_data_send));
            for (int i = 0; i< article_get_dataSource.size()-1; i+=7) {
                /*article_context = article_get_dataSource.get(i + 3);
                if (article_context.length() > 18) {
                    article_context = article_context.substring(0, 18) + ".....";
                }//限制文章字數*/

                article_put_dataSource.add(article_get_dataSource.get(i) + AAF_splitword +
                        article_get_dataSource.get(i + 1) + AAF_splitword +
                        article_get_dataSource.get(i + 2) + AAF_splitword +
                        article_get_dataSource.get(i + 3) + AAF_splitword +
                        article_get_dataSource.get(i + 4) + AAF_splitword +
                        article_get_dataSource.get(i + 5) + AAF_splitword +
                        article_get_dataSource.get(i + 6));


            /*announcement_get_dataSource.get(i)為論壇id,announcement_get_dataSource.get(i+1)為文章標題,
            announcement_get_dataSource.get(i+2)為主題,announcement_get_dataSource.get(i+3)為標題圖片,
            announcement_get_dataSource.get(i+4)為文章內容,announcement_get_dataSource.get(i+5)為時間,
            announcement_get_dataSource.get(i+6)為發文者帳號*/
                AAF_article_id.add(article_get_dataSource.get(i));//記錄標題id
                AAF_arttitle.add(article_get_dataSource.get(i + 1));//記錄文章標題
                AAF_arttheme.add(article_get_dataSource.get(i + 2));//記錄主題
                AAF_allarticlepic.add(article_get_dataSource.get(i + 3));//記錄標題圖片
                AAF_artcontext.add(article_get_dataSource.get(i + 4));//記錄文章內容
                AAF_poster.add(article_get_dataSource.get(i + 6));//記錄發文者


            }
            AAF_imagelinks = new String[AAF_allarticlepic.size()];

            AAF_imagelinks = AAF_allarticlepic.toArray(AAF_imagelinks);
            new GetImage().execute(AAF_imagelinks);



            //將所有公告的資料存成全域資料
            AAF_alldata = (GlobalVariable)requireActivity().getApplicationContext();
            AAF_alldata.setArrayList(article_put_dataSource);




        }

    }
    //藉由Post從資料庫拿出所有公告
    private class BoardListRunnable implements Runnable{
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("function","boardList"));
            //將參數加入params這個List中等待post
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //帶上post參數且將參數設置為UTF-8的格式
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //執行
                AAF_postresult = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                //把結果(php的echo內容)取出轉換成String型態且為UTF-8的格式
                AAF_article_data = AAF_postresult.split("u,4t;4e04");
            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }
            Message msg = threadHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putStringArray("article_data", AAF_article_data);
            msg.setData(bundle);
            threadHandler.sendMessage(msg);
        }// run()
    }

    //用來處理該文章在顯示所有公告處的小圖然後顯示出來
    private class GetImage extends AsyncTask<String , Integer , Bitmap>{
                                   //執行續的一種


        @Override
        protected void onPreExecute() {
            //執行前 設定可以在這邊設定

            super.onPreExecute();
        }

        @Override
        /*String...可以傳單一一個String，或者是一個String陣列都可以。
        假如你丟只一個String進去，你只要取得第一個元素即可。
        EX:String urlStr = imageUrl[0];*/
        protected Bitmap doInBackground(String... imageUrl) {

            //執行中 在背景做事情
            int progress = 0;
            //因為不會用到但不能為空，所以隨便找一張塞進去
            AAF_nullbit = BitmapFactory.decodeResource(getResources(),R.drawable.a);

            //讀取網路圖片，型態為Bitmap
            for (String url : imageUrl){
                if (url.equals("沒有圖片")){
                    AAF_allbitmap.add(AAF_nullbit);
                    AAF_countbit += 1;//紀錄圖片數
                }else {
                    try
                    {
                        URL imagelink = new URL(AAF_downlink+url);
                        HttpURLConnection connection = (HttpURLConnection) imagelink.openConnection();
                        connection.setDoInput(true);
                        InputStream input = connection.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(input);
                        AAF_allbitmap.add(bitmap);

                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    AAF_countbit += 1;//紀錄圖片數
                }

            }



            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            //執行中 可以在這邊告知使用者進度
            super.onProgressUpdate(values);
            //取得更新的進度
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //執行後 完成背景任務
            super.onPostExecute(bitmap);
            //創建公告

            if (AAF_arttitle != null && AAF_arttheme != null && AAF_artcontext != null){
                for (int i = 0;i < AAF_arttitle.size();i++){
                    if (AAF_allarticlepic.get(i).equals("沒有圖片")){

                        //如果沒有圖片就只顯示文字的部分
                        AAF_memberList.add(new CardMember(AAF_artcontext.get(i),
                                AAF_arttitle.get(i),
                                AAF_arttheme.get(i),
                                null,
                                10,10,10));

                    }else {
                        AAF_memberList.add(new CardMember(AAF_artcontext.get(i),
                                AAF_arttitle.get(i),
                                AAF_arttheme.get(i),
                                zoombig(AAF_allbitmap.get(i),1000),
                                10, 10, 10));
                    }

                }
            }

            //將設訂好的公告加入recyclerView中顯示出來
            AAF_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
            AAF_recyclerView.setAdapter(new MemberAdapter(requireActivity(), AAF_memberList));
            AAF_progressBar.dismiss();

        }
    }
    /**Bitmap放大的方法*/
    private static Bitmap zoombig(Bitmap bitmap,int newscale) {
        Matrix matrix = new Matrix();//計算縮放比例
        float scaleWidth = ((float) newscale)/bitmap.getWidth();
        matrix.postScale(scaleWidth,scaleWidth); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
    }

    //訂閱公告內容處理
    private class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {

        private Context context;
        private List<CardMember> memberList;
        private ImageView imageView;


        MemberAdapter(Context context, List<CardMember> memberList){
            this.context = context;
            this.memberList = memberList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.articlecard,parent,false);
            imageView = view.findViewById(R.id.articlepic);
            return new ViewHolder(view);
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
                    AAF_choosearticle = position;
                    //執行獲取個人資訊的動作
                    PersonalInformationRunnable personalInformationRunnable = new PersonalInformationRunnable();
                    Thread GetPersonalInformation = new Thread(personalInformationRunnable);
                    GetPersonalInformation.start();
                    getTime();
                    //點擊任意公告會前往該公告內容的頁面
                    //AA = Article_Activity
                    Intent toAA = new Intent(requireActivity(),Article_Activity.class);
                    toAA.putExtra("context_toAA", article_put_dataSource.get(position));
                    toAA.putExtra("entrytime",AAF_entrytime);

                    startActivity(toAA);
                    //requireActivity().finish();
                }
            });
        }
        private void getTime(){
            SimpleDateFormat nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.TAIWAN);
            nowDate.setTimeZone(TimeZone.getTimeZone("GMT+08"));//台灣為GMT+8時區
            Date curDate = new Date(System.currentTimeMillis()) ;
            AAF_entrytime = nowDate.format(curDate);
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

    @Override
    public void onResume() {
        super.onResume();
        //Log.e("GG", AAF_refresh.getRefresh()+"");
        if (AAF_refresh.getRefresh()){
            requireActivity().recreate();
            AAF_refresh.setRefresh(false);
        }
        if (AAF_search.getSearch()){
            requireActivity().recreate();
        }
    }






}