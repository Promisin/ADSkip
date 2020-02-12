package com.ypp.adskip;

import android.content.ComponentName;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

public class AccessUtils {
    private static final String TAG = "AccessUtils";
    public static void click(AccessibilityNodeInfo info) {
        if (info==null){
            Log.d(TAG, "click: 不存在指定控件");
            return;
        }
        if (info.isClickable()) {
            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            click(info.getParent());
        }
    }

    public static List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(AccessibilityNodeInfo rootNodeInfo, String targetString){
        List<AccessibilityNodeInfo> resultList = new ArrayList<>();
        if (rootNodeInfo!=null && rootNodeInfo.getChildCount()!=0){
            for (int i = 0; i < rootNodeInfo.getChildCount(); i++) {
                if (rootNodeInfo.getChild(i)!=null &&
                        rootNodeInfo.getChild(i).getText()!=null &&
                        rootNodeInfo.getChild(i).getText().toString().contains(targetString)) {
                    resultList.add(rootNodeInfo.getChild(i));
                }
                resultList.addAll(findAccessibilityNodeInfosByText(rootNodeInfo.getChild(i), targetString));
            }
        }
        return resultList;
    }

    public static List<AccessibilityNodeInfo> findAccessibilityNodeInfosByIDContain(AccessibilityNodeInfo rootNodeInfo, String targetString){
        List<AccessibilityNodeInfo> resultList = new ArrayList<>();
        if (rootNodeInfo!=null && rootNodeInfo.getChildCount()!=0){
            for (int i = 0; i < rootNodeInfo.getChildCount(); i++) {
                if (rootNodeInfo.getChild(i)!=null &&
                        rootNodeInfo.getChild(i).getViewIdResourceName()!=null &&
                        rootNodeInfo.getChild(i).getViewIdResourceName().contains(targetString)) {
                    resultList.add(rootNodeInfo.getChild(i));
                }
                resultList.addAll(findAccessibilityNodeInfosByIDContain(rootNodeInfo.getChild(i), targetString));
            }
        }
        return resultList;
    }

    public static boolean isAccessibilityServiceEnabled(Context context, Class serviceClass){
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
