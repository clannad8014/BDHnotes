package com.clannad.menu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.clannad.menu.models.*;

import java.util.List;

public class HistoryAdapter extends ArrayAdapter<note_content> {

    private int resourceId;

    public HistoryAdapter(Context context, int textViewResourceId, List<note_content> objects) {
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        note_content nc= getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);

        TextView bid=view.findViewById(R.id.tv_history_bid);
        TextView xhnum=view.findViewById(R.id.tv_history_xhnum);
        TextView xcontent=view.findViewById(R.id.tv_history_xcontent);
        TextView xtime=view.findViewById(R.id.tv_history_xtime);
        TextView xid=view.findViewById(R.id.tv_history_xid);

        bid.setText(nc.getBid());
        int tempnum=nc.getXhnum();
        xhnum.setText(""+tempnum);
        xcontent.setText(nc.getXcontent());
        xtime.setText(nc.getXtime());
        xid.setText(nc.getXid());
        return view;
    }





}
