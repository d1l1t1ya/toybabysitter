package tw.com.edu.ntue.toybabysitter;


import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyDialog extends AlertDialog {
    private final OnItemClickListener mCallBack;
    public MyDialog(Context context, int theme, OnItemClickListener callBack, String title, final View ContentView){
        super(context, theme);
        mCallBack = callBack;
        Context themeContext = getContext();
        setIcon(0);
        LayoutInflater inflater = (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.my_dialog, null);
        TextView txt_ok=view.findViewById(R.id.dialog_ok);
        TextView txt_cancel=view.findViewById(R.id.dialog_cancel);
        txt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCallBack != null) {
                    dismiss();
                    mCallBack.Positive(ContentView);
                }
            }
        });
        LinearLayout contentView=view.findViewById(R.id.content_view);
        contentView.removeAllViews();
        contentView.addView(ContentView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        setView(view);
        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        getWindow().setBackgroundDrawableResource(R.color.Transparent);

    }
    public interface OnItemClickListener {
        void Positive(View view);
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

}

