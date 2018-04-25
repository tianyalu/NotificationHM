package com.sty.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String CHANNEL_ID1 = "chat";
    private static final String CHANNEL_NAME1 = "聊天消息";
    private static final String CHANNEL_ID2 = "subscribe";
    private static final String CHANNEL_NAME2 = "订阅消息";

    private Button btnSendChatNotification;
    private Button btnSendSubscribeNotification;
    private Button btnCloseNotification;

    private NotificationManager nm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setListeners();
        createTwoNotificationChannel();
    }

    private void initViews(){
        btnSendChatNotification = findViewById(R.id.btn_send_chat_notification);
        btnSendSubscribeNotification = findViewById(R.id.btn_send_subscribe_notification);
        btnCloseNotification = findViewById(R.id.btn_close_notification);

        //获取通知的管理者
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private void setListeners(){
        btnSendChatNotification.setOnClickListener(this);
        btnSendSubscribeNotification.setOnClickListener(this);
        btnCloseNotification.setOnClickListener(this);
    }

    private void createTwoNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //在小米手机上重要性等级设置也无效，需要手动设置
            createNotificationChannel(CHANNEL_ID1, CHANNEL_NAME1, NotificationManager.IMPORTANCE_HIGH);
            createNotificationChannel(CHANNEL_ID2, CHANNEL_NAME2, NotificationManager.IMPORTANCE_DEFAULT);
            Log.i("sty" , "-----Build.VERSION_CODES.O-----");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_send_chat_notification:
                sendChatNotification();
                break;
            case R.id.btn_send_subscribe_notification:
                sendSubscribeNotification();
                break;
            case R.id.btn_close_notification:
                closeNotification();
                break;
            default:
                break;
        }
    }

    private void sendChatNotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = nm.getNotificationChannel(CHANNEL_ID1);
            if(channel.getImportance() == NotificationManager.IMPORTANCE_NONE){ //小米貌似当前还不支持渠道设置，所有该操作没有效果
                Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
                startActivity(intent);
                Toast.makeText(this, "请手动将通知打开", Toast.LENGTH_SHORT).show();
            }
        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID1)
                .setContentTitle("收到一条聊天消息") //设置通知标题
                .setContentText("今天中午吃什么？") //设置通知内容
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.bamboo) //设置小图标（在小米手机上该设置无效，只能显示本应用的图标，可能是小米系统UI作了限制）
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.bamboo)) //设置大图标
                .setAutoCancel(true)
                .build();
        nm.notify(10, notification); //发送通知
    }

    private void sendSubscribeNotification(){
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID2)
                .setContentTitle("收到一条订阅消息") //设置通知标题
                .setContentText("地铁沿线30万商铺抢购中") //设置通知内容
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.bamboo) //设置小图标（在小米手机上该设置无效，只能显示本应用的图标，可能是小米系统UI作了限制）
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.bamboo)) //设置大图标
                .setNumber(20)
                .setAutoCancel(true)
                .build();
        nm.notify(20, notification); //发送通知
    }

    private void closeNotification(){
        //取消通知
        nm.cancel(10);
        nm.cancel(20);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance){
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.enableLights(true); //是否在桌面icon右上角展示小红点
        channel.setLightColor(Color.RED); //小红点颜色
        channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }
}
