package tw.com.edu.ntue.toybabysitter;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import tw.com.edu.ntue.toybabysitter.model.DbLogin;
import tw.com.edu.ntue.toybabysitter.model.DbNoteSelect;
import tw.com.edu.ntue.toybabysitter.model.DbResult;

public class WebService {

    private static final int CONNECT_TIMEOUT = 10;
    //syscode.idv.tw/sandy、192.168.1.12
    private static final String Host="syscode.idv.tw/sandy/graduation_topic";
    private static String performPostCall(Context context, String url, final HashMap<String, String> params) {
        String result = "";
        if(!Global.isNetworkAvailable(context)){
            return "";
        }
        try {
            RequestQueue queue = Volley.newRequestQueue(context);
            RequestFuture<String> future = RequestFuture.newFuture();
            StringRequest request = new StringRequest(Request.Method.POST, url, future, future) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return params;
                }
            };
            queue.add(request);

            result = future.get(CONNECT_TIMEOUT, TimeUnit.SECONDS); // Blocks for at most 10 seconds.
        } catch (Exception e) {
            e.printStackTrace();
            result="";
            Log.e("--performPostCallERR--",e.getMessage());
        }

        return result;
    }
    public static synchronized ArrayList<DbLogin> Login(Context context,String account,String password) {
        ArrayList<DbLogin> result = new ArrayList<>();
        String url="http://"+Host+"/login.php";

        HashMap<String, String> param = new HashMap<String, String>();
        param.put("account",account);
        param.put("password", password);
        try {
            String response = performPostCall(context, url, param);
            Log.e("response",response);
            if (!Global.isEmptyString(response)) {
                JsonParser parser = new JsonParser();
                JsonArray jsonArray = parser.parse(response).getAsJsonArray();
                Gson gson = new Gson();

                for (JsonElement object : jsonArray) {
                    DbLogin db = gson.fromJson(object, DbLogin.class);
                    result.add(db);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Err",e.getMessage());
        }
        return result;
    }
    public static synchronized ArrayList<DbResult> CheckToken_id(Context context,String token_id) {
        ArrayList<DbResult> result = new ArrayList<>();
        String url="http://"+Host+"/tokenid.php";

        HashMap<String, String> param = new HashMap<String, String>();
        param.put("token_id",token_id);
        try {
            String response = performPostCall(context, url, param);
            Log.e("response",response);
            if (!Global.isEmptyString(response)) {
                JsonParser parser = new JsonParser();
                Gson gson = new Gson();
                JsonObject json= parser.parse(response).getAsJsonObject();

                DbResult db = gson.fromJson(json, DbResult.class);
                result.add(db);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Err",e.getMessage());
        }
        return result;
    }

    public static synchronized ArrayList<DbResult> InsertMember(Context context,String username,String password,String sex) {
        ArrayList<DbResult> result = new ArrayList<>();
        String url="http://"+Host+"/validate.php";

        HashMap<String, String> param = new HashMap<String, String>();
        param.put("username",username);
        param.put("password",password);
        param.put("sex",sex);
        try {
            String response = performPostCall(context, url, param);
            Log.e("response",response);
            if (!Global.isEmptyString(response)) {
                JsonParser parser = new JsonParser();
                Gson gson = new Gson();
                JsonObject json= parser.parse(response).getAsJsonObject();

                DbResult db = gson.fromJson(json, DbResult.class);
                result.add(db);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Err",e.getMessage());
        }
        return result;
    }

    public static synchronized ArrayList<DbResult> Update(Context context,String token_id,String status,String message) throws Exception{
        ArrayList<DbResult> result = new ArrayList<>();
        String url="http://"+Host+"/update.php";

        HashMap<String, String> param = new HashMap<String, String>();
        param.put("token_id",token_id);
        param.put("status",status);
        switch (status){
            case "pass":
                param.put("password",message);
                break;
            case "nickname":
                param.put("nickname",message);
                break;
            case "gender":
                param.put("sex",message);
                break;
            case "headportrait":
                param.put("headportrait",message);
                break;
        }
        try {
            String response = performPostCall(context, url, param);
            Log.e("response",response);
            if (!Global.isEmptyString(response)) {
                JsonParser parser = new JsonParser();
                Gson gson = new Gson();
                JsonObject json= parser.parse(response).getAsJsonObject();

                DbResult db = gson.fromJson(json, DbResult.class);
                result.add(db);

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Err",e.getMessage());
        }
        return result;
    }
    public static synchronized ArrayList<DbResult> UpdateNote(Context context,String username,String status,String content,String date){
        ArrayList<DbResult> result = new ArrayList<>();
        String url="http://"+Host+"/updatenote.php";

        HashMap<String, String> param = new HashMap<String, String>();
        param.put("username",username);
        param.put("status",status);
        param.put("content",content);
        param.put("date",date);
        try {
            String response = performPostCall(context, url, param);
            Log.e("response",response);
            if (!Global.isEmptyString(response)) {
                JsonParser parser = new JsonParser();
                Gson gson = new Gson();
                JsonObject json= parser.parse(response).getAsJsonObject();
                DbResult db = gson.fromJson(json, DbResult.class);
                result.add(db);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Err",e.getMessage());
        }
        return result;
    }
    public static synchronized ArrayList<DbResult> NoteSingleSearch(Context context,String username,String date){
        ArrayList<DbResult> result = new ArrayList<>();
        String url="http://"+Host+"/notesearch.php";

        HashMap<String, String> param = new HashMap<String, String>();
        param.put("username",username);
        param.put("date",date);
        try {
            String response = performPostCall(context, url, param);
            Log.e("response",response);
            if (!Global.isEmptyString(response)) {
                JsonParser parser = new JsonParser();
                Gson gson = new Gson();
                JsonObject json= parser.parse(response).getAsJsonObject();
                DbResult db = gson.fromJson(json, DbResult.class);
                result.add(db);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Err",e.getMessage());
        }
        return result;
    }
    public static synchronized ArrayList<DbNoteSelect> NoteSearch(Context context,String username,String year,String month){
        ArrayList<DbNoteSelect> result = new ArrayList<>();
        String url;
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("username",username);
        param.put("year",year);
        if(!month.equals("Empty"))param.put("month",month);


        url="http://"+Host+"/notesearch.php";
        try {
            String response = performPostCall(context,url,param);
            if (!Global.isEmptyString(response)) {
                JsonParser parser = new JsonParser();
                JsonArray jsonArray = parser.parse(response).getAsJsonArray();
                Gson gson = new Gson();
                for (JsonElement object : jsonArray) {
                    DbNoteSelect db = gson.fromJson(object,DbNoteSelect.class);
                    result.add(db);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Err",e.getMessage());
        }
        return result;
    }
}
