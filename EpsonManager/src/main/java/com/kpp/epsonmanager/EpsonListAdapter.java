package com.kpp.epsonmanager;

import android.app.Activity;
import android.app.LauncherActivity;
import android.app.Service;
import android.content.Context;

import android.text.Editable;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

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
    private EpsonHolder holder = null;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;


        if(row == null)
        {

            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            //row.set
            holder = new EpsonHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.imgConnected = (ImageView)row.findViewById(R.id.imgEpsonConnected);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.txtHostname = (TextView)row.findViewById(R.id.txtHostname);
            holder.nome_switcher=(ViewSwitcher)row.findViewById(R.id.nome_switcher);
            holder.hostname_switcher=(ViewSwitcher)row.findViewById(R.id.Hostname_Switcher);

            row.setTag(holder);
        }
        else
        {
            holder = (EpsonHolder)row.getTag();
        }

        final Epson epson = data[position];



        final TextView txtHostname=holder.txtHostname;
        holder.txtHostname.setText(epson.getHostname());


        final TextView txtTitle=holder.txtTitle;

        holder.txtTitle.setText(epson.getTitle());


        holder.imgIcon.setImageResource(epson.icon);

        if(epson.isConnected()){
            holder.imgConnected.setImageResource(R.drawable.img_conn);
        }
        else{
            holder.imgConnected.setImageResource(R.drawable.img_disc);
        }


//        final EditText teste=(EditText)row.findViewById(R.id.teste);
//
//        teste.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                teste.setFocusable(true);
//                teste.setFocusableInTouchMode(true);
//                teste.requestFocusFromTouch();
//                teste.requestFocus();
//                teste.setFocusable(false);
//                teste.setFocusableInTouchMode(false);
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
        ViewSwitcher nome_switcher;
        ViewSwitcher hostname_switcher;
    }
}
