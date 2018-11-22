package tw.com.edu.ntue.toybabysitter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import tw.com.edu.ntue.toybabysitter.database.UserConfig;
import tw.com.edu.ntue.toybabysitter.model.DbLogin;

import static tw.com.edu.ntue.toybabysitter.PageChange.Page;


public class login extends CommonActivity implements View.OnClickListener{

    EditText account;
    EditText password;
    String nickname;
    private ArrayList<DbLogin> loginArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (!CacheActivity.activityList.contains(context)) {
            CacheActivity.addActivity(login.this);
        }
        findVeiws();
        setListeners();
    }
    private void findVeiws(){
        account=findViewById(R.id.account);
        password=findViewById(R.id.password);
    }
    private void setListeners(){
        findViewById(R.id.btn_logging).setOnClickListener(this);
        findViewById(R.id.btn_sign).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_sign:
                Page(this,2);
                break;
            case R.id.btn_logging:
                loginArrayList=new ArrayList<>();
                final String PostAccount=account.getText().toString();
                final String PostPassword=password.getText().toString();
                //將帳號密碼記錄在手機資料庫
                UserConfig.setAccount(context,PostAccount);
                UserConfig.setPassword(context,PostPassword);
                new Thread(){
                    public void run(){
                        loginArrayList=WebService.Login(context,PostAccount,PostPassword);
                        if(loginArrayList.size()>0){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(loginArrayList.get(0).getStatus()){

                                        UserConfig.setTokenId(context,loginArrayList.get(0).getToken_id());
                                        UserConfig.setSex(context,loginArrayList.get(0).getSex());
                                        nickname=loginArrayList.get(0).getNickname();
                                    /*if(!Global.unicodeToString(loginArrayList.get(0).getNickname()).equals(""))
                                    nickname=Global.unicodeToString(loginArrayList.get(0).getNickname());*/
                                        Log.d("Nickname:",loginArrayList.get(0).getNickname());
                                        Log.d("unicodeToString:",nickname);
                                        UserConfig.setNickname(context,loginArrayList.get(0).getNickname());
                                        UserConfig.setHeadportrait(context,loginArrayList.get(0).getHeadportrait());
                                        Welcome.i = true;
                                        Page(context,3);
                                    }else {
                                        showAlertView(context,loginArrayList.get(0).getMessage());
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
                break;
        }
    }
}
