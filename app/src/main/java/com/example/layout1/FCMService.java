package com.example.layout1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FCMService extends FirebaseMessagingService {
    public static final String TAG = FCMService.class.getSimpleName();
    private String CHANNEL_ID = "156";

    public FCMService() {
    }

    @Override
    public void onNewToken(@NonNull String s){
        super.onNewToken(s);
        Log.d(TAG, "裝置Token: "+s);
    }
    /*@Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage){
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived: "+remoteMessage.getData());
    }*/



    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "onMessageReceived: " + remoteMessage.getData());

        //從這邊開始往下
        /**檢查手機版本是否支援通知；若支援則新增"頻道"*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "layout0414", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(channel);
        }
        /*如果專案的目標平台是Android 8.0(API level 26)或者更高的API，
        開發者一定要實作通知頻道，若 APP 在 7.1 或之前的舊版本 Android 手機中執行
        ，它會以舊的通知方式展示，也就是沒有通知頻道的效果。*/


        /**設定點選通知會跳轉的頁面*/
        Map<String,String> s = remoteMessage.getData();
        Intent intent = new Intent(this, Version_Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.putExtra("data",s.get("key_1"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0
                ,intent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.b);
        /**建置通知欄位的內容*/
        NotificationCompat.Builder builder
                = new NotificationCompat.Builder(FCMService.this,CHANNEL_ID)
                .setColor(Color.BLACK)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                //.setLargeIcon(largeIcon)
                .setTicker(s.get("title"))
                .setContentTitle(s.get("title"))
                .setContentText(s.get("body"))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(s.get("body")))
                .setDefaults(Notification.DEFAULT_ALL)
                /*當我們的訊息內容較長會超過一行時預設會將訊息結尾變成...而不能完整顯示
                ，此時可以再加入一行BigText讓長訊息能完整顯示*/
                /*.setDefaults(Notification.DEFAULT_ALL) 設置提醒，有震動(DEFAULT_VIBRATE)
                音效(DEFAULT_VIBRATE)燈光(DEFAULT_LIGHT)等3種，DEFAULT_ALL則為全部都用
                 */
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setWhen(System.currentTimeMillis())// 立即顯示
                .setContentIntent(pendingIntent);//點選通知框會跳轉

        /**發出通知*/
        NotificationManagerCompat notificationManagerCompat
                = NotificationManagerCompat.from(FCMService.this);
        notificationManagerCompat.notify(1,builder.build());
        //到這邊
    }
}