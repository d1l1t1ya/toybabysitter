package tw.com.edu.ntue.toybabysitter;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import tw.com.edu.ntue.toybabysitter.database.UserConfig;
import tw.com.edu.ntue.toybabysitter.model.DbNoteSelect;
import tw.com.edu.ntue.toybabysitter.model.DbResult;

public class MainActivity extends CommonActivity implements View.OnClickListener,DatePickerDialog.OnDateSetListener{
    ImageView headimg;
    TextView welcomeText,text_date,text_content;
    Intent it;
    Calendar c=Calendar.getInstance();
    private ArrayList<DbResult> resultArrayList;
    public static String DELETE="delete";
    public static String WRITE="write";
    int year,month,day;
    static SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
    private LayoutInflater inflater;
    private View v;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private ArrayList<DbNoteSelect> NoteSelectList;
    private List<Map<String, Object>> items=new ArrayList<Map<String,Object>>();
    private MyDiary myDiary;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!CacheActivity.activityList.contains(context)) {
            CacheActivity.addActivity(MainActivity.this);
        }
        findVeiws();
        setListeners();
        //呼叫檢查thread
        text_date.setText(Date_Transform(c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.DAY_OF_MONTH)));
        CommonActivity.handler.post(checkToken_id);
        year=c.get(Calendar.YEAR);
        month=c.get(Calendar.MONTH)+1;
        day=c.get(Calendar.DAY_OF_MONTH);
        UpdateContent();


    }
    private void findVeiws(){
        headimg=findViewById(R.id.Head_portrait);
        welcomeText=findViewById(R.id.welcomeText);
        text_date=findViewById(R.id.text_date);
        text_content=findViewById(R.id.text_content);
    }
    private void setListeners(){
        findViewById(R.id.btn_video).setOnClickListener(this);
        findViewById(R.id.img_setting).setOnClickListener(this);
        findViewById(R.id.btn_gps).setOnClickListener(this);
        findViewById(R.id.btn_edit).setOnClickListener(this);
        text_date.setOnClickListener(this);
        findViewById(R.id.date_front).setOnClickListener(this);
        findViewById(R.id.date_defore).setOnClickListener(this);
        findViewById(R.id.btn_search).setOnClickListener(this);

    }
    @Override
    protected void onResume() {
        super.onResume();

        headimg.setImageResource(image[Integer.parseInt(UserConfig.getHeadportrait(context))]);
        if(!UserConfig.getNickname(context).equals("null")&&!(UserConfig.getNickname(context).length() < 1))
        welcomeText.setText(UserConfig.getNickname(context));
        else welcomeText.setText("Hello!!");
        UpdateContent();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.text_date:
                new DatePickerDialog(context,this,year,month-1,day).show();
                break;
            case R.id.date_defore:

                text_date.setText(getBeforeDate(1));
                UpdateContent();
                break;
            case R.id.date_front:
                text_date.setText(getFrontDate(1));
                UpdateContent();
                break;
            case R.id.btn_video:
                it =new Intent(context,videofragment.class);
                startActivity(it);

                break;
            case R.id.img_setting:
                it =new Intent(context,setting.class);
                startActivity(it);
                break;
            case R.id.btn_search:
                new MyDatePickerDialog(MainActivity.this, 0, new MyDatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
                                          int startDayOfMonth) {

                        Intent it;
                        it=new Intent(context,ShowNoteSearch.class);
                        it.putExtra("year_select_result",String.valueOf(startYear));
                        it.putExtra("month_select_result",String.format("%02d", startMonthOfYear+1));
                        context.startActivity(it);
                    }
                }, year, month-1,day).show();
                break;
            case R.id.btn_gps:
                it =new Intent(context,MapsActivity.class);
                startActivity(it);
                break;
            case R.id.btn_edit:
                myDiary=new MyDiary(text_date.getText().toString(),text_content.getText().toString());
                myDiary.ShowDiary(context);
                myDiary.setDiaryChangeListener(new MyDiary.DiaryChangeListener() {
                    @Override
                    public void Update() {
                        UpdateContent();
                    }
                });
                break;

        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
        text_date.setText(Date_Transform(y+"-"+(m+1)+"-"+d));
        year=y;
        month=m+1;
        day=d;
        UpdateContent();
    }
    private void UpdateContent(){
        resultArrayList=new ArrayList<>();
        new Thread(){
            public void run(){
                try {
                    resultArrayList=WebService.NoteSingleSearch(context,UserConfig.getAccount(context),text_date.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(resultArrayList.size()>0){
                    if(resultArrayList.get(0).getStatus()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                text_content.setText(resultArrayList.get(0).getMessage());
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
    private String getFrontDate(int distanceDay) {

        SimpleDateFormat dfy = new SimpleDateFormat("yyyy");
        SimpleDateFormat dfm = new SimpleDateFormat("MM");
        SimpleDateFormat dfd = new SimpleDateFormat("dd");
        Calendar date = Calendar.getInstance();
        Date now_date = null;
        try {
            now_date=dft.parse(text_date.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date.setTime(now_date);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        year=Integer.parseInt(dfy.format(endDate));
        month=Integer.parseInt(dfm.format(endDate));
        day=Integer.parseInt(dfd.format(endDate));
        Log.d("前"+distanceDay+"天=",dft.format(endDate));
        return dft.format(endDate);
    }
    private String getBeforeDate(int distanceDay) {
        SimpleDateFormat dfy = new SimpleDateFormat("yyyy");
        SimpleDateFormat dfm = new SimpleDateFormat("MM");
        SimpleDateFormat dfd = new SimpleDateFormat("dd");
        Calendar date = Calendar.getInstance();
        Date now_date = null;
        try {
            now_date=dft.parse(text_date.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date.setTime(now_date);
        date.set(Calendar.DATE, date.get(Calendar.DATE) - distanceDay);
        Date endDate = null;
        try {
            endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("前"+distanceDay+"天=",dft.format(endDate));
        year=Integer.parseInt(dfy.format(endDate));
        month=Integer.parseInt(dfm.format(endDate));
        day=Integer.parseInt(dfd.format(endDate));
        return dft.format(endDate);
    }
    private String Date_Transform(String DateString){
        Date date= null;
        try {
            date = dft.parse(DateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dft.format(date);
    }
}

