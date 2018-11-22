package tw.com.edu.ntue.toybabysitter;

import android.content.Context;
import android.content.Intent;

public class PageChange {
    public static void Page(final Context context, int page){
        Intent it;
        switch (page){
            //登入頁面
            case 1:
                it =new Intent(context,login.class);

                //it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(it);
                CacheActivity.finishActivity();
                CacheActivity.finishSingleActivityByClass(Welcome.class);
                break;
            //註冊頁面
            case 2:
                it =new Intent(context,SignPage.class);
                context.startActivity(it);
                break;
            //主頁面
            case 3:
                it =new Intent(context,MainActivity.class);
                context.startActivity(it);
                CacheActivity.finishSingleActivityByClass(Welcome.class);

                break;
            default:
                break;
        }

    }


}
