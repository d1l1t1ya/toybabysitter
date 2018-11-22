package tw.com.edu.ntue.toybabysitter;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tw.com.edu.ntue.toybabysitter.database.UserConfig;
import tw.com.edu.ntue.toybabysitter.model.DbResult;


public class GridAdapter extends ArrayAdapter {
    ArrayList<DbResult> resultArrayList;
    private static String StatusHead="headportrait";
    public GridAdapter(Context context, List<Map<String, Object>> items) {
        super(context,0, items);
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.grid_item, null);
        }

        ImageView headportrait = row.findViewById(R.id.image);
        final ImageView circle = row.findViewById(R.id.circle);
        headportrait.setImageResource(CommonActivity.image[position]);
        if(Integer.parseInt(UserConfig.getHeadportrait(getContext()))==position){
            circle.setVisibility(View.VISIBLE);
        }
        headportrait.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                new Thread(){
                    public void run(){
                        try {
                            resultArrayList=WebService.Update(getContext(),UserConfig.getTokenId(getContext()),StatusHead,String.valueOf(position));
                            if(resultArrayList.size()>0){

                                if(resultArrayList.get(0).getStatus()){
                                    UserConfig.setHeadportrait(getContext(),String.valueOf(position));
                                    Head_portrait.adapterheadler.sendEmptyMessage(0);
                                }else {
                                    Head_portrait.adapterheadler.sendEmptyMessage(1);
                                }

                            }else {
                                Head_portrait.adapterheadler.sendEmptyMessage(2);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

            }
        });

        return row;
    }

}
