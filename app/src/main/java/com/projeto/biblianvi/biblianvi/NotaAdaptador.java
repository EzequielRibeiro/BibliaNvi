package com.projeto.biblianvi.biblianvi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Ezequiel on 27/04/2016.
 */
public class NotaAdaptador extends ArrayAdapter<Anotacao> {


    private Button buttonExcluiNota;
    private Context context;


    public NotaAdaptador(Context context, ArrayList<Anotacao> notas) {
        super(context,0,notas);
        this.context = context;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Anotacao nota = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_nota, parent, false);
        }
        // Lookup view for data population
        TextView textViewAssunto = (TextView) convertView.findViewById(R.id.textViewAssunto );
        TextView textViewData = (TextView) convertView.findViewById(R.id. textViewData);
        TextView textViewTexto = (TextView)   convertView.findViewById(R.id.textViewTexto);
        TextView textViewAnoId = (TextView) convertView.findViewById(R.id.textViewAnoId) ;
        buttonExcluiNota = (Button) convertView.findViewById(R.id.buttonExcluiNota);
        buttonExcluiNota.setTag(position);

        // Populate the data into the template view using the data object
        textViewAssunto.setText(nota.getTitulo());
        textViewData.setText(nota.getData());
        textViewTexto.setText(nota.getTexto());
        textViewAnoId.setText(nota.getId());



        buttonExcluiNota.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Anotacao nota = (Anotacao) getItem(position);

                Toast.makeText(context, "Removido", Toast.LENGTH_SHORT).show();
                ActivityAnotacao.LISTA.remove(position);
                ActivityAnotacao.NOTAADAPTADOR.notifyDataSetChanged();

                new BibliaBancoDadosHelper(getContext()).deleteNota(nota.getId());

                return false;
            }
        });
        /*
        buttonExcluiNota.setOnClickListener(icon_new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Anotacao nota = (Anotacao) getItem(position);

                Toast.makeText(context, "Removido", Toast.LENGTH_SHORT).show();
                ActivityAnotacao.LISTA.remove(position);
                ActivityAnotacao.NOTAADAPTADOR.notifyDataSetChanged();

                icon_new BibliaBancoDadosHelper(getContext()).deleteNota(nota.getId());
            }
        });
            */
        return convertView;
    }


    private class ItemSuporte {

        private TextView titulo;
        private TextView texto;
        private TextView data;

        public ItemSuporte(View v) {

            titulo = (TextView) v.findViewById(R.id.textViewAssunto);
            texto = (TextView) v.findViewById(R.id.textViewTexto);
            data = (TextView) v.findViewById(R.id.textViewData);


        }

    }
}
