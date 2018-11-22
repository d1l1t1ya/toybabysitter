package tw.com.edu.ntue.toybabysitter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import tw.com.edu.ntue.toybabysitter.database.UserConfig;

public class setting extends CommonActivity implements View.OnClickListener{
    private Intent it;
    LayoutInflater inflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        if (!CacheActivity.activityList.contains(context)) {
            CacheActivity.addActivity(setting.this);
        }
        CommonActivity.handler.post(checkToken_id);
        setListeners();
    }

    private void setListeners(){
        findViewById(R.id.logout).setOnClickListener(this);
        findViewById(R.id.setting_HeadPortrait).setOnClickListener(this);
        findViewById(R.id.go_personalinformation).setOnClickListener(this);
        findViewById(R.id.app_info).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.go_personalinformation:
                it =new Intent(context,Update.class);
                startActivity(it);
                break;
            case R.id.setting_HeadPortrait:
                it =new Intent(context,Head_portrait.class);
                startActivity(it);
                break;
            case R.id.logout:
                inflater = LayoutInflater.from(context);
                View createView = inflater.inflate(R.layout.ask_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(createView);
                final AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawableResource(R.color.Transparent);
                dialog.show();
                TextView dialog_title=createView.findViewById(R.id.dialog_title);
                dialog_title.setText(getResources().getString(R.string.logout));
                TextView ask_detail=createView.findViewById(R.id.ask_detail);
                ask_detail.setText(getResources().getString(R.string.Are_you_sure_want_to_logout));
                TextView dialog_ok=createView.findViewById(R.id.dialog_ok);
                dialog_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserConfig.setTokenId(context,"");
                        handler.removeCallbacks(checkToken_id);
                        PageChange.Page(context,1);

                    }
                });
                TextView dialog_cancel=createView.findViewById(R.id.dialog_cancel);
                dialog_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                break;
            case R.id.back:
                handler.removeCallbacks(checkToken_id);
                finish();
                break;
            case R.id.app_info:
                inflater = LayoutInflater.from(context);
                final View v = inflater.inflate(R.layout.info, null);
                final AlertDialog dialog1 = new AlertDialog.Builder(context)
                        .setView(v)
                        .show();

                break;

        }
    }
}
