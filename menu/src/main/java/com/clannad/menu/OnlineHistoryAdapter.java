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

public class OnlineHistoryAdapter extends ArrayAdapter<RoomContent> {

private int resourceId;

public OnlineHistoryAdapter(Context context, int textViewResourceId, List<RoomContent> objects) {
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
        }

@Override
public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        RoomContent roomContent= getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);

        TextView rid=view.findViewById(R.id.tv_online_rid);
        TextView uid=view.findViewById(R.id.tv_online_uid);
        TextView xnum=view.findViewById(R.id.tv_online_xnum);
        TextView xcontent=view.findViewById(R.id.tv_online_xcontent);
        TextView xtime=view.findViewById(R.id.tv_online_xtime);


        rid.setText(""+roomContent.getRid());
        uid.setText(roomContent.getUid());
        xnum.setText(""+roomContent.getXnum());
        xcontent.setText(roomContent.getXcontent());
        xtime.setText(roomContent.getXtime());
        return view;
        }





        }
