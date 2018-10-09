package com.example.com.webapp;

import android.app.Activity;

import android.os.Bundle;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity {
    WebView mWebView;
    TextView mTextView;
    EditText mEditText;
    Button mButton;
    String mStringHead;

    //intent page 추가
    Intent myintent;

    private final Handler handler = new Handler();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = (WebView) findViewById(R.id.webview);
        mTextView = (TextView) findViewById(R.id.textview);
        mButton = (Button) findViewById(R.id.button);
        mEditText = (EditText) findViewById(R.id.EditText01);
        mStringHead = this.getString(R.string.string_head);


        // 웹뷰에서 자바스크립트실행가능
        mWebView.getSettings().setJavaScriptEnabled(true);
        // Bridge 인스턴스 등록
        mWebView.addJavascriptInterface(new AndroidBridge(), "MainActivity");

        mWebView.loadUrl("file:///android_asset/test.html");  // 로컬 HTML 파일 로드

        mWebView.setWebViewClient(new HelloWebViewClient());  // WebViewClient 지정

        mButton.setOnClickListener( new OnClickListener(){
            public void onClick(View view) {
                mWebView.loadUrl("javascript:setMessage('"+mEditText.getText()+"')");
            }
        });

    }


    private class AndroidBridge {
        public void setMessage(final String arg) { // must be final
            handler.post(new Runnable() {
                public void run() {
                    Log.d("MainActivity", "setMessage("+arg+")");
                    mTextView.setText(mStringHead+arg);
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}