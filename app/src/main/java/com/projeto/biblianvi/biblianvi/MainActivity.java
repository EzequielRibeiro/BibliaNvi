package com.projeto.biblianvi.biblianvi;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends Activity {


    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] menuTitulos;
    private BibliaBancoDadosHelper bibliaHelp;
    private boolean existeBancoDados = false;
    private Button buttonNoticias,buttonClock,button_biblia,button_dicionario,button_qualificar;

    private ProgressDialog progressDialog;
    private CheckBancoExiste checkBancoExiste;
    private Intent intent;
    private SharedPreferences sharedPrefs;
    private ListView listView;
    private InterstitialAd mInterstitialAd;
    private boolean chamarNoticia = true;

    TextView textViewAssuntoVers;
    TextView textViewVersDia;

    boolean criarMenuLateral = false;

    private WebView  myWebView;
    private String url = "http://biblianvi.byethost17.com/aviso.html";
    private  FrameLayout myLayoutBase;

    private Button button_menu_lateral;

    private FirebaseAnalytics  mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
          requestWindowFeature(Window.FEATURE_ACTION_BAR);

         setContentView(R.layout.activity_main);


        mTitle = mDrawerTitle = getTitle();
        menuTitulos = getResources().getStringArray(R.array.menu_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, menuTitulos));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer


        if (android.os.Build.VERSION.SDK_INT >= 11) {


        }

        if(getActionBar() != null)
        getActionBar().setDisplayHomeAsUpEnabled(true);
        if(getActionBar() != null)
        getActionBar().setHomeButtonEnabled(true);



        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
           // selectItem(0);
        }


        if(!checarAlarmeExiste())
           agendarAlarmeVersiculo(9,0);

        /*
        if(!isServiceRunning())
        startService(icon_new Intent(this, ServiceNotification.class));
       // stopService(icon_new Intent(this, ServiceNotification.class));
        */




        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        /*
        Bundle bundle = icon_new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
         */

        bibliaHelp = new BibliaBancoDadosHelper(this);

        myLayoutBase = (FrameLayout) findViewById(R.id.frame_verso_dia);
        listView       = (ListView) findViewById(R.id.listView);
       // buttonLer = (Button) findViewById(R.id.buttonLer);
        buttonNoticias = (Button) findViewById(R.id.buttonNoticias);
      //  botaoBusca     = (Button) findViewById(R.id.buttonBusca);
      //  spinnerLiv     = (Spinner) findViewById(R.id.spinner1);
      //  spinnerLiv.setPrompt("Livro");

      //  spinnerCap     = (Spinner) findViewById(R.id.spinner2);
      //  spinnerCap.setPrompt("Capítulo");
      //  spinnerVers    = (Spinner) findViewById(R.id.spinner3);
     //   spinnerVers.setPrompt("Versículo");
        buttonClock = (Button) findViewById(R.id.buttonClock);

        textViewAssuntoVers = (TextView) findViewById(R.id.textViewAssuntoVers);
        textViewVersDia     = (TextView) findViewById(R.id.textViewVersDia);
        button_qualificar   = (Button)   findViewById(R.id.button_qualificar);



      //  button_menu_lateral = (Button) findViewById(R.id.button_menu_lateral);

      /*  button_menu_lateral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLateral();
            }
        });
        */

        button_qualificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
            }
        });

        buttonNoticias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isNetworkAvailable())
                   chamarNoticia();
                else
                  Toast.makeText(getApplication(),"Sem conexão",Toast.LENGTH_LONG).show();
              }
        });

        /*
        botaoBusca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(),Activity_busca_avancada.class);
                 startActivity(in);
                }
        });

        */

        textViewVersDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(bibliaHelp != null) {
                    //  bibliaHelp.versDoDiaText(textViewAssuntoVers, textViewVersDia,false);
                }
            }
        });

        buttonClock.setBackgroundResource(R.mipmap.alarm_clock);
        buttonClock.setText("");
        buttonClock.setPadding(0,0,5,0);
        buttonClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alterarHoraAlarme();
            }
        });


        //checa nova versão se conectado
        if(isNetworkAvailable()) {

            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

                if(sharedPrefs != null)
                if(sharedPrefs.getBoolean("checarNovaVersao", false))
                     new UpdateRunnable(this, new Handler()).start();
                }


        checkBancoExiste = new CheckBancoExiste(getBaseContext());


        try {
            if (checkBancoExiste.checkDataBase() && checkBancoExiste.checarIntegridadeDoBanco()) {

                existeBancoDados = true;
                Log.e("Main: Banco existe", "sim");


            }else {

                new ChegarBanco().execute("");
            }
        } catch (Exception e) {

            FirebaseCrash.report(e.getCause());
        }


        button_biblia = (Button) findViewById(R.id.button_biblia);
        button_dicionario = (Button) findViewById(R.id.button_dicionario);

        button_biblia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent i = new Intent();
                i.setClass(MainActivity.this,MainActivityFragment.class);
                i.putExtra("Biblia","biblia");

                startActivity(i);



            }
        });

        button_dicionario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,DicionarioActivity.class));

            }
        });





    }

    public void compartilharVers(View v){

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textViewVersDia.getText().toString().concat("\n(Bíblia Adonai)"));
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Compartilhar com"));



    }

    public boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.projeto.biblianvi.biblianvi.ServiceNotification".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private boolean checarAlarmeExiste(){

        boolean alarmUp = (PendingIntent.getBroadcast(MainActivity.this, 0,
                new Intent("EXECUTAR_ALARME"),
                PendingIntent.FLAG_NO_CREATE) != null);

        return alarmUp;

    }

    private void cancelarAgendarAlarmeVersiculo(){


        Intent intent = new Intent("EXECUTAR_ALARME");
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if(alarmManager != null)
        alarmManager.cancel(sender);



    }

    private void agendarAlarmeVersiculo(int hora, int min){

        Intent it = new Intent("EXECUTAR_ALARME");
        PendingIntent p = PendingIntent.getBroadcast(MainActivity.this,0,it,0);

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, hora);
        c.set(Calendar.MINUTE, min);


        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),AlarmManager.INTERVAL_DAY,p);

        Log.e("Alarm",Long.toString(c.getTimeInMillis()));



    }

    private void alterarHoraAlarme(){


        SharedPreferences settings = getSharedPreferences("alarme", Activity.MODE_PRIVATE);
        final SharedPreferences.Editor editor = settings.edit();

        String h = settings.getString("hora","09");
        String m = settings.getString("minuto","00");

        AlterarAlarm alterarAlarm;


        TextView title = new TextView(this);
        title.setText("Aviso de versículo diário programado para:");
        title.setTextColor(getResources().getColor(R.color.white));
        title.setPadding(5, 5, 5, 5);
        title.setGravity(View.TEXT_ALIGNMENT_CENTER);
        // title.setTextColor(getResources().getColor(R.color.greenBG));
        title.setTextSize(18);

        final TextView horaText = new TextView(this);
        horaText.setTextColor(getResources().getColor(R.color.blue));
       // horaText.setBackgroundColor(getResources().getColor(R.color.white));
        horaText.setText(h);
        horaText.setTextSize(22);

        //altura comprimento
        LinearLayout layoutTextHora = new LinearLayout(this);
        layoutTextHora.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutTextHora.setGravity(Gravity.CENTER);
        layoutTextHora.addView(horaText);

        final TextView minText = new TextView(this);
        minText.setTextColor(getResources().getColor(R.color.blue));
       // horaText.setBackgroundColor(getResources().getColor(R.color.white));
        minText.setText(m);
        minText.setTextSize(22);

        alterarAlarm = new AlterarAlarm(horaText,minText);

        LinearLayout layoutTextMin = new LinearLayout(this);
        layoutTextMin.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutTextMin.setGravity(Gravity.CENTER);
        layoutTextMin.addView(minText);

        Button horaMaisButton = new Button(this);
        horaMaisButton.setTag("horaMaisButton");
        horaMaisButton.setBackgroundResource(R.mipmap.img_mais);
        horaMaisButton.setLayoutParams(new LinearLayout.LayoutParams(50,40));
        horaMaisButton.setOnClickListener(alterarAlarm);

        Button horaMenosButton = new Button(this);
        horaMenosButton.setTag("horaMenosButton");
        horaMenosButton.setLayoutParams(new LinearLayout.LayoutParams(50,40));
        horaMenosButton.setBackgroundResource(R.mipmap.img_menos);
        horaMenosButton.setOnClickListener(alterarAlarm);

        Button minMaisButton = new Button(this);
        minMaisButton.setTag("minMaisButton");
        minMaisButton.setLayoutParams(new LinearLayout.LayoutParams(50,40));
        minMaisButton.setBackgroundResource(R.mipmap.img_mais);
        minMaisButton.setOnClickListener(alterarAlarm);

        Button minMenosButton = new Button(this);
        minMenosButton.setTag("minMenosButton");
        minMenosButton.setLayoutParams(new LinearLayout.LayoutParams(50,40));
        minMenosButton.setBackgroundResource(R.mipmap.img_menos);
        minMenosButton.setOnClickListener(alterarAlarm);

        LinearLayout horaLayout = new LinearLayout(this);
        horaLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,0,15,0);

        TextView txtHora = new TextView(this);
        txtHora.setText("Hora");
        txtHora.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT ));
        txtHora.setTextColor(getResources().getColor(R.color.white));

        horaLayout.setLayoutParams(params);
        horaLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        horaLayout.addView(txtHora);
        horaLayout.addView(horaMaisButton);
        horaLayout.addView(layoutTextHora);
        horaLayout.addView(horaMenosButton);


        LinearLayout minLayout= new LinearLayout(this);
        minLayout.setOrientation(LinearLayout.VERTICAL);
        minLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        minLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT ));

        TextView txtMin = new TextView(this);
        txtMin.setText("Minuto");
        txtMin.setTextColor(getResources().getColor(R.color.white));
        txtMin.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        minLayout.addView(txtMin);
        minLayout.addView(minMaisButton);
        minLayout.addView(layoutTextMin);
        minLayout.addView(minMenosButton);


        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.HORIZONTAL);
        content.setGravity(Gravity.CENTER);
        content.setBackgroundColor(getResources().getColor(R.color.dark));
        content.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        content.addView(horaLayout);
        content.addView(minLayout);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);

        alertDialogBuilder.setView(content);
        alertDialogBuilder.setCustomTitle(title);


        // set dialog message
        alertDialogBuilder.setPositiveButton("Redefinir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                editor.putString("hora",horaText.getText().toString());
                editor.putString("minuto",minText.getText().toString());
                editor.commit();

                int h = Integer.parseInt(horaText.getText().toString());
                int m = Integer.parseInt(minText.getText().toString());

                if(checarAlarmeExiste()){

                   cancelarAgendarAlarmeVersiculo();

                }

                agendarAlarmeVersiculo(h,m);

                Toast.makeText(MainActivity.this,"Horário redefinido: "
                        +horaText.getText().toString()+":"
                        +minText.getText().toString()+"h"
                        ,Toast.LENGTH_LONG).show();

            }
        });

        alertDialogBuilder.setNegativeButton("Cancelar",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();


    }

    private void versiculoDoDia() throws ParseException {


        SharedPreferences settings = getSharedPreferences("data_preferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date1 = sdf.parse(settings.getString("dia","1")+
                "/"+settings.getString("mes","1")+
                "/"+settings.getString("ano","1999"));


        Calendar c2 = Calendar.getInstance();

        Date date2 = sdf.parse(Integer.toString(c2.get(Calendar.DAY_OF_MONTH))+
                "/"+Integer.toString(c2.get(Calendar.MONTH))+
        "/"+Integer.toString(c2.get(Calendar.YEAR)));



        if(existeBancoDados)
        if(date1.before(date2)){

            if(bibliaHelp != null)
                bibliaHelp.versDoDiaText(textViewAssuntoVers, textViewVersDia,true);

            editor.putString("ano",Integer.toString(c2.get(Calendar.YEAR)));
            editor.putString("mes",Integer.toString(c2.get(Calendar.MONTH)));
            editor.putString("dia",Integer.toString(c2.get(Calendar.DAY_OF_MONTH)));

            editor.commit();

        }else{

            settings =  getSharedPreferences("versDia", Activity.MODE_PRIVATE);
            textViewAssuntoVers.setText(settings.getString("assunto","Paz"));
            textViewAssuntoVers.setMinLines(2);
            textViewVersDia.setText(settings.getString("versDia","Tenho-vos dito isto, para que em mim tenhais paz; no mundo tereis aflições, mas tende bom ânimo, eu venci o mundo.\n" +
                    "João 16:33"));


        }

    }

    private void webView(){


        myWebView = new WebView(MainActivity.this);
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

        myWebView.setWebViewClient(new WebViewClient(){

            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url != null ) {


                    Bundle bundle = new Bundle();

                    bundle.putString("url", url );

                    Intent intent = new Intent(MainActivity.this,ActivityBrowser.class);

                    intent.putExtras(bundle);

                    startActivity(intent);


                    return true;
                } else {
                    return false;
                }
            }
        });

        myWebView.loadUrl(url);

        //myWebView.loadData(SITE, "text/html; charset=UTF-8", null);

        WebSettings settings = myWebView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);



    }

    private void chamarNoticia() {


        if (chamarNoticia) {

            if(myWebView == null)
              webView();

            myLayoutBase.addView(myWebView);

            buttonNoticias.setText("voltar");

            chamarNoticia = false;

        } else {

            myLayoutBase.removeView(myWebView);

            buttonNoticias.setText("Notícias");

            chamarNoticia = true;

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    /*

    private void carregarSpinnerVersiculo(String liv,String cap){

        int  versiculos;


        if (existeBancoDados) {

            bibliaHelp = new BibliaBancoDadosHelper(this);


            versiculos = bibliaHelp.getQuantidadeVersos(liv, cap) ;


            Log.e("Versos", Integer.toString(versiculos));

          List<Integer> list = new ArrayList<Integer>();

            for (int ii = 1; ii <= versiculos; ii++) {

                list.add(ii);


            }

            ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<Integer>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerVers.setAdapter(dataAdapter);


        }


    }

    private void carregarLivros(){

        String[] livros = getResources().getStringArray(R.array.bibliaLivEp_arrays);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.activity_list_view,livros);
        spinnerLiv.setAdapter(adapter);

    }

    private void carregarSpinnerCapitulo(String livro){


       int capitulos;

       if (existeBancoDados) {

       bibliaHelp = new BibliaBancoDadosHelper(this);


       capitulos = bibliaHelp.getQuantidadeCapitulos(livro) ;


       Log.e("Capitulo", Integer.toString(capitulos));


       List<Integer> list = new ArrayList<Integer>();

       for (int ii = 1; ii <= capitulos; ii++) {

           list.add(ii);


       }

       ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<Integer>(this,
               android.R.layout.simple_spinner_item, list);
       dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       spinnerCap.setAdapter(dataAdapter);


   }


   }   */

    protected  void onStop(){



        super.onStop();

    }

    protected  void onStart(){
        super.onStart();

        }

    protected void onPostResume(){
        super.onPostResume();


      }

    protected  void onResume(){
        super.onResume();

      boolean b =  checarAlarmeExiste();

        AdView mAdView = (AdView) findViewById(R.id.adViewMain);
       // mAdView.setAdSize(AdSize.LARGE_BANNER);

        if(isNetworkAvailable() && (mAdView != null)){


            mAdView.setAdListener(new AdListener() {

                public void onAdFailedToLoad(int errorCode) {

                    switch (errorCode) {

                        case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                            Log.e("ADMOB ERRO:","ERROR_CODE_INTERNAL_ERROR");
                            break;
                        case AdRequest.ERROR_CODE_INVALID_REQUEST:
                            Log.e("ADMOB ERRO:","ERROR_CODE_INVALID_REQUEST");
                            break;
                        case AdRequest.ERROR_CODE_NETWORK_ERROR:
                            Log.e("ADMOB ERRO:","ERROR_CODE_NETWORK_ERROR");
                            break;
                        case AdRequest.ERROR_CODE_NO_FILL:
                            Log.e("ADMOB ERRO:","ERROR_CODE_NO_FILL");
                            break;
                        default:
                            Log.e("ADMOB ERRO:","NENHUM_ERRO");
                            break;
                    }
                }

            });


            //propaganda Google
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

            String id = getResources().getString(R.string.interstitial_ad_unit_id);

            mInterstitialAd = new InterstitialAd(getApplication());
            mInterstitialAd.setAdUnitId(id);

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    // finish();
                }
            });


        }

        requestNewInterstitial();

        try {
            versiculoDoDia();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    public void onBackPressed() {


            if ( mInterstitialAd != null)
                if(mInterstitialAd.isLoaded()){

                    mInterstitialAd.show();

                }
                    super.onBackPressed();

                    return;




    }

    private void requestNewInterstitial() {


        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        if(mInterstitialAd != null) {
            mInterstitialAd.loadAd(adRequest);
            Log.e("Main Intertitial: ", Boolean.toString(mInterstitialAd.isLoaded()));
        }
    }

    protected void onPause(){
        super.onPause();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /*
        switch (item.getItemId()) {

            case R.id.action_settings:
                Intent settingsActivity = icon_new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsActivity);
                 return true;

            case R.id.action_devocional:

                Intent in = icon_new Intent(getApplicationContext(),NetworkActivityDevocional.class);
                startActivity(in);
                return true;
            case R.id.action_graph:
            Intent estatistica = icon_new Intent(getApplicationContext(), GraficoGeral.class);
            startActivity(estatistica);
            return true;

            case R.id.action_exit:
                 finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
        */

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_websearch:
                // create intent to perform web search for this planet
               // Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
               // intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
                // catch event that there's no activity to handle intent

                Intent intent1 = new Intent();
                intent1.setClass(getApplication(),Activity_busca_avancada.class);


                if (intent1.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent1);
                } else {
                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new MenuLateralTeste.PlanetFragment();
        Bundle args = new Bundle();
        args.putInt(MenuLateralTeste.PlanetFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
       // fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
       // setTitle(menuTitulos[position]);
        chamarActivity(position);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void chamarActivity(int posicao){


        switch (posicao){


            case 0:

                intent = new Intent(MainActivity.this,Activity_favorito.class);
                startActivity(intent);

                break;

            case 1:

                intent = new Intent(MainActivity.this,ActivityAnotacao.class);
                startActivity(intent);

                break;
            case 2:

                intent = new Intent(MainActivity.this,DicionarioActivity.class);
                startActivity(intent);
                break;

            case 3:

                if(isNetworkAvailable()) {
                    intent = new Intent(MainActivity.this, Mensagem.class);
                    startActivity(intent);
                }else{

                    Toast.makeText(getApplication(),"Sem conexão",Toast.LENGTH_LONG).show();

                }
                break;

            case 4:

                intent = new Intent(MainActivity.this,GraficoGeral.class);
                startActivity(intent);
                break;


            case 5:

                intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
                break;

            case 6:

                mostrarAviso();

                break;

            default:

                break;



        }








    }

    /*
    private void menuLateral() {

        LayoutInflater layoutInflaterBase =
                (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View addView = layoutInflaterBase.inflate(R.layout.menu_opcao_lateral, null);

        LinearLayout myLayoutBase = (LinearLayout) findViewById(R.id.layout_menu_main);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) myLayoutBase.getLayoutParams();



        if (!criarMenuLateral) {


            myLayoutBase.addView(addView);

            params.height = FrameLayout.LayoutParams.WRAP_CONTENT;

            myLayoutBase.setLayoutParams(params);


             ListenerMenu listenerMenu = new ListenerMenu();

             LinearLayout favorito = (LinearLayout) findViewById(R.id.layout_favorito);
             LinearLayout anotacao = (LinearLayout) findViewById(R.id.layout_anotacao);
             LinearLayout dicionario = (LinearLayout) findViewById(R.id.layout_dicionario);
             LinearLayout online = (LinearLayout) findViewById(R.id.layout_online);
             LinearLayout estatistica = (LinearLayout) findViewById(R.id.layout_estatistica);
             LinearLayout informacao = (LinearLayout) findViewById(R.id.layout_informacao);
             LinearLayout configuracao = (LinearLayout) findViewById(R.id.layout_configuracao);

             favorito.setOnClickListener(listenerMenu);
             anotacao.setOnClickListener(listenerMenu);
             dicionario.setOnClickListener(listenerMenu);
             online.setOnClickListener(listenerMenu);
             estatistica.setOnClickListener(listenerMenu);
             informacao.setOnClickListener(listenerMenu);
             configuracao.setOnClickListener(listenerMenu);

            criarMenuLateral = true;

        } else {

            View myView = findViewById(R.id.layout_main_lateral_container);
            ViewGroup parent = (ViewGroup) myView.getParent();
            parent.removeView(myView);

            myLayoutBase.setLayoutParams(params);

            criarMenuLateral = false;

        }
    }
 */
    private void mostrarAviso(){


        TextView title = new TextView(this);
        title.setText("Informação");
        title.setPadding(5, 5, 5, 5);
        title.setGravity(View.TEXT_ALIGNMENT_CENTER);
     // title.setTextColor(getResources().getColor(R.color.greenBG));
        title.setTextSize(18);

        TextView msg = new TextView(this);
        msg.setTextColor(getResources().getColor(R.color.white));
        msg.setText(R.string.aviso);
        msg.setPadding(10, 10, 10, 10);
        msg.setGravity(View.TEXT_ALIGNMENT_CENTER);
        msg.setTextSize(18);


        ScrollView scrollView = new ScrollView(getApplicationContext());
        scrollView.setBackgroundColor(getResources().getColor(R.color.dark));
        scrollView.addView(msg);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);

        alertDialogBuilder.setView(scrollView);
        alertDialogBuilder.setCustomTitle(title);

        // set dialog message
        alertDialogBuilder.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();

            }
        });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private class ChegarBanco extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {


            try {
                checkBancoExiste.createDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                existeBancoDados = checkBancoExiste.checkDataBase() && checkBancoExiste.checarIntegridadeDoBanco()? true:false;
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                if ((progressDialog != null) && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            } catch (final IllegalArgumentException e) {
                // Handle or log or ignore
            } catch (final Exception e) {
                // Handle or log or ignore
            } finally {
                progressDialog = null;
            }


                if(existeBancoDados) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //carregarSpinnerCapitulo("Gênesis");
                            //carregarSpinnerVersiculo("Gênesis", "1");

                        }
                    });

                }




           // alertaReiniciarApp();
        }


        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Biblia Sagrada Atual");
            progressDialog.setMessage("Não interrompa esse processo. Um arquivo necessário para" +
                    " o aplicativo funcionar offline está sendo descompactado. Aguarde o processo até completar 100%.");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgressNumberFormat(null);
            progressDialog.setCancelable(false);
           // progressDialog.setMax(100);
           // progressDialog.setIndeterminate(true);
            progressDialog.show();

            checkBancoExiste.setBarraDeProgresso(progressDialog);
        }




        protected void onProgressUpdate(Integer... values) {



        }
    }

    private class ListenerMenu implements View.OnClickListener {

        private Intent intent;

        @Override
        public void onClick(View v) {

            switch (v.getId()){


                case R.id.layout_favorito:

                    intent = new Intent(MainActivity.this,Activity_favorito.class);
                    startActivity(intent);

                    break;

                case R.id.layout_anotacao:

                    intent = new Intent(MainActivity.this,ActivityAnotacao.class);
                    startActivity(intent);

                    break;
                case R.id.layout_dicionario:

                    intent = new Intent(MainActivity.this,DicionarioActivity.class);
                    startActivity(intent);
                    break;

                case R.id.layout_online:

                    intent = new Intent(MainActivity.this,Mensagem.class);
                    startActivity(intent);
                    break;

                case R.id.layout_estatistica:

                    intent = new Intent(MainActivity.this,GraficoGeral.class);
                    startActivity(intent);
                    break;

                case R.id.layout_informacao:

                    mostrarAviso();

                    break;

                case R.id.layout_configuracao:

                    intent = new Intent(MainActivity.this,SettingsActivity.class);
                    startActivity(intent);
                    break;

                default:

                    break;



            }


        }
    }

    private class AlterarAlarm implements View.OnClickListener{


        TextView textViewhora,textViewMin;
        int hora, min;

        public AlterarAlarm( TextView horaView,TextView minView){

            textViewhora = horaView;
            textViewMin = minView;

            hora = Integer.parseInt(horaView.getText().toString());
            min =  Integer.parseInt(minView.getText().toString());

        }

        @Override
        public void onClick(View v) {


              if(v.getTag().toString().equals("horaMaisButton")){

                  if(hora <= 22) {
                      ++hora;
                      setHora(hora);
                  }
                  else {
                      hora = 0;
                      setHora(hora);
                  }

              }
              else if (v.getTag().toString().equals("horaMenosButton")) {


                  if(hora >= 1) {
                      --hora;
                      setHora(hora);
                  }
                  else {
                      hora = 23;
                      setHora(hora);
                  }
              }
              else if (v.getTag().toString().equals("minMaisButton")){

                  if(min <= 58) {
                      ++min;
                      setMin(min);
                  }
                  else {
                   min = 0;
                   setMin(min);
                  }
              }
              else if (v.getTag().toString().equals("minMenosButton")){

                  if(min >= 1) {
                      --min;
                      setMin(min);
                  }
                  else {

                      min = 59;
                      setMin(min);
                  }

              }

            }

        private void setHora(int h){

               if(h < 10)
                    textViewhora.setText("0" + Integer.toString(h));
                else
                    textViewhora.setText(Integer.toString(h));

        }

        private void setMin(int m){


                if(m < 10)
                     textViewMin.setText("0" + Integer.toString(m));
                 else
                      textViewMin.setText(Integer.toString(m));


        }


        }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }


    public static class PlanetFragment extends Fragment {
        public static final String ARG_PLANET_NUMBER = "planet_number";

        public PlanetFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_planet, container, false);
            int i = getArguments().getInt(ARG_PLANET_NUMBER);
            String planet = getResources().getStringArray(R.array.menu_array)[i];

            int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
                    "drawable", getActivity().getPackageName());
            ((ImageView) rootView.findViewById(R.id.image)).setImageResource(imageId);
            getActivity().setTitle(planet);
            return rootView;
        }
    }



}
