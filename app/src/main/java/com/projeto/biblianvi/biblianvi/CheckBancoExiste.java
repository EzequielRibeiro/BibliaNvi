package com.projeto.biblianvi.biblianvi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class CheckBancoExiste extends SQLiteOpenHelper  {

	//The Android's default system path of your application database.
	private String DB_PATH;
	private String myPatch;
	public static final String DB_NAME = "nvi_1.db3";
	private static final String DB_ANTERIOR = "nvi.db3";
	private boolean dbExist = false;
	private ProgressDialog barraDeProgresso;


	private SQLiteDatabase myDataBase;

	private Context myContext;

	/**
	 * Constructor
	 * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
	 * @param context
	 */
	public CheckBancoExiste(Context context) {

		super(context, DB_NAME, null, 1);
		this.myContext = context;

		DB_PATH = "data/data/"+context.getPackageName()+"/databases";
		myPatch = DB_PATH+"/"+ DB_NAME;

	}



	public void setBarraDeProgresso(ProgressDialog barraDeProgresso){

		this.barraDeProgresso = barraDeProgresso;
	}
	/**
	 * Creates a empty database on the system and rewrites it with your own database.
	 * */
	public void createDataBase() throws IOException {


            copyDataBase();


	   //	return dbExist;

	}


	public void deletarBancoDadosAntigo(){


        File f = new File(DB_PATH+"/"+ DB_ANTERIOR);
        boolean delete = false;

        if(f.exists()){

            delete = f.delete();

       }

        Log.e("Banco deletado: "+DB_ANTERIOR, Boolean.toString(delete) );

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each time you open the application.
	 * @return true if it exists, false if it doesn't
	 */
	public boolean checkDataBase() {

        SQLiteDatabase checkDB = null;
        boolean checking = false;

        try {

            //checkDB = SQLiteDatabase.openDatabase(myPatch, null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);

			checking = myContext.getDatabasePath(DB_NAME).exists();

			Log.i("Tem banco", Boolean.toString(checking));

        } catch (Exception e) {

           // throw icon_new Error("Erro abrir banco");
        }

		deletarBancoDadosAntigo();

        return checking;

			}

	private File criarArquivoTemp(){

		File f = new File(DB_PATH);

		 if(!f.exists()){

				 f.mkdir();


		 }

          return  f;


	}


	   int tamanho = 0;
	public boolean checarIntegridadeDoBanco() throws IOException {

		File f = new File(myPatch);

		AssetManager assetManager = myContext.getAssets();
		InputStream myInput = assetManager.open(DB_NAME);

		tamanho = myInput.available();


		if(f.exists()){

			// f.mkdir();

			Log.e("Tamanho do Banco 1",Integer.toString((int)f.length()));
			Log.e("Tamanho do Banco 2",Integer.toString(tamanho));

		}
		return  (int)f.length() >= tamanho;


	}


	private void copyDataBase() throws IOException{

		File arquivoGravado = new File(criarArquivoTemp().getAbsolutePath(),DB_NAME);
		int tamanhoArquivo;


		AssetManager assetManager = myContext.getAssets();

		String[] files = assetManager.list("");

		Log.e("File", Arrays.toString(files));
		Log.e("Patch", arquivoGravado.getAbsolutePath());

		//Open your local db as the input stream
		InputStream myInput = assetManager.open(DB_NAME);

		tamanhoArquivo = myInput.available();

		//Open the empty db as the output stream
		FileOutputStream myOutput = new FileOutputStream(arquivoGravado);

		//transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		int lido = 0;

		while ((length = myInput.read(buffer))>0){
			myOutput.write(buffer, 0, length);

			lido += length;
			barraDeProgresso.setProgress((lido * 100)/tamanhoArquivo);

		}

		//Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();


	}


	@Override
	public void close() {

		if(myDataBase != null)
			myDataBase.close();

		super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}