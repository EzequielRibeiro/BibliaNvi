package com.projeto.biblianvi;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        myWebView = (WebView) findViewById(R.id.webViewBrowser);

        webView();

    }

    private void webView(){



        myWebView.getSettings().setJavaScriptEnabled(true);


        myWebView.loadUrl(getString(R.string.url_noticias));


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
