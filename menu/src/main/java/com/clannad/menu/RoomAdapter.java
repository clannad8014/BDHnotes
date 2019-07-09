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

public class RoomAdapter extends ArrayAdapter<Room> {

    private int resourceId;

    public RoomAdapter(Context context, int textViewResourceId, List<Room> objects) {
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Room room= getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);

        TextView rid=view.findViewById(R.id.tv_room_rid);
        TextView rtitle=view.findViewById(R.id.tv_room_rtitle);
        TextView rboss=view.findViewById(R.id.tv_room_rboss);
        TextView rtime=view.findViewById(R.id.tv_room_rtime);

        rid.setText(""+room.getRid());
        rtitle.setText(room.getRtitle());
        rboss.setText(room.getRboss());
        rtime.setText(room.getRtime());
        return view;
    }





}
