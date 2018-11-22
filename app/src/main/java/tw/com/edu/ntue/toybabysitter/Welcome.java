package tw.com.edu.ntue.toybabysitter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import tw.com.edu.ntue.toybabysitter.database.UserConfig;

public class Welcome extends CommonActivity {
    Context context=Welcome.this;
    static boolean i=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        UserConfig.initialValue(context);
        if (!CacheActivity.activityList.contains(context)) {
            CacheActivity.addActivity(Welcome.this);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                i=false;
                CommonActivity.handler.post(checkToken_id);
            }}, 2000);

    }

}
