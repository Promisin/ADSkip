package com.ypp.adskip;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
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
    static final int ACTION_CUSTOM_CLICK = 4;
    private String[] permissions = {Manifest.permission.SYSTEM_ALERT_WINDOW};
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
            List<ApplicationInfo> applicationInfos = packageManager.getInstalledApplications(0);
            for (ApplicationInfo info : applicationInfos){
                if (packageManager.getLaunchIntentForPackage(info.packageName) != null){
                    AppNodeInfo appNodeInfo = new AppNodeInfo(
                            info.loadIcon(packageManager),
                            info.packageName,
                            (String) info.loadLabel(packageManager));
                    appNodeInfoList.add(appNodeInfo);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return appNodeInfoList;
    }

    static boolean canDrawOverlays(Context context){
        return Settings.canDrawOverlays(context);
    }

    static void showChooseClickActionDialog(final Context context, final AppNodeInfo appNodeInfo){
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
