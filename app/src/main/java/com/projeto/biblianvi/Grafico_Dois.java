package com.projeto.biblianvi;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Iterator;

public class Grafico_Dois extends Activity {

    final int idImageCarta[] = {R.id.imageViewMat,R.id.imageViewMarc,R.id.imageViewLuc,R.id.imageViewJoa,
            R.id.imageViewAto,R.id.imageViewRom,R.id.imageView1Cor,R.id.imageView2Cor,R.id.imageViewGal,
            R.id.imageViewEfe,R.id.imageViewFil,R.id.imageViewCol,R.id.imageView1Tes,R.id.imageView2Tes,
            R.id.imageView1Tim,R.id.imageView2Tim,R.id.imageViewTit,R.id.imageViewFile,R.id.imageViewHeb,
            R.id.imageViewTia,R.id.imageView1Pe,R.id.imageView2Pe,R.id.imageView1Jo,R.id.imageView2Jo,
            R.id.imageView3Jo,R.id.imageViewJud,R.id.imageViewApo};

    final int idTextPorc[]  = {R.id.textViewMat2,R.id.textViewMarc2,R.id.textViewLuc2,R.id.textViewJoa2,
            R.id.textViewAto2,R.id.textViewRom2,R.id.textView1Cor2,R.id.textView2Cor2,R.id.textViewGal2,
            R.id.textViewEfe2,R.id.textViewFil2,R.id.textViewCol2,R.id.textView1Tes2,R.id.textView2Tes2,
            R.id.textView1Tim2,R.id.textView2Tim2,R.id.textViewTit2,R.id.textViewFile2,R.id.textViewHeb2,
            R.id.textViewTia2,R.id.textView1Pe2,R.id.textView2Pe2,R.id.textView1Jo2,R.id.textView2Jo2,
            R.id.textView3Jo2,R.id.textViewJud2,R.id.textViewApo2};

    private ImageView imagemGrafico[];
    private TextView  textPorceDados[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setTheme(android.R.style.Widget_Holo_Light);

        setContentView(R.layout.activity_grafico_dois);

        imagemGrafico = new ImageView[idImageCarta.length];
        textPorceDados = new TextView[idTextPorc.length];

        CheckBancoExiste bancoExiste = new CheckBancoExiste(getApplicationContext());
        BibliaBancoDadosHelper bibliaBancoDadosHelper = new BibliaBancoDadosHelper(getApplicationContext());

        for(int i = 0; i<idImageCarta.length;i++){

            imagemGrafico[i] = (ImageView) findViewById(idImageCarta[i]);
            textPorceDados[i] = (TextView) findViewById(idTextPorc[i]);
        }

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayoutNovo);

        if(bancoExiste.checkDataBase()){

            int total = 0 ;
            int lidos = 0;
            int versosTotal = 0;
            int i = 0;
            int width = 0;

            Iterator iterator = bibliaBancoDadosHelper.getVersosLidos(2).iterator();

            while(iterator.hasNext()){

                GraficoDadosBanco obj = (GraficoDadosBanco) iterator.next();

                lidos = obj.getTotalVersoslidos();
                versosTotal = obj.getTotalDeVersos();
                width =  linearLayout.getLayoutParams().width;


                if(lidos > 0){
                    total = (lidos*100)/versosTotal;
                    width = (width * total) / 100;
                    imagemGrafico[i].getLayoutParams().width = width;
                    textPorceDados[i].setText(String.format("%d",total) + "%");
                }
                i++;

                //  Log.d("Livro",obj.getNomeLivro());
                //  Log.d("Lidos",Integer.toString(obj.getTotalVersoslidos()));
                //  Log.d("Total versos",Integer.toString(obj.getTotalDeVersos()));


            }

        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_grafico_um, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
