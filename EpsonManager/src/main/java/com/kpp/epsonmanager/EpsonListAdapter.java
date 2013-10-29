package com.kpp.epsonmanager;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EpsonListAdapter extends ArrayAdapter<Epson> {

    Context context;
    int layoutResourceId;
    Epson data[] = null;


    public EpsonListAdapter(Context context, int layoutResourceId, Epson[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        EpsonHolder holder = null;

        if(row == null)
        {

            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new EpsonHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.imgConnected = (ImageView)row.findViewById(R.id.imgEpsonConnected);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.txtHostname = (TextView)row.findViewById(R.id.txtHostname);

            row.setTag(holder);
        }
        else
        {
            holder = (EpsonHolder)row.getTag();
        }

        final Epson epson = data[position];



        holder.txtTitle.setText(epson.title);
        holder.txtHostname.setText(epson.getHostname());
        holder.imgIcon.setImageResource(epson.icon);

        if(epson.isConnected()){
            holder.imgConnected.setImageResource(R.drawable.img_conn);
        }
        else{
            holder.imgConnected.setImageResource(R.drawable.img_disc);
        }

        //Propriedades.getInstance().setSelectedEpson(epson);
//
//        row.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Propriedades.getInstance().setSelectedEpson(epson);
//
//            }
//        });
//
//        holder.imgIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Propriedades.getInstance().setSelectedEpson(epson);
////                v.setBackgroundColor(Color.parseColor("#6E94FF"));
//
//            }
//        });
//        holder.txtTitle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                v.setBackgroundResource(0);
////                v.setBackgroundResource(android.R.color.holo_green_light);
//                Propriedades.getInstance().setSelectedEpson(epson);
//
//
//            }
//        });

        return row;

    }



    static class EpsonHolder
    {
        ImageView imgIcon;
        ImageView imgConnected;
        TextView txtTitle;
        TextView txtHostname;
    }
}
