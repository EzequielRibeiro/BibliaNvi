package com.projeto.biblianvi.biblianvi;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class GraficoGeral extends TabActivity {


    private static final int QUANT_VERSOS = 31062;
    private TextView textViewTotalLido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico_geral);


        textViewTotalLido  = (TextView) findViewById(R.id.textViewTotalLido);

        Resources ressources = getResources();
        TabHost tabHost = getTabHost();

        // Android tab
        Intent intentAndroid = new Intent().setClass(this, Grafico_Um.class);
        TabHost.TabSpec tabSpecAndroid = tabHost
                .newTabSpec("Android")
                .setIndicator("", ressources.getDrawable(R.drawable.icon_tab_old))
                .setContent(intentAndroid);

        // Apple tab
        Intent intentApple = new Intent().setClass(this, Grafico_Dois.class);
        TabHost.TabSpec tabSpecApple = tabHost
                .newTabSpec("Apple")
                .setIndicator("", ressources.getDrawable(R.drawable.icon_tab_new))
                .setContent(intentApple);

        // add all tabs
        tabHost.addTab(tabSpecAndroid);
        tabHost.addTab(tabSpecApple);


        //set Windows tab as default (zero based)
        tabHost.setCurrentTab(2);



        //propaganda Google
        AdView mAdView = (AdView) findViewById(R.id.adViewGraf);
        if(mAdView != null){
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

            Log.e("Carregou admob", "Tela Tab");
        }
        else{
            Log.e("Erro admob", "Tela Tab");}


    }

    protected  void onResume(){
        super.onResume();

        LinearLayout myLayoutBase = (LinearLayout) findViewById(R.id.linearLayoutGrafAd);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) myLayoutBase.getLayoutParams();

        if(isNetworkAvailable()){

            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;

            //propaganda Google
            AdView mAdView = (AdView) findViewById(R.id.adViewGraf);
            if(mAdView != null){
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);}
            else{Log.e("Erro Admob", "Tela Gráfico");}


        }else{

            myLayoutBase.setBackgroundResource(R.drawable.banner_logo);
           //  params.height = 0;

        }

        textViewTotalLido.setText(String.format("%.2f", quantVersosLidos()) + "%");

    }


    private float quantVersosLidos(){

        int i;

        BibliaBancoDadosHelper bi = new BibliaBancoDadosHelper(getApplicationContext());

        i = bi.getQuantVersosLidosTotal();


        return (float) (i * 100) /QUANT_VERSOS;

    }

    protected  void onStop(){
        super.onStop();



    }

    protected  void onStart(){
        super.onStart();



    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

          /*
            case R.id.action_settings:
                Intent settingsActivity = icon_new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsActivity);
                return true;
            case R.id.action_devocional:
                Intent in = icon_new Intent(getApplicationContext(),NetworkActivityDevocional.class);
                startActivity(in);
                return true;
                */
            case R.id.action_exit:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
