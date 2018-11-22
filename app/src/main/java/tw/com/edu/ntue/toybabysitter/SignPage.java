package tw.com.edu.ntue.toybabysitter;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import tw.com.edu.ntue.toybabysitter.model.DbResult;

import static tw.com.edu.ntue.toybabysitter.PageChange.Page;

public class SignPage extends CommonActivity implements View.OnClickListener,/*View.OnFocusChangeListener,*/AdapterView.OnItemSelectedListener,TextWatcher {
    protected Spinner spinner;
    protected EditText edt_username,edt_password,edt_checkpassword;
    protected Handler Signhandler;
    private TextView passerr,checkpasserr,accounterr;
    String spinnerresult="daddy";
    private ArrayList<DbResult> resultArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //設置豎屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_sign_page);
        if (!CacheActivity.activityList.contains(context)) {
            CacheActivity.addActivity(SignPage.this);
        }
        findVeiws();
        setListeners();
        ArrayAdapter<CharSequence> sexList = ArrayAdapter.createFromResource(context,
                R.array.sex_array,
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(sexList);
    }
    private void findVeiws(){
        edt_username=findViewById(R.id.edt_username);
        edt_password=findViewById(R.id.edt_password);
        edt_checkpassword=findViewById(R.id.edt_checkpassword);
        accounterr=findViewById(R.id.accounterr);
        passerr=findViewById(R.id.passerr);
        checkpasserr=findViewById(R.id.checkpasserr);
        spinner=findViewById(R.id.sex_spinner);

    }
    private void setListeners(){
        /*edt_username.setOnFocusChangeListener(this);
        edt_password.setOnFocusChangeListener(this);
        edt_checkpassword.setOnFocusChangeListener(this);*/
        edt_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s="";
                s=isMeetActRule(edt_username.getText().toString());
                accounterr.setText(s);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edt_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s=isMeetPwdRule(edt_password.getText().toString());
                passerr.setText(s);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edt_checkpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!edt_password.getText().toString().equals(edt_checkpassword.getText().toString())){
                    checkpasserr.setText("Password doesn't match");
                }else{
                    checkpasserr.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        spinner.setOnItemSelectedListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.btn_cancel){
            finish();
        }else if(view.getId()==R.id.btn_submit){
            String s=isMeetPwdRule(edt_password.getText().toString())+isMeetActRule(edt_username.getText().toString());
            if(s==""&&edt_password.getText().toString().equals(edt_checkpassword.getText().toString())){
                resultArrayList=new ArrayList<>();
                new Thread(){
                    public void run(){
                        resultArrayList=WebService.InsertMember(context,edt_username.getText().toString(),edt_password.getText().toString(),spinnerresult);
                        if(resultArrayList.size()>0){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (resultArrayList.get(0).getStatus()) {
                                        PageChange.Page(context,1);
                                        showAlertView(context,resultArrayList.get(0).getMessage());
                                    }else{
                                        showAlertView(context,resultArrayList.get(0).getMessage());
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

            }else{
                showAlertView(context,getResources().getString(R.string.Please_fill_in_correctly));
            }
        }



    }
    private String isMeetActRule(String str){
        boolean isMeetPwdRule = false;
        String result="";
        if(str.length()>20){
            result="At most 20 lengths.";
            return result;
        }
        if(str.length() > 5) {
            String regex = "^[a-zA-Z0-9\\p{Punct}]+$";
            isMeetPwdRule =str.matches(regex);
            if(!isMeetPwdRule){
                result="Can't have special characters";
                return result;
            }return result;
        }
        result="Account must be at least 6 characters";
        return result;
    }
    static protected String isMeetPwdRule(String str) {
        boolean isMeetPwdRule = false;
        String result="";
        if(str.length() > 5) {
            boolean isDigit = false;
            boolean isLetter = false;
            if(str.length()>20){
                result="At most 20 lengths.";
                return result;
            }
            for(int i = 0 ; i < str.length(); i++) {
                if(Character.isDigit(str.charAt(i))) {
                    isDigit = true;
                }
                if(Character.isLetter(str.charAt(i))) {
                    isLetter = true;
                }
                if(isDigit && isLetter){
                    result="";
                    break;
                }else{
                    result="Requires at least 1 digit and 1 letter";
                }

            }
            if(result!="")return result;

            String regex = "^[a-zA-Z0-9\\p{Punct}]+$";
            isMeetPwdRule =str.matches(regex);
            if(!isMeetPwdRule){
                result="Can't have special characters";
                return result;
            }return result;
        }
            result="Password must be at least 6 characters";
            return result;
    }

    /*@Override
    public void onFocusChange(View view, boolean b) {
        if(!b){
            String s=isMeetPwdRule(edt_password.getText().toString());
            passerr.setText(s);
            if(!edt_password.getText().toString().equals(edt_checkpassword.getText().toString())){
                checkpasserr.setText("Password doesn't match");
            }else{
                checkpasserr.setText("");
            }
            s=isMeetActRule(edt_username.getText().toString());
            accounterr.setText(s);
        }
    }*/

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String[] sex_array=getResources().getStringArray(R.array.sex_array);
        spinnerresult=sex_array[i];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String s=isMeetPwdRule(edt_password.getText().toString());
        passerr.setText(s);
        if(!edt_password.getText().toString().equals(edt_checkpassword.getText().toString())){
            checkpasserr.setText(getResources().getString(R.string.Password_not_match));
        }else{
            checkpasserr.setText("");
        }
        s=isMeetActRule(edt_username.getText().toString());
        accounterr.setText(s);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
