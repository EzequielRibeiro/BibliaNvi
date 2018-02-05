package com.projeto.biblianvi.biblianvi;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ezequiel on 09/07/2015.
 */
public class ListaAdaptador extends BaseAdapter {


    private Context context;
    private LayoutInflater mInflater;
    private List<Biblia> itensList;
    private Biblia currentListData;
    private BibliaBancoDadosHelper bancoHelper;
    private Intent intent;
    private final int LIDO = 1;

    private ItemSuporteBiblia itemSuporteBiblia;
    private boolean mostrarVersiculosLidos;
    private SharedPreferences sharedPrefs;
    private boolean modoNoturno;
    private boolean pesquiar = false;
    private String fonte = "Arial";


    public ListaAdaptador(Context cont,List<Biblia> itens, boolean b) {

        context = cont;
        mInflater = LayoutInflater.from(this.context);
        itensList = itens;
        pesquiar = b;

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(cont);

        SharedPreferences sp = context.getSharedPreferences("versiculo", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("texto_biblico","vazio");
        editor.commit();


        fonte = sharedPrefs.getString("fonteEstilo", "Arial");

    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        mostrarVersiculosLidos = sharedPrefs.getBoolean("mostrarVersiculosLidos", true);
        modoNoturno = sharedPrefs.getBoolean("noturnoPref", false);


        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_biblia_aberta, null);
            itemSuporteBiblia = new ItemSuporteBiblia(convertView);
            convertView.setTag(itemSuporteBiblia);

        }else{

            itemSuporteBiblia = (ItemSuporteBiblia) convertView.getTag();

        }


        currentListData = getItem(position);

        //altera o tamanho da fonte
        tamanhoFonte(itemSuporteBiblia);

        //Altera a cor de fundo e do texto. Altera tamanho da fonte
        if(modoNoturno) {
            modoNoturno(itemSuporteBiblia,currentListData);
        }else{

            if((currentListData.getLido() == 1) && mostrarVersiculosLidos)
                itemSuporteBiblia.textoAberto.setBackgroundColor(context.getResources().getColor(R.color.cinzaclaro));
            else {
                itemSuporteBiblia.textoAberto.setBackgroundColor(context.getResources().getColor(R.color.white));
                itemSuporteBiblia.textoAberto.setTextColor(Color.rgb(0,0,0));
            }

        }

        //Habilita o botão de abrir livro quando for pesquisa
        if(pesquiar){

            itemSuporteBiblia.buttonAbrirLivro.setVisibility(View.VISIBLE);
            itemSuporteBiblia.buttonAbrirLivro.setEnabled(true);
            itemSuporteBiblia.textoAberto.setText(Html.fromHtml(currentListData.toPesquisarString()));

            itemSuporteBiblia.buttonAbrirLivro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    chamarLivro(getItem(position));
                    Log.e("teste","teste");

                }
            });


        }else{

            itemSuporteBiblia.textoAberto.setText(Html.fromHtml(currentListData.toString()));

        }


      itemSuporteBiblia.textoAberto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                Biblia verso = getItem(position);

                if (verso.getLido() != LIDO) {

                    bancoHelper = new BibliaBancoDadosHelper(context);
                    bancoHelper.setLidoVerso(getItem(position));

                    if(mostrarVersiculosLidos)

                     if(!modoNoturno) {
                         view.setBackgroundColor(context.getResources().getColor(R.color.cinzaclaro));

                     }else{

                         view.setBackgroundColor(context.getResources().getColor(R.color.dark));

                     }


                     verso.setLido(LIDO);

                }

                return false;
            }
        });


           return convertView;
    }

    private void tamanhoFonte(ItemSuporteBiblia text){

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        String t = sharedPrefs.getString("fontePref", "18");
        text.textoAberto.setTextSize(Integer.parseInt(t));

    }

    private void modoNoturno(ItemSuporteBiblia text, Biblia currentListData){

        text.textoAberto.setTextColor(Color.rgb(192, 192, 192));

        if((currentListData.getLido() == 1) && mostrarVersiculosLidos)
            text.textoAberto.setBackgroundColor(context.getResources().getColor(R.color.dark));
        else {
            text.textoAberto.setBackgroundColor(Color.DKGRAY);
        }


    }

    public boolean isPackageInstalled(String packageName) {

        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void close() {
        if (bancoHelper != null) {
            bancoHelper = null;
        }
    }

    public void chamarLivro(Biblia bi){

        intent = new Intent(context, Lista_Biblia.class);
        intent.putExtra("livro", bi.getBooksName());
        intent.putExtra("capitulo", bi.getVersesChapter());
        intent.putExtra("versiculo", bi.getVersesNum());
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity(intent);


    }

    public int getCount() {
        return itensList.size();
    }

    @Override
    public Biblia getItem(int position) {

        if(pesquiar)
        itensList.get(position).setContext(context);

        return itensList.get(position);

    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    private class ItemSuporteBiblia {

        TextView textoAberto;
        Button   buttonAbrirLivro;

        public ItemSuporteBiblia(View v) {

            textoAberto =      (TextView) v.findViewById(R.id.textoAberto);
            buttonAbrirLivro = (Button)  v.findViewById(R.id.buttonAbrirLivro);


            if(!fonte.contains("Arial")) {
                Typeface font = Typeface.createFromAsset(context.getAssets(), "Font/"+fonte);
                textoAberto.setTypeface(font);
            }

        }

    }

         }

