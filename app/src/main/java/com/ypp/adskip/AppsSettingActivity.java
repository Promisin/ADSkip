package com.ypp.adskip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AppsSettingActivity extends AppCompatActivity {
    private final String TAG = "AppsSettingActivity";
    private ListView appList;
    private EditText searchEt;
    private ContentLoadingProgressBar listLoadingPb;
    private AppListAdapter listAdapter;
    private List<AppNodeInfo> resultList;
    private List<AppNodeInfo> currentList;
    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_setting);
        setTitle(R.string.setting_activity_title);
        currentList = new ArrayList<AppNodeInfo>();
        listAdapter = new AppListAdapter(AppsSettingActivity.this);
        appList = findViewById(R.id.app_list);
        searchEt = findViewById(R.id.search_et);
        listLoadingPb = findViewById(R.id.list_loading_pb);
        appList.setAdapter(listAdapter);
        appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AppsSettingActivity.this, ActionSettingActivity.class);
                intent.putExtra("packageName", currentList.get(position).getPackageName());
                startActivity(intent);
            }
        });
        new Thread(){
            @Override
            public void run() {
                super.run();
                final List<AppNodeInfo> appNodeInfoList = Utils.getInstalledAppList(getApplicationContext());
                resultList = appNodeInfoList;
                currentList.addAll(appNodeInfoList);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listAdapter.setAppNodeInfoList(appNodeInfoList);
                        listLoadingPb.setVisibility(View.GONE);
                    }
                });
            }
        }.start();
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterApp(s.toString());
                listAdapter.setAppNodeInfoList(currentList);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void filterApp(String key) {
        currentList.clear();
        if (key==null||key.trim().length()==0){
            currentList.addAll(resultList);
            return;
        }
        for (AppNodeInfo info : resultList){
            if (info.getName().toLowerCase().contains(key.toLowerCase())){
                currentList.add(info);
            }
        }
    }
}
