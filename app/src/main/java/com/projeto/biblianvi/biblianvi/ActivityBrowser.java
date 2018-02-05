package com.projeto.biblianvi.biblianvi;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class ActivityBrowser extends Activity {

    private WebView myWebView;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        myWebView = (WebView) findViewById(R.id.webViewBrowser);

        Bundle bundle = getIntent().getExtras();

        url = bundle.getString("url");

        webView();

    }

    private void webView(){


        myWebView.setBackgroundColor(Color.WHITE);
        myWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        myWebView.setInitialScale(1);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setLoadWithOverviewMode(true);
        myWebView.getSettings().setUseWideViewPort(true);
        myWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        myWebView.setScrollbarFadingEnabled(false);
        myWebView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        myWebView.loadUrl(url);


        WebSettings settings = myWebView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);



    }


}
