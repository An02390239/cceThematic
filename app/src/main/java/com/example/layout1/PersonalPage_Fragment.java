package com.example.layout1;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class PersonalPage_Fragment extends Fragment {

    private static final int RESULT_OK = -1;
    private int PPF_serverResponseCode = 0;
    private final String My_PostURL = Login_Fragment.My_PostURL;
    private ListView PPF_setList;
    private AlertDialog.Builder PPF_logoutdia,PPF_imagedia;
    private ProgressDialog PPF_choosepicprogressbar, PPF_downpicprogressBar;
    private SharedPreferences islogin,get_personname,get_personsex;
    private TextView PPF_usernameTV;
    private String PPF_username, PPF_Imagelink, PPF_result,PPF_downlink = Login_Fragment.Downlink;
    private String PPF_uploadimagepath, PPF_upLoadServerUri = Login_Fragment.My_PostURL;
    private String[] PPF_diastring = {"相機","圖片集"};
    private GlobalVariable PPF_getusername,PPF_getpersondata;
    private boolean check = false;
    private ImageButton heart;
    private Bitmap PPF_downloadbitmap, PPF_uploadbitmap = null,PPF_personalimage;
    private ImageView PPF_userImage;
    private DownloadHandler downloadHandler = new DownloadHandler();
    private Uri PPF_CrameraimageUri;
    private int piccount = 0;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.personal_page_fragment, container, false);


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        PPF_downpicprogressBar = new ProgressDialog(requireActivity());
        PPF_downpicprogressBar.setMessage("Loading...");
        PPF_downpicprogressBar.setCancelable(false);
        PPF_downpicprogressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        PPF_downpicprogressBar.show();

        PPF_getusername = (GlobalVariable)requireActivity().getApplicationContext();
        PPF_getpersondata = (GlobalVariable)requireActivity().getApplicationContext();
        islogin = requireActivity().getSharedPreferences("islogin", Context.MODE_PRIVATE);
        get_personname = requireActivity().getSharedPreferences("Personname", Context.MODE_PRIVATE);
        get_personsex = requireActivity().getSharedPreferences("SEX", Context.MODE_PRIVATE);
        PPF_setList = view.findViewById(R.id.SetList);
        PPF_usernameTV = view.findViewById(R.id.PPF_usernameTV);
        PPF_username = PPF_getusername.getUsername();

        PPF_usernameTV.setText(get_personname.getString("personname",""));
        PPF_userImage = view.findViewById(R.id.PPF_userImage);

        //載入圖片
        DownloadPersonPicPost downloadPost = new DownloadPersonPicPost();
        Thread downImage = new Thread(downloadPost);
        downImage.start();

        /*優化個人圖片載入
        if (PPF_personalimage!=null){
            PPF_userImage.setImageBitmap(PPF_personalimage);
        }*/


        PPF_logoutdia = new AlertDialog.Builder(requireActivity());
        PPF_logoutdia.setPositiveButton("確認", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent toLF = new Intent(requireActivity(),Version_Activity.class);
                startActivity(toLF);
                islogin.edit()
                        .putBoolean("check",false)
                        .apply();
                requireActivity().finish();
            }
        });
        PPF_logoutdia.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        PPF_logoutdia.setCancelable(false);

        PPF_imagedia = new AlertDialog.Builder(requireActivity());
        PPF_imagedia.setTitle("請選擇").setItems(PPF_diastring,
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    //創建File物件，用於儲存拍照後的圖片
                    //requireActivity().getExternalCacheDir()
                    File outputImage = new File(requireActivity().getExternalCacheDir(),"outputImage.png");

                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    try {
                        outputImage.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //判斷SDK版本高低，ImageUri方法不同
                    if (Build.VERSION.SDK_INT >= 24) {
                    PPF_CrameraimageUri = FileProvider.getUriForFile(requireActivity(),
                                BuildConfig.APPLICATION_ID  + ".provider", outputImage);
                    } else {
                        PPF_CrameraimageUri = Uri.fromFile(outputImage);
                    }

                    //啟動相機
                    Intent Camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Camera.putExtra(MediaStore.EXTRA_OUTPUT, PPF_CrameraimageUri);
                    Camera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    activityResultLaucher.launch(Camera);

                }else {
                    Intent picture = new Intent(Intent.ACTION_PICK);
                    picture.setType("image/*");
                    activityResultLaucher.launch(picture);
                }
            }
        });
        OnImageClick onImageClick = new OnImageClick();
        PPF_userImage.setOnClickListener(onImageClick);


        Personal_FunctionAdapter arrayAdapter = new Personal_FunctionAdapter(requireActivity()
                ,R.layout.personal_functionlist_item,FunctionItem.getAllfunction());
        PPF_setList.setAdapter(arrayAdapter);

        PPF_setList.setOnItemClickListener(new OnItemClick());

        return view;
    }


    //取代OnActivityResult的方法
    ActivityResultLauncher<Intent> activityResultLaucher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {


                if(result.getResultCode() == RESULT_OK && result.getData() != null){

                    Intent data = result.getData();
                    Uri ImageUri = data.getData();


                    if (ImageUri == null){

                        try {
                            PPF_uploadbitmap = BitmapFactory.decodeStream(requireActivity().
                                    getContentResolver().openInputStream(PPF_CrameraimageUri));

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        final String dir = "/storage/emulated/0/Pictures/";
                        File newdir = new File(dir);
                        if (!newdir.exists()) {
                            newdir.mkdir();
                        }
                        File picmove = new File(newdir,System.currentTimeMillis() + PPF_username + piccount + ".png");
                        if (!picmove.exists()) {
                            try {
                                picmove.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        BufferedOutputStream bos = null;
                        try {
                            bos = new BufferedOutputStream(new FileOutputStream(picmove));
                            PPF_uploadbitmap.compress(Bitmap.CompressFormat.PNG, 80, bos);
                            bos.flush();
                            bos.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }



                        //Log.e("path",picmove.getPath());
                        new uploadPersonalImg().execute(picmove.getPath());
                        //new uploadPersonalImg().execute(getPath(PPF_CrameraimageUri));

                    }else{
                        PPF_uploadimagepath = getPath(ImageUri);
                        ///storage/emulated/0/Pictures/IMG_20220309_085651.jpg
                        try {
                            PPF_uploadbitmap = BitmapFactory.decodeStream(requireActivity().
                                    getContentResolver().openInputStream(ImageUri));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        //content://com.example.layout1.provider/external_files/Android/data/com.example.layout1/cache/outputImage.jpg
                        //content://media/external/images/media/68/ORIGINAL/NONE/image/jpeg/388616323
                        new uploadPersonalImg().execute(PPF_uploadimagepath);

                    }
                }else if (PPF_CrameraimageUri != null){


                    try {
                        PPF_uploadbitmap = BitmapFactory.decodeStream(requireActivity().
                                getContentResolver().openInputStream(PPF_CrameraimageUri));

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    final String dir = "/storage/emulated/0/Pictures/";
                    File newdir = new File(dir);
                    if (!newdir.exists()) {
                        newdir.mkdir();
                    }
                    File picmove = new File(newdir,System.currentTimeMillis() + PPF_username + piccount + ".png");
                    if (!picmove.exists()) {
                        try {
                            picmove.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    BufferedOutputStream bos = null;
                    try {
                        bos = new BufferedOutputStream(new FileOutputStream(picmove));
                        PPF_uploadbitmap.compress(Bitmap.CompressFormat.PNG, 80, bos);
                        bos.flush();
                        bos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                    //Log.e("path",picmove.getPath());
                    new uploadPersonalImg().execute(picmove.getPath());
                    //new uploadPersonalImg().execute(getPath(PPF_CrameraimageUri));
                }
            });

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        String returnString = null;
        Cursor cursor = null;

        cursor = requireActivity().getContentResolver().query(uri,
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


    private class OnItemClick implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            FragmentManager FM4 = requireActivity().getSupportFragmentManager();
            FragmentTransaction FT4 = FM4.beginTransaction();
            //獲取當前的fragment
            Fragment currentFragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.PPAflayout);
            switch ((int) id){
                case 0:
                    //宣告物件
                    PeopleSub_Fragment PSF = new PeopleSub_Fragment();
                    //創造PeopleSub_Fragment物件
                    //創造Fragment物件並取得其內容

                    FT4.hide(currentFragment);
                    FT4.add(R.id.PPAflayout,PSF);//將原本的畫面(PPF)加入PSF
                    FT4.addToBackStack(null);//加入堆疊，使他可以在下一個頁面按下返回鍵後回到這個頁面

                    FT4.commit();//類似執行的動作
                    break;
                case 1:
                    HistoryBoard_Fragment HBF = new HistoryBoard_Fragment();
                    FT4.hide(currentFragment);
                    FT4.add(R.id.PPAflayout,HBF);//將原本的畫面(PPF)加入PSF
                    FT4.addToBackStack(null);//加入堆疊，使他可以在下一個頁面按下返回鍵後回到這個頁面
                    FT4.commit();
                    break;
                case 2:
                    Intent changePwd = new Intent(getActivity(),ChangePwd_Activity.class);
                    startActivity(changePwd);
                    break;
                case 3:
                    PPF_logoutdia.setMessage("確定要登出嗎?");
                    PPF_logoutdia.show();
                    break;
            }
        }
    }
    //點擊大頭照會顯示出更換照片的選項
    private class OnImageClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            PPF_imagedia.show();
        }
    }

    //將獲取的圖片名稱轉換成實際圖片
    private class GetImage extends AsyncTask<String , Integer , Bitmap> {

        @Override
        protected void onPreExecute() {
            //執行前 設定可以在這邊設定
            super.onPreExecute();
        }

        @Override
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
                PPF_downloadbitmap = bitmap;
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
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //執行後 完成背景任務
            super.onPostExecute(bitmap);
            PPF_downpicprogressBar.dismiss();
            if (PPF_downloadbitmap!=null){
                PPF_userImage.setImageBitmap(PPF_downloadbitmap);
            }else {
                if (get_personsex.getString("personsex","").equals("男")){
                    PPF_userImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.man,null));
                }else if (get_personsex.getString("personsex","").equals("女")){
                    PPF_userImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.female,null));
                }
            }
        }
    }
    //藉由Handler執行轉換動作
    private class DownloadHandler extends Handler {
        public void handleMessage(Message msg){
            Bundle postmsg = msg.getData();
            PPF_Imagelink = postmsg.getString("result");
            Looper.prepare();
            new GetImage().execute(PPF_Imagelink);
            Looper.loop();
        }

    }
    //獲取資料庫圖片名稱
    private class DownloadPersonPicPost implements Runnable{

        @Override
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("function","downloadimg"));
            params.add(new BasicNameValuePair("username",PPF_username));
            //將參數加入params這個List中等待post
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //帶上post參數且將參數設置為UTF-8的格式
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //執行
                PPF_result = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                if (PPF_result.contains("\n")){

                    String[] split = PPF_result.split("\n");
                    PPF_result = split[1];
                }
                PPF_result = PPF_downlink + PPF_result;
                //把結果(php的echo內容)取出轉換成String型態且為UTF-8的格式
            }catch (IOException e){
                e.printStackTrace();// 如果出事，回傳錯誤訊息
            }finally {
                client.getConnectionManager().shutdown();
            }
            Message msg = downloadHandler.obtainMessage();
            Bundle B = new Bundle();
            B.putString("result", PPF_result);
            msg.setData(B);
            downloadHandler.handleMessage(msg);
        }
    }
    //將大頭照上傳到資料夾儲存起來
    private class uploadPersonalImg extends AsyncTask< String, Integer, Integer>{


        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        String function = "uploadPersonalImg",username = PPF_username;

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;

        @Override
        protected void onPreExecute() {
            //執行前 設定可以在這邊設定
            super.onPreExecute();
            PPF_choosepicprogressbar = new ProgressDialog(requireActivity());
            PPF_choosepicprogressbar.setMessage("Loading...");
            PPF_choosepicprogressbar.setCancelable(false);
            PPF_choosepicprogressbar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            PPF_choosepicprogressbar.show();
        }

        @Override
        protected Integer doInBackground(String... strings) {
            //執行中 在背景做事情
            File sourceFile = new File(strings[0]);
            String fileName = strings[0];
            if (!sourceFile.isFile()) {

                PPF_choosepicprogressbar.dismiss();

                Log.e("uploadPersonalImg", "Source File not exist :" + strings[0]);


                return 0;

            }
            else
            {
                try {
                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(PPF_upLoadServerUri);

                    // Open a HTTP  connection to  the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy

                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Charset", "UTF-8");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("username", username);
                    conn.setRequestProperty("uploaded_file", fileName);
                    conn.setRequestProperty("function",function);

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

                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    PPF_serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();

                    Log.i("uploadPersonalImg", "HTTP Response is : "
                            + serverResponseMessage + ": " + PPF_serverResponseCode);


                    //close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();
                } catch (MalformedURLException ex) {

                    PPF_choosepicprogressbar.dismiss();
                    ex.printStackTrace();
                    Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                } catch (Exception e) {

                    PPF_choosepicprogressbar.dismiss();
                    e.printStackTrace();
                    Log.e("Upload file to server", "Exception : "  + e.getMessage(), e);
                }
            }
            return PPF_serverResponseCode;
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

            PPF_userImage.setImageBitmap(PPF_uploadbitmap);
            PPF_choosepicprogressbar.dismiss();
            Toast.makeText(requireActivity(), "更換完成", Toast.LENGTH_SHORT).show();
            piccount += 1;
        }

    }





}