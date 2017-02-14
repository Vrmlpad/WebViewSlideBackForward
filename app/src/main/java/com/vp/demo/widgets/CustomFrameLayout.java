package com.vp.demo.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * Created by Vrmlpad on 2016/12/1.
 */

public class CustomFrameLayout extends FrameLayout {
    /** Native布局是否拦截Touch事件 */
    private boolean isInterceptTouchEvent =false;
    /** H5是否需要Touch事件 */
    private boolean isH5NeedTouchEvent =false;
    private float mInterceptEventX;
    private float mInterceptEventY;
    private float mTouchEventX;
    private float mTouchEventY;
    private boolean isOk=false;
    private int mResponseDistance;
    private int mTouchSlop=0;
    private Context mContext;
    private WebView mWebView;
    private boolean mWebViewLoaded=false;
    public CustomFrameLayout(Context context) {
        super(context);
        init(context);
    }

    public CustomFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mContext=context;
        mTouchSlop= ViewConfiguration.get(mContext).getScaledTouchSlop();
        mResponseDistance =mTouchSlop*3;
    }

    public void setWebView(WebView webView){
        mWebView=webView;
    }
    public void setWebViewLoaded(boolean webViewLoaded){
        mWebViewLoaded=webViewLoaded;
    }
    public void h5NeedEvent(){
        isH5NeedTouchEvent =true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if(mWebViewLoaded){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    isOk=false;
                    mTouchEventX = mInterceptEventX = event.getX();
                    mTouchEventY = mInterceptEventY = event.getY();
                    isInterceptTouchEvent = false;
                    isH5NeedTouchEvent = false;
                    mWebView.loadUrl("javascript:window.__needNotifyNative__=true;");
                    break;
                case MotionEvent.ACTION_MOVE:
                    float diffX=Math.abs(event.getX()-mInterceptEventX);
                    float diffY=Math.abs(event.getY()-mInterceptEventY);
                    if(diffX>=diffY && diffX>mTouchSlop){
                        if(isH5NeedTouchEvent){
                            isInterceptTouchEvent =false;
                        }else if(mResponseDistance < Math.abs(diffX)){
                            isInterceptTouchEvent =true;
                        }
                    }
                    break;
            }
        }
        return isInterceptTouchEvent;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                touchMove(event);
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void touchMove(MotionEvent ev){
        float diffX=Math.abs(ev.getX()- mTouchEventX);
        float diffY=Math.abs(ev.getY()- mTouchEventY);
        mTouchEventX=ev.getX();
        mTouchEventY=ev.getY();
        //水平滑动
        if(diffX>=diffY && diffX>mTouchSlop && !isOk){
            isOk=true;
            String text;
            if(diffX>0){ //向右滑动
                text="触发向右滑动事件";
            }else{    //向左滑动
                text="触发向左滑动事件";
            }
            Toast.makeText(mContext,text,Toast.LENGTH_SHORT).show();
        }
    }
}
