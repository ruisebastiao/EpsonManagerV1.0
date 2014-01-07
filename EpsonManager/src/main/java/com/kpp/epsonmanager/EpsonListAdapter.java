package com.kpp.epsonmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;


public class EpsonListAdapter extends ArrayAdapter<Epson> {

    Context context;
    int layoutResourceId;
    ArrayList<Epson> data = null;


    public EpsonListAdapter(Context context, int layoutResourceId,ArrayList<Epson> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;

    }

    @Override
    public Epson getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    private EpsonHolder holder = null;
    public static View SelectedRow =null;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;


        final Epson epson = data.get(position);

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



            row.setTag(holder);
            //this.notifyDataSetInvalidated();

            SelectedRow =row;


        }
        else
        {
            holder = (EpsonHolder)row.getTag();
        }


        ViewSwitcher switcher= (ViewSwitcher) EpsonListAdapter.SelectedRow.findViewById(R.id.connectswitcher);
        if (switcher!=null){
            if ((switcher.getCurrentView() !=  EpsonListAdapter.SelectedRow.findViewById(R.id.frameIcon))&& epson.getStat()!= Epson.ConnectionState.CONNECTING){
                switcher.showPrevious();
            } else if (((switcher.getCurrentView() !=  EpsonListAdapter.SelectedRow.findViewById(R.id.connect_load))) && epson.getStat()==Epson.ConnectionState.CONNECTING){
                switcher.showNext();
            }
        }



        final TextView txtHostname=holder.txtHostname;
        holder.txtHostname.setText(epson.getHostname());


        final TextView txtTitle=holder.txtTitle;

        holder.txtTitle.setText(epson.getTitle());


        holder.imgIcon.setImageResource(epson.icon);



        if(epson.getStat()== Epson.ConnectionState.CONNECTED){
            holder.imgConnected.setImageResource(R.drawable.img_conn);
        }
        else{
            holder.imgConnected.setImageResource(R.drawable.img_disc);
        }

        holder.imgConnected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                ViewSwitcher switcher= (ViewSwitcher) SelectedRow.findViewById(R.id.connectswitcher);
//                if (switcher!=null){
//                    switcher.showNext();
//                }
                epson.setRobotConnected(!(epson.getStat()== Epson.ConnectionState.CONNECTED));

            }
        });

        final ArrayAdapter listadapter=this;
        holder.txtTitle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // get prompts.xml view
                LayoutInflater layoutInflater = LayoutInflater.from(context);

                View promptView = layoutInflater.inflate(R.layout.prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set prompts.xml to be the layout file of the alertdialog builder
                alertDialogBuilder.setView(promptView);

                final EditText input = (EditText) promptView.findViewById(R.id.input_edit_text);
                input.setText(epson.getTitle());
                // setup a dialog window
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                epson.setTitle(input.getText().toString());
                                listadapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,	int id) {
                                        dialog.cancel();
                                    }
                                });

                // create an alert dialog
                AlertDialog alertD = alertDialogBuilder.create();

                alertD.show();

                return true;
            }
        });

        holder.txtHostname.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // get prompts.xml view
                LayoutInflater layoutInflater = LayoutInflater.from(context);

                View promptView = layoutInflater.inflate(R.layout.prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set prompts.xml to be the layout file of the alertdialog builder
                alertDialogBuilder.setView(promptView);

                final EditText input = (EditText) promptView.findViewById(R.id.input_edit_text);
                input.setText(epson.getHostname());
                // setup a dialog window
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                epson.setHostname(input.getText().toString());
                                listadapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create an alert dialog
                AlertDialog alertD = alertDialogBuilder.create();

                alertD.show();

                return true;
            }
        });


//        final EditText teste=(EditText)row.findViewById(R.id.teste);
//


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
