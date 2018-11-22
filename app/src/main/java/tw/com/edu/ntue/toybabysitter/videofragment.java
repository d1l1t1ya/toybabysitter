package tw.com.edu.ntue.toybabysitter;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegSurfaceView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import tw.com.edu.ntue.toybabysitter.database.UserConfig;

public class videofragment extends CommonActivity {
    WebView webView;
    Button btn_cut;
    Bitmap temBitmap=null;
    ImageView error_img;
    Boolean loadError=false;
    // Flag indicates that a loadUrl timeout occurred
    int loadUrlTimeout = 0;

    // LoadUrl timeout value in msec (default of 20 sec)
    protected int loadUrlTimeoutValue = 20000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videofragment);
        webView = (WebView)findViewById(R.id.webview);
        error_img=findViewById(R.id.error_img);
        btn_cut=findViewById(R.id.btn_cut);
        final int currentLoadUrlTimeout = this.loadUrlTimeout;
        WebSettings webSettings = webView.getSettings();
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                showWaitAlertView();
                webView.setEnabled(false);// 當加載往頁的時候講網頁進行隱藏
                btn_cut.setVisibility(View.GONE);
                webView.setVisibility(View.GONE);
                super.onPageStarted(view, url, favicon);
            }


            @Override
            public void onPageFinished(WebView view, String url) {//網頁加載結束
                dismissWaitAlertView();
                if (!loadError) {//當網頁加載完成之後判斷是否加載成功
                    webView.setEnabled(true);
                    btn_cut.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.VISIBLE);
                } else { //加載失敗的話,初始化頁面加載失敗圖
                    webView.removeAllViews();
                    error_img.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                dismissWaitAlertView();
                int errorCode = error.getErrorCode();
                // 斷網或網路超時連結
                loadError=true;
                if (errorCode == ERROR_HOST_LOOKUP || errorCode == ERROR_CONNECT || errorCode == ERROR_TIMEOUT) {
                    view.loadUrl("about:blank"); // 避免出現默認錯誤介面
                }
                if (404 == errorCode || 500 == errorCode) {
                    view.loadUrl("about:blank");// 避免出現默認錯誤介面
                    loadError=true;
                }
            }
        });


        webView.loadUrl("http://172.20.10.3:8080/stream.html");
        btn_cut=findViewById(R.id.btn_cut);
        btn_cut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(webView.getHeight()>0&&webView.getWidth()>0){
                    popShotSrceenDialog();
                }else {

                }

            }
        });

    }

    private void popShotSrceenDialog(){
        final AlertDialog cutDialog = new AlertDialog.Builder(this).create();
        View dialogView = View.inflate(this, R.layout.show_cut_screen_layout, null);
        ImageView showImg = (ImageView) dialogView.findViewById(R.id.show_cut_screen_img);
        dialogView.findViewById(R.id.share_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cutDialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.share_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showChooseDirectoryDialog();
                if(temBitmap!=null){
                    savePic(temBitmap);
                }

            }
        });
        //获取当前屏幕的大小
        int width = webView.getWidth();
        int height = webView.getHeight();
        //生成相同大小的图片
        temBitmap = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888 );
        //找到当前页面的跟布局
        //设置缓存
        webView.setDrawingCacheEnabled(true);
        webView.buildDrawingCache();
        //从缓存中获取当前屏幕的图片
        temBitmap = webView.getDrawingCache();
        showImg.setImageBitmap(temBitmap);

        cutDialog.setView(dialogView);
        Window window = cutDialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager m = window.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
        //p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的0.6
        p.gravity = Gravity.CENTER;//设置弹出框位置
        window.setAttributes(p);
        window.setWindowAnimations(R.style.dialogWindowAnim);
        cutDialog.show();
    }
    private void savePic(Bitmap bmp){
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }
        try {
            // 獲取內建SD卡路徑
            String sdCardPath = Environment.getExternalStorageDirectory().getPath();
            // 圖片檔案路徑
            /*String filePath = sdCardPath+File.separator+"test.png";
            File file = new File(filePath);*/
            File mediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory
                                    (Environment.DIRECTORY_PICTURES),getResources().getString(R.string.app_name));

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile;
            String mImageName = timeStamp +".jpg";
            mediaFile = new File(mediaStorageDir.getPath()+ mImageName);
            FileOutputStream os = new FileOutputStream(mediaFile);
            Log.e("file",mediaFile.getPath());
            bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
            showAlertView(context,getResources().getString(R.string.Save_successfully));
        } catch (Exception e) {
            Log.e("ERR",e.getMessage());
            showAlertView(context,getResources().getString(R.string.Image_file_storage_error));
            
        }
    }


}
