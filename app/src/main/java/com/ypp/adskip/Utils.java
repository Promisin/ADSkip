package com.ypp.adskip;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

class Utils {
    private static final String TAG = "Utils";
    static final int ACTION_NO_CLICK = 1;
    static final int ACTION_VIEW_CLICK = 2;
    static final int ACTION_SCREEN_CLICK = 3;
    static final String[] ACTION_DESCRIP = {"不点击：对指定APP不执行点击跳过广告",
            "控件点击：对找到的跳过按钮直接点击",
            "屏幕点击：模拟对跳过按钮在屏幕上对应位置的点击，可能误点击悬浮框、" +
                    "弹出到的通知等在按钮上层的控件，建议在控件点击无效的情况下尝试使用"};
    static boolean isServiceRunning(Context context) {
        return getSharedPreferences(context).getBoolean("service",false);
    }

    static void setServiceRunning(Context context, boolean isRunning){
        getSharedPreferences(context).edit().putBoolean("service", isRunning).apply();
    }

    private static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences("config",Context.MODE_PRIVATE);
    }

    static List<AppNodeInfo> getInstalledAppList(Context context){
        PackageManager packageManager = context.getPackageManager();
        List<AppNodeInfo> appNodeInfoList = new ArrayList<AppNodeInfo>();
        try {
            List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
            for (PackageInfo info : packageInfos){
                if (!info.packageName.startsWith("com.android") && info.applicationInfo.loadLabel(packageManager).length()<16){
                    AppNodeInfo appNodeInfo = new AppNodeInfo(
                            info.applicationInfo.loadIcon(packageManager),
                            info.packageName,
                            (String) info.applicationInfo.loadLabel(packageManager));
                    appNodeInfoList.add(appNodeInfo);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return appNodeInfoList;
    }

    public static void showChooseClickActionDialog(final Context context, final AppNodeInfo appNodeInfo){
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_choose, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        AlertDialog dialog =builder.create();
        ImageView helpButton = view.findViewById(R.id.help_button);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder hintBuilder = new AlertDialog.Builder(context);
                /*hintBuilder.setTitle("选项说明")
                        .setItems(ACTION_DESCRIP,null);*/
                View hintView = LayoutInflater.from(context).inflate(R.layout.dialog_descripton,null);
                hintBuilder.setView(hintView);
                hintBuilder.create().show();
            }
        });
        RadioGroup actionRg = view.findViewById(R.id.action_rg);
        final SharedPreferences sharedPreferences = context.getApplicationContext()
                .getSharedPreferences("app_action",Context.MODE_PRIVATE);
        switch (sharedPreferences.getInt(appNodeInfo.getPackageName(),ACTION_VIEW_CLICK)){
            case ACTION_NO_CLICK:
                actionRg.check(R.id.no_click_rb);
                break;
            case ACTION_SCREEN_CLICK:
                actionRg.check(R.id.screen_click_rb);
                break;
            case ACTION_VIEW_CLICK:
            default:
                actionRg.check(R.id.view_click_rb);
                break;
        }
        actionRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, "onCheckedChanged: "+checkedId);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                switch (checkedId){
                    case R.id.no_click_rb:
                        editor.putInt(appNodeInfo.getPackageName(),Utils.ACTION_NO_CLICK);
                        break;
                    case R.id.view_click_rb:
                        editor.putInt(appNodeInfo.getPackageName(),Utils.ACTION_VIEW_CLICK);
                        break;
                    case R.id.screen_click_rb:
                        editor.putInt(appNodeInfo.getPackageName(),Utils.ACTION_SCREEN_CLICK);
                        break;
                    default:break;
                }
                editor.apply();
            }
        });
        dialog.show();
    }
}
