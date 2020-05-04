package com.projeto.biblianvi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.BuildConfig;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Lista_Biblia extends Activity {

    private InterstitialAd  interstitialAd;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] menuTitulos;
    private BibliaBancoDadosHelper bibliaHelp;
    private List<Biblia> lista = null;
    private TextView textViewCap;
    private TextView textViewLivro;
    private ListView listView;
    private String[] newString;
    private boolean buscar;
    private String buscarTestamento;
    private String termos;
    private Button buttonMenuOpcao;
    private ListaAdaptador listaAdaptador;
    private View buttonCompartilhar;
    private PesquisarBanco pesquisarBanco;
    private boolean recarregarLista = false;
    private boolean criarMenuSuspenso = true, criarMenuBase = true;
    private Spinner spinnerLivro;
    private Spinner spinnerCap;
    private Spinner spinnerVers;
    private Button buttonChamarLivro;
    private Button buttonBuscaList;
    private Button buttonSetaMenu;
    private Button buttonRetroceder;
    private Button buttonAvancar;
    private Button buttonFullScreen;
    private EditText editTextPesquisarList;
    private Intent intent;
    private boolean modoNoturno;
    private LinearLayout linearLayoutLivCap;
    private LinearLayout linearLayoutShareLike;
    private TextView textViewComp;
    private Button buttonNota;
    private Button buttonSound;
    private SharedPreferences sharedPrefs;
    private AudioManager amanager;
    private boolean keepScreenOn = false;
    final static private String SEEK_VALOR_KEY = "seekValor";
    private PopupWindow  pw;
    private FirebaseAnalytics  mFirebaseAnalytics;
    private int REQUEST_STORAGE;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);


        if (getSharedPreferences("fullscreen", Activity.MODE_PRIVATE).getBoolean("fullscreen", false)) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        }


        setContentView(R.layout.activity_list_view);
        bibliaHelp = new BibliaBancoDadosHelper(getApplicationContext());

        textViewComp = findViewById(R.id.textViewComp);
        textViewCap = findViewById(R.id.textViewCapit);
        textViewLivro = findViewById(R.id.textViewLivro);
        buttonMenuOpcao = findViewById(R.id.buttonMenuOpcao);
        listView = findViewById(R.id.listView);
        buttonCompartilhar = findViewById(R.id.buttonMenuShare);

        buttonSetaMenu = findViewById(R.id.buttonSetaMenu);
        linearLayoutLivCap = findViewById(R.id.linearLayoutLivCap2);
        linearLayoutShareLike = findViewById(R.id.linearLayoutShareLike);
        buttonRetroceder = findViewById(R.id.buttonRetroceder);
        buttonAvancar = findViewById(R.id.buttonAvancar);
        buttonFullScreen = findViewById(R.id.buttonFullScreen);
        buttonNota = findViewById(R.id.buttonNota);
        buttonSound = findViewById(R.id.buttonSound);
        progressBar = findViewById(R.id.progressBarBibliaActivity);
        progressBar.setProgress(0);
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);
        AvancarCap av = new AvancarCap();
        buttonAvancar.setOnClickListener(av);
        buttonRetroceder.setOnClickListener(av);

        newString = new String[6];

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            newString[0] = extras.getString("livro");
            newString[1] = extras.getString("capitulo");
            newString[2] = extras.getString("versiculo");
            newString[3] = extras.getString("buscar");
            newString[4] = extras.getString("termoBusca");
            newString[5] = extras.getString("buscarTestamento");
            buscar = Boolean.parseBoolean(newString[3]);
            buscarTestamento = newString[5];

            if (buscar) {
                LinearLayout lin = findViewById(R.id.linearLayoutLivCap);
                lin.setVisibility(View.GONE);
                try {
                    Class.forName("android.os.AsyncTask");
                    pesquisarBanco = new PesquisarBanco(getParent());
                    pesquisarBanco.execute("");
                } catch (Throwable ignore) {

                    Log.e("error: ",ignore.getCause().toString());

                }


            } else {

                carregarLista();

            }
        } else {

            finish();
            Toast.makeText(getBaseContext(),
                    "Um erro ocorreu. Por favor comunique o BUG desenvolverdor", Toast.LENGTH_LONG).show();
        }

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                callPopup(i);

                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Biblia b = (Biblia) parent.getItemAtPosition(position);

                if(!criarMenuSuspenso)
                    menuSuspenso();

                if(!criarMenuBase)
                    menuListBase();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        setProgressBar(b.getIdBook());
                    }
                }).start();


            }
        });

        buttonMenuOpcao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuSuspenso();

            }
        });

        buttonCompartilhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                BibliaBancoDadosHelper bancoDadosHelper = new BibliaBancoDadosHelper(Lista_Biblia.this);

                if (bancoDadosHelper.getVersCompartilhar().length() > 0) {

                    compartilharRedeSocial(bancoDadosHelper.getVersCompartilhar().append(" -Bíblia Adonai-"));
                    bancoDadosHelper.setVersLimparCompartilhar();

                    textViewComp.setText(Integer.toString(bancoDadosHelper.getQuantCompartilhar()));

                } else {

                    Toast.makeText(getBaseContext(), R.string.versiculo_selecionado_share, Toast.LENGTH_LONG).show();

                }
            }
        });

        buttonSetaMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuListBase();
            }

        });

        buttonFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SharedPreferences sp = getSharedPreferences("fullscreen", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                //Se a Activity estiver em modo FullScreen a próxima Activity será chamada com tela padrão
                // e vice-versa
                if (sp.getBoolean("fullscreen", false)) {

                    editor.putBoolean("fullscreen", false);

                } else {

                    editor.putBoolean("fullscreen", true);

                }


                editor.commit();


                intent = new Intent(Lista_Biblia.this, Lista_Biblia.class);

                Biblia bi;
                bi = (Biblia) listView.getItemAtPosition(listView.getFirstVisiblePosition());

                intent.putExtra("livro", bi.getBooksName());
                intent.putExtra("capitulo", bi.getChapter());
                intent.putExtra("versiculo", bi.getVersesNum());
                intent.putExtra("termoBusca", "nada");

                startActivity(intent);

                finish();


            }
        });


        buttonNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(Lista_Biblia.this, ActivityAnotacao.class);

                startActivity(it);

            }
        });


        boolean visivel = true;
        linearLayoutLivCap.setOnClickListener(new LayoutTopo(visivel));
        amanager = (AudioManager) getSystemService(AUDIO_SERVICE);

        buttonSound.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                requestPermission();
                boolean on = getSharedPreferences("sound", Activity.MODE_PRIVATE).getBoolean("sound", false);

                if (!on) {

                    amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
                    getSharedPreferences("sound", Activity.MODE_PRIVATE).edit().putBoolean("sound", true).commit();
                    buttonSound.setBackgroundResource(R.mipmap.sound_off);
                    Toast.makeText(Lista_Biblia.this, "Som de notificações desativado", Toast.LENGTH_LONG).show();

                } else {

                    amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
                    getSharedPreferences("sound", Activity.MODE_PRIVATE).edit().putBoolean("sound", false).commit();
                    buttonSound.setBackgroundResource(R.mipmap.sound_on);
                    Toast.makeText(Lista_Biblia.this, "Som de notificações ativado", Toast.LENGTH_LONG).show();

                }
            }
        });

        mTitle = mDrawerTitle = getTitle();
        menuTitulos = getResources().getStringArray(R.array.menu_array);
        mDrawerLayout = findViewById(R.id.drawer_layout_list);
        mDrawerList = findViewById(R.id.left_drawer_list);

        // set a custom shadow that overlays the activity_fragment content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, menuTitulos));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        if(getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);
        if(getActionBar() != null)
            getActionBar().setHomeButtonEnabled(true);

        @SuppressLint("ResourceType") Toolbar toolbar = findViewById(R.menu.main);
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Objects.requireNonNull(getActionBar()).setTitle(mTitle);
                }else{
                    getActionBar().setTitle(mTitle);

                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Objects.requireNonNull(getActionBar()).setTitle(mDrawerTitle);
                }else{
                    getActionBar().setTitle(mDrawerTitle);
                }
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        //  mDrawerLayout.setDrawerListener(mDrawerToggle);


        // Create the InterstitialAd and set the adUnitId.
        interstitialAd = new InterstitialAd(this);
        // Defined in res/values/strings.xml
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

                Log.i("Intertitial",String.valueOf(errorCode));

                Bundle bundle = new Bundle();
                bundle.putString("ERROR",String.valueOf(errorCode));
                bundle.putString("COUNTRY",getResources().getConfiguration().locale.getDisplayCountry());
                mFirebaseAnalytics.logEvent("Intertitial",bundle);


            }

        });


        modoNoturno();


    }

    private void setProgressBar(int idBook) {
        BibliaBancoDadosHelper db = new BibliaBancoDadosHelper(getApplicationContext());
        Biblia b = db.getSumVersosReadByBooks(idBook);
        int totalVersiculos = b.getTotalDeVersiculos();
        int totalDeLidos = b.getTotalDeVersosLidos();
        int value = (totalDeLidos * 100) / totalVersiculos;

        progressBar.setProgress(value);

    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(Lista_Biblia.this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Lista_Biblia.this, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, REQUEST_STORAGE);
            }
        }
    }

    private void callPopup(int i) {

        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.layout_popup,null);

        pw = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT,  ViewGroup.LayoutParams.WRAP_CONTENT, true);

        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);

        pw.setOutsideTouchable(true);
        pw.setTouchable(true);

        Button favo = layout.findViewById(R.id.buttonPopFavorito);
        Button com = layout.findViewById(R.id.buttonPopCompartilhar);
        Button clo = layout.findViewById(R.id.buttonPopClose);


        final Biblia bi = (Biblia) listView.getAdapter().getItem(i);


        favo.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

           BibliaBancoDadosHelper h = new BibliaBancoDadosHelper(Lista_Biblia.this);

           if(h.tabelaExiste(Activity_favorito.TABELANAME) != 0){


               h.setFavorito(bi.getIdVerse());


           } else{

               h.criarTabela(Activity_favorito.TABELANAME,Activity_favorito.CAMPOS);

               h.setFavorito(bi.getIdVerse());

           }

             pw.dismiss();

             Toast.makeText(getBaseContext(), "Favorito: " + bi.getBooksName() + " " + bi.getChapter() + ":" + bi.getVersesNum(), Toast.LENGTH_LONG).show();

         }
       });

        com.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {


             Toast.makeText(getBaseContext(), getString(R.string.versiculos_selecionados) + bi.getBooksName() + " " + bi.getChapter() + ":" + bi.getVersesNum(), Toast.LENGTH_LONG).show();

             new BibliaBancoDadosHelper(getApplicationContext()).setVersCompartilhar(bi);

             textViewComp.setText(Integer.toString(new BibliaBancoDadosHelper(Lista_Biblia.this).getQuantCompartilhar()));

             pw.dismiss();
         }
       });

        clo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                  pw.dismiss();
                                }
                            });
    }


    private void menuSuspenso() {

        LayoutInflater layoutInflater =
                (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View addView = layoutInflater.inflate(R.layout.menu_opcao_topo_list, null);

        LinearLayout myLayoutBusca = findViewById(R.id.linearLayoutBusca);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) myLayoutBusca.getLayoutParams();

        myLayoutBusca.addView(addView);

        if (criarMenuSuspenso) {

            bibliaHelp = new BibliaBancoDadosHelper(this);

            params.height = LinearLayout.MarginLayoutParams.WRAP_CONTENT;

            myLayoutBusca.setLayoutParams(params);

            criarMenuSuspenso = false;

            spinnerLivro = findViewById(R.id.spinner4);
            spinnerCap = findViewById(R.id.spinner5);
            spinnerVers = findViewById(R.id.spinner6);
            buttonChamarLivro = findViewById(R.id.buttonChamarLivro);
            buttonBuscaList = findViewById(R.id.buttonBuscarList);
            editTextPesquisarList = findViewById(R.id.editTextPesquisarList);

            editTextPesquisarList.setHint(R.string.digite_palavra);

            BibliaBancoDadosHelper bibliaHelp = new BibliaBancoDadosHelper(getApplicationContext());
            List<Biblia> bookNameList = bibliaHelp.getAllBooksName();
            String[] livro = new String[bookNameList.size()];

            for(int i = 0 ;i <= bookNameList.size()-1; i++){
                livro[i] = bookNameList.get(i).getBooksName();
            }
            ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,livro);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            spinnerLivro.setAdapter(aa);

            spinnerLivro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    carregarSpinnerCapitulo(spinnerLivro.getSelectedItem().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            spinnerCap.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    carregarSpinnerVersiculo(spinnerLivro.getSelectedItem().toString(), spinnerCap.getSelectedItem().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            editTextPesquisarList.setOnEditorActionListener(new TextView.OnEditorActionListener() {


                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if ((actionId == EditorInfo.IME_ACTION_DONE) ||
                            (actionId == EditorInfo.IME_ACTION_NEXT) ||
                            (actionId == EditorInfo.IME_ACTION_GO)) {

                        pesquisar();
                    }

                    return false;
                }
            });

            buttonChamarLivro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    intent = new Intent(getApplicationContext(), Lista_Biblia.class);

                    intent.putExtra("livro", spinnerLivro.getSelectedItem().toString());
                    intent.putExtra("capitulo", spinnerCap.getSelectedItem().toString());
                    intent.putExtra("versiculo", spinnerVers.getSelectedItem().toString());
                    intent.putExtra("termoBusca", "nada");


                    startActivity(intent);

                }
            });

            buttonBuscaList.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View view) {
                                                       pesquisar();
                                                   }
                                               }
            );

        } else {

            if (addView != null) {
                myLayoutBusca.removeAllViews();
            }

            criarMenuSuspenso = true;
        }


    }

    private void pesquisar() {

        intent = new Intent(getApplicationContext(), Lista_Biblia.class);

        intent.putExtra("livro", "0");
        intent.putExtra("capitulo", "0");
        intent.putExtra("versiculo", "0");
        intent.putExtra("buscar", "true");
        intent.putExtra("buscarTestamento", "0");
        intent.putExtra("termoBusca", editTextPesquisarList.getText().toString());

        //salva o termo da busca para ser usado por Biblia para realçar a cor da palavra
        SharedPreferences settings = getSharedPreferences("termo_busca", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("busca",editTextPesquisarList.getText().toString());
        editor.commit();


                if (editTextPesquisarList.getText().length() >= 2)
                    startActivity(intent);
                else
                    editTextPesquisarList.setHint(getString(R.string.digite_palavra));


    }


    private void inicializarSeekbar() {

        if (modoNoturno) {

            SeekBar seekBarBrilho = findViewById(R.id.seekBarBrilho);
            SharedPreferences settings = getSharedPreferences("seekbar", Activity.MODE_PRIVATE);
            seekBarBrilho.setMax(BRIGHTNESS_FULL);
            seekBarBrilho.setVisibility(View.VISIBLE);
            // seekBarBrilho.setKeyProgressIncrement(25);
            seekBarBrilho.setOnSeekBarChangeListener(new OnSeekBar());
            seekBarBrilho.setProgress(settings.getInt(SEEK_VALOR_KEY, BRIGHTNESS_FULL / 2));

        }

    }

    private void menuListBase() {

        LayoutInflater layoutInflater =
                (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View addView = layoutInflater.inflate(R.layout.menu_opcao_base_list, null);

        LinearLayout myLayoutBusca = findViewById(R.id.linearLayoutListBase);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) myLayoutBusca.getLayoutParams();

        myLayoutBusca.addView(addView);



        if (criarMenuBase) {

            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            myLayoutBusca.setLayoutParams(params);

            inicializarSeekbar();

            Button buttonNoticia = findViewById(R.id.buttonViewGrafMain);
            buttonNoticia.setOnClickListener(new ButtonAction());
            Button buttonViewFeeMain = findViewById(R.id.buttonViewSermon);
            buttonViewFeeMain.setOnClickListener(new ButtonAction());
            Button buttonViewConfMain = findViewById(R.id.buttonViewConfMain);
            buttonViewConfMain.setOnClickListener(new ButtonAction());
            Button buttonViewFavorito = findViewById(R.id.buttonViewFavorito);
            buttonViewFavorito.setOnClickListener(new ButtonAction());
            Button buttonViewLupaMain = findViewById(R.id.buttonViewLupaMain);
            buttonViewLupaMain.setOnClickListener(new ButtonAction());

            criarMenuBase = false;
            buttonSetaMenu.setBackgroundResource(R.mipmap.seta_menu_alto);

        } else {

            if (addView != null) {
                myLayoutBusca.removeAllViews();
            }

            criarMenuBase = true;
            buttonSetaMenu.setBackgroundResource(R.mipmap.seta_menu_baixo);

        }


    }


    private void carregarSpinnerVersiculo(String liv, String cap) {

        int versiculos;

        bibliaHelp = new BibliaBancoDadosHelper(this);


        versiculos = bibliaHelp.getQuantidadeVersos(liv, cap);


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

    private void carregarSpinnerCapitulo(String livro) {

        int capitulos;

        bibliaHelp = new BibliaBancoDadosHelper(this);


        capitulos = bibliaHelp.getQuantidadeCapitulos(livro);


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

    private void onListPosicao(ListView l) {

        l.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                int firstVisibleRow = listView.getFirstVisiblePosition();
                int lastVisibleRow = listView.getLastVisiblePosition();
                int ii;


                Biblia bi = (Biblia) listView.getItemAtPosition(listView.getFirstVisiblePosition());

                textViewCap.setText(bi.getChapter());

                if (bi.getBooksName().equals("Lamentações de Jeremias")) {

                    textViewLivro.setText("Lamentações");

                } else {

                    textViewLivro.setText(bi.getBooksName());
                }


              /*  for (ii = firstVisibleRow; ii <= lastVisibleRow; ii++) {

                    Biblia bi = (Biblia) listView.getItemAtPosition(ii);

                    textViewCap.setText(bi.getVersesChapter());

                    if (bi.getBooksName().equals("Lamentações de Jeremias")) {

                        textViewLivro.setText("Lamentações");

                    } else {

                        textViewLivro.setText(bi.getBooksName());
                    }


                }*/

            }
        });

    }

    private void carregarLista() {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {

                    lista = bibliaHelp.getBook(newString[0]);

                    if (!lista.isEmpty()) {

                        listaAdaptador = new ListaAdaptador(getApplicationContext(), lista, false);

                        listView.setAdapter(listaAdaptador);

                        bibliaHelp = null;

                        onListPosicao(listView);

                        correntePosicao();

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }

    private void modoNoturno() {

        modoNoturno = sharedPrefs.getBoolean("noturnoPref", true);

        LinearLayout linearLayoutShareLike = findViewById(R.id.linearLayoutShareLike);
        LinearLayout linearLayoutLivCap = findViewById(R.id.linearLayoutLivCap);
        TextView textLivro = findViewById(R.id.textViewLivro);
        TextView textCap = findViewById(R.id.textViewCapit);


        if (modoNoturno) {

            linearLayoutShareLike.setBackgroundColor(getResources().getColor(R.color.barrasuperiorescuro));
            linearLayoutLivCap.setBackgroundColor(getResources().getColor(R.color.barrainferiorescuro));
            //   buttonRetroceder.setBackgroundResource(R.drawable.barra_livro_escuro);
            //  buttonAvancar.setBackgroundResource(R.drawable.barra_livro_escuro);
            //  textLivro.setBackgroundResource(R.drawable.barra_livro_escuro);
            //  textCap.setBackgroundResource(R.drawable.barra_livro_escuro);
            textLivro.setTextColor(Color.rgb(192, 192, 192));
            textCap.setTextColor(Color.rgb(192, 192, 192));

            SharedPreferences settings = getSharedPreferences("seekbar", Activity.MODE_PRIVATE);

            alterarBrilhoTela(settings.getInt(SEEK_VALOR_KEY, BRIGHTNESS_FULL));


        } else {

            linearLayoutShareLike.setBackgroundColor(getResources().getColor(R.color.barrasuperior));
            linearLayoutLivCap.setBackgroundColor(getResources().getColor(R.color.barrainferior));
            // buttonRetroceder.setBackgroundResource(R.drawable.barra_livro);
            //  buttonAvancar.setBackgroundResource(R.drawable.barra_livro);
            //  textLivro.setBackgroundResource(R.drawable.barra_livro);
            //  textCap.setBackgroundResource(R.drawable.barra_livro);
            textLivro.setTextColor(Color.rgb(0, 0, 0));
            textCap.setTextColor(Color.rgb(0, 0, 0));

            alterarBrilhoTela(getScreenBrightness(getApplicationContext()));

        }

        if (listaAdaptador != null)
            listaAdaptador.notifyDataSetChanged();

    }

    protected void onStart() {
        super.onStart();

        startIntertitialAd();



    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();
        } else {
            Log.i("Intertitial","Ad did not load");
            startIntertitialAd();
        }
    }

    private void startIntertitialAd() {
        // Request a new ad if one isn't already loaded, hide the button, and kick off the timer.
        if (!interstitialAd.isLoading() && !interstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            interstitialAd.loadAd(adRequest);
        }

    }

    private void compartilharRedeSocial(StringBuffer stringBuffer) {


        LinearLayout layout = new LinearLayout(Lista_Biblia.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        ScrollView scrollView = new ScrollView(Lista_Biblia.this);

        TextView textVers = new TextView(getBaseContext());
        textVers.setSelected(false);
        textVers.setTextColor(Color.BLACK);
        textVers.setText(stringBuffer.toString());
        textVers.setTextSize(16);

        EditText input = new EditText(getBaseContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setSingleLine(false);
        input.setTextColor(Color.BLACK);

        TextView textM = new TextView(getBaseContext());
        textM.setSelected(false);
        textM.setTextColor(Color.BLACK);
        textM.setText(R.string.escrever_mensagem_compartilhar);
        textM.setTextSize(16);

        layout.addView(textVers);
        layout.addView(textM);
        layout.addView(input);

        AlertDialog.Builder builder = new AlertDialog.Builder(Lista_Biblia.this);
        builder.setTitle(R.string.compartilhar);


        scrollView.addView(layout);
        scrollView.setBackgroundColor(getResources().getColor(R.color.white));
        builder.setView(scrollView);

        builder.setPositiveButton(R.string.prosseguir, new CompartilharVerso(stringBuffer, input));

        builder.setNegativeButton(getText(R.string.cancelar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
                new BibliaBancoDadosHelper(Lista_Biblia.this).setVersLimparCompartilhar();
            }
        });

        builder.show();


    }

    protected void onPostResume() {
        super.onPostResume();

    }

    public void onBackPressed() {

            super.onBackPressed();
            amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
        getSharedPreferences("sound", Activity.MODE_PRIVATE).edit().putBoolean("sound", false).commit();
            return;

    }

    public void onPause(){
        super.onPause();
        SharedPreferences settings = getSharedPreferences("seekbar", Activity.MODE_PRIVATE);
        alterarBrilhoTela(settings.getInt("brilhoAtual", BRIGHTNESS_FULL));
    }

    public void onStop() {
        super.onStop();
        amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
        getSharedPreferences("sound", Activity.MODE_PRIVATE).edit().putBoolean("sound", false).commit();
        buttonSound.setBackgroundResource(R.mipmap.sound_on);
        showInterstitial();
    }

    protected void onResume() {
        super.onResume();

        try {
            Biblia b = (Biblia) listView.getItemAtPosition(0);
            setProgressBar(b.getIdBook());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        textViewComp.setText(Integer.toString(new BibliaBancoDadosHelper(Lista_Biblia.this).getQuantCompartilhar()));

        modoNoturno();

        //SharedPreferences sp = getSharedPreferences("telaPref", Activity.MODE_PRIVATE);

        keepScreenOn = sharedPrefs.getBoolean("telaPref", false);

        if (keepScreenOn)
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        requestNewInterstitial();
        SharedPreferences sp = getSharedPreferences("altPref", Activity.MODE_PRIVATE);

        if(sp.getBoolean("alteracao", false)) {

            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("alteracao", false);
            editor.commit();

            Intent in = getIntent();
            finish();
            startActivity(in);

        }
    }

    protected void onDestroy() {
        super.onDestroy();

        amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
        getSharedPreferences("sound", Activity.MODE_PRIVATE).edit().putBoolean("sound", false).commit();

        if (bibliaHelp != null) {
            bibliaHelp.close();
        }


    }

    private void correntePosicao() {

        int totalItem = listView.getCount();

        int i;

        if (totalItem > 0)
            for (i = 0; i <= totalItem; i++) {

                Biblia bi = (Biblia) listView.getItemAtPosition(i);

                if (bi.getChapter().equals(newString[1]) && bi.getVersesNum().equals(newString[2])) {

                    textViewCap.setText(bi.getChapter());
                    textViewLivro.setText(bi.getBooksName());
                    listView.setSelection(i);
                }


            }

    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_MENU) {

            menuListBase();

            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
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

    public void chamarActivity(int posicao){


        switch (posicao){


            case 0:

                intent = new Intent(getApplication(),Activity_favorito.class);
                startActivity(intent);

                break;

            case 1:

                intent = new Intent(getApplication(),ActivityAnotacao.class);
                startActivity(intent);

                break;
            case 2:
                MainActivity.opcaoDicionario(getApplicationContext());
                break;

            case 3:

                if(isNetworkAvailable()) {
                    intent = new Intent(getApplication(), Sermoes.class);
                    startActivity(intent);
                }else{

                    Toast.makeText(getApplication(), getText(R.string.sem_conexao), Toast.LENGTH_LONG).show();

                }
                break;

            case 4:

                intent = new Intent(getApplication(),GraficoGeral.class);
                startActivity(intent);
                break;


            case 5:

                intent = new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(intent);
                break;

            case 6:
                intent = new Intent(Lista_Biblia.this, ActivityPoliticaPrivacidade.class);
                startActivity(intent);
                break;
            case 7:
                mostrarAviso();
                break;
            default:
                break;



        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private void requestNewInterstitial() {




    }

    private void mostrarAviso() {


        TextView title = new TextView(this);
        title.setText("Informação");
        title.setPadding(5, 5, 5, 5);
        title.setGravity(View.TEXT_ALIGNMENT_CENTER);
        // title.setTextColor(getResources().getColor(R.color.greenBG));
        title.setTextSize(18);

        TextView msg = new TextView(this);
        msg.setTextColor(getResources().getColor(R.color.white));
        String t = getString(R.string.aviso);
        t = t.replace("@app_version@", BuildConfig.VERSION_NAME).replace("@bible_version@",
                new BibliaBancoDadosHelper(getApplicationContext()).getBibleVersion());
        msg.setText(t);
        msg.setPadding(10, 10, 10, 10);
        msg.setGravity(View.TEXT_ALIGNMENT_CENTER);
        msg.setTextSize(18);

        ScrollView scrollView = new ScrollView(getApplicationContext());
        scrollView.setBackgroundColor(getResources().getColor(R.color.dark));
        scrollView.addView(msg);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                Lista_Biblia.this);

        alertDialogBuilder.setView(scrollView);
        alertDialogBuilder.setCustomTitle(title);

        // set dialog message
        alertDialogBuilder.setPositiveButton(getText(R.string.fechar_about), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();

            }
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    // Get the screen current brightness
    static public int getScreenBrightness(Context context) {

        float brightnessValue = Settings.System.getInt(
                context.getApplicationContext().getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS,
                0
        );

        return (int) brightnessValue;
    }

    private void alterarBrilhoTela(int i) {

        float v = 0.0f;

        WindowManager.LayoutParams layout = getWindow().getAttributes();

        if (i <= 100) {
            v = i / 100.0f;
        } else {
            v = i / 255.0f;
        }

        if ((v > 0f) && (v < 1f)) {
            layout.screenBrightness = v;
        } else {
            layout.screenBrightness = 0.5f;
        }

        getWindow().setAttributes(layout);

    }

    private class LayoutTopo implements View.OnClickListener {

        private boolean visivel = false;

        public LayoutTopo(boolean visivel) {
            this.visivel = visivel;
        }

        @Override
        public void onClick(View v) {

            if (visivel) {
                linearLayoutShareLike.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
                // linearLayoutShareLike.setVisibility(View.GONE);
                visivel = false;
            } else {
                linearLayoutShareLike.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                // linearLayoutShareLike.setVisibility(View.VISIBLE);
                visivel = true;
            }
        }
    }

    public class CompartilharVerso implements DialogInterface.OnClickListener {

        private StringBuffer stringBufferVersos;
        private EditText entradaTexto;


        public CompartilharVerso(StringBuffer buffer, EditText editText) {

            stringBufferVersos = buffer;
            entradaTexto = editText;

        }


        private void escolherRedeSocial(String texto) {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, texto);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Compartilhar com"));


        }


        @Override
        public void onClick(DialogInterface dialogInterface, int i) {


            if (!entradaTexto.toString().isEmpty())
                stringBufferVersos.append("\n\n ");

            stringBufferVersos.append(entradaTexto.getText().toString());

            escolherRedeSocial(stringBufferVersos.toString());

        }
    }


     /* tipoDeBusca
     0 = Tota biblia,  1 = NT,   2 = VT,  3 = Livro */

    private class PesquisarBanco extends AsyncTask<String, Integer, String> {


        private ProgressDialog progressDialog;
        private Activity act;


        public PesquisarBanco(Activity ac) {

            act = ac;
        }


        protected String doInBackground(String... params) {


            if (buscarTestamento.equals("0")) {

                lista = bibliaHelp.pesquisarBiblia(newString[4]);

            } else if (buscarTestamento.equals("1") || buscarTestamento.equals("2")) {

                lista = bibliaHelp.pesquisarBibliaTestamento(newString[4], buscarTestamento);

            } else if (buscarTestamento.equals("3")) {

                lista = bibliaHelp.pesquisarBibliaLivro(newString[0], newString[4]);

            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {


            if (!lista.isEmpty()) {

                int i = lista.size();

                listaAdaptador = new ListaAdaptador(getBaseContext(), lista, true);

                listView.setAdapter(listaAdaptador);

                onListPosicao(listView);

                Toast.makeText(getBaseContext(), i + getString(R.string.foram_encontrados), Toast.LENGTH_LONG).show();

            } else {

                Toast.makeText(getBaseContext(), R.string.nada_encontrado, Toast.LENGTH_LONG).show();
                finish();
            }

            if (recarregarLista) {

                listaAdaptador.notifyDataSetChanged();

            }

            progressDialog.dismiss();


        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(Lista_Biblia.this);
            progressDialog.setTitle(getString(R.string.pesquisando));
            progressDialog.setMessage(getString(R.string.aguarde_pesquisando));
            progressDialog.setCancelable(true);
            // progressDialog.setMax(100);
            // progressDialog.setIndeterminate(true);

            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {

                    Toast.makeText(Lista_Biblia.this, R.string.pesquisa_cancelada, Toast.LENGTH_LONG).show();
                    finish();
                    cancel(true);


                }
            });


            progressDialog.show();


        }

        protected void onProgressUpdate(Integer... values) {


        }





    }

    final static int BRIGHTNESS_FULL = 255;
    private class OnSeekBar implements SeekBar.OnSeekBarChangeListener {


        private int valor = 0;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            if (progress >= 1 && progress <= BRIGHTNESS_FULL) {
                alterarBrilhoTela(progress);

            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {


        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            SharedPreferences settings = getSharedPreferences("seekbar", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();

            if (seekBar.getProgress() >= 1 && seekBar.getProgress() <= BRIGHTNESS_FULL) {
                editor.putInt(SEEK_VALOR_KEY, seekBar.getProgress());
                editor.commit();
                Log.e("seekbar", Integer.toString(seekBar.getProgress()));

            }


        }

    }

    private class AvancarCap implements View.OnClickListener {

            int capAtual;
            int capUltimo;


            @Override
            public void onClick(View v) {

                int total = listView.getCount();

                //Primeiro item visivel na tela
                Biblia bi1 = (Biblia) listView.getItemAtPosition(listView.getFirstVisiblePosition());

                //ùltimo item da lista
                Biblia bi2 = (Biblia) listView.getItemAtPosition(total - 1);

                capAtual = Integer.parseInt(bi1.getChapter());
                capUltimo = Integer.parseInt(bi2.getChapter());

                Biblia bi3;

                if ((v.getId() == buttonAvancar.getId()) && capAtual < capUltimo) {
                    capAtual++;

                    for (int k = listView.getFirstVisiblePosition(); k <= total - 1; k++) {

                        bi3 = (Biblia) listView.getItemAtPosition(k);

                        if ((bi3.getChapter().equals(Integer.toString(capAtual))) && bi3.getVersesNum().equals("1"))
                            listView.setSelection(k);
                    }

                }

                if ((v.getId() == buttonRetroceder.getId()) && capAtual > 1) {
                    capAtual--;

                    for (int k = listView.getFirstVisiblePosition(); k >= 0; k--) {

                        bi3 = (Biblia) listView.getItemAtPosition(k);

                        if ((bi3.getChapter().equals(Integer.toString(capAtual))) && bi3.getVersesNum().equals("1"))
                            listView.setSelection(k);

                    }

                }

            }
        }

    private class ButtonAction implements Button.OnClickListener {

        @Override
        public void onClick(View v) {

            Intent intent;

            switch (v.getId()) {

                case R.id.buttonViewLupaMain:
                    intent = new Intent(getApplicationContext(), Activity_busca_avancada.class);
                    startActivity(intent);
                    break;
                case R.id.buttonViewConfMain:
                    finish();
                    intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(intent);
                    break;
                case R.id.buttonViewSermon:
                    intent = new Intent(getApplication(), Sermoes.class);
                    startActivity(intent);
                    break;
                case R.id.buttonViewGrafMain:
                    intent = new Intent(getApplicationContext(), GraficoGeral.class);
                    startActivity(intent);
                    break;
                case R.id.buttonViewFavorito:
                    intent = new Intent(getApplicationContext(), Activity_favorito.class);
                    startActivity(intent);
                    break;

            }
            menuListBase();

        }
    }


}

