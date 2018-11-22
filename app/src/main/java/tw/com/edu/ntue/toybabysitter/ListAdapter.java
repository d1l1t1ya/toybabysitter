package tw.com.edu.ntue.toybabysitter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tw.com.edu.ntue.toybabysitter.database.UserConfig;
import tw.com.edu.ntue.toybabysitter.model.DbNoteSelect;
import tw.com.edu.ntue.toybabysitter.model.DbResult;

public class ListAdapter extends ArrayAdapter<DbNoteSelect> {
    ArrayList<DbResult> resultArrayList;
    LayoutInflater inflate;
    AlertDialog dialog;
    View v;
    private static String DELETE="delete";
    private static String WRITE="write";
    public ListAdapter(Context context, List<DbNoteSelect> items) {
        super(context,0, items);
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final DbNoteSelect db = getItem(position);
        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.show_note_list, null);
        }

        TextView date_text = row.findViewById(R.id.date);
        TextView content_text = row.findViewById(R.id.content);
        content_text.setText(db.getMessage());
        date_text.setText(db.getDate());
        return row;
    }


}
