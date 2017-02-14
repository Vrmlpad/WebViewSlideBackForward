package com.vp.demo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.vp.demo.widgets.CustomFrameLayout;


/**
 * Created by Vrmlpad on 2016/11/28.
 */

public class MainActivity extends Activity {

    private TextView mTVTitle;
    private CustomFrameLayout mFrameLayout;
    private WebView mWebView;
    private String mUrl;
    private static final String INJECT_JAVASCRIPT="javascript:" +
            "window.__needNotifyNative__=true;"+
            "var oldPreventDefault = Event.prototype.preventDefault;" +
            "Event.prototype.preventDefault = function(){" +
            "    if(window.__needNotifyNative__){"+
            "        window.__needNotifyNative__=window.nativeInterface.h5NeedEvent();" +
            "    }"+
            "    oldPreventDefault.call(this)" +
            "};";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(//强制打开GPU渲染
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        setContentView(R.layout.activity_main);
        mTVTitle = (TextView) findViewById(R.id.title);
        mWebView= (WebView) findViewById(R.id.webview);
        mFrameLayout= (CustomFrameLayout) findViewById(R.id.framelayout);
        mFrameLayout.setWebView(mWebView);

        mWebView.setWebChromeClient(new WebChromeClient(){});
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //注入滑动控制通知JS,可以灵活注入
                mWebView.loadUrl(INJECT_JAVASCRIPT);
                Toast.makeText(MainActivity.this,"页面加载完毕",Toast.LENGTH_SHORT).show();
                mFrameLayout.setWebViewLoaded(true);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JavaScriptInterface(),"nativeInterface");
        mUrl="http://m.tv.sohu.com/";
        mWebView.loadUrl(mUrl);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(KeyEvent.KEYCODE_BACK == keyCode){
            if(null != mWebView && mWebView.canGoBack()){
                mWebView.goBack();
                return  true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class JavaScriptInterface{
        @JavascriptInterface
        public boolean h5NeedEvent(){
            mFrameLayout.h5NeedEvent();
            return  false;
        }
    }
}
