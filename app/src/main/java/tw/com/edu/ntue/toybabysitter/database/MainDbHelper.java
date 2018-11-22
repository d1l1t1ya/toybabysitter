package tw.com.edu.ntue.toybabysitter.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MainDbHelper extends SQLiteOpenHelper {
    final private static int _DB_VERSION = 1;
    final private static String _DB_DATABASE_NAME = "data.db";
    private static Context context = null;

    protected static Object lockObject = new Object();

    public MainDbHelper(Context context) {
        super(context, _DB_DATABASE_NAME, null, _DB_VERSION);
    }

    public MainDbHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    private void execSQL(SQLiteDatabase db, String sql) {
        try {
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        execSQL(db,
                "CREATE TABLE IF NOT EXISTS UserConfig (" +
                        "ID VARCHAR(50) PRIMARY KEY, " +
                        "VAL VARCHAR(500) NULL " +
                        ");");

        execSQL(db,
                "CREATE TABLE IF NOT EXISTS BUFFER (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "NAME TEXT" +
                        ")");
        }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public static String sqlData(String data) {
        return data.replace("'", "''");
    }
}
