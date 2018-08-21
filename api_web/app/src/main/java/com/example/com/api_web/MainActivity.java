package com.example.com.api_web;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);

        web = new WebView(this);
        WebSettings webSet = web.getSettings();

        webSet.setJavaScriptEnabled(true);
        webSet.setUseWideViewPort(true);
        webSet.setBuiltInZoomControls(false);
        webSet.setAllowUniversalAccessFromFileURLs(true);
        webSet.setJavaScriptCanOpenWindowsAutomatically(true);
        webSet.setSupportMultipleWindows(true);
        webSet.setSaveFormData(false);
        webSet.setSavePassword(false);
        webSet.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient());
        web.loadUrl("file:///android_asset/index.html");

        layout.addView(web);
        setContentView(layout);

    }

    public void onBackPressed() {
        if(web.canGoBack()) web.goBack();
        else finish();
    }


}
