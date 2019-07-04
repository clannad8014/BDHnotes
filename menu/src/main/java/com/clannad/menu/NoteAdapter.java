package com.clannad.menu;
import com.clannad.menu.models.*;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

public class NoteAdapter extends ArrayAdapter<show_list> {

    private int resourceId;

    public NoteAdapter(Context context, int textViewResourceId, List<show_list> objects) {
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        show_list show_list = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);

        TextView bid=view.findViewById(R.id.tv_flag_id);
        TextView title = view.findViewById(R.id.tv_flag_title);
        TextView content = view.findViewById(R.id.tv_flag_content);
        TextView date = view.findViewById(R.id.tv_flag_date);
        bid.setText(show_list.getBid());
        title.setText(show_list.getTitle());
        content.setText(show_list.getA_content());

//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        //date.setText(dateFormat.format(show_list.getCtime()));
        date.setText(show_list.getCtime());
        return view;
    }


    //region 设置菜单

//    @Override
//    public int getItemViewType(int position) {
//        Flag flag = getItem(position);
//        if(flag.getState().toString().trim().equals("待完成")){
//            return COMPLISH;
//        }else{
//            return CONTINUE;
//        }
//    }

//    @Override
//    public int getViewTypeCount() {
//        return 2;
//    }

    //endregion
}
