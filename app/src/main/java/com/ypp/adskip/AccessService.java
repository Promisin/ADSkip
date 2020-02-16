package com.ypp.adskip;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import java.util.List;


public class AccessService extends AccessibilityService {
    private final String TAG = "AccessService";
    private final String CHANNEL_ID = "keepAccessService";
    private final String CHANNEL_NAME = "keepAccessService";
    private final String CHANNEL_DESCRIPTION = "前台服务保活";
    private final int NOTIFICATION_ID = 391;
    private static AccessService mAccessService;
    private NotificationCompat.Builder builder;
    private RemoteViews viewNoti;
    private NotificationManager manager;
    private SharedPreferences sharedPreferences;
    private boolean canStartWork = false;
    private boolean canConfirmPackage = false;
    private long lastClickTime;
    private String currentPackage;

    public AccessService() {
        mAccessService = this;
    }

    public static AccessService getServiceInstance(){
        return mAccessService;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType()==AccessibilityEvent.TYPE_VIEW_CLICKED){
            Log.d(TAG, "onAccessibilityEvent: "+event.toString());
        }
        if (canConfirmPackage){
            currentPackage = event.getPackageName().toString();
            canConfirmPackage = false;
        }
        if (event.getPackageName() != null &&
                event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED &&
                event.getPackageName().toString().contains("launcher") &&
                !event.getPackageName().toString().equals(this.getPackageName())) {
            canStartWork = true;
            canConfirmPackage = true;
            lastClickTime = event.getEventTime();
        }
        if (!canStartWork) {
            return;
        }
        int actionFlag = Utils.ACTION_VIEW_CLICK;
        if (event.getPackageName()!=null && (actionFlag = sharedPreferences
                .getInt(currentPackage,Utils.ACTION_VIEW_CLICK))==Utils.ACTION_NO_CLICK){
            canStartWork = false;
            return;
        }
        Log.d(TAG, "onAccessibilityEvent: "+actionFlag);
        try {
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
                event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED &&
                event.getEventTime()>lastClickTime) {
                //进入一个新的Activity
                List<AccessibilityWindowInfo> windows = getWindows();
                AccessibilityNodeInfo rootInfo = null;
                for (AccessibilityWindowInfo info : windows){
                    Log.d(TAG, "onAccessibilityEvent: "+info.toString());
                    if (info.getType()==AccessibilityWindowInfo.TYPE_APPLICATION){
                        rootInfo = info.getRoot();
                    }
                }
                if (rootInfo!=null){
                    ExecuteIntentService.startExecuteInfo(getApplicationContext(), rootInfo, actionFlag);
                }
                if ((event.getEventTime()-lastClickTime)>3000){
                    canStartWork = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            canStartWork = false;
        }


    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        Utils.setServiceRunning(getApplicationContext(),true);
        sharedPreferences = getApplicationContext().getSharedPreferences("app_action",MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getBooleanExtra("tryDisable",false)){
            disableSelf();
        }
        Log.d(TAG, "onStartCommand: ");
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN);
        channel.setDescription(CHANNEL_DESCRIPTION);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        channel.setSound(null, null);
        channel.enableVibration(false);
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        viewNoti = new RemoteViews(getPackageName(), R.layout.notification_layout);
        builder = new NotificationCompat.Builder(this.getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.notify_icon)
                .setContentIntent(PendingIntent.getActivity(
                        this, 0,
                        new Intent(this, MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                        0))
                .setWhen(System.currentTimeMillis())
                .setContent(viewNoti)
                .setPriority(NotificationCompat.PRIORITY_MIN);
        startForeground(NOTIFICATION_ID, builder.build());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        manager.cancel(NOTIFICATION_ID);
        Utils.setServiceRunning(getApplicationContext(),false);
    }
}
