package com.ypp.adskip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class AppListAdapter extends BaseAdapter {
    private List<AppNodeInfo> appNodeInfoList;
    private Context mContext;
    public AppListAdapter(Context context, List<AppNodeInfo> appNodeInfoList) {
        this.mContext = context;
        this.appNodeInfoList = appNodeInfoList;
    }

    public AppListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setAppNodeInfoList(List<AppNodeInfo> appNodeInfoList) {
        this.appNodeInfoList = appNodeInfoList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (appNodeInfoList!=null){
            return appNodeInfoList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (appNodeInfoList!=null && appNodeInfoList.size()>position){
            return appNodeInfoList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.app_list_item, null);
            viewHolder.icon = convertView.findViewById(R.id.list_item_icon);
            viewHolder.name = convertView.findViewById(R.id.list_item_name);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        AppNodeInfo appNodeInfo = appNodeInfoList.get(position);
        viewHolder.icon.setImageDrawable(appNodeInfo.getIcon());
        viewHolder.name.setText(appNodeInfo.getName());

        return convertView;
    }

    class ViewHolder{
        ImageView icon;
        TextView name;
    }
}
