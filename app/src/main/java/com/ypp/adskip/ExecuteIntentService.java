package com.ypp.adskip;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityNodeInfo;

import java.sql.Time;
import java.util.List;
import java.util.Timer;

public class ExecuteIntentService extends IntentService {
    private final String TAG = "ExecuteIntentService";
    private SharedPreferences positionPreferences;
    private SharedPreferences delayPreferences;

    private static final String ACTION_EXECUTE = "com.ypp.adskip.action.EXECUTE";

    private static final String EXTRA_INFO = "com.ypp.adskip.extra.INFO";
    private static final String EXTRA_ACTION = "com.ypp.adskip.extra.ACTION";
    private static final String EXTRA_PACKAGE = "com.ypp.adskip.extra.PACKAGE";

    public ExecuteIntentService() {
        super("ExecuteIntentService");
    }

    public static void startExecuteInfo(Context context, AccessibilityNodeInfo rootInfo, String packageName, int actionFlag) {
        Intent intent = new Intent(context, ExecuteIntentService.class);
        intent.setAction(ACTION_EXECUTE);
        intent.putExtra(EXTRA_INFO, rootInfo);
        intent.putExtra(EXTRA_ACTION, actionFlag);
        intent.putExtra(EXTRA_PACKAGE, packageName);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (positionPreferences==null || delayPreferences==null){
            positionPreferences = getApplicationContext()
                    .getSharedPreferences("app_position",MODE_PRIVATE);
            delayPreferences = getApplicationContext()
                    .getSharedPreferences("app_delay",MODE_PRIVATE);
        }
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_EXECUTE.equals(action)) {
                final AccessibilityNodeInfo rootInfo = intent.getParcelableExtra(EXTRA_INFO);
                final int actionFlag = intent.getIntExtra(EXTRA_ACTION, Utils.ACTION_VIEW_CLICK);
                String packageName = intent.getStringExtra(EXTRA_PACKAGE);
                handleActionExecute(rootInfo, packageName, actionFlag);
            }
        }
    }


    private void handleActionExecute(AccessibilityNodeInfo rootInfo, String packageName, int actionFlag) {
        if (actionFlag==Utils.ACTION_CUSTOM_CLICK){
            Log.d(TAG, "handleActionExecute: "+packageName);
            final int x =  Integer.parseInt(positionPreferences.getString(packageName+"x","-1"));
            final int y = Integer.parseInt(positionPreferences.getString(packageName+"y","-1"));
            final int delay = Integer.parseInt(delayPreferences.getString(packageName,"1000"));
            Log.d(TAG, "handleActionExecute: "+x+" "+y+" "+delay);
            //AccessUtils.clickInScreen(x,y);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(delay);
                        AccessUtils.clickInScreen(x,y);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            return;
        }
        List<AccessibilityNodeInfo> resultInfoList
                = rootInfo.findAccessibilityNodeInfosByText("跳过");
        /*if (resultInfoList.isEmpty()){
            resultInfoList = rootInfo.findAccessibilityNodeInfosByText("skip");
        }*/
        if (resultInfoList.isEmpty()) {
            resultInfoList = AccessUtils.findAccessibilityNodeInfosByIDContain(rootInfo, "skip");
        }
        if (!resultInfoList.isEmpty()) {
            for (AccessibilityNodeInfo info : resultInfoList) {
                switch (actionFlag){
                    case Utils.ACTION_VIEW_CLICK:
                        viewClick(info);
                        Log.d(TAG, "handleActionExecute: view");
                        break;
                    case Utils.ACTION_SCREEN_CLICK:
                        screenClick(info);
                        Log.d(TAG, "handleActionExecute: screen");
                        break;
                    default:
                        break;
                }
                info.recycle();
            }
            stopSelf();
        }
    }

    private void viewClick(AccessibilityNodeInfo info){
        AccessUtils.click(info);
    }

    private void screenClick(AccessibilityNodeInfo info){
        Rect rect = new Rect();
        info.getBoundsInScreen(rect);
        AccessUtils.clickInScreen(rect.centerX(), rect.centerY());
    }
}
