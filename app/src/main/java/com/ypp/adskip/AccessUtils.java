package com.ypp.adskip;

import android.accessibilityservice.GestureDescription;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Path;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

import static com.ypp.adskip.AccessService.getServiceInstance;

class AccessUtils {
    private static final String TAG = "AccessUtils";
    static void click(AccessibilityNodeInfo info) {
        if (info==null){
            Log.d(TAG, "click: 不存在指定控件");
            return;
        }
        if (info.isClickable()) {
            Log.d(TAG, "click: "+info.getViewIdResourceName());
            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            click(info.getParent());
        }
    }

    static void clickInScreen(int x, int y){
        int duration = ViewConfiguration.getTapTimeout()+200;
        Path path = new Path();
        path.moveTo(x, y);
        GestureDescription.StrokeDescription description =
                new GestureDescription.StrokeDescription(path, 0, duration);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(description);
        getServiceInstance().dispatchGesture(builder.build(), null, null);
        Log.d(TAG, "clickInScreen: "+x+" "+y);
    }

    static List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(AccessibilityNodeInfo rootNodeInfo, String targetString){
        List<AccessibilityNodeInfo> resultList = new ArrayList<>();
        if (rootNodeInfo!=null && rootNodeInfo.getChildCount()!=0){
            for (int i = 0; i < rootNodeInfo.getChildCount(); i++) {
                AccessibilityNodeInfo childInfo = rootNodeInfo.getChild(i);
                if (childInfo!=null &&
                        childInfo.getText()!=null &&
                        childInfo.getText().toString().contains(targetString)) {
                    resultList.add(childInfo);
                }
                resultList.addAll(findAccessibilityNodeInfosByText(childInfo, targetString));
                if (childInfo!=null && !resultList.contains(childInfo)){
                    childInfo.recycle();
                }
            }
        }
        return resultList;
    }

    static List<AccessibilityNodeInfo> findAccessibilityNodeInfosByIDContain(AccessibilityNodeInfo rootNodeInfo, String targetString){
        List<AccessibilityNodeInfo> resultList = new ArrayList<>();
        if (rootNodeInfo!=null && rootNodeInfo.getChildCount()!=0){
            for (int i = 0; i < rootNodeInfo.getChildCount(); i++) {
                AccessibilityNodeInfo childInfo = rootNodeInfo.getChild(i);
                if (childInfo!=null && childInfo.getViewIdResourceName()!=null) {
                    String[] resourceID = childInfo.getViewIdResourceName().split(":");
                    if (resourceID.length>=2 && resourceID[1].contains(targetString)){
                        resultList.add(childInfo);
                    }
                }
                resultList.addAll(findAccessibilityNodeInfosByIDContain(childInfo, targetString));
                if (childInfo!=null && !resultList.contains(childInfo)){
                    childInfo.recycle();
                }
            }
        }
        return resultList;
    }

    static boolean isAccessibilityServiceEnabled(Context context, Class serviceClass){
        ComponentName componentName = new ComponentName(context, serviceClass);
        String enabledService = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledService != null){
            TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');
            splitter.setString(enabledService);

            while (splitter.hasNext()){
                String name = splitter.next();
                ComponentName currentComName =ComponentName.unflattenFromString(name);
                if (currentComName.equals(componentName)){
                    return true;
                }
            }
        }
        return false;
    }
}
