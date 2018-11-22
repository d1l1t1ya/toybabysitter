package tw.com.edu.ntue.toybabysitter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import tw.com.edu.ntue.toybabysitter.database.UserConfig;
import tw.com.edu.ntue.toybabysitter.model.DbResult;

import static tw.com.edu.ntue.toybabysitter.PageChange.Page;

public class CommonActivity extends AppCompatActivity {
    protected Context context;
    protected Activity activity;
    private ArrayList<DbResult> resultArrayList;
    public static int[] image = {
            R.drawable.head_portrait1, R.drawable.head_portrait2,R.drawable.head_portrait3,R.drawable.head_portrait4,
            R.drawable.head_portrait5, R.drawable.head_portrait6,R.drawable.head_portrait7,R.drawable.head_portrait8
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        activity = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public static Handler handler = new Handler();
    Runnable checkToken_id = new Runnable() {
        @Override
        public void run() {
            //3秒鐘檢查一次token_id
            tokenCheck();
            //handler.postDelayed(checkToken_id, 3000);
        }
    };

    public static void showAlertView(Context context,String title){
        LayoutInflater inflater = LayoutInflater.from(context);
        View createView = inflater.inflate(R.layout.dialog_hint, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(createView);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.Transparent);
        dialog.show();
        dialog.setCancelable(true);
        TextView txt_title=createView.findViewById(R.id.dialog_title);
        txt_title.setText(title);
        Handler Alerthandler = new Handler();
        Alerthandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                dialog.dismiss();

            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void tokenCheck() {
        resultArrayList=new ArrayList<>();
        new Thread(){
            public void run(){
                resultArrayList=WebService.CheckToken_id(context, UserConfig.getTokenId(context));
                if(resultArrayList.size()>0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!resultArrayList.get(0).getStatus()){
                                Page(context, 1);
                                handler.removeCallbacks(checkToken_id);
                                if(!resultArrayList.get(0).getMessage().equals("")){
                                    showAlertView(context,getResources().getString(R.string.Log_failure));
                                }
                            }else {
                                if (!Welcome.i) {
                                    Welcome.i = true;
                                    Page(context, 3);
                                }
                            }
                        }
                    });
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showAlertView(context,getResources().getString(R.string.Please_check_your_network));
                        }
                    });
                }

            }
        }.start();

    }
    private Dialog alertDialog;
    protected void showWaitAlertView() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (alertDialog == null) {
                    //alertDialog = Global.createWaitAlert(context);
                    alertDialog = new Dialog(activity/*, android.R.style.Theme_Black_NoTitleBar_Fullscreen*/);
                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alertDialog.setContentView(R.layout.dialog_wait_alert);
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.setCancelable(false);
                    alertDialog.setCanceledOnTouchOutside(false);
                    View waitView = (View)alertDialog.findViewById(R.id.waitView);
                    waitView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dismissWaitAlertView();
                        }
                    });
                }


                try {
                    if (alertDialog.isShowing()) alertDialog.dismiss();
                    alertDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void dismissWaitAlertView() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(checkToken_id);
    }
}