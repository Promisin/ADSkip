package com.ypp.adskip;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
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
                handleActionFoo(rootInfo);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(AccessibilityNodeInfo rootInfo) {
        /*List<AccessibilityNodeInfo> resultInfoList
                = AccessUtils.findAccessibilityNodeInfosByText(rootInfo, "跳过");*/
        List<AccessibilityNodeInfo> resultInfoList
                = rootInfo.findAccessibilityNodeInfosByText("跳过");
        if (resultInfoList.isEmpty()) {
            Log.d(TAG, "onAccessibilityEvent: startSearchByID");
            resultInfoList = AccessUtils.findAccessibilityNodeInfosByIDContain(rootInfo, "skip");
        }
        Log.d(TAG, "onAccessibilityEvent: list:" + resultInfoList.toString());
        if (!resultInfoList.isEmpty()) {
            for (AccessibilityNodeInfo info : resultInfoList) {
                Log.d(TAG, "onAccessibilityEvent: Find&Click");
                AccessUtils.click(info);
                info.recycle();
            }
            stopSelf();
        }
    }
}
