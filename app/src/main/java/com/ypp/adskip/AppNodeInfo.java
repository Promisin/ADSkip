package com.ypp.adskip;

import android.graphics.drawable.Drawable;

public class AppNodeInfo {
    private Drawable icon;
    private String packageName;
    private String name;

    AppNodeInfo(Drawable icon, String packageName, String name) {
        this.icon = icon;
        this.packageName = packageName;
        this.name = name;
    }

    Drawable getIcon() {
        return icon;
    }

    String getPackageName() {
        return packageName;
    }

    public String getName() {
        return name;
    }

}
