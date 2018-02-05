package com.projeto.biblianvi.biblianvi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Activity_favorito extends Activity {


    private ListView listaFavorito;
    private List<Biblia> listBiblia;
    private BibliaBancoDadosHelper bancoDadosHelper;
    public static final String TABELANAME = "favorito";
    public static final String CAMPOS = "(id integer primary key,idVerso TINYINT(3) not null)";
    public FavoritoAdapter favoritoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorito);

        listaFavorito = (ListView) findViewById(R.id.listViewFavorito);

        bancoDadosHelper = new BibliaBancoDadosHelper(this);


        if(bancoDadosHelper.tabelaExiste(TABELANAME) == 1){

            listBiblia = bancoDadosHelper.getFavorito();

            favoritoAdapter = new FavoritoAdapter(Activity_favorito.this,listBiblia);

            listaFavorito.setAdapter(favoritoAdapter);


            listaFavorito.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    Biblia b = (Biblia) parent.getItemAtPosition(position);

                    confirmarDelete(b.getIdVerse(),position);

                    return false;
                }
            });


        }else{

            bancoDadosHelper.criarTabela(TABELANAME,CAMPOS);

        }

    }

    private void confirmarDelete(String idVer,int position){


        TextView textView = new TextView(this);
        textView.setText("Deseja apagar esse versículo dos favoritos ?");
        textView.setTextSize(18);

        TextView textViewTitle = new TextView(this);
        textViewTitle.setText("Apagar versículo");

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setView(textView);
        // alertDialogBuilder.setCustomTitle(textViewTitle);


        alertDialogBuilder.setPositiveButton("Confimar",new dialogo(idVer,position));



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

    private class FavoritoAdapter extends  ArrayAdapter<Biblia>{


        public FavoritoAdapter(Context context, List<Biblia> biblias) {
            super(context,0,biblias);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Biblia objBiblia = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_custom_list_favorito, parent, false);
            }
            // Lookup view for data population
            TextView textViewLivro = (TextView) convertView.findViewById(R.id.textViewFavorLivro);
            TextView textViewVerCap = (TextView) convertView.findViewById(R.id.textViewFavorText);

            textViewLivro.setText(objBiblia.getBooksName()+" "+objBiblia.getVersesChapter()+':'+objBiblia.getVersesNum());
            textViewVerCap.setText(objBiblia.getVersesText());


            return convertView;
        }


    }

    private class dialogo implements  DialogInterface.OnClickListener{

        private String idVerso;
        private int posicao;

        public dialogo(String idVerso , int posicao){

            this.idVerso = idVerso;
            this.posicao = posicao;

        }

        @Override
        public void onClick(DialogInterface dialog, int which) {

            bancoDadosHelper.deleteFavorito(idVerso);

            Log.e("Deletar Favorito",idVerso);

            listBiblia.remove(posicao);
            favoritoAdapter.notifyDataSetChanged();


        }
    }{



    }

}
