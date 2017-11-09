package com.example.yang.xwalkandroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import org.xwalk.core.XWalkActivityDelegate;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkView;

public class MainActivity extends Activity  implements XWalkLibraryLoader.DecompressListener, XWalkLibraryLoader.DownloadListener {
    private XWalkView walkView;
    private XWalkActivityDelegate mActivityDelegate;
    private ProgressDialog mypDialog = null;
    private void onXWalkReady() {
        if(mypDialog.isShowing())
            mypDialog.dismiss();
        walkView.load("https://m.baidu.com/?from=1013843a&pu=sz%401321_480&wpo=btmfast", null);
//        walkView.load("file:///android_asset/index.html", null);
    }

    public boolean isXWalkReady() {
        return this.mActivityDelegate.isXWalkReady();
    }

    public boolean isSharedMode() {
        return this.mActivityDelegate.isSharedMode();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0沉浸状态栏
//            Window window = getWindow();//透明状态栏
//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(Color.TRANSPARENT);//设置状态栏的颜色
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//            getWindow().setNavigationBarColor(Color.TRANSPARENT);
//        }
        super.onCreate(savedInstanceState);
        mypDialog = new ProgressDialog(this);
        Runnable cancelCommand = new Runnable() {
            public void run() {
                finish();
            }
        };
        Runnable completeCommand = new Runnable() {
            public void run() {
                onXWalkReady();
            }
        };
        this.mActivityDelegate = new XWalkActivityDelegate(this, cancelCommand, completeCommand);
        //setContentView(R.layout.activity_main);
//        if( !XWalkLibraryLoader.libIsReady(this) ){
//            //启动下载
//            XWalkLibraryLoader.startDownload(this, this, "http://www.soupan.me/libxwalkcore.so.armeabi_v7a");
//        }else{
            initWebView();
//        }
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void initWebView(){

        walkView = new XWalkView(this, this);
        walkView.setResourceClient(new XWalkResourceClient(walkView ));
        //设置Ui回调
        walkView.setUIClient(new XWalkUIClientEx(walkView));
        walkView.setEnabled(true);//uuu
        walkView.setClickable(true);
        walkView.setLongClickable(true);
        walkView.setSelected(true);
        Paint  paint =new Paint() ;
        paint.setColor(Color.TRANSPARENT);
        walkView.setLayerPaint(paint );

        walkView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override public boolean onLongClick(View v) {
                Log.i("longClick","长按事件:"+v);
                Log.i("longClick","长按事件:"+v.getClass().getSimpleName());
                Log.i("longClick","长按事件:"+v.toString());
                return false;
            }
        });
//        walkView.setBackgroundResource(R.mipmap.ic_launcher);
//        walkView.setBackgroundColor(128);
//        walkView.setBackground(getResources().getDrawable(R.mipmap.ic_launcher));
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.addView(walkView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        setContentView(linearLayout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        walkView.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mActivityDelegate != null){
            this.mActivityDelegate.onResume();
        }
    }
    private void showProgressDialog(String text){
        mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //mypDialog.setTitle("Google");
        mypDialog.setMessage(text);
        //mypDialog.setIcon(R.drawable.android);
        //mypDialog.setButton("Google",this);
        mypDialog.setIndeterminate(false);
        mypDialog.setCancelable(false);
        mypDialog.show();
    }

    @Override
    public void onDecompressStarted() {
        showProgressDialog("数据解压中...");
    }

    @Override
    public void onDecompressCancelled() {
        if(mypDialog.isShowing())
            mypDialog.dismiss();
    }

    @Override
    public void onDecompressCompleted() {
        initWebView();
    }

    @Override
    public void onDownloadStarted() {
        Log.i("MainActivity", "onDownloadStarted");

        showProgressDialog("数据下载中...");

    }

    @Override
    public void onDownloadUpdated(int percent) {
        mypDialog.setMessage("数据下载中 "+ percent+"%");
    }

    @Override
    public void onDownloadCancelled() {
        if(mypDialog.isShowing())
            mypDialog.dismiss();
        Log.i("MainActivity", "onDownloadCancelled");

    }

    @Override
    public void onDownloadCompleted(Uri var1) {
        if(mypDialog.isShowing())
            mypDialog.dismiss();

        //File file = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        XWalkLibraryLoader.startDecompress(this, this);
    }

    @Override
    public void onDownloadFailed(int var1, int reason) {
        if(mypDialog.isShowing())
            mypDialog.dismiss();
        Log.i("MainActivity", "onDownloadFailed reason:"+ reason);
    }
}
