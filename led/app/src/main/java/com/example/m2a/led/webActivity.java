package com.example.m2a.led;

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

import java.io.File;
import java.io.FileNotFoundException;

import java.util.Scanner;

public class webActivity extends Activity {
    WebView mWebView;
    EditText mEditText;
    Button mButton;
    String mStringHead;

    EditText mMulti;
    Button mMultiB;


    private final Handler handler = new Handler();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        mWebView = (WebView) findViewById(R.id.webview);
        mButton = (Button) findViewById(R.id.button);
        mEditText = (EditText) findViewById(R.id.EditText01);
        mStringHead = this.getString(R.string.string_head);

        try{
            File file = new File("/storage/emulated/0/DCIM/work_data/LOG.txt");
            Scanner scan = new Scanner(file);
            String[] inTime;

            while( scan.hasNextLine()) {
                inTime = scan.nextLine().split("_");
                mEditText.setText("Access Date : " + inTime[0] + "<br>Access Time : " + inTime[1] + "<br>State : " + inTime[2]);
                //mMulti.setText(scan.nextLine());
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        // 웹뷰에서 자바스크립트실행가능
        mWebView.getSettings().setJavaScriptEnabled(true);
        // Bridge 인스턴스 등록
        mWebView.addJavascriptInterface(new AndroidBridge(), "webActivity");

        mWebView.loadUrl("file:///android_asset/test.html");  // 로컬 HTML 파일 로드

        mWebView.setWebViewClient(new HelloWebViewClient());  // WebViewClient 지정

        mButton.setOnClickListener( new OnClickListener(){
            public void onClick(View view) {
                mWebView.loadUrl("javascript:setMessage('"+mEditText.getText()+"')");
            }
        });

        /*
        mMultiB.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mWebView.loadUrl("javascript:setMessage('"+mMulti.getText()+"')");
            }
        });
        */

    }


    private class AndroidBridge {
        public void setMessage(final String arg) { // must be final
            handler.post(new Runnable() {
                public void run() {
                    Log.d("webActivity", "setMessage("+arg+")");
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
