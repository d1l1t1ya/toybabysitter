package tw.com.edu.ntue.toybabysitter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import tw.com.edu.ntue.toybabysitter.database.UserConfig;
import tw.com.edu.ntue.toybabysitter.model.DbNoteSelect;
import tw.com.edu.ntue.toybabysitter.model.DbResult;

public class ShowNoteSearch extends CommonActivity {
    private ArrayList<DbNoteSelect> NoteSelectList;
    Intent it;
    ImageView img_empty;
    GridView show_list;
    private MyDiary myDiary;
    protected static Handler adapterheadler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_note_search);
        findVeiws();
        setListeners();
        it=getIntent();
        setList();

    }

    private void findVeiws(){
        show_list=findViewById(R.id.search_list);
        img_empty=findViewById(R.id.img_empty);
    }
    private void setListeners(){
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    private void setList(){
        NoteSelectList=new ArrayList<>();
        img_empty.setVisibility(View.GONE);
        new Thread(){
            public void run(){
                NoteSelectList=WebService.NoteSearch(context, UserConfig.getAccount(context),it.getStringExtra("year_select_result"),it.getStringExtra("month_select_result"));
                if(NoteSelectList.size()>0){
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                show_list.setAdapter(new ListAdapter(context,NoteSelectList));
                            }
                        });

                    }catch (Exception e){
                        Log.e("err",e.getMessage());
                    }

                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            img_empty.setVisibility(View.VISIBLE);
                            show_list.setAdapter(new ListAdapter(context,NoteSelectList));
                        }
                    });
                }
            }
        }.start();
        show_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                myDiary=new MyDiary(NoteSelectList.get(i).getDate(),NoteSelectList.get(i).getMessage());
                myDiary.ShowDiary(context);
                myDiary.setDiaryChangeListener(new MyDiary.DiaryChangeListener() {
                    @Override
                    public void Update() {
                        setList();
                    }
                });
            }
        });
    }
}
