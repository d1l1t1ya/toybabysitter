package tw.com.edu.ntue.toybabysitter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.ResourceBundle;

import tw.com.edu.ntue.toybabysitter.database.UserConfig;
import tw.com.edu.ntue.toybabysitter.model.DbResult;

import static tw.com.edu.ntue.toybabysitter.PageChange.Page;


public class Update extends CommonActivity implements View.OnClickListener {
    protected Handler Updatehandler;
    protected TextView Username,Nickname,Gender;
    private ArrayList<DbResult> resultArrayList;
    private static String StatusPass="pass";
    private static String StatusNick="nickname";
    private static String StatusGender="gender";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        findVeiws();
        Username.setText(UserConfig.getAccount(context));
        if (!UserConfig.getNickname(context).equals("null")&&!(UserConfig.getNickname(context).length() < 1)){
            Nickname.setText(UserConfig.getNickname(context));
        }else{
            Nickname.setText("Not set yet");
        }
        Gender.setText(UserConfig.getSex(context));
        setListeners();
        CommonActivity.handler.post(checkToken_id);
    }
    private void findVeiws(){
        Gender=findViewById(R.id.show_gender);
        Username=findViewById(R.id.show_username);
        Nickname=findViewById(R.id.show_nickname);
    }
    private void setListeners(){
        findViewById(R.id.updatepassword).setOnClickListener(this);
        findViewById(R.id.updategender).setOnClickListener(this);
        findViewById(R.id.updatenickname).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.updatepassword) {
            LayoutInflater inflater = LayoutInflater.from(context);
            final View v = inflater.inflate(R.layout.checkpassword, null);
            final AlertDialog dialog = new AlertDialog.Builder(context)
                    .setView(v)
                    .setPositiveButton("ok", null)
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
            dialog.show();
            final EditText oldpass = v.findViewById(R.id.edt_oldpass);
            final EditText newpass = v.findViewById(R.id.edt_newpass);
            final EditText checknewpass = v.findViewById(R.id.edt_checknewpass);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!oldpass.getText().toString().equals(UserConfig.getPassword(context))) {
                        showAlertView(context,getResources().getString(R.string.Original_incorrect));

                        return;
                    }
                    if(oldpass.getText().toString().equals(newpass.getText().toString())){
                        showAlertView(context,getResources().getString(R.string.nPw_different_from_oPw));
                        return;
                    }
                    String s=SignPage.isMeetPwdRule(newpass.getText().toString());
                    if(s!=""){
                        showAlertView(context,s);
                        return;
                    }

                    if(!newpass.getText().toString().equals(checknewpass.getText().toString())){
                        showAlertView(context,getResources().getString(R.string.Password_not_match));
                        return;
                    }

                    new AlertDialog.Builder(context)
                            .setTitle("Are you sure to change your password?")
                            .setPositiveButton("YES",new DialogInterface.OnClickListener(){

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    resultArrayList=new ArrayList<>();
                                    new Thread(){
                                        public void run(){
                                            try {
                                                resultArrayList=WebService.Update(context,UserConfig.getTokenId(context),StatusPass,newpass.getText().toString());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            if(resultArrayList.size()>0){
                                                if(resultArrayList.get(0).getStatus()){
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            showAlertView(context,getResources().getString(R.string.Modified_successfully));
                                                            UserConfig.setPassword(context,UserConfig.getStorage(context));
                                                        }
                                                    });
                                                }
                                            }else {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        showAlertView(context,getResources().getString(R.string.Please_check_your_network));
                                                        //handler.removeCallbacks(checkToken_id);

                                                    }
                                                });
                                            }


                                        }
                                    }.start();
                                }
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                    dialog.dismiss();
                    UserConfig.setStorage(context,newpass.getText().toString());
                }
            });

        }
        if(view.getId()==R.id.updatenickname){
            LayoutInflater inflater = LayoutInflater.from(context);
            View createView = inflater.inflate(R.layout.updatenick, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(createView);
            final AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawableResource(R.color.Transparent);
            dialog.show();
            TextView dialog_title=createView.findViewById(R.id.dialog_title);
            dialog_title.setText(getResources().getString(R.string.Please_enter_your_nickname));
            TextView dialog_ok=createView.findViewById(R.id.dialog_ok);
            final EditText edt_internick = (EditText) (createView.findViewById(R.id.internick));
            dialog_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String internick=edt_internick.getText().toString();
                    if(internick.length()<1){
                        showAlertView(context,getResources().getString(R.string.Can_not_be_blank));
                        return;
                    }
                    if(internick.length()>10){
                        showAlertView(context,getResources().getString(R.string.length_too_short));
                        return;
                    }
                    dialog.dismiss();

                    resultArrayList=new ArrayList<>();
                    new Thread(){
                        public void run(){
                            try {
                                resultArrayList=WebService.Update(context,UserConfig.getTokenId(context),StatusNick,edt_internick.getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if(resultArrayList.size()>0){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(resultArrayList.get(0).getStatus()){
                                            showAlertView(context,getResources().getString(R.string.Modified_successfully));
                                            Nickname.setText(UserConfig.getStorage(context));
                                            UserConfig.setNickname(context,UserConfig.getStorage(context));
                                        }
                                    }
                                });
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showAlertView(context,getResources().getString(R.string.Please_check_your_network));
                                        //handler.removeCallbacks(checkToken_id);

                                    }
                                });
                            }



                        }
                    }.start();
                    UserConfig.setStorage(context,edt_internick.getText().toString());
                    dialog.dismiss();

                }
            });
            TextView dialog_cancel=createView.findViewById(R.id.dialog_cancel);
            dialog_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

        }
        if(view.getId()==R.id.updategender){
            final String[] sex=getResources().getStringArray(R.array.sex_array);
            AlertDialog.Builder dialog_list = new AlertDialog.Builder(context);
            dialog_list.setTitle("Choose your gender");
            dialog_list.setItems(sex, new DialogInterface.OnClickListener(){
                @Override

                //只要你在onClick處理事件內，使用which參數，就可以知道按下陣列裡的哪一個了
                public void onClick(DialogInterface dialog, final int which) {

                    new Thread(){
                        public void run(){
                            try {
                                resultArrayList= WebService.Update(context,UserConfig.getTokenId(context),StatusGender,sex[which]);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if(resultArrayList.size()>0){
                                if(resultArrayList.get(0).getStatus()){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showAlertView(context,getResources().getString(R.string.Modified_successfully));
                                            Gender.setText(UserConfig.getStorage(context));
                                            UserConfig.setSex(context,UserConfig.getStorage(context));
                                        }
                                    });
                                }
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showAlertView(context,getResources().getString(R.string.Please_check_your_network));
                                        //handler.removeCallbacks(checkToken_id);

                                    }
                                });
                            }


                        }
                    }.start();
                    UserConfig.setStorage(context,sex[which]);
                }
            });
            dialog_list.show();
        }
        if(view.getId() == R.id.back){
            finish();
        }

    }


}
