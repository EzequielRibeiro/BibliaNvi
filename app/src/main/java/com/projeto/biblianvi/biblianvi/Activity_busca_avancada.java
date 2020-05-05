package com.projeto.biblianvi.biblianvi;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;


import java.util.List;


public class Activity_busca_avancada extends Activity {

    /* tipoDeBusca = "buscarTestamento"
     0 = Tota biblia,  1 = NT,   2 = VT,  3 = Livro */
    private String tipoDeBusca = "3";
    private Button botaoPesquisar;
    private EditText editText;
    private Spinner spinnerLivros;
    private RadioButton radioNovo, radioVelho, radioBib;
    private TextView textViewDeveloper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_busca_avancada);

        botaoPesquisar = findViewById(R.id.buttonPesquisaAvanc);
        editText = findViewById(R.id.editTextAvanc);
        spinnerLivros = findViewById(R.id.spinnerBuscaAvanc);
        radioVelho = findViewById(R.id.radio_velho);
        radioNovo = findViewById(R.id.radio_novo);
        radioBib = findViewById(R.id.radio_Biblia);
        textViewDeveloper = findViewById(R.id.textViewDeveloperBusca);

        textViewDeveloper.setText(BuildConfig.VERSION_NAME);

        botaoPesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              pesquisar();
            }

        });


        //editText.setHint("Digite uma palavra ou frase");
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {


            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if((actionId == EditorInfo.IME_ACTION_DONE) ||
                        (actionId == EditorInfo.IME_ACTION_NEXT)||
                        (actionId == EditorInfo.IME_ACTION_GO)){

                    pesquisar();
                }

                return false;
            }
        });



        BibliaBancoDadosHelper bibliaHelp = new BibliaBancoDadosHelper(getApplicationContext());
        List<Biblia> bookNameList = bibliaHelp.getAllBooksName();
        String[] livro = new String[bookNameList.size()];

        for(int i = 0 ;i <= bookNameList.size()-1; i++){
            livro[i] = bookNameList.get(i).getBooksName();
        }
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,livro);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinnerLivros.setAdapter(aa);

        spinnerLivros.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                radioVelho.setChecked(false);
                radioNovo.setChecked(false);
                radioBib.setChecked(false);
                tipoDeBusca = "3";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                tipoDeBusca = "3";
            }

        });


    }

    private void pesquisar(){

            if (editText.getText().length() >= 2) {

                    Intent  intent = new Intent(getApplicationContext(), Lista_Biblia.class);

                    intent.putExtra("livro", spinnerLivros.getSelectedItem().toString());
                    intent.putExtra("capitulo", "0");
                    intent.putExtra("versiculo", "0");
                    intent.putExtra("buscar", "true");
                    intent.putExtra("buscarTestamento","0");
                    intent.putExtra("termoBusca", editText.getText().toString());

                    //salva o termo da busca para ser usado por Biblia para realçar a cor da palavra
                    SharedPreferences settings = getSharedPreferences("termo_busca", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("busca",editText.getText().toString());
                    editor.commit();


                    if(tipoDeBusca.equals("0")){

                        intent.putExtra("buscarTestamento","0");
                        startActivity(intent);

                    }
                    else if (tipoDeBusca.equals("1")) {

                        intent.putExtra("buscarTestamento","1");
                        startActivity(intent);

                    } else if(tipoDeBusca.equals("2")){

                        intent.putExtra("buscarTestamento","2");
                        startActivity(intent);

                    }else if(tipoDeBusca.equals("3")){

                        intent.putExtra("buscarTestamento","3");
                        startActivity(intent);
                    }


                } else {
                editText.setHint(getString(R.string.digite_palavra));
                }
    }



    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        if (checked) {

            switch (view.getId()) {
                case R.id.radio_velho:
                    if (checked)
                        tipoDeBusca = "1";
                    break;
                case R.id.radio_novo:
                    if (checked)
                        tipoDeBusca = "2";
                    break;
                case R.id.radio_Biblia:
                    if (checked)
                         tipoDeBusca = "0";
                    break;

            }


        }

    }

    protected void onResume(){
        super.onResume();


    }


    public void onBackPressed() {
        super.onBackPressed();
        return;
    }



}
