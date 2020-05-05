package com.projeto.biblianvi.biblianvi;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ActivityAnotacao extends Activity {


    static public ArrayList<Anotacao> LISTA;
    static public NotaAdaptador NOTAADAPTADOR;
    private ListView listView;
    private LinearLayout layoutNotaAdcionar;
    private BibliaBancoDadosHelper bibliaBancoDadosHelper;
    Button buttonNotaSalvar;
    Button buttonNotaCancel;
    EditText editTextTittuloNota, editTextNotaTexto;
    FrameLayout item;
    View child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anotacao);

        listView = findViewById(R.id.listNota);
        layoutNotaAdcionar = findViewById(R.id.layoutNotaAdcionar);


       layoutNotaAdcionar.setOnClickListener(new View.OnClickListener()
        {

        @Override
        public void onClick (View v){

            item = findViewById(R.id.frameNota);
        child = getLayoutInflater().inflate(R.layout.nota_adicionar, null);
        item.addView(child);

            buttonNotaSalvar = findViewById(R.id.buttonNotaSalvar);
            buttonNotaCancel = findViewById(R.id.buttonNotaCancel);
            editTextTittuloNota = findViewById(R.id.editTextTittuloNota);
            editTextNotaTexto = findViewById(R.id.editTextNotaTexto);

        frameComponentes();

        }
     }

    );
}

    private void frameComponentes(){

        buttonNotaSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                String currentDateandTime = sdf.format(new Date());


                String titulo = editTextTittuloNota.getText().toString();
                String texto = editTextNotaTexto.getText().toString();
                String data = currentDateandTime;

                if (titulo.length() > 1 || texto.length() > 1) {

                    bibliaBancoDadosHelper = new BibliaBancoDadosHelper(getApplicationContext());
                    bibliaBancoDadosHelper.salvarNota(titulo, texto, data);


                    Toast.makeText(getApplicationContext(), "Nota salva!", Toast.LENGTH_LONG).show();

                    item.removeView(child);

                    carregarLista();

                } else {

                    Toast.makeText(getApplicationContext(), "Digite algo !", Toast.LENGTH_LONG).show();

                }
            }
        });

        buttonNotaCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                item.removeView(child);
            }
        });


    }

    protected void onResume(){
        super.onResume();
        carregarLista();

    }

    private void carregarLista() {


        LISTA = new BibliaBancoDadosHelper(ActivityAnotacao.this).getNota();

        if (!LISTA.isEmpty()) {

            NOTAADAPTADOR = new NotaAdaptador(getApplicationContext(),this, LISTA);

            listView.setAdapter(NOTAADAPTADOR);


        } else {

           LISTA.add(new Anotacao());

            NOTAADAPTADOR = new NotaAdaptador(getApplicationContext(),this ,LISTA);

            listView.setAdapter(NOTAADAPTADOR);

        }


    }

}