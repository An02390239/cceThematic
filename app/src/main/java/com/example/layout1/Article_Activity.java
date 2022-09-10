package com.example.layout1;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Article_Activity extends AppCompatActivity {

    //點進公告頁面

    private final String My_PostURL = Login_Fragment.My_PostURL;
    private int AA_serverResponseCode = 0;
    private TextView AA_contextText, AA_timeText, AA_nameText, AA_titleText;
    private ListViewForScrollView AA_messageList;
    private Button AA_peopleSubButton, AA_articleSubButton,AA_press;
    private CardView AA_likeCard;
    private ImageView avatarImage,AA_likeImage;
    private ArrayList<MessageMember> AA_messageMembers;
    private ArrayAdapter<String> arrayAdapter;
    private String AA_title,AA_context,AA_time,AA_theme,AA_articleid,AA_entrytime,AA_departtime,AA_articlepic;
    private String AA_username,AA_poster, AA_Imagelink,AA_subscribe,AA_postresult,
            AA_downlink = Login_Fragment.Downlink,AA_personmsg,AA_personname,AA_msgpersonname,AA_msgpersonsex;
    private String context_from_AAF, AA_delresult, AA_subresult,AA_uploadimagepath = null;
    private String[] aftersplit,AA_allmessage,AA_imagelinks,AA_personaldata_aftersplit;
    private ArrayList<String> AA_allmsgarraylist, AA_allmsg_image, AA_allmsg_title,AA_allmsg_personname,
            AA_allmsg_username,AA_allmsg_context,AA_allmsg_time,AA_allmsg_sex;
    private ArrayList<Bitmap> AA_allmsg_bitmap;
    private Bitmap AA_bitmap,AA_uploadbitmap,AA_nullbit;
    private AlertDialog.Builder AA_checkdel,AA_deldia;
    private boolean AA_checksubresult = false,AA_articlesubcheck = false,AA_peoplesubcheck = false,
            AA_checkheart = false;
    private Intent from;
    private GlobalVariable AA_getusername,AA_refresh,AA_getpersonaldata;
    private ProgressDialog AA_progressDialog, AA_choosepicprogressbar;
    private int screenWidth,screenHeight,AA_likeclick = 0,AA_allimagenull = 0;
    private int AA_whichperson = 0;
    private SharedPreferences get_articlesubcheck,get_peoplesubcheck,get_messageLike,get_articleLike;
    private EditText AA_messageEdittext;
    private MessageAdapter AA_messageAdapter;
    private BoardMessage_Handler boardMessage_handler = new BoardMessage_Handler();
    private ImageButton AA_selectimage;
    private Editable AA_editable;
    private CharSequence AA_sequence;
    private boolean AA_articleLike = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_activity);


        //用來記錄是否點下訂閱按鈕
        get_articlesubcheck = this.getSharedPreferences("get_articlesubcheck", Context.MODE_PRIVATE);
        get_peoplesubcheck = this.getSharedPreferences("get_peoplesubcheck", Context.MODE_PRIVATE);
        get_articleLike = this.getSharedPreferences("get_articleLike",Context.MODE_PRIVATE);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        DisplayMetrics dm = new DisplayMetrics();
        //取得視窗屬性
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //視窗的寬度
        screenWidth = dm.widthPixels;
        //視窗高度
        screenHeight = dm.heightPixels;


        init();


        AA_progressDialog.setMessage("載入中...");
        AA_progressDialog.show();

        AA_messageAdapter = new MessageAdapter(Article_Activity.this, AA_messageMembers);
        //在這個頁面開始時先分別判斷兩個訂閱按鈕是否被點下
        //int color = getResources().getColor(R.color.gray1);

        AA_username = AA_getusername.getUsername();
        //三個點
        getOverflowMenu();

        //AAF = AllArticle_Fragment
        from = getIntent();
        context_from_AAF = from.getStringExtra("context_toAA");
        AA_entrytime = from.getStringExtra("entrytime");

        if (context_from_AAF != null) {
            String[] forsplit;
            aftersplit = context_from_AAF.split("u,4t;4e04");
            AA_articleid = aftersplit[0];
            if (AA_articleid.contains("\n")){
                forsplit = AA_articleid.split("\n");
                AA_articleid = forsplit[1];
            }
            AA_title = aftersplit[1];
            AA_theme = aftersplit[2];
            AA_articlepic = aftersplit[3];
            AA_context = aftersplit[4];
            AA_time = aftersplit[5];
            AA_poster = aftersplit[6];

        }else{
            AA_articleid = from.getStringExtra("articleidtoAA");
            AA_title = from.getStringExtra("titletoAA");
            AA_theme = from.getStringExtra("themetoAA");
            AA_articlepic = from.getStringExtra("themetoAA");
            AA_context = from.getStringExtra("contexttoAA");
            AA_time = from.getStringExtra("timetoAA");
            AA_poster = from.getStringExtra("postertoAA");
        }


        //訂閱作者按鈕
        if(get_peoplesubcheck.getString(AA_poster+"poster","").equals(AA_poster)){
            AA_peopleSubButton.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.sub_button,null));
            AA_peopleSubButton.setTextColor(Color.GRAY);
            AA_peopleSubButton.setText("已訂閱");
            AA_peoplesubcheck = true;
        }else{
            AA_peopleSubButton.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.unsub_button,null));
            AA_peopleSubButton.setTextColor(Color.WHITE);
            AA_peopleSubButton.setText("訂閱作者");
            AA_peoplesubcheck = false;
        }
        //訂閱公告按鈕
        if(get_articlesubcheck.getBoolean(AA_articleid+"articlesub",true)
                && get_articlesubcheck.getString(AA_articleid+"art_individual","").equals(AA_articleid)){
            AA_articleSubButton.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.sub_button,null));
            AA_articleSubButton.setTextColor(Color.GRAY);
            AA_articleSubButton.setText("已訂閱");
            AA_articlesubcheck = true;
        }else{
            AA_articleSubButton.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.unsub_button,null));
            AA_articleSubButton.setTextColor(Color.WHITE);
            AA_articleSubButton.setText("訂閱公告");
            AA_articlesubcheck = false;
        }

        if(get_articleLike.getBoolean(AA_articleid+"articlelike",true)
                && get_articleLike.getString(AA_articleid+"art_individual","").equals(AA_articleid)){
            AA_likeImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                    R.drawable.ic_heart_red,null));
            AA_articleLike = true;
        }else{
            AA_likeImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                    R.drawable.ic_heart_,null));
            AA_articleLike = false;
        }


        AA_checkdel.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DelArticleRunnable delArticleRunnable = new DelArticleRunnable();
                Thread delarticle = new Thread(delArticleRunnable);
                delarticle.start();
            }
        });
        AA_checkdel.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AA_checkdel.setCancelable(false);

        AA_deldia.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (AA_checksubresult){
                    Intent toAAF = new Intent(Article_Activity.this,Article_First_Page_Activity.class);
                    startActivity(toAAF);
                    Article_Activity.this.finish();
                }

            }
        });
        AA_deldia.setCancelable(false);


        //加載網路圖片
        if (!AA_articlepic.equals("沒有圖片")){
            Log.e("AA_articlepic",AA_articlepic+"");
            AA_Imagelink = AA_downlink + AA_articlepic;
            new GetArticleBitmapFromURL().execute(AA_Imagelink);
        }
        //判斷發文者和使用者是否為同一人
        if (AA_username.equals(AA_poster)) {
            //若是，則兩個訂閱按鈕將不會顯示
            AA_peopleSubButton.setVisibility(View.INVISIBLE);
            AA_articleSubButton.setVisibility(View.INVISIBLE);
        }

        //設定內文
        setTitle(AA_title);
        AA_contextText.setText(AA_context);
        AA_timeText.setText(AA_time);
        AA_titleText.setText(AA_title);
        //設置成姓名
        AA_nameText.setText(AA_getpersonaldata.getPersonaldata()[3]);
        //按鈕事件
        OnClickListener onClickListener = new OnClickListener();
        AA_peopleSubButton.setOnClickListener(onClickListener);
        AA_articleSubButton.setOnClickListener(onClickListener);
        AA_press.setOnClickListener(onClickListener);
        AA_selectimage.setOnClickListener(onClickListener);
        AA_likeImage.setOnClickListener(onClickListener);
        AA_likeCard.setOnClickListener(onClickListener);


        //讀取留言
        BoardMessage boardMessage = new BoardMessage();
        Thread BoardMessage = new Thread(boardMessage);
        BoardMessage.start();

        MyselfInformationRunnable myselfInformationRunnable = new MyselfInformationRunnable();
        Thread getmyinformation = new Thread(myselfInformationRunnable);
        getmyinformation.start();

    }


    //初始化元件
    private void init(){
        AA_getusername = (GlobalVariable)this.getApplicationContext();
        AA_refresh = (GlobalVariable) this.getApplicationContext();
        AA_getpersonaldata = (GlobalVariable) this.getApplicationContext();
        AA_messageMembers = new ArrayList<>();

        AA_contextText = findViewById(R.id.AA_contentText);
        AA_messageList = findViewById(R.id.AA_messageList);
        AA_timeText = findViewById(R.id.AA_timeText);
        AA_nameText = findViewById(R.id.AA_nameText);
        AA_titleText = findViewById(R.id.AA_titleText);
        AA_peopleSubButton = findViewById(R.id.AA_peopleSubButton);
        AA_articleSubButton = findViewById(R.id.articleSubButton);
        avatarImage = findViewById(R.id.AA_avatarImage);
        AA_messageEdittext = findViewById(R.id.AA_messageEditText);
        AA_press = findViewById(R.id.AA_editTextpress);
        AA_selectimage = findViewById(R.id.AA_pictureButton);
        AA_likeCard = findViewById(R.id.AA_likeCard);
        AA_likeImage = findViewById(R.id.AA_likeImage);

        AA_allmsgarraylist = new ArrayList<>();
        AA_allmsg_title = new ArrayList<>();
        AA_allmsg_username = new ArrayList<>();
        AA_allmsg_sex = new ArrayList<>();
        AA_allmsg_image = new ArrayList<>();
        AA_allmsg_context = new ArrayList<>();
        AA_allmsg_time = new ArrayList<>();
        AA_allmsg_bitmap = new ArrayList<>();
        AA_allmsg_personname = new ArrayList<>();

        AA_checkdel = new AlertDialog.Builder(Article_Activity.this);
        AA_deldia = new AlertDialog.Builder(Article_Activity.this);

        AA_progressDialog = new ProgressDialog(Article_Activity.this);
        AA_choosepicprogressbar = new ProgressDialog(Article_Activity.this);
    }
    //藉由Post從資料庫拿出自己的資料
    private class MyselfInformationRunnable implements Runnable{
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("function","personalInformation"));
            params.add(new BasicNameValuePair("username",AA_username));
            //將參數加入params這個List中等待post
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //帶上post參數且將參數設置為UTF-8的格式
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //執行
                AA_postresult = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                //把結果(php的echo內容)取出轉換成String型態且為UTF-8的格式
                AA_personaldata_aftersplit = AA_postresult.split("u,4t;4e04");
                AA_msgpersonname = AA_personaldata_aftersplit[3];
                AA_msgpersonsex = AA_personaldata_aftersplit[4];
            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }
        }// run()
    }
    //讀取公告圖片
    private class GetArticleBitmapFromURL extends AsyncTask<String , Integer , Bitmap> {

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
            //讀取網路圖片，型態為Bitmap
            try
            {
                URL url = new URL(imageUrl[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                AA_bitmap = bitmap;

                return bitmap;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
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

            LinearLayout layout = findViewById(R.id.AA_piclinearlayout);
            ImageView AA_userImage = new ImageView(getApplicationContext());
            int zoom ;
            RelativeLayout.LayoutParams params = null;
            if (AA_bitmap!=null){
                params = new RelativeLayout.LayoutParams(ZoomBitmap(AA_bitmap,600).getWidth()
                        ,ZoomBitmap(AA_bitmap,600).getHeight());
                params.setMargins(50,0,20,20);
                AA_userImage.setImageBitmap(AA_bitmap);
                layout.addView(AA_userImage,params);
            }
        }
    }
    //控制按下按鈕後的動作
    private class OnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                //訂閱作者按鈕
                case R.id.AA_peopleSubButton:
                    AA_subscribe = AA_poster;
                    //若還沒訂閱時按下按鈕
                    if (!AA_peoplesubcheck){
                        Thread subpeople = new Thread(new Subscribepeople_Post());
                        subpeople.start();
                        AA_peopleSubButton.setBackground(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.sub_button,null));
                        AA_peopleSubButton.setTextColor(Color.GRAY);
                        AA_peopleSubButton.setText("已訂閱");
                        //用來判斷按鈕是否被點下
                        AA_peoplesubcheck = true;
                        //用來紀錄按鈕的狀態
                        get_peoplesubcheck.edit()
                                .putBoolean(AA_articleid+"peoplesub",true)
                                .putString(AA_articleid+"peo_individual",AA_articleid)
                                .putString(AA_poster+"poster",AA_poster)
                                .apply();
                        //取消訂閱發文者
                        FirebaseMessaging.getInstance().subscribeToTopic(AA_subscribe);

                    }else {//若已訂閱時按下按鈕
                        Thread delsubpeople = new Thread(new Subscribepeopledel_Post());
                        delsubpeople.start();
                        AA_peopleSubButton.setBackground(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.unsub_button,null));
                        AA_peopleSubButton.setTextColor(Color.WHITE);
                        AA_peopleSubButton.setText("訂閱作者");
                        //用來判斷按鈕是否被點下
                        AA_peoplesubcheck = false;
                        //用來紀錄按鈕的狀態
                        get_peoplesubcheck.edit()
                                .putBoolean(AA_articleid+"peoplesub",false)
                                .putString(AA_articleid+"peo_individual","")
                                .putString(AA_poster+"poster","")
                                .apply();
                        //取消訂閱發文者
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(AA_subscribe);
                    }
                    //設定為需要重整
                    AA_refresh.setRefresh(true);
                    break;
                    //訂閱公告按鈕
                case R.id.articleSubButton:
                    AA_subscribe = AA_articleid;
                    //若還沒訂閱時按下按鈕
                    if (!AA_articlesubcheck){
                        Thread subarticle = new Thread(new Subscribearticle_Post());
                        subarticle.start();
                        AA_articleSubButton.setBackground(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.sub_button,null));
                        AA_articleSubButton.setTextColor(Color.GRAY);
                        AA_articleSubButton.setText("已訂閱");
                        //用來判斷按鈕是否被點下
                        AA_articlesubcheck = true;
                        //用來紀錄按鈕的狀態
                        get_articlesubcheck.edit()
                                .putBoolean(AA_articleid+"articlesub",true)
                                .putString(AA_articleid+"art_individual",AA_articleid)
                                .apply();
                        //訂閱公告
                        FirebaseMessaging.getInstance().subscribeToTopic(AA_subscribe);
                    }else {//若已訂閱時按下按鈕
                        Thread delsubarticle = new Thread(new Subscribearticledel_Post());
                        delsubarticle.start();
                        AA_articleSubButton.setBackground(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.unsub_button,null));
                        AA_articleSubButton.setTextColor(Color.WHITE);
                        AA_articleSubButton.setText("訂閱公告");
                        //用來判斷按鈕是否被點下
                        AA_articlesubcheck = false;
                        //用來紀錄按鈕的狀態
                        get_articlesubcheck.edit()
                                .putBoolean(AA_articleid+"articlesub",false)
                                .putString(AA_articleid+"art_individual","")
                                .apply();
                        //取消訂閱公告
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(AA_subscribe);
                    }
                    //設定為需要重整
                    AA_refresh.setRefresh(true);
                    break;
                    //留言按鈕
                case R.id.AA_editTextpress:
                    AA_personmsg = AA_messageEdittext.getText().toString();
                    AA_messageEdittext.setText("");
                    if (AA_personmsg.equals("")){

                    }else {
                        if (AA_uploadimagepath!=null){
                            Log.e("AA_personmsg",AA_personmsg+"");
                            String[] aftersplitmsg = AA_personmsg.split(AA_sequence.toString());
                            AA_personmsg = "";
                            StringBuilder stringBuilder = new StringBuilder();
                            for (String s : aftersplitmsg) {
                                stringBuilder.append(s);
                            }
                            AA_messageEdittext.setText("");
                            if (AA_msgpersonsex.equals("男")){
                                //留言區信息
                                AA_messageMembers.add(new MessageMember(AA_msgpersonname
                                        ,stringBuilder.toString(),R.drawable.man,AA_uploadbitmap));
                            }else if (AA_msgpersonsex.equals("女")){
                                //留言區信息
                                AA_messageMembers.add(new MessageMember(AA_msgpersonname
                                        ,stringBuilder.toString(),R.drawable.female,AA_uploadbitmap));
                            }
                            AA_messageList.setAdapter(AA_messageAdapter);
                            new MessageuploadFile().execute(AA_uploadimagepath);
                        }else {
                            if (AA_msgpersonsex.equals("男")){
                                //留言區信息
                                AA_messageMembers.add(new MessageMember(AA_msgpersonname
                                        ,AA_personmsg,R.drawable.man));
                            }else if (AA_msgpersonsex.equals("女")){
                                //留言區信息
                                AA_messageMembers.add(new MessageMember(AA_msgpersonname
                                        ,AA_personmsg,R.drawable.female));
                            }
                            AA_messageList.setAdapter(AA_messageAdapter);
                            MessageToSQL_Post messageToSQLPost = new MessageToSQL_Post();
                            Thread message = new Thread(messageToSQLPost);
                            message.start();
                        }
                    }

                    //設定為需要重整
                    AA_refresh.setRefresh(true);
                    break;
                case R.id.AA_pictureButton:
                    Intent picture = new Intent(Intent.ACTION_PICK);
                    picture.setType("image/*");
                    activityResultLauncher.launch(picture);
                    break;
                case R.id.AA_likeCard:
                case R.id.AA_likeImage:
                    if(!AA_articleLike) {
                        AA_likeImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.ic_heart_red,null));
                        AA_articleLike = true;

                        get_articleLike.edit()
                                .putBoolean(AA_articleid+"articlelike",true)
                                .putString(AA_articleid+"art_individual",AA_articleid)
                                .apply();

                    }else {
                        AA_likeImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.ic_heart_,null));
                        AA_articleLike = false;

                        get_articleLike.edit()
                                .putBoolean(AA_articleid+"articlelike",true)
                                .putString(AA_articleid+"art_individual","")
                                .apply();
                    }
                    break;
            }
        }
    }
    //接收Intent資訊的地方，目前用來接收圖片集被選擇的圖片
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null){
                    Intent data = result.getData();
                    Uri ImageUri = data.getData();

                    AA_uploadimagepath = getPath(ImageUri);
                    try {
                        AA_uploadbitmap = BitmapFactory.decodeStream(this.getContentResolver()
                                .openInputStream(ImageUri));
                        AA_uploadbitmap = ZoomBitmap(AA_uploadbitmap,300);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    AA_editable = AA_messageEdittext.getText();
                    AA_sequence = getDrawableStr(AA_uploadimagepath);
                    AA_editable.insert(AA_messageEdittext.getSelectionStart(), AA_sequence);


                }
            });

    /**
     * 編輯插入的内容
     *
     * @param picPath
     * @return
     */
    private CharSequence getDrawableStr(String picPath) {
        String str = "<img src=\"" + picPath + "\"/>";
        Bitmap bm = createImageThumbnail(picPath);
        final SpannableString ss = new SpannableString(str);
        // 定義插入圖片
        Drawable drawable = new BitmapDrawable(getResources(),bm);

        float scenewidth = screenWidth / 3;
        float width = drawable.getIntrinsicWidth();
        float height = drawable.getIntrinsicHeight();
        if (width > scenewidth) {
            width = width - 20;
            height = height - 20;
        } else {
            float scale = (scenewidth) / width;
            width *= scale;
            height *= scale;
        }

        //設置圖片的寬高
        drawable.setBounds(2, 0, (int) width, (int) height);
        //ALIGN_BOTTOM 調整圖片距離字有一定的間隙
        VerticalCenterImageSpan span = new VerticalCenterImageSpan(drawable, 1);
        //SPAN_INCLUSIVE_EXCLUSIVE 會導致刪除後面的文字消失
        ss.setSpan(span, 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
       /*
       Spannable.SPAN_EXCLUSIVE_EXCLUSIVE：前後都不包括，即在指定範圍的前面和後面插入新字符都不會應用新樣式
       Spannable.SPAN_EXCLUSIVE_INCLUSIVE：前面不包括，後麵包括。即僅在範圍字符的後面插入新字符時會應用新樣式
       Spannable.SPAN_INCLUSIVE_EXCLUSIVE：前麵包括，後面不包括。
       Spannable.SPAN_INCLUSIVE_INCLUSIVE：前後都包括。
        */
        return ss;
    }


    /**
     * 創建圖片
     * @param filePath
     * @return
     */
    public static Bitmap createImageThumbnail(String filePath) {
        Bitmap bitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTempStorage = new byte[100 * 1024];
        // 默認是Bitmap.Config.ARGB_8888
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inSampleSize = 2;
        try {
            bitmap = BitmapFactory.decodeFile(filePath, opts);
        } catch (Exception e) {
        }
        return bitmap;
    }

    public class VerticalCenterImageSpan extends ImageSpan {

        public VerticalCenterImageSpan(Drawable d, int verticalAlignment) {
            super(d, verticalAlignment);
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end,
                         float x, int top, int y, int bottom, Paint paint) {
            Drawable b = getDrawable();
            canvas.save();

            int transY = bottom - b.getBounds().bottom;
            if (mVerticalAlignment == ALIGN_BASELINE) {
                transY -= paint.getFontMetricsInt().descent;
            } else if (mVerticalAlignment == ALIGN_BOTTOM) {

            } else {
                transY += paint.getFontMetricsInt().descent * 2;
            }

            canvas.translate(x, transY);
            b.draw(canvas);
            canvas.restore();
        }
    }



    /*將所取得的照片的Path轉換成可以用來傳輸的Path，因為取得的Path為Content://開頭
    但這個Path無法用來傳輸，所以需要轉換*/
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        String returnString = null;
        Cursor cursor = null;

        cursor = this.getContentResolver().query(uri,
                projection, null, null, null);

        if (cursor != null){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            returnString = cursor.getString(column_index);
        }

        if (cursor != null){
            cursor.close();
        }

        return returnString;
    }
    //將留言資訊上傳到資料夾儲存起來(有圖片)
    private class MessageuploadFile extends AsyncTask<String, Integer, Integer>{


        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String function = "message",username = AA_username,articleid = AA_articleid,
        message = AA_personmsg;
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;

        @Override
        protected void onPreExecute() {
            //執行前 設定可以在這邊設定
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... strings) {
            //執行中 在背景做事情
            File sourceFile = new File(strings[0]);
            String fileName = strings[0];
            if (!sourceFile.isFile()) {

                AA_choosepicprogressbar.dismiss();

                Log.e("uploadFile", "Source File not exist :" + strings[0]);


                return 0;

            }
            else
            {
                try {
                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(My_PostURL);

                    // Open a HTTP  connection to  the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy

                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Charset", "UTF-8");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="
                            + boundary);
                    conn.setRequestProperty("function",function);
                    conn.setRequestProperty("articleid",articleid);
                    conn.setRequestProperty("username", username);
                    conn.setRequestProperty("message",function);
                    conn.setRequestProperty("uploaded_file", fileName);

                    dos = new DataOutputStream(conn.getOutputStream());
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename= \""
                            + fileName + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    }



                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"function\""+ lineEnd + lineEnd
                            + function );
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"username\"" + lineEnd +lineEnd
                            + username );
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"articleid\"" + lineEnd +lineEnd
                            + articleid );
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"message\"" + lineEnd +lineEnd
                            + message );
                    dos.writeBytes(lineEnd);

                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    AA_serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();

                    Log.i("uploadFile", "HTTP Response is : "
                            + serverResponseMessage + ": " + AA_serverResponseCode);

                    //close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();
                } catch (MalformedURLException ex) {

                    AA_choosepicprogressbar.dismiss();
                    ex.printStackTrace();
                    Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                } catch (Exception e) {

                    AA_choosepicprogressbar.dismiss();
                    e.printStackTrace();
                    Log.e("Upload file to server", "Exception : "  + e.getMessage(), e);
                }
            } // End else block

            return AA_serverResponseCode;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //執行中 可以在這邊告知使用者進度
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            //執行後 完成背景任務
            super.onPostExecute(integer);

        }

    }

    //傳送留言資訊到資料庫(無圖片)
    private class MessageToSQL_Post implements Runnable{
        @Override
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("function","message"));
            params.add(new BasicNameValuePair("articleid",AA_articleid));
            params.add(new BasicNameValuePair("username",AA_username));
            params.add(new BasicNameValuePair("message",AA_personmsg));
            //將參數加入params這個List中等待post
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //帶上post參數且將參數設置為UTF-8的格式
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //執行
                AA_subresult = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                if ( AA_subresult.contains("\n")){
                    String[] split = AA_subresult.split("\n");
                    AA_subresult = split[1];
                }
                if (!AA_subresult.equals("留言成功")){
                    Looper.prepare();
                    Toast.makeText(Article_Activity.this, AA_subresult, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }
        }
    }
    //縮放圖片
    private Bitmap ZoomBitmap(Bitmap oldBitmap,int zoomto){
        //取得原圖長寬
        int oldWidth = oldBitmap.getWidth();
        int oldHeight = oldBitmap.getHeight();
        float scale;
        //計算縮放比例
        if (oldWidth>oldHeight){
            scale = ((float) zoomto)/oldWidth;
        }else {
            scale = ((float) zoomto)/oldHeight;
        }
        //取得想要縮放的matrix參數
        Matrix matrix = new Matrix();
        matrix.postScale(scale,scale);
        Bitmap newBitmap = Bitmap.createBitmap(oldBitmap,0,0,oldWidth,oldHeight,matrix,true);
        return newBitmap;

    }

    //讀取留言圖片
    private class GetMessageBitmapFromURL extends AsyncTask<String , Integer , Bitmap> {

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
            //讀取網路圖片，型態為Bitmap
            //因為不會用到但不能為空，所以隨便找一張塞進去
            AA_nullbit = BitmapFactory.decodeResource(getResources(),R.drawable.a);
            for (String url : imageUrl){
                if (url.equals("沒有圖片")){
                    AA_allmsg_bitmap.add(AA_nullbit);
                }else {
                    try
                    {
                        URL imagelink = new URL(AA_downlink+url);
                        HttpURLConnection connection = (HttpURLConnection) imagelink.openConnection();
                        connection.setDoInput(true);
                        InputStream input = connection.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(input);
                        AA_allmsg_bitmap.add(ZoomBitmap(bitmap,300));

                    }catch (IOException e){
                        e.printStackTrace();
                    }
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

            for (int i = 0;i < AA_allmsg_image.size();i++){
                //留言區信息
                if (!AA_allmsg_image.get(i).equals("沒有圖片")){
                    if (AA_allmsg_sex.get(i).equals("男")){
                        AA_messageMembers.add(new MessageMember(AA_allmsg_personname.get(i),AA_allmsg_context.get(i),
                                R.drawable.man,AA_allmsg_bitmap.get(i)));
                    }else if (AA_allmsg_sex.get(i).equals("女")){
                        AA_messageMembers.add(new MessageMember(AA_allmsg_personname.get(i),AA_allmsg_context.get(i),
                                R.drawable.female,AA_allmsg_bitmap.get(i)));
                    }
                }else {
                    if (AA_allmsg_sex.get(i).equals("男")){
                        AA_messageMembers.add(new MessageMember(AA_allmsg_personname.get(i),AA_allmsg_context.get(i),
                                R.drawable.man));
                    }else if (AA_allmsg_sex.get(i).equals("女")){
                        AA_messageMembers.add(new MessageMember(AA_allmsg_personname.get(i),AA_allmsg_context.get(i),
                                R.drawable.female));
                    }
                }
            }

            AA_messageList.setAdapter(AA_messageAdapter);
            AA_progressDialog.dismiss();
        }
    }

    //讀取公告留言
    private class BoardMessage_Handler extends Handler{
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            AA_allmessage = bundle.getStringArray("allmessage");
            AA_allmsgarraylist.addAll(Arrays.asList(AA_allmessage));
            for (int i = 0;i < AA_allmsgarraylist.size()-1;i+=7){
                AA_allmsg_title.add(AA_allmsgarraylist.get(i));
                AA_allmsg_username.add(AA_allmsgarraylist.get(i+1));
                AA_allmsg_personname.add(AA_allmsgarraylist.get(i+2));
                AA_allmsg_sex.add(AA_allmsgarraylist.get(i+3));
                AA_allmsg_image.add(AA_allmsgarraylist.get(i+4));
                AA_allmsg_context.add(AA_allmsgarraylist.get(i+5));
                AA_allmsg_time.add(AA_allmsgarraylist.get(i+6));
                /**AA_allmsgarraylist.get(i)為論壇名稱，AA_allmsgarraylist.get(i+1)為留言者帳號，
                  AA_allmsgarraylist.get(i+2)為留言者姓名，AA_allmsgarraylist.get(i+3)為留言者性別，
                  AA_allmsgarraylist.get(i+4)為留言圖片，AA_allmsgarraylist.get(i+5)為留言內容，
                  AA_allmsgarraylist.get(i+6)為留言時間*/


            }
            //紀錄有幾個留言沒有圖片
            for (int i = 0;i < AA_allmsg_image.size();i++){
                if (AA_allmsg_image.get(i).equals("沒有圖片")){
                    AA_allimagenull ++;
                }
            }
            //判斷是否所有留言都沒有圖片
            if (AA_allimagenull == AA_allmsg_image.size()){
                //是，直接顯示留言
                for (int i = 0;i < AA_allmsg_username.size();i++) {
                    if (AA_allmsg_sex.get(i).equals("男")){
                        AA_messageMembers.add(new MessageMember(AA_allmsg_personname.get(i),AA_allmsg_context.get(i),
                                R.drawable.man));
                    }else if (AA_allmsg_sex.get(i).equals("女")){
                        AA_messageMembers.add(new MessageMember(AA_allmsg_personname.get(i),AA_allmsg_context.get(i),
                            R.drawable.female));
                    }
                }
                AA_messageList.setAdapter(AA_messageAdapter);
                AA_progressDialog.dismiss();

            }else {
                //否，執行讀取圖片的動作，並在讀取完成之後顯示留言
                AA_imagelinks = new String[AA_allmsg_image.size()];
                AA_imagelinks = AA_allmsg_image.toArray(AA_imagelinks);
                new GetMessageBitmapFromURL().execute(AA_imagelinks);
            }

        }
    }
    //從資料庫抓取留言
    private class BoardMessage implements Runnable{
        @Override
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("function","boardMessage"));
            params.add(new BasicNameValuePair("articleid",AA_articleid));
            //將參數加入params這個List中等待post
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //帶上post參數且將參數設置為UTF-8的格式
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //執行
                AA_subresult = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                AA_allmessage = AA_subresult.split("u,4t;4e04");
            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }
            Message msg = boardMessage_handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putStringArray("allmessage", AA_allmessage);
            msg.setData(bundle);
            boardMessage_handler.sendMessage(msg);
        }
    }
    //訂閱發文者
    private class Subscribepeople_Post implements Runnable{
        @Override
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("function","subscribeU"));
            params.add(new BasicNameValuePair("follower",AA_username));
            params.add(new BasicNameValuePair("user",AA_poster));
            //將參數加入params這個List中等待post
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //帶上post參數且將參數設置為UTF-8的格式
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //執行
                AA_subresult = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                if ( AA_subresult.contains("\n")){
                    String[] split = AA_subresult.split("\n");
                    AA_subresult = split[1];
                }
                Looper.prepare();
                Toast.makeText(Article_Activity.this, AA_subresult, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }

        }
    }
    //取消訂閱發文者
    private class Subscribepeopledel_Post implements Runnable{
        @Override
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("function","subscribeUDel"));
            params.add(new BasicNameValuePair("follower",AA_username));
            params.add(new BasicNameValuePair("user",AA_poster));
            //將參數加入params這個List中等待post
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //帶上post參數且將參數設置為UTF-8的格式
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //執行
                AA_subresult = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                if ( AA_subresult.contains("\n")){
                    String[] split = AA_subresult.split("\n");
                    AA_subresult = split[1];
                }
                Looper.prepare();
                Toast.makeText(Article_Activity.this, AA_subresult, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }
        }
    }
    //訂閱公告
    public class Subscribearticle_Post implements Runnable{
        @Override
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("function","subscribeA"));
            params.add(new BasicNameValuePair("username",AA_username));
            params.add(new BasicNameValuePair("articleid",AA_articleid));
            //將參數加入params這個List中等待post
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //帶上post參數且將參數設置為UTF-8的格式
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //執行
                AA_subresult = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                if ( AA_subresult.contains("\n")){
                    String[] split = AA_subresult.split("\n");
                    AA_subresult = split[1];
                }
                Looper.prepare();
                Toast.makeText(Article_Activity.this, AA_subresult, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }
        }
    }
    //取消訂閱公告
    public class Subscribearticledel_Post implements Runnable{
        @Override
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("function","subscribeADel"));
            params.add(new BasicNameValuePair("username",AA_username));
            params.add(new BasicNameValuePair("articleid",AA_articleid));
            //將參數加入params這個List中等待post
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //帶上post參數且將參數設置為UTF-8的格式
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //執行
                AA_subresult = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                if ( AA_subresult.contains("\n")){
                    String[] split = AA_subresult.split("\n");
                    AA_subresult = split[1];
                }
                Looper.prepare();
                Toast.makeText(Article_Activity.this, AA_subresult, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }
        }
    }
    // actionbar要放的東西
   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       if (AA_username.equals(AA_poster)){
           // 相當於在res/menu/main.xml檔案中，給menu增加一個新的條目item，這個條目會顯示title標籤的文字（如備註1）
           menu.add(0, 0, 1, "編輯公告");
           //第二個引數代表唯一的item ID.
           menu.add(0, 1, 2, "刪除公告");
       }
        return true;
    }
    //產生overflow(就是三個點點)
    private void getOverflowMenu() {
        try {
            //得到一個已經設定好裝置的顯示密度的物件
            ViewConfiguration config = ViewConfiguration.get(this);
            //反射獲取其中的方法sHasPermanentMenuKey()，他的作用是報告裝置的選單是否對使用者可用，如果不可用可強制視覺化。
            @SuppressLint("SoonBlockedPrivateApi")
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                //強制設定引數,讓其重繪三個點
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //actionbar butoon按鍵判斷
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case 0:
                //EAA = EditArticle_Activity
                Intent toEAA = new Intent(Article_Activity.this, EditArticle_Activity.class);
                toEAA.putExtra("titletoEAA",AA_title);
                toEAA.putExtra("contexttoEAA", AA_context);
                toEAA.putExtra("articleidtoEAA",AA_articleid);
                toEAA.putExtra("themetoEAA",AA_theme);
                toEAA.putExtra("timetoEAA",AA_time);
                toEAA.putExtra("postertoEAA",AA_poster);
                startActivity(toEAA);
                Article_Activity.this.finish();
                break;
            case 1:
                AA_checkdel.setTitle("");
                AA_checkdel.setMessage("確定要刪除這則公告嗎?");
                AA_checkdel.show();
                break;
        }
        if (item.getItemId() == android.R.id.home) {
            Article_Activity.this.finish();
        }
       return super.onOptionsItemSelected(item);
    }
    //藉由Post從資料庫刪除當前公告
    public class DelArticleRunnable implements Runnable{
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("function","announcementDel"));
            params.add(new BasicNameValuePair("username",AA_username));
            params.add(new BasicNameValuePair("articleid",AA_articleid));
            //將參數加入params這個List中等待post
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //帶上post參數且將參數設置為UTF-8的格式
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //執行
                AA_delresult = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                if ( AA_delresult.contains("\n")){
                    String[] split = AA_delresult.split("\n");
                    AA_subresult = split[1];
                }
                if (AA_subresult.equals("刪除成功")) AA_checksubresult = true;
                //把結果(php的echo內容)取出轉換成String型態且為UTF-8的格式
                AA_deldia.setTitle("");
                AA_deldia.setMessage(AA_subresult);
                Looper.prepare();
                AA_deldia.show();
                Looper.loop();
            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }
        }// run()
    }// class TimerRunnable
    //記錄瀏覽資訊
    public class BrowerHistoryRunnable implements Runnable{
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("function","history"));
            params.add(new BasicNameValuePair("entrytime",AA_entrytime));
            params.add(new BasicNameValuePair("departuretime",AA_departtime));
            params.add(new BasicNameValuePair("username",AA_username));
            params.add(new BasicNameValuePair("articleid",AA_articleid));
            //將參數加入params這個List中等待post
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //帶上post參數且將參數設置為UTF-8的格式
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //執行
                AA_delresult = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                //把結果(php的echo內容)取出轉換成String型態且為UTF-8的格式
            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }
        }// run()
    }
    //messagecard(留言區)的處理
    public class MessageAdapter extends BaseAdapter {
        private LayoutInflater myInflater;
        private List<MessageMember> messageMembers;

        @Override
        public int getCount() {
            return messageMembers.size();
        }

        @Override
        public Object getItem(int position) {
            return messageMembers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return messageMembers.indexOf(getItem(position));
        }

        class ViewHolder{
            TextView textName,textMessage;
            ImageButton likeBtn;
            ImageView messageimage,headshot;
            CardView likecard;

            public ViewHolder(TextView textName,TextView textMessage,ImageButton likeBtn,
                              ImageView messageimage,ImageView headshot,CardView likecard){
                this.textName = textName;
                this.textMessage = textMessage;
                this.likeBtn = likeBtn;
                this.messageimage = messageimage;
                this.headshot = headshot;
                this.likecard = likecard;
            }
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if(convertView == null){
                convertView = myInflater.inflate(R.layout.messagecard, null);
                holder = new ViewHolder(convertView.findViewById(R.id.userNameText),
                        convertView.findViewById(R.id.userMessageText),
                        convertView.findViewById(R.id.likeButton),
                        convertView.findViewById(R.id.messageimage),
                        convertView.findViewById(R.id.avatar),convertView.findViewById(R.id.likecard));
                convertView.setTag(holder);

            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            ViewHolder finalHolder = holder;

            class messageOnClickListener implements View.OnClickListener{
                @Override
                public void onClick(View v) {
                    if(!AA_checkheart) {
                        finalHolder.likeBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.ic_heart_red,null));
                        AA_checkheart = true;

                    }else {
                        finalHolder.likeBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                                R.drawable.ic_heart_,null));
                        AA_checkheart = false;

                    }
                }
            }

            messageOnClickListener likeListener = new messageOnClickListener();
            holder.likecard.setOnClickListener(likeListener);
            holder.likeBtn.setOnClickListener(likeListener);

            MessageMember messageMember = (MessageMember) getItem(position);
            holder.textMessage.setText(messageMember.getAA_userMessage());
            holder.textName.setText(messageMember.getAA_userName());
            holder.messageimage.setImageBitmap(messageMember.getAA_messageimage());
            holder.headshot.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                    messageMember.getAA_imageId(),null));
            return convertView;
        }

        public MessageAdapter(Context context,List<MessageMember> messageMember){
            myInflater = LayoutInflater.from(context);
            this.messageMembers = messageMember;
        }
    }

    //可以理解為按下返回鍵返回時會執行
    @Override
    protected void onPause(){
        super.onPause();
        SimpleDateFormat nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.TAIWAN);
        nowDate.setTimeZone(TimeZone.getTimeZone("GMT+08"));//台灣為GMT+8時區
        Date curDate = new Date(System.currentTimeMillis()) ;
        AA_departtime = nowDate.format(curDate);
        Thread history = new Thread(new BrowerHistoryRunnable());
        history.start();

    }

}