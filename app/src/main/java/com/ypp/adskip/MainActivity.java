package com.ypp.adskip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatToggleButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import java.net.Inet4Address;

public class MainActivity extends AppCompatActivity {
    private AppCompatToggleButton buttonStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonStart = (AppCompatToggleButton) findViewById(R.id.button_start);
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
    }
}
