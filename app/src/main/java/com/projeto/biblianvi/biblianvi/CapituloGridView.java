package com.projeto.biblianvi.biblianvi;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;


/**
 * Created by Ezequiel on 06/05/2017.
 */

public class CapituloGridView extends BaseAdapter {

   private Context mContext;
   private int capMax = 0 ;
   private Button button;
   private Biblia biblia;
    private ViewGroup viewGroupParent;


    public CapituloGridView(Context c, int capMax, Biblia b) {
        mContext = c;
        this.capMax = capMax;
        biblia = b;

    }

    public int getCount() {
        return capMax;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        viewGroupParent = parent;

       if (convertView == null) {
            // if it's not recycled, initialize some attributes



        } else {

            button = (Button) convertView;

           }


        button = new Button(mContext);
        button.setLayoutParams(new GridView.LayoutParams(80,80));
        button.setBackgroundColor(mContext.getResources().getColor(R.color.barrasuperiorescuro));
        button.setTextColor(mContext.getResources().getColor(R.color.green));
      //  button.setBackground(mContext.getResources().getDrawable(R.drawable.appthemebutton_btn_default_normal_holo_dark));
        button.setPadding(3,3,3,3);

        if(position < capMax)
        button.setText(Integer.toString(position + 1));


        Log.e("position",Integer.toString(position));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Button b = (Button) v;

                biblia.setChapter(b.getText().toString());

                BibliaBancoDadosHelper bibliaHelp = new BibliaBancoDadosHelper(mContext);

                int versiculos = bibliaHelp.getQuantidadeVersos(biblia.getBooksName(), biblia.getChapter());

                GridView gridViewVersiculos = (GridView) viewGroupParent;
                gridViewVersiculos.setAdapter(new VersiculoGridView(mContext,versiculos,biblia));
                gridViewVersiculos.deferNotifyDataSetChanged();

                /*
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View l = inflater.inflate(R.layout.details,viewGroupParent,false);

                TextView textView = (TextView) l.findViewById(R.id.textGridView);
                textView.setText("Versiculo");
                textView.setText(R.string.nviText);
                */

                Toast.makeText(mContext,"Versiculo",Toast.LENGTH_LONG).show();
            }
        });

        return button;
    }


}
