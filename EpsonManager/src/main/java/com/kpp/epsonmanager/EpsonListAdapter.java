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

            row.setTag(holder);
        }
        else
        {
            holder = (EpsonHolder)row.getTag();
        }

        final Epson epson = data[position];

        final TextView txtTitle=holder.txtTitle;

        holder.txtTitle.setText(epson.getTitle());
        holder.txtTitle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
////                InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
////                imm.showSoftInput(txtTitle, 0);
////                txtTitle.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
//                txtTitle.setTextIsSelectable(true);
//                txtTitle.setFocusable(true);
//
                //txtTitle.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_light));



                final ViewSwitcher switcher = holder.nome_switcher;


                final EditText nome_in = (EditText) switcher.findViewById(R.id.hidden_nome);

                nome_in.setText(txtTitle.getText());
                nome_in.setFocusable(true);
                nome_in.setFocusable(true);
                nome_in.setFocusableInTouchMode(true);
                nome_in.setClickable(true);


                nome_in.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        //holder.txtTitle.setText(Integer.toString(keyCode));
                        //switcher.showPrevious();
                        if (keyCode==13 || keyCode==66){

                            epson.setTitle(nome_in.getText().toString());
                            txtTitle.setText(epson.getTitle());
                            switcher.showPrevious();
                            txtTitle.requestFocus();
                            InputMethodManager imm =(InputMethodManager)((Activity)context).getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(txtTitle.getWindowToken(), 0);
                        }
                        return false;
                    }
                });
                nome_in.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(hasFocus){
                            nome_in.setSelection(nome_in.getText().length());
                            InputMethodManager imm =(InputMethodManager)((Activity)context).getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(nome_in, 0);
                        }else {
                         //switcher.showPrevious();
                        }
                    }
                });

                switcher.showNext(); //or switcher.showPrevious();
                nome_in.requestFocus();
                return  false;
            }
        });
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
        ViewSwitcher nome_switcher;
    }
}
