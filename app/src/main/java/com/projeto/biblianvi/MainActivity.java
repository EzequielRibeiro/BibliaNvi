package com.projeto.biblianvi;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import static com.projeto.biblianvi.R.*;
import static com.projeto.biblianvi.R.menu.*;


public class MainActivity extends AppCompatActivity {


    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] menuTitulos;
    private BibliaBancoDadosHelper bibliaHelp;
    private boolean existeBancoDados = false;
    private Button buttonNoticias, buttonClock, button_biblia, button_dicionario, button_pesquisar, button_qualificar;
    private ProgressDialog progressDialog;
    private CheckBancoExiste checkBancoExiste;
    private Intent intent;
    private ListView listView;
    private AdView mAdView;
    private int REQUEST_STORAGE = 200;
    private TextView textViewAssuntoVers;
    private TextView textViewVersDia;
    private TextView textViewDeveloper;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(layout.activity_main);


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mTitle = mDrawerTitle = getTitle();
        menuTitulos = getResources().getStringArray(array.menu_array);
        mDrawerLayout = (DrawerLayout) findViewById(id.drawer_layout);
        mDrawerList = (ListView) findViewById(id.left_drawer);
        textViewDeveloper = (TextView) findViewById(id.textViewDeveloper);


        textViewDeveloper.setText(BuildConfig.VERSION_NAME);
        // set a custom shadow that overlays the activity_fragment content when the drawer opens
        mDrawerLayout.setDrawerShadow(drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                layout.drawer_list_item, menuTitulos));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                null,  /* nav drawer image to replace 'Up' caret */
                string.drawer_open,  /* "open drawer" description for accessibility */
               string.drawer_close  /* "close drawer" description for accessibility */
        ) {
           public void onDrawerClosed(View view) {
             //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
             //      Objects.requireNonNull(getActionBar()).setTitle(mTitle);
             //  } else {
            //        getActionBar().setTitle(mTitle);

            //  }
             //   invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
              //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
             //       Objects.requireNonNull(getActionBar()).setTitle(mDrawerTitle);
             //   } else {
            //        getActionBar().setTitle(mDrawerTitle);
             //   }
             //   invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
          mDrawerLayout.setDrawerListener(mDrawerToggle);



        if (!checarAlarmeExiste())
            agendarAlarmeVersiculo(9, 0);


        bibliaHelp = new BibliaBancoDadosHelper(this);


        listView = (ListView) findViewById(id.listView);
        buttonNoticias = (Button) findViewById(id.buttonNoticias);
        buttonClock = (Button) findViewById(id.buttonClock);

        textViewAssuntoVers = (TextView) findViewById(id.textViewAssuntoVers);
        textViewVersDia = (TextView) findViewById(id.textViewVersDia);
        button_qualificar = (Button) findViewById(id.button_qualificar);


        button_qualificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
            }
        });

        buttonNoticias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable())
                    startActivity(new Intent(getBaseContext(), ActivityBrowser.class));
                else
                    Toast.makeText(getApplication(), "Sem conexão", Toast.LENGTH_LONG).show();
            }
        });

        textViewVersDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bibliaHelp != null) {
                    //  bibliaHelp.versDoDiaText(textViewAssuntoVers, textViewVersDia,false);
                }
            }
        });

        buttonClock.setBackgroundResource(mipmap.alarm_clock);
        buttonClock.setText("");
        buttonClock.setPadding(0, 0, 5, 0);
        buttonClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alterarHoraAlarme();
            }
        });

        button_biblia = (Button) findViewById(id.button_biblia);
        button_dicionario = (Button) findViewById(id.button_dicionario);
        button_pesquisar = (Button) findViewById(id.button_pesquisar);

        button_biblia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (existeBancoDados) {
                    Intent i = new Intent();
                    i.setClass(MainActivity.this, MainActivityFragment.class);
                    i.putExtra("Biblia", "biblia");
                    startActivity(i);
                } else {

                    checkBanco();

                }


            }
        });

        button_dicionario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (existeBancoDados) {

                    startActivity(new Intent(MainActivity.this, DicionarioActivity.class));

                } else {

                    checkBanco();

                }


            }
        });

        button_pesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (existeBancoDados) {

                    startActivity(new Intent(MainActivity.this,Activity_busca_avancada.class));

                } else {

                    checkBanco();

                }


            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE);
            }
        } else {
            checkBanco();
        }


        // MobileAds.initialize(this, getString(R.string.ADMOB_APP_ID));
        mAdView = findViewById(id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {


            @Override
            public void onAdLoaded() {
                if (mAdView.getVisibility() == View.GONE)
                    mAdView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.

                if (errorCode == AdRequest.ERROR_CODE_NO_FILL) {
                    Log.i("admob", String.valueOf(errorCode));

                    mAdView.setVisibility(View.GONE);

                    Bundle bundle = new Bundle();
                    bundle.putString("ERROR", String.valueOf(errorCode));
                    bundle.putString("COUNTRY", getResources().getConfiguration().locale.getDisplayCountry());
                    mFirebaseAnalytics.logEvent("ADMOB", bundle);
                }


            }


        });
   /*
        if (isServiceRunning())
            Log.i("service", "is running");
        else
            Log.i("service", "not running");

    */

    }

    private void checkBanco() {

        checkBancoExiste = new CheckBancoExiste(getBaseContext());

        try {
            if (checkBancoExiste.checkDataBase() && checkBancoExiste.checarIntegridadeDoBanco()) {

                existeBancoDados = true;
                Log.e("Main: Banco existe", "sim");


            } else {

                new ChegarBanco().execute("");
            }
        } catch (Exception e) {

            Log.e("error: ", e.getCause().toString());


        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //start audio recording or whatever you planned to do
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show an explanation to the user *asynchronously*
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("O aplicativo precisa dessa permissão para criar o banco de dados.")
                            .setTitle("Permissão importante requerida");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        }
                    });
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
                } else {
                    finish();
                }
            }
        }
    }

    public void compartilharVers(View v) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textViewVersDia.getText().toString().concat("\n(Bíblia Adonai)"));
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Compartilhar com"));


    }

    public boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.projeto.biblianvi.ServiceNotification".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private boolean checarAlarmeExiste() {

      /*  boolean alarmUp = (PendingIntent.getBroadcast(MainActivity.this, 121312131,
                new Intent("com.projeto.biblianvi.VersiculoDiario"),
                PendingIntent.FLAG_NO_CREATE) != null);*/
        Intent tempIntent = new Intent(MainActivity.this, VersiculoDiario.class);
        tempIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        boolean alarmUp = (PendingIntent.getBroadcast(MainActivity.this, 121312131, tempIntent, PendingIntent.FLAG_NO_CREATE) != null);


        if(alarmUp)
            Log.e("alarme ","ativado");
       else{
            Log.e("alarme ","desativado");
        }
        return alarmUp;

    }

    private void cancelarAgendarAlarmeVersiculo() {


        Intent intent = new Intent("com.projeto.biblianvi.VersiculoDiario");
        PendingIntent sender = PendingIntent.getBroadcast(this, 121312131, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null)
            alarmManager.cancel(sender);


    }

    private void agendarAlarmeVersiculo(int hora, int min) {

        Intent it = new Intent(this, VersiculoDiario.class);
        PendingIntent p = PendingIntent.getBroadcast(MainActivity.this, 121312131, it, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, hora);
        c.set(Calendar.MINUTE, min);


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, p);



    }

    private void alterarHoraAlarme() {


        SharedPreferences settings = getSharedPreferences("alarme", Activity.MODE_PRIVATE);
        final SharedPreferences.Editor editor = settings.edit();

        String h = settings.getString("hora", "09");
        String m = settings.getString("minuto", "00");

        AlterarAlarm alterarAlarm;


        TextView title = new TextView(this);
        title.setText("Aviso de versículo diário programado para:");
        title.setTextColor(getResources().getColor(color.white));
        title.setPadding(5, 5, 5, 5);
        title.setGravity(View.TEXT_ALIGNMENT_CENTER);
        // title.setTextColor(getResources().getColor(R.color.greenBG));
        title.setTextSize(18);

        final TextView horaText = new TextView(this);
        horaText.setTextColor(getResources().getColor(color.blue));
        // horaText.setBackgroundColor(getResources().getColor(R.color.white));
        horaText.setText(h);
        horaText.setTextSize(22);

        //altura comprimento
        LinearLayout layoutTextHora = new LinearLayout(this);
        layoutTextHora.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutTextHora.setGravity(Gravity.CENTER);
        layoutTextHora.addView(horaText);

        final TextView minText = new TextView(this);
        minText.setTextColor(getResources().getColor(color.blue));
        // horaText.setBackgroundColor(getResources().getColor(R.color.white));
        minText.setText(m);
        minText.setTextSize(22);

        alterarAlarm = new AlterarAlarm(horaText, minText);

        LinearLayout layoutTextMin = new LinearLayout(this);
        layoutTextMin.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutTextMin.setGravity(Gravity.CENTER);
        layoutTextMin.addView(minText);

        Button horaMaisButton = new Button(this);
        horaMaisButton.setTag("horaMaisButton");
        horaMaisButton.setBackgroundResource(mipmap.img_mais);
        horaMaisButton.setLayoutParams(new LinearLayout.LayoutParams(50, 40));
        horaMaisButton.setOnClickListener(alterarAlarm);

        Button horaMenosButton = new Button(this);
        horaMenosButton.setTag("horaMenosButton");
        horaMenosButton.setLayoutParams(new LinearLayout.LayoutParams(50, 40));
        horaMenosButton.setBackgroundResource(mipmap.img_menos);
        horaMenosButton.setOnClickListener(alterarAlarm);

        Button minMaisButton = new Button(this);
        minMaisButton.setTag("minMaisButton");
        minMaisButton.setLayoutParams(new LinearLayout.LayoutParams(50, 40));
        minMaisButton.setBackgroundResource(mipmap.img_mais);
        minMaisButton.setOnClickListener(alterarAlarm);

        Button minMenosButton = new Button(this);
        minMenosButton.setTag("minMenosButton");
        minMenosButton.setLayoutParams(new LinearLayout.LayoutParams(50, 40));
        minMenosButton.setBackgroundResource(mipmap.img_menos);
        minMenosButton.setOnClickListener(alterarAlarm);

        LinearLayout horaLayout = new LinearLayout(this);
        horaLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 15, 0);

        TextView txtHora = new TextView(this);
        txtHora.setText("Hora");
        txtHora.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        txtHora.setTextColor(getResources().getColor(color.white));

        horaLayout.setLayoutParams(params);
        horaLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        horaLayout.addView(txtHora);
        horaLayout.addView(horaMaisButton);
        horaLayout.addView(layoutTextHora);
        horaLayout.addView(horaMenosButton);


        LinearLayout minLayout = new LinearLayout(this);
        minLayout.setOrientation(LinearLayout.VERTICAL);
        minLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        minLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView txtMin = new TextView(this);
        txtMin.setText("Minuto");
        txtMin.setTextColor(getResources().getColor(color.white));
        txtMin.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        minLayout.addView(txtMin);
        minLayout.addView(minMaisButton);
        minLayout.addView(layoutTextMin);
        minLayout.addView(minMenosButton);


        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.HORIZONTAL);
        content.setGravity(Gravity.CENTER);
        content.setBackgroundColor(getResources().getColor(color.dark));
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

                editor.putString("hora", horaText.getText().toString());
                editor.putString("minuto", minText.getText().toString());
                editor.commit();

                int h = Integer.parseInt(horaText.getText().toString());
                int m = Integer.parseInt(minText.getText().toString());

                if (checarAlarmeExiste()) {

                    cancelarAgendarAlarmeVersiculo();

                }

                agendarAlarmeVersiculo(h, m);

                Toast.makeText(MainActivity.this, "Horário redefinido: "
                                + horaText.getText().toString() + ":"
                                + minText.getText().toString() + "h"
                        , Toast.LENGTH_LONG).show();

            }
        });

        alertDialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
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

                SharedPreferences settings;
                settings = getSharedPreferences("versDiaPreference", Activity.MODE_PRIVATE);
                textViewAssuntoVers.setText(settings.getString("assunto", "Paz"));
                textViewAssuntoVers.setMinLines(2);
                textViewVersDia.setText(settings.getString("versDia", "Tenho-vos dito isto, para que em mim tenhais paz; no mundo tereis aflições, mas tende bom ânimo, eu venci o mundo.\n")
                        +" (" + settings.getString("livroNome","João") +" "+ settings.getString("capVersDia","16")+":"
        + settings.getString("verVersDia","33") +")" )  ;


    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


    protected void onStop() {
        super.onStop();

    }

    protected void onStart() {
        super.onStart();


    }

    protected void onPostResume() {
        super.onPostResume();


    }


    protected void onResume() {
        super.onResume();

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

        super.onBackPressed();

        return;


    }


    protected void onPause() {

        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(main, menu);
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case id.action_websearch:
                // create intent to perform web search for this planet
                // Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                // intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
                // catch event that there's no activity to handle intent

                Intent intent1 = new Intent();
                intent1.setClass(getApplication(), Activity_busca_avancada.class);


                if (intent1.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent1);
                } else {
                    Toast.makeText(this, string.app_not_available, Toast.LENGTH_LONG).show();
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
        // update the activity_fragment content by replacing fragments
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

    public void chamarActivity(int posicao) {


        if (existeBancoDados == false) {
            checkBanco();
        } else {

            switch (posicao) {


                case 0:

                    intent = new Intent(MainActivity.this, Activity_favorito.class);
                    startActivity(intent);

                    break;

                case 1:

                    intent = new Intent(MainActivity.this, ActivityAnotacao.class);
                    startActivity(intent);

                    break;
                case 2:

                    intent = new Intent(MainActivity.this, DicionarioActivity.class);
                    startActivity(intent);
                    break;

                case 3:

                    if (isNetworkAvailable()) {
                        intent = new Intent(MainActivity.this, Mensagem.class);
                        startActivity(intent);
                    } else {

                        Toast.makeText(getApplication(), "Sem conexão", Toast.LENGTH_LONG).show();

                    }
                    break;

                case 4:

                    intent = new Intent(MainActivity.this, GraficoGeral.class);
                    startActivity(intent);
                    break;


                case 5:

                    intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    break;

                case 6:

                    intent = new Intent(MainActivity.this, ActivityPoliticaPrivacidade.class);
                    startActivity(intent);

                    break;

                case 7:

                    mostrarAviso();

                    break;

                default:

                    break;

            }

        }
    }


    private void mostrarAviso() {


        TextView title = new TextView(this);
        title.setText("Informação");
        title.setPadding(5, 5, 5, 5);
        title.setGravity(View.TEXT_ALIGNMENT_CENTER);
        // title.setTextColor(getResources().getColor(R.color.greenBG));
        title.setTextSize(18);

        TextView msg = new TextView(this);
        msg.setTextColor(getResources().getColor(color.white));
        msg.setText(string.aviso);
        msg.setPadding(10, 10, 10, 10);
        msg.setGravity(View.TEXT_ALIGNMENT_CENTER);
        msg.setTextSize(18);


        ScrollView scrollView = new ScrollView(getApplicationContext());
        scrollView.setBackgroundColor(getResources().getColor(color.dark));
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
                existeBancoDados = checkBancoExiste.checkDataBase() && checkBancoExiste.checarIntegridadeDoBanco() ? true : false;
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


            if (existeBancoDados) {

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

            if (existeBancoDados == false) {
                checkBanco();
            } else {

                switch (v.getId()) {


                    case id.layout_favorito:

                        intent = new Intent(MainActivity.this, Activity_favorito.class);
                        startActivity(intent);

                        break;

                    case id.layout_anotacao:

                        intent = new Intent(MainActivity.this, ActivityAnotacao.class);
                        startActivity(intent);

                        break;
                    case id.layout_dicionario:

                        intent = new Intent(MainActivity.this, DicionarioActivity.class);
                        startActivity(intent);
                        break;

                    case id.layout_online:

                        intent = new Intent(MainActivity.this, Mensagem.class);
                        startActivity(intent);
                        break;

                    case id.layout_estatistica:

                        intent = new Intent(MainActivity.this, GraficoGeral.class);
                        startActivity(intent);
                        break;

                    case id.layout_informacao:

                        mostrarAviso();

                        break;

                    case id.layout_configuracao:

                        intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        break;

                    default:

                        break;


                }

            }
        }
    }

    private class AlterarAlarm implements View.OnClickListener {


        TextView textViewhora, textViewMin;
        int hora, min;

        public AlterarAlarm(TextView horaView, TextView minView) {

            textViewhora = horaView;
            textViewMin = minView;

            hora = Integer.parseInt(horaView.getText().toString());
            min = Integer.parseInt(minView.getText().toString());

        }

        @Override
        public void onClick(View v) {


            if (v.getTag().toString().equals("horaMaisButton")) {

                if (hora <= 22) {
                    ++hora;
                    setHora(hora);
                } else {
                    hora = 0;
                    setHora(hora);
                }

            } else if (v.getTag().toString().equals("horaMenosButton")) {


                if (hora >= 1) {
                    --hora;
                    setHora(hora);
                } else {
                    hora = 23;
                    setHora(hora);
                }
            } else if (v.getTag().toString().equals("minMaisButton")) {

                if (min <= 58) {
                    ++min;
                    setMin(min);
                } else {
                    min = 0;
                    setMin(min);
                }
            } else if (v.getTag().toString().equals("minMenosButton")) {

                if (min >= 1) {
                    --min;
                    setMin(min);
                } else {

                    min = 59;
                    setMin(min);
                }

            }

        }

        private void setHora(int h) {

            if (h < 10)
                textViewhora.setText("0" + Integer.toString(h));
            else
                textViewhora.setText(Integer.toString(h));

        }

        private void setMin(int m) {


            if (m < 10)
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
            View rootView = inflater.inflate(layout.fragment_planet, container, false);
            int i = getArguments().getInt(ARG_PLANET_NUMBER);
            String planet = getResources().getStringArray(array.menu_array)[i];

            int imageId = getResources().getIdentifier(planet.toLowerCase(Locale.getDefault()),
                    "drawable", getActivity().getPackageName());
            ((ImageView) rootView.findViewById(id.image)).setImageResource(imageId);
            getActivity().setTitle(planet);
            return rootView;
        }
    }


    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }


}
