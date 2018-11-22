package tw.com.edu.ntue.toybabysitter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import javax.security.auth.callback.Callback;

import tw.com.edu.ntue.toybabysitter.database.UserConfig;
import tw.com.edu.ntue.toybabysitter.model.DbResult;

import static tw.com.edu.ntue.toybabysitter.CommonActivity.showAlertView;

public class MyDiary {
    LayoutInflater inflater;
    View v;
    AlertDialog.Builder builder;
    AlertDialog dialog;
    private Handler handler = new Handler();
    private String date ;
    private String text_content ;
    private ArrayList<DbResult> resultArrayList;
    private DiaryChangeListener diaryChangeListener;
    private Callback callback;
    public MyDiary(String d,String t){
        date=d;
        text_content=t;
        this.diaryChangeListener = null;
    }
    public interface DiaryChangeListener {
        public void Update();
    }
    public void setDiaryChangeListener(DiaryChangeListener listener) {
        this.diaryChangeListener = listener;
    }
    public void ShowDiary(final Context context){
        inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.edit_layout, null);
        builder = new AlertDialog.Builder(context);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.Transparent);
        dialog.setView(v);
        dialog.setCancelable(false);
        dialog.create();
        dialog.show();
        final TextView TextView=v.findViewById(R.id.edit_date);
        TextView.setText(date);
        final TextView TextContent=v.findViewById(R.id.edit_content);
        TextContent.setText(text_content);
        v.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater1 = LayoutInflater.from(context);
                View createView = inflater1.inflate(R.layout.ask_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(createView);
                final AlertDialog dialogask = builder.create();
                dialogask.getWindow().setBackgroundDrawableResource(R.color.Transparent);
                dialogask.show();
                TextView dialog_title=createView.findViewById(R.id.dialog_title);
                dialog_title.setText(context.getResources().getString(R.string.Message));
                TextView ask_detail=createView.findViewById(R.id.ask_detail);
                ask_detail.setText(context.getResources().getString(R.string.Are_you_sure_to_delete_this_journal));
                TextView dialog_ok=createView.findViewById(R.id.dialog_ok);
                dialog_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resultArrayList=new ArrayList<>();
                        new Thread(){
                            public void run(){
                                try {
                                    resultArrayList=WebService.UpdateNote(context,UserConfig.getAccount(context),MainActivity.DELETE,TextView.getText().toString(),date);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if(resultArrayList.size()>0){
                                    if(resultArrayList.get(0).getStatus()){
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                showAlertView(context,context.getResources().getString(R.string.Delete_successfully));
                                                TextContent.setText("");
                                                dialogask.dismiss();
                                                dialog.dismiss();
                                                diaryChangeListener.Update();

                                            }
                                        });

                                    }
                                }else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            showAlertView(context,context.getResources().getString(R.string.Please_check_your_network));
                                        }
                                    });
                                }


                            }
                        }.start();

                    }
                });
                TextView dialog_cancel=createView.findViewById(R.id.dialog_cancel);
                dialog_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogask.dismiss();
                    }
                });

            }
        });
        v.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater1 = LayoutInflater.from(context);
                View createView = inflater1.inflate(R.layout.ask_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(createView);
                final AlertDialog dialogask = builder.create();
                dialogask.getWindow().setBackgroundDrawableResource(R.color.Transparent);
                dialogask.show();
                TextView dialog_title=createView.findViewById(R.id.dialog_title);
                dialog_title.setText(context.getResources().getString(R.string.Message));
                TextView ask_detail=createView.findViewById(R.id.ask_detail);
                ask_detail.setText(context.getResources().getString(R.string.Are_you_sure_to_save_this_journal));
                TextView dialog_ok=createView.findViewById(R.id.dialog_ok);
                dialog_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        resultArrayList=new ArrayList<>();
                        new Thread(){
                            public void run(){
                                EditText editText=v.findViewById(R.id.edit_content);
                                try {
                                    resultArrayList=WebService.UpdateNote(context,UserConfig.getAccount(context),MainActivity.WRITE,editText.getText().toString(),date);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if(resultArrayList.size()>0){
                                    if(resultArrayList.get(0).getStatus()){
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                showAlertView(context,context.getResources().getString(R.string.Save_successfully));
                                                diaryChangeListener.Update();
                                                UserConfig.setPassword(context,UserConfig.getStorage(context));
                                                dialogask.dismiss();
                                                dialog.dismiss();
                                            }
                                        });

                                    }else {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                showAlertView(context,resultArrayList.get(0).getMessage());
                                                dialogask.dismiss();
                                            }
                                        });

                                    }
                                }else {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            showAlertView(context,context.getResources().getString(R.string.Please_check_your_network));
                                        }
                                    });
                                }


                            }
                        }.start();

                    }
                });
                TextView dialog_cancel=createView.findViewById(R.id.dialog_cancel);
                dialog_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogask.dismiss();
                    }
                });


            }
        });
        v.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater1 = LayoutInflater.from(context);
                View createView = inflater1.inflate(R.layout.ask_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(createView);
                final AlertDialog dialogask = builder.create();
                dialogask.getWindow().setBackgroundDrawableResource(R.color.Transparent);
                dialogask.show();
                TextView dialog_title=createView.findViewById(R.id.dialog_title);
                dialog_title.setText(context.getResources().getString(R.string.Message));
                TextView ask_detail=createView.findViewById(R.id.ask_detail);
                ask_detail.setText(context.getResources().getString(R.string.Are_you_sure_to_leave_this_journal));
                TextView dialog_ok=createView.findViewById(R.id.dialog_ok);
                dialog_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogask.dismiss();
                        dialog.dismiss();
                    }
                });
                TextView dialog_cancel=createView.findViewById(R.id.dialog_cancel);
                dialog_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogask.dismiss();
                    }
                });
            }
        });


    }
}
