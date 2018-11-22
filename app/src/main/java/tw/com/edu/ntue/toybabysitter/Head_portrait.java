package tw.com.edu.ntue.toybabysitter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tw.com.edu.ntue.toybabysitter.database.UserConfig;

public class Head_portrait extends CommonActivity {
    private GridView gridView;
    private ImageView mainImg;
    protected static Handler adapterheadler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_portrait);

        findVeiws();
        final List<Map<String, Object>> items = new ArrayList<Map<String,Object>>();
        for (int i = 0; i < image.length; i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("image", image[i]);
            items.add(item);
        }

        adapterheadler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        gridView.setAdapter(new GridAdapter(context,items));
                        showAlertView(context,getResources().getString(R.string.Modified_successfully));
                        break;
                    case 1:
                        showAlertView(context,getResources().getString(R.string.Modified_failure));
                        break;
                    case 2:
                        showAlertView(context,getResources().getString(R.string.Please_check_your_network));
                        break;
                }

            }
        };
        gridView.setAdapter(new GridAdapter(context,items));

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    private void findVeiws(){
        gridView=findViewById(R.id.main_page_gridview);
    }

}
