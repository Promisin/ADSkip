package com.ypp.adskip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            case "android.intent.action.BOOT_COMPLETED":
                Intent serviceIntent = new Intent(context, AccessService.class);
                context.startService(serviceIntent);
                Toast.makeText(context, "ADSkip启动", Toast.LENGTH_SHORT).show();
            default:break;
        }
    }
}
