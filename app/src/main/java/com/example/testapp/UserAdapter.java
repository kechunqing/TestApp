package com.example.seeulinkdemo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.konka.sdk.bean.AgentUser;

import java.util.List;

public class UserAdapter extends BaseAdapter {

    private List<AgentUser> users;
    private Context context;

    public UserAdapter(List<AgentUser> users, Context context) {
        this.users = users;
        this.context = context;
    }

    public void setUsers(List<AgentUser> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return users == null ? 0 : users.size();
    }

    @Override
    public Object getItem(int position) {
        return users == null ? null : users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = View.inflate(context, android.R.layout.simple_spinner_item, null);
        AgentUser user = users.get(position);
        TextView userTv = convertView.findViewById(android.R.id.text1);
        userTv.setText(user.userId);
        return convertView;
    }
}
