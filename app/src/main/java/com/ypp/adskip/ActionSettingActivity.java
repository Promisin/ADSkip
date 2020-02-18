package com.ypp.adskip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import static com.ypp.adskip.Utils.ACTION_CUSTOM_CLICK;
import static com.ypp.adskip.Utils.ACTION_NO_CLICK;
import static com.ypp.adskip.Utils.ACTION_SCREEN_CLICK;
import static com.ypp.adskip.Utils.ACTION_VIEW_CLICK;

public class ActionSettingActivity extends AppCompatActivity {
    private final String TAG = "ActionSettingActivity";
    private RadioGroup actionRg;
    private RadioButton customClickRb;
    private ConstraintLayout customLayout;
    private TextView xDisTv;
    private TextView yDisTv;
    private EditText delayEt;
    private Button saveButton;
    private TextView actionDescription;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SharedPreferences positionPreferences;
    private SharedPreferences.Editor positionEditor;
    private SharedPreferences delayPreferences;
    private SharedPreferences.Editor delayEditor;
    private String packageName;
    private WindowManager windowManager;
    private View anchorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_setting);
        setTitle(R.string.action_setting_title);
        sharedPreferences = getApplicationContext()
                .getSharedPreferences("app_action", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        positionPreferences = getApplicationContext()
                .getSharedPreferences("app_position",MODE_PRIVATE);
        positionEditor = positionPreferences.edit();
        delayPreferences = getApplicationContext()
                .getSharedPreferences("app_delay",MODE_PRIVATE);
        delayEditor = delayPreferences.edit();
        packageName = getIntent().getStringExtra("packageName");
        actionRg = findViewById(R.id.action_rg);
        customClickRb = findViewById(R.id.custom_click_rb);
        actionDescription = findViewById(R.id.action_description);
        customLayout = findViewById(R.id.custom_layout);
        xDisTv = findViewById(R.id.x_dis_tv);
        yDisTv = findViewById(R.id.y_dis_tv);
        delayEt = findViewById(R.id.delay_et);
        saveButton = findViewById(R.id.save_button);
        actionRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, "onCheckedChanged: " + checkedId);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                switch (checkedId) {
                    case R.id.no_click_rb:
                        editor.putInt(packageName, Utils.ACTION_NO_CLICK);
                        actionDescription.setText(R.string.description_no_click);
                        break;
                    case R.id.view_click_rb:
                        editor.putInt(packageName, Utils.ACTION_VIEW_CLICK);
                        actionDescription.setText(R.string.description_view_click);
                        break;
                    case R.id.screen_click_rb:
                        editor.putInt(packageName, Utils.ACTION_SCREEN_CLICK);
                        actionDescription.setText(R.string.description_screen_click);
                        break;
                    case R.id.custom_click_rb:
                        actionDescription.setText(R.string.description_custom_click);
                    default:
                        break;
                }
                editor.apply();
            }
        });
        customClickRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    showAnchor();
                    xDisTv.setText(positionPreferences.getInt(packageName+"x",0));
                    yDisTv.setText(positionPreferences.getInt(packageName+"y",0));
                    delayEt.setText(delayPreferences.getInt(packageName,1000));
                    customLayout.setVisibility(View.VISIBLE);
                }
                else {
                    removeAnchor();
                    customLayout.setVisibility(View.GONE);
                }
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String positionX = xDisTv.getText().toString();
                String positionY= yDisTv.getText().toString();
                Log.d(TAG, "onClick: "+positionX+" "+positionY);
                String delay = delayEt.getText().toString();
                if (positionX.length()!=0 && positionY.length()!=0 && delay.length()!=0){
                    positionEditor.putString(packageName+"x",positionX);
                    positionEditor.putString(packageName+"y",positionY);
                    positionEditor.apply();
                    delayEditor.putString(packageName,delay);
                    delayEditor.apply();
                    editor.putInt(packageName, ACTION_CUSTOM_CLICK);
                    editor.apply();
                    customLayout.setVisibility(View.GONE);
                    removeAnchor();
                    Toast.makeText(ActionSettingActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(ActionSettingActivity.this,"参数不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
        switch (sharedPreferences.getInt(packageName, ACTION_VIEW_CLICK)) {
            case ACTION_NO_CLICK:
                actionRg.check(R.id.no_click_rb);
                break;
            case ACTION_SCREEN_CLICK:
                actionRg.check(R.id.screen_click_rb);
                break;
            case ACTION_CUSTOM_CLICK:
                actionRg.check(R.id.custom_click_rb);
                break;
            case ACTION_VIEW_CLICK:
            default:
                actionRg.check(R.id.view_click_rb);
                break;
        }
    }

    private void showAnchor() {
        anchorView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.view_anchor, null);
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.format = PixelFormat.TRANSPARENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        windowManager.addView(anchorView, layoutParams);
        anchorView.setOnTouchListener(new View.OnTouchListener() {
            float originX,originY;
            float currentX,currentY;
            boolean isClick;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch: " + event.getX() + " " + event.getRawX());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        originX = currentX = event.getRawX();
                        originY = currentY = event.getRawY();
                        isClick = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        WindowManager.LayoutParams wmParams = (WindowManager.LayoutParams) anchorView.getLayoutParams();
                        wmParams.x += event.getRawX()-currentX;
                        wmParams.y += event.getRawY()-currentY;
                        windowManager.updateViewLayout(anchorView, wmParams);
                        currentX = event.getRawX();
                        currentY = event.getRawY();
                        int[] location = new int[2];
                        anchorView.getLocationOnScreen(location);
                        xDisTv.setText(String.valueOf(location[0]+anchorView.getWidth()/2));
                        yDisTv.setText(String.valueOf(location[1]+anchorView.getHeight()/2));
                        break;
                    case MotionEvent.ACTION_UP:
                        float translationX = Math.abs(event.getRawX()-originX);
                        float translationY = Math.abs(event.getRawY()-originY);
                        if (translationX<50 && translationY<50){
                            anchorView.performClick();
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void removeAnchor(){
        if (windowManager!=null && anchorView.isAttachedToWindow()){
            windowManager.removeViewImmediate(anchorView);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeAnchor();
    }
}
