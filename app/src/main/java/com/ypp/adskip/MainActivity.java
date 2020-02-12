package com.ypp.adskip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatToggleButton;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private AppCompatToggleButton buttonStart;
    private TextView permissionTv;
    private LinearLayout permissionLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonStart = findViewById(R.id.button_start);
        buttonStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(MainActivity.this, AccessService.class);
                if (isChecked){
                    startService(intent);
                }
                else {
                    stopService(intent);
                }
            }
        });
        permissionTv = findViewById(R.id.permission_tv);
        permissionLayout = findViewById(R.id.permission_layout);
        permissionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccessUtils.isAccessibilityServiceEnabled(getApplicationContext(), AccessService.class)){
                    Toast.makeText(MainActivity.this, "权限已开启",Toast.LENGTH_SHORT).show();
                }
                else {
                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        permissionTv.post(new Runnable() {
            @Override
            public void run() {
                if (AccessUtils.isAccessibilityServiceEnabled(getApplicationContext(),AccessService.class)){
                    Drawable drawable = getResources().getDrawable(R.drawable.permission_yes, null);
                    drawable.setBounds(0,0,permissionTv.getMeasuredHeight(),permissionTv.getMeasuredHeight());
                    permissionTv.setCompoundDrawablesRelative(null,null,drawable,null);
                }
                else {
                    Drawable drawable = getResources().getDrawable(R.drawable.permission_no, null);
                    drawable.setBounds(0,0,permissionTv.getMeasuredHeight(),permissionTv.getMeasuredHeight());
                    permissionTv.setCompoundDrawablesRelative(null,null,drawable,null);
                }
            }
        });

    }
}
