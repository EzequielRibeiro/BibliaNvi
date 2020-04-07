package com.projeto.biblianvi;


import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

public class Grafico_Um extends Activity {



    final int idImageLivro[] = {R.id.imageViewGen,R.id.imageViewExo,R.id.imageViewLev,R.id.imageViewNum,
            R.id.imageViewDeu,R.id.imageViewJos,R.id.imageViewJui,R.id.imageViewRut,R.id.imageView1Sam,
            R.id.imageView2Sam,R.id.imageView1Reis,R.id.imageView2Reis,R.id.imageView1Cron,R.id.imageView2Cron,
            R.id.imageViewEsd,R.id.imageViewNee,R.id.imageViewEster,R.id.imageViewJo,R.id.imageViewSal,
            R.id.imageViewPro,R.id.imageViewEcle,R.id.imageViewCan,R.id.imageViewIsa,R.id.imageViewJer,
            R.id.imageViewLam,R.id.imageViewEze,R.id.imageViewDan,R.id.imageViewOse,R.id.imageViewJoe,
            R.id.imageViewAmo,R.id.imageViewOba,R.id.imageViewJon,R.id.imageViewMiq,R.id.imageViewNau,
            R.id.imageViewHab,R.id.imageViewSof,R.id.imageViewAge,R.id.imageViewZac,R.id.imageViewMal};

    final int idTextPorc[]  = {R.id.textViewGen2,R.id.textViewExo2,R.id.textViewLev2,R.id.textViewNum2,
            R.id.textViewDeu2,R.id.textViewJos2,R.id.textViewJui2,R.id.textViewRut2,R.id.textView1Sam2,
            R.id.textView2Sam2,R.id.textView1Reis2,R.id.textView2Reis2,R.id.textView1Cron2,R.id.textView2Cron2,
            R.id.textViewEsd2,R.id.textViewNee2,R.id.textViewEster2,R.id.textViewJo2,R.id.textViewSal2,
            R.id.textViewPro2,R.id.textViewEcle2,R.id.textViewCan2,R.id.textViewIsa2,R.id.textViewJer2,
            R.id.textViewLam2,R.id.textViewEze2,R.id.textViewDan2,R.id.textViewOse2,R.id.textViewJoe2,
            R.id.textViewAmo2,R.id.textViewOba2,R.id.textViewJon2,R.id.textViewMiq2,R.id.textViewNau2,
            R.id.textViewHab2,R.id.textViewSof2,R.id.textViewAge2,R.id.textViewZac2,R.id.textViewMal2};

    final int idTextLivro[]  = {R.id.textViewGen,R.id.textViewExo,R.id.textViewLev,R.id.textViewNum,
            R.id.textViewDeu,R.id.textViewJos,R.id.textViewJui,R.id.textViewRut,R.id.textView1Sam,
            R.id.textView2Sam,R.id.textView1Reis,R.id.textView2Reis,R.id.textView1Cron,R.id.textView2Cron,
            R.id.textViewEsd,R.id.textViewNee,R.id.textViewEster,R.id.textViewJo,R.id.textViewSal,
            R.id.textViewPro,R.id.textViewEcle,R.id.textViewCan,R.id.textViewIsa,R.id.textViewJere,
            R.id.textViewLam,R.id.textViewEze,R.id.textViewDan,R.id.textViewOse,R.id.textViewJoe,
            R.id.textViewAmo,R.id.textViewOba,R.id.textViewJon,R.id.textViewProMiq,R.id.textViewNau,
            R.id.textViewHab,R.id.textViewSof,R.id.textViewAge,R.id.textViewZac,R.id.textViewMal};

     private ImageView imagemGrafico[];
     private TextView  textPorceDados[];

     List<GraficoDadosBanco> graficoDadosBancoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setTheme(android.R.style.Widget_Holo_Light);

        setContentView(R.layout.activity_grafico_um);

        imagemGrafico = new ImageView[idImageLivro.length];
        textPorceDados = new TextView[idTextPorc.length];

        CheckBancoExiste bancoExiste = new CheckBancoExiste(getApplicationContext());
        BibliaBancoDadosHelper bibliaBancoDadosHelper = new BibliaBancoDadosHelper(getApplicationContext());

        for(int i = 0; i<idImageLivro.length;i++){

            imagemGrafico[i] = (ImageView) findViewById(idImageLivro[i]);
            textPorceDados[i] = (TextView) findViewById(idTextPorc[i]);
        }

        if(bancoExiste.checkDataBase()){


            float total = 0f;
            int   lidos;
            int   versosTotal;
            int i = 0;

            Iterator iterator = bibliaBancoDadosHelper.getVersosLidos(1).iterator();

            while(iterator.hasNext()){

                GraficoDadosBanco obj = (GraficoDadosBanco) iterator.next();

                lidos = obj.getTotalVersoslidos();
                versosTotal = obj.getTotalDeVersos();

                if(lidos > 0)
                 total = (lidos*100)/versosTotal;

                imagemGrafico[i].getLayoutParams().width = (int) total;
                textPorceDados[i].setText(String.format("%3.0f",total) + "%");

                i++;

              //  Log.d("Livro",obj.getNomeLivro());
              //  Log.d("Lidos",Integer.toString(obj.getTotalVersoslidos()));
              //  Log.d("Total versos",Integer.toString(obj.getTotalDeVersos()));

                lidos = 0;
                versosTotal = 0;
                total = 0f;

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
