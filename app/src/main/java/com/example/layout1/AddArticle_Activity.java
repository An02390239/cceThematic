package com.example.layout1;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AddArticle_Activity extends AppCompatActivity {
    private final String My_PostURL = Login_Fragment.My_PostURL;
    private int AAA_serverResponseCode = 0;
    private String AAA_username, AAA_title, AAA_context, AAA_theme, AAA_postresult,AAA_uploadimagepath;
    private ImageView avatarImage;
    private EditText AAA_contextedit, AAA_titleedit, AAA_themeedit;
    private ImageButton AAA_selectpic;
    private AlertDialog.Builder AAA_postdia,AAA_checkdia;
    private ProgressDialog AAA_choosepicprogressbar;
    private boolean AAA_check = false;
    private TextView AAA_postertext;
    private GlobalVariable g;
    private Bitmap AAA_uploadbitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addarticle_activity);
        init();
        AAA_username = g.getUsername();
        AAA_postertext.setText(AAA_username);

        AAA_postdia.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //????????????
                if (AAA_check){
                    if (AAA_uploadimagepath!=null){
                        Intent add = new Intent(AddArticle_Activity.this, Article_First_Page_Activity.class);
                        startActivity(add);
                        AddArticle_Activity.this.finish();
                    }else {
                        Intent add = new Intent(AddArticle_Activity.this, Article_First_Page_Activity.class);
                        startActivity(add);
                        AddArticle_Activity.this.finish();
                        AAA_choosepicprogressbar.dismiss();
                    }
                }
            }
        });
        AAA_postdia.setCancelable(false);
        AAA_checkdia.setPositiveButton("???", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AddRunnable addRunnable = new AddRunnable();
                Thread modify = new Thread(addRunnable);
                modify.start();

            }
        });
        AAA_checkdia.setNegativeButton("???", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AAA_checkdia.setCancelable(false);


        OnClickListener onClickListener = new OnClickListener();
        AAA_selectpic.setOnClickListener(onClickListener);

    }

    private class OnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.AAA_pictureButton:
                    Intent picture = new Intent(Intent.ACTION_PICK);
                    picture.setType("image/*");
                    activityResultLauncher.launch(picture);

                    break;
            }
        }
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null){
                    Intent data = result.getData();
                    Uri ImageUri = data.getData();

                    AAA_uploadimagepath = getPath(ImageUri);
                    try {
                        AAA_uploadbitmap = BitmapFactory.decodeStream(this.getContentResolver()
                                .openInputStream(ImageUri));

                        LinearLayout layout = findViewById(R.id.AAA_piclinearlayout);
                        ImageView AAA_userImage = new ImageView(getApplicationContext());
                        int zoom ;
                        RelativeLayout.LayoutParams params = null;
                        if (AAA_uploadbitmap!=null){
                            zoom = AAA_uploadbitmap.getWidth()/300;
                            if (AAA_uploadbitmap.getWidth()<300 && AAA_uploadbitmap.getHeight()<300){
                                params = new RelativeLayout.LayoutParams(AAA_uploadbitmap.getWidth()
                                        ,AAA_uploadbitmap.getHeight());
                            }else {
                                params = new RelativeLayout.LayoutParams(AAA_uploadbitmap.getWidth()/zoom
                                        ,AAA_uploadbitmap.getHeight()/zoom);
                            }

                            params.setMargins(50,100,20,20);
                            AAA_userImage.setImageBitmap(AAA_uploadbitmap);

                            layout.addView(AAA_userImage,params);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

    //?????????????????????????????????????????????
    private class uploadBoardImg extends AsyncTask< String, Integer, Integer> {


        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        String function = "uploadBoardImg",username = AAA_username;

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;

        @Override
        protected void onPreExecute() {
            //????????? ???????????????????????????
            super.onPreExecute();

        }

        @Override
        protected Integer doInBackground(String... strings) {
            //????????? ??????????????????
            File sourceFile = new File(strings[0]);
            String fileName = strings[0];
            if (!sourceFile.isFile()) {

                AAA_choosepicprogressbar.dismiss();

                Log.e("uploadFile", "Source File not exist :" + strings[0]);


                return 0;

            }
            else
            {
                if (Build.VERSION.SDK_INT >= 23) {//??????????????????
                    int REQUEST_CODE_PERMISSION_STORAGE = 100;
                    String[] permissions = {
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    };

                    for (String str : permissions) {
                        if (AddArticle_Activity.this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                            AddArticle_Activity.this.requestPermissions(permissions, REQUEST_CODE_PERMISSION_STORAGE);
                        }else {
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
                                dos.writeBytes("Content-Disposition: form-data; name=\"username\"" + lineEnd + lineEnd
                                        + username );
                                dos.writeBytes(lineEnd);

                                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                                // Responses from the server (code and message)
                                AAA_serverResponseCode = conn.getResponseCode();
                                String serverResponseMessage = conn.getResponseMessage();

                                Log.i("uploadFile", "HTTP Response is : "
                                        + serverResponseMessage + ": " + AAA_serverResponseCode);

                                //close the streams //
                                fileInputStream.close();
                                dos.flush();
                                dos.close();
                            } catch (MalformedURLException ex) {

                                AAA_choosepicprogressbar.dismiss();
                                ex.printStackTrace();
                                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                            } catch (Exception e) {

                                AAA_choosepicprogressbar.dismiss();
                                e.printStackTrace();
                                Log.e("Upload file to server", "Exception : "  + e.getMessage(), e);
                            }

                        }
                    }

                }else {
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
                        AAA_serverResponseCode = conn.getResponseCode();
                        String serverResponseMessage = conn.getResponseMessage();

                        Log.i("uploadFile", "HTTP Response is : "
                                + serverResponseMessage + ": " + AAA_serverResponseCode);


                        //close the streams //
                        fileInputStream.close();
                        dos.flush();
                        dos.close();
                    } catch (MalformedURLException ex) {

                        AAA_choosepicprogressbar.dismiss();
                        ex.printStackTrace();
                        Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                    } catch (Exception e) {

                        AAA_choosepicprogressbar.dismiss();
                        e.printStackTrace();
                        Log.e("Upload file to server", "Exception : "  + e.getMessage(), e);
                    }
                }


            } // End else block

            return AAA_serverResponseCode;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //????????? ????????????????????????????????????
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            //????????? ??????????????????
            super.onPostExecute(integer);
            //?????????(php???echo??????)???????????????String????????????UTF-8?????????
            AAA_postdia.setTitle("");
            AAA_postdia.setMessage(AAA_postresult);
            AAA_postdia.show();

            AAA_choosepicprogressbar.dismiss();

        }

    }



    /*????????????????????????Path??????????????????????????????Path??????????????????Path???Content://??????
        ?????????Path???????????????????????????????????????*/
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
    private void init(){
        g = (GlobalVariable) this.getApplicationContext();
        avatarImage = findViewById(R.id.AAA_avatarImage);
        AAA_contextedit = findViewById(R.id.AAA_contentEdit);
        AAA_titleedit = findViewById(R.id.AAA_titleEdit);
        AAA_themeedit = findViewById(R.id.AAA_themeEdit);
        AAA_selectpic = findViewById(R.id.AAA_pictureButton);
        AAA_postertext = findViewById(R.id.AAA_posterText);


        AAA_contextedit.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        AAA_contextedit.setSingleLine(false);
        AAA_contextedit.setHorizontallyScrolling(false);

        AAA_titleedit.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        AAA_titleedit.setSingleLine(false);
        AAA_titleedit.setHorizontallyScrolling(false);

        AAA_themeedit.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        AAA_themeedit.setSingleLine(false);
        AAA_themeedit.setHorizontallyScrolling(false);

        AAA_postdia = new AlertDialog.Builder(AddArticle_Activity.this);
        AAA_checkdia = new AlertDialog.Builder(AddArticle_Activity.this);
        AAA_choosepicprogressbar = new ProgressDialog(AddArticle_Activity.this);

    }

    // ??????menu???main.xml??????; ???action bar????????????
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //actionbar butoon????????????
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //????????????
        if(id == R.id.finish){
            AAA_title = AAA_titleedit.getText().toString();
            AAA_context = AAA_contextedit.getText().toString();
            AAA_theme = AAA_themeedit.getText().toString();
            if (!AAA_title.isEmpty() && !AAA_context.isEmpty() && !AAA_theme.isEmpty()){
                AAA_checkdia.setMessage("????????????????????????????");
                AAA_checkdia.show();
            }else {
                AddRunnable addRunnable = new AddRunnable();
                Thread modify = new Thread(addRunnable);
                modify.start();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    //??????Post????????????
    private class AddRunnable implements Runnable{
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("function","announcement"));
            params.add(new BasicNameValuePair("username", AAA_username));
            params.add(new BasicNameValuePair("title", AAA_title));
            params.add(new BasicNameValuePair("context", AAA_context));
            params.add(new BasicNameValuePair("theme", AAA_theme));
            //???????????????params??????List?????????post
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //??????post???????????????????????????UTF-8?????????
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //??????
                AAA_postresult = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);

                if ( AAA_postresult.contains("\n")){
                    String[] split = AAA_postresult.split("\n");
                    AAA_postresult = split[1];
                }
                if (AAA_postresult.equals("????????????")) AAA_check = true;
                if (AAA_uploadimagepath!=null){
                    new uploadBoardImg().execute(AAA_uploadimagepath);
                }else {
                    AAA_postdia.setTitle("");
                    AAA_postdia.setMessage(AAA_postresult);
                    Looper.prepare();
                    AAA_postdia.show();
                    Looper.loop();
                }



            }catch (IOException e){
                e.printStackTrace();// ?????????????????????????????????
            }finally {
                client.getConnectionManager().shutdown();
            }
        }// run()
    }// class TimerRunnable

    @Override//???????????????
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            ConfirmExit();//????????????????????????????????????
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    //????????????
    public void ConfirmExit(){
        Intent backtoAA = new Intent(AddArticle_Activity.this,
                Article_First_Page_Activity.class);
        startActivity(backtoAA);
        AddArticle_Activity.this.finish();
    }
}