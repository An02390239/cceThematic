package com.example.layout1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Article_First_Page_Activity extends AppCompatActivity {

    private String My_PostURL = Login_Fragment.My_PostURL;
    private String AFPA_splitword = Login_Fragment.splitword;
    private String AFPA_postresult,AFPA_keyword;
    private String[] AFPA_article_data_search;
    private ArrayList<String> AFPA_searchdata_arraylist;
    private AllArticle_Fragment fragment1 = new AllArticle_Fragment();
    private SubscribeArticle_Fragment fragment2 = new SubscribeArticle_Fragment();
    private TabLayout AFPA_tabLayout;
    private FloatingActionButton AFPA_newButton;
    int now = 0;
    private ProgressDialog AFPA_progress;
    private GlobalVariable AFPA_refresh,AFPA_search, AFPA_searchdatatoAAF;
    private SearchHandler searchHandler = new SearchHandler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_first_page_activity);
        init();

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);



        ButtonHandler btnHandler = new ButtonHandler();
        AFPA_newButton.setOnClickListener(btnHandler);



        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainerView,fragment1,"??????");
        fragmentTransaction.add(R.id.fragmentContainerView,fragment2,"??????");



        fragmentTransaction.hide(fragment2);
        fragmentTransaction.commit();


        AFPA_tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                fragmentChange(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    private void init(){
        AFPA_progress = new ProgressDialog(Article_First_Page_Activity.this);
        AFPA_refresh = (GlobalVariable)Article_First_Page_Activity.this.getApplicationContext();
        AFPA_search = (GlobalVariable)Article_First_Page_Activity.this.getApplicationContext();
        AFPA_searchdatatoAAF = (GlobalVariable)Article_First_Page_Activity.this.getApplicationContext();
        AFPA_tabLayout = findViewById(R.id.tabLayout);
        AFPA_newButton = findViewById(R.id.newButton);
        AFPA_searchdata_arraylist = new ArrayList<>();
    }

    private class ButtonHandler implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Article_First_Page_Activity.this, AddArticle_Activity.class);
            startActivity(intent);
            Article_First_Page_Activity.this.finish();
        }
    }

    //??????????????????or??????????????????
    private void fragmentChange(int position){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (now){
            case 0:
               fragmentTransaction.hide(fragment1);
               break;
            case 1:

                fragmentTransaction.hide(fragment2);
                break;
        }
        switch (position){
            case 0:
                fragmentTransaction.show(fragment1);
                break;
            case 1:
                fragmentTransaction.show(fragment2);
                break;
        }
        fragmentTransaction.commit();
        now = position;
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // ??????menu???main.xml??????; ???action bar????????????
        getMenuInflater().inflate(R.menu.myself, menu);

        MenuItem menuSearchItem = menu.findItem(R.id.my_search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menuSearchItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.equals("")){
                    AFPA_keyword = query;

                    SearchRunnable searchRunnable = new SearchRunnable();
                    Thread search = new Thread(searchRunnable);
                    search.start();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }
    //Search???Handler
    private class SearchHandler extends Handler {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            AFPA_article_data_search = bundle.getStringArray("article_data_search");
            //Log.e("searchdata",AFPA_article_data_search[0]);
            AFPA_searchdata_arraylist.addAll(Arrays.asList(AFPA_article_data_search));
            AFPA_searchdatatoAAF.setArrayList(AFPA_searchdata_arraylist);
            /*for (int i = 0;i < AFPA_article_data_search.length-1;i+=7){
                AFPA_searchdata_arraylist.add(AFPA_article_data_search[i]+AFPA_splitword+
                        AFPA_article_data_search[i+1]+AFPA_splitword+AFPA_article_data_search[i+2]+AFPA_splitword+
                        AFPA_article_data_search[i+3]+AFPA_splitword+AFPA_article_data_search[i+4]+AFPA_splitword+
                        AFPA_article_data_search[i+5]+AFPA_splitword+ AFPA_article_data_search[i+6]);
            }*/
            /*AFPA_article_data_search[i]?????????id,AFPA_article_data_search[i+1]???????????????,
              AFPA_article_data_search[i+2]?????????,AFPA_article_data_search[i+3]???????????????,
              AFPA_article_data_search[i+4]???????????????,AFPA_article_data_search[i+5]?????????,
              AFPA_article_data_search[i+6]??????????????????*/

            AFPA_search.setSearch(true);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView,fragment1,"??????");
            fragmentTransaction.replace(R.id.fragmentContainerView,fragment2,"??????");

            fragmentTransaction.hide(fragment2);
            fragmentTransaction.commit();

            fragment1.onResume();
        }

    }
    //????????????
    private class SearchRunnable implements Runnable{

        @Override
        public void run() {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost myPost = new HttpPost(My_PostURL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("function","search"));
            params.add(new BasicNameValuePair("keyword",AFPA_keyword));
            //???????????????params??????List?????????post
            try {
                myPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //??????post???????????????????????????UTF-8?????????
                HttpResponse response = new DefaultHttpClient().execute(myPost);
                //??????
                AFPA_postresult = EntityUtils.toString(response.getEntity(),HTTP.UTF_8);
                //?????????(php???echo??????)???????????????String????????????UTF-8?????????
                if (!AFPA_postresult.equals("??????")){
                    AFPA_article_data_search = AFPA_postresult.split("u,4t;4e04");
                }else {
                    Log.e("AFPA_postresult",AFPA_postresult);
                }
            }catch (IOException e){
                e.printStackTrace();// ?????????????????????????????????
            }finally {
                client.getConnectionManager().shutdown();
            }
            Message msg = searchHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putStringArray("article_data_search", AFPA_article_data_search);
            msg.setData(bundle);
            searchHandler.sendMessage(msg);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        //??????????????????
        if(id == R.id.personal){
            Intent myself = new Intent(Article_First_Page_Activity.this, PersonalPage_Activity.class);
            startActivity(myself);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Reload current fragment

        //Log.e("GG", AFPA_refresh.getRefresh()+"");
        if (AFPA_refresh.getRefresh()){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView,fragment1,"??????");
            fragmentTransaction.replace(R.id.fragmentContainerView,fragment2,"??????");

            fragmentTransaction.hide(fragment2);
            fragmentTransaction.commit();
        }
    }


}