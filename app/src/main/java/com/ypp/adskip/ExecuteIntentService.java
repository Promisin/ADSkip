package com.ypp.adskip;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class ExecuteIntentService extends IntentService {
    private final String TAG = "ExecuteIntentService";

    private static final String ACTION_EXECUTE = "com.ypp.adskip.action.EXECUTE";

    private static final String EXTRA_INFO = "com.ypp.adskip.extra.INFO";
    private static final String EXTRA_ACTION = "com.ypp.adskip.extra.ACTION";

    public ExecuteIntentService() {
        super("ExecuteIntentService");
    }

    public static void startExecuteInfo(Context context, AccessibilityNodeInfo rootInfo, int actionFlag) {
        Intent intent = new Intent(context, ExecuteIntentService.class);
        intent.setAction(ACTION_EXECUTE);
        intent.putExtra(EXTRA_INFO, rootInfo);
        intent.putExtra(EXTRA_ACTION, actionFlag);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_EXECUTE.equals(action)) {
                final AccessibilityNodeInfo rootInfo = intent.getParcelableExtra(EXTRA_INFO);
                final int actionFlag = intent.getIntExtra(EXTRA_ACTION, Utils.ACTION_VIEW_CLICK);
                handleActionExecute(rootInfo, actionFlag);
            }
        }
    }


    private void handleActionExecute(AccessibilityNodeInfo rootInfo, int actionFlag) {
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
                        break;
                    case Utils.ACTION_SCREEN_CLICK:
                        screenClick(info);
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
