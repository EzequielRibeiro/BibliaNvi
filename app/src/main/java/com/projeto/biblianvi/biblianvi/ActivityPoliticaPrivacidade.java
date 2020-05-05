package com.projeto.biblianvi.biblianvi;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;


public class ActivityPoliticaPrivacidade extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_politica_privacidade);


        WebView webView = findViewById(R.id.webViewPoliticaPrivacidade);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.loadUrl("file:///android_asset/privacy_policy.html");




    }
}
