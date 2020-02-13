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

    public ExecuteIntentService() {
        super("ExecuteIntentService");
    }

    public static void startExecuteInfo(Context context, AccessibilityNodeInfo rootInfo) {
        Intent intent = new Intent(context, ExecuteIntentService.class);
        intent.setAction(ACTION_EXECUTE);
        intent.putExtra(EXTRA_INFO, rootInfo);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_EXECUTE.equals(action)) {
                final AccessibilityNodeInfo rootInfo = intent.getParcelableExtra(EXTRA_INFO);
                handleActionExecute(rootInfo);
            }
        }
    }


    private void handleActionExecute(AccessibilityNodeInfo rootInfo) {
        /*List<AccessibilityNodeInfo> resultInfoList
                = AccessUtils.findAccessibilityNodeInfosByText(rootInfo, "跳过");*/
        List<AccessibilityNodeInfo> resultInfoList
                = rootInfo.findAccessibilityNodeInfosByText("跳过");
        if (resultInfoList.isEmpty()) {
            resultInfoList = AccessUtils.findAccessibilityNodeInfosByIDContain(rootInfo, "skip");
        }
        if (!resultInfoList.isEmpty()) {
            for (AccessibilityNodeInfo info : resultInfoList) {
                Rect rect = new Rect();
                info.getBoundsInScreen(rect);
                AccessUtils.clickInScreen(rect.centerX(), rect.centerY());
                info.recycle();
            }
            stopSelf();
        }
    }
}
