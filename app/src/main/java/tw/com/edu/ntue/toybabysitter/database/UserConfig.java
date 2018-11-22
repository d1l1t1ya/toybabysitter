package tw.com.edu.ntue.toybabysitter.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public final class UserConfig {
    private static MainDbHelper writeDbHelper = null;
    private static MainDbHelper readDbHelper = null;

    public synchronized static SQLiteDatabase getDb(Context context, boolean writable) {
        SQLiteDatabase result = null;

        synchronized(MainDbHelper.lockObject) {
            if (writable) {
                if (writeDbHelper == null) {
                    writeDbHelper = new MainDbHelper(context);
                    SQLiteDatabase db = writeDbHelper.getWritableDatabase();
                    writeDbHelper.onCreate(db);
                    db.close();
                }

                result = writeDbHelper.getWritableDatabase();
            } else {
                if (readDbHelper == null) {
                    readDbHelper = new MainDbHelper(context);
                    SQLiteDatabase db = readDbHelper.getWritableDatabase();
                    readDbHelper.onCreate(db);
                    db.close();
                }

                result = readDbHelper.getReadableDatabase();
            }
        }

        return result;
    }

    public synchronized static void setValue(Context context, String id, String value) {
        SQLiteDatabase db = getDb(context, true);

        db.execSQL( "INSERT OR REPLACE INTO UserConfig VALUES('" + id + "', '" + MainDbHelper.sqlData(value) + "')");
        db.close();
    }

    public synchronized static String getValue(Context context, String id, String defaultValue) {
        String result = defaultValue;
        SQLiteDatabase db = getDb(context, false);
        Cursor cursor = db.rawQuery("SELECT VAL FROM UserConfig WHERE ID='" + id + "';", null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result = cursor.getString(0);
            }
            cursor.close();
        }

        db.close();

        return result;
    }

    public static void initialValue(Context context) {
        SQLiteDatabase db = getDb(context, false);
        Cursor cursor = db.rawQuery("SELECT * FROM UserConfig;", null);

        if (cursor.getCount()==0) {
            setTokenId(context,"");
            cursor.close();
        }
        db.close();
    }
    public static String getTokenId(Context context) {
        return getValue(context, "kTOKEN_ID", "");
    }

    public static void setTokenId(Context context, String value) {
        setValue(context, "kTOKEN_ID", value);
    }
    public static String getPassword(Context context){return  getValue(context,"kPASSWORD","");}
    public static void setPassword(Context context, String value) {
        setValue(context, "kPASSWORD", value);
    }
    public static String getAccount(Context context){return  getValue(context,"kACCOUNT","");}
    public static void setAccount(Context context, String value) {
        setValue(context, "kACCOUNT", value);
    }
    public static String getSex(Context context){return  getValue(context,"kSEX","");}
    public static void setSex(Context context, String value) {
        setValue(context, "kSEX", value);
    }
    public static String getNickname(Context context){return  getValue(context,"kNICKNAME","");}
    public static void setNickname(Context context, String value) {
        setValue(context, "kNICKNAME", value);
    }
    public static String getHeadportrait(Context context){return  getValue(context,"kHEAD","");}
    public static void setHeadportrait(Context context, String value) {
        setValue(context, "kHEAD", value);
    }
    public static String getStorage(Context context){return  getValue(context,"kSTORAGE","");}
    public static void setStorage(Context context, String value) {
        setValue(context, "kSTORAGE", value);
    }
    public static String getExportPath(Context context) {
        return getValue(context, "kEXPORT_PATH", "");
    }

    public static void setExportPath(Context context, String value) {
        setValue(context, "kEXPORT_PATH", value);
    }

/*
    public static String getLoginStaus(Context context) {
        return getValue(context, "kIS_LOGIN", "");
    }

    public static void setLoginStaus(Context context, String value) {
        setValue(context, "kIS_LOGIN", value);
    }
    */
}

