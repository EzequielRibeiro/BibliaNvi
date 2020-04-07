package com.projeto.biblianvi;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class BibliaBancoDadosHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name

    private Cursor cursor;

    private SQLiteDatabase myDataBase;

    private String DB_PATH;

    private Context myContext;

    public BibliaBancoDadosHelper(Context context) {
        super(context, CheckBancoExiste.DB_NAME, null, DATABASE_VERSION);

        DB_PATH = "data/data/"+context.getPackageName()+"/databases/";
        myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    /**
     * CRUD operations (create "add", read "get", update, delete) book + get all books + delete all books
     */

    // Books table name
    private static final String TABELA_VERSES = "verses";


  /*
    public void addBook(Biblia book){
        Log.d("addBook", book.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = icon_new ContentValues();
        values.put(KEY_TITLE, book.getTitle()); // get title
        values.put(KEY_AUTHOR, book.getAuthor()); // get author

        // 3. insert
        db.insert(TABLE_BOOKS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }
   */

    private void openDataBase() {

        String myPath = DB_PATH + CheckBancoExiste.DB_NAME;

        try {
            myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
            //  SQLiteDatabase db = this.getWritableDatabase();
            //  myDataBase = this.getReadableDatabase();
            Log.e("Banco aberto", Boolean.toString(myDataBase.isOpen()));
            Log.e("Banco patch", myDataBase.getPath().toString());

        }catch (NonFatalError e){}

    }

    public List<GraficoDadosBanco> getVersosLidos(int id) {

        List<GraficoDadosBanco> versosLidos = new LinkedList<GraficoDadosBanco>();

        // 1. build the query
        String query = "select books.[id], books.[name] ,count(verses.[text]), sum (verses.[lido]) from  verses " +
                "inner join books on verses.[book] = books.id where verses.[testament] = "+id+" " +
                "group by books.[name] " +
                "ORDER BY books.[id]";

        // 2. get reference to writable DB
        //SQLiteDatabase db = this.getWritableDatabase();

        openDataBase();

        cursor = myDataBase.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        GraficoDadosBanco graficoDadosBanco;

        if (cursor.moveToFirst()) {
            do {
                graficoDadosBanco = new GraficoDadosBanco();
                graficoDadosBanco.setId(cursor.getInt(0));
                graficoDadosBanco.setNomeLivro(cursor.getString(1));
                graficoDadosBanco.setTotalDeVersos(cursor.getInt(2));
                graficoDadosBanco.setTotalVersoslidos(cursor.getInt(3));
                versosLidos.add(graficoDadosBanco);
            } while (cursor.moveToNext());
        }

        close();
        // return books
        return versosLidos;
    }

    public List<Biblia> getBook(String book) {

        List<Biblia> books = new LinkedList<Biblia>();

        // 1. build the query
        String query = "select testament.name,books.name,verses.chapter,verses.verse,verses.text,verses.lido,verses.id " +
                "from testament,verses,books where testament.id = " +
                "verses.testament and books.id = verses.book and books.name like '" + book + "%';";

        // 2. get reference to writable DB
        //SQLiteDatabase db = this.getWritableDatabase();

        openDataBase();

        cursor = myDataBase.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Biblia biblia;

        if (cursor.moveToFirst()) {
            do {
                biblia = new Biblia();
                biblia.setTestamentName(cursor.getString(0));
                biblia.setBooksName(cursor.getString(1));
                biblia.setVersesChapter(cursor.getString(2));
                biblia.setVerseNum(cursor.getString(3));
                biblia.setText(cursor.getString(4));
                biblia.setLido(cursor.getInt(5));
                biblia.setIdVerse(cursor.getString(6));

                // Add book to books
                books.add(biblia);
            } while (cursor.moveToNext());
        }

        close();
        // return books
        return books;
    }

    // Get All Books
    public List<Biblia> getAllBooks() {

        List<Biblia> books = new LinkedList<Biblia>();

        // 1. build the query
        String query = "select testament.name,books.name,verses.chapter,verses.verse,verses.text,verses.lido,verses.id " +
                "from testament,verses,books where testament.id = " +
                "verses.testament and books.id = verses.book;";

        // 2. get reference to writable DB
        //SQLiteDatabase db = this.getWritableDatabase();
        openDataBase();

        cursor = myDataBase.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Biblia biblia;

        if (cursor.moveToFirst()) {
            do {
                biblia = new Biblia();
                biblia.setTestamentName(cursor.getString(0));
                biblia.setBooksName(cursor.getString(1));
                biblia.setVersesChapter(cursor.getString(2));
                biblia.setVerseNum(cursor.getString(3));
                biblia.setText(cursor.getString(4));
                biblia.setLido(cursor.getInt(5));
                biblia.setIdVerse(cursor.getString(6));
                // Add book to books
                books.add(biblia);
            } while (cursor.moveToNext());
        }


        close();
        // return books
        return books;
    }

    public void limparVersLidos(){


        int row;
        String query = "UPDATE "+TABELA_VERSES+" set lido = 0 where lido = 1";
        // openDataBase();

        String patch = DB_PATH + CheckBancoExiste.DB_NAME ;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(patch, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        // SQLiteDatabase db = this.getWritableDatabase();

        ContentValues vl = new ContentValues();
        vl.put("lido", "0");


        try {

            db.beginTransaction();

            db.execSQL(query);

          //  row = db.update("verses", vl, "id = ?", icon_new String[]{});

            db.setTransactionSuccessful();


        } catch (Exception e) {

            throw  new Error("Update");

        } finally {
            db.endTransaction();
            db.close();
        }



    }

    public int getQuantidadeVersos(String livroEpistola, String capitulo) {

        int i = 0;
        String query = "select verses.verse " +
                "from testament,verses,books where testament.id = verses.testament and books.id = verses.book " +
                "and books.name like '" + livroEpistola + "%' and verses.chapter =" + capitulo +
                " GROUP BY verses.chapter ";

        openDataBase();
        cursor = myDataBase.rawQuery(query, null);

        if (cursor.moveToFirst())
            i = cursor.getInt(0);

        close();

        return i;
    }

    public int getQuantidadeCapitulos(String livroEpistola) {

        int i = 0;
        String query ="select verses.chapter from testament,verses,books " +
                "where testament.id = verses.testament and books.id = verses.book " +
                "and books.name like '"+livroEpistola+"%' GROUP BY books.name";

        openDataBase();
        cursor = myDataBase.rawQuery(query, null);

        if (cursor.moveToFirst())
            i = cursor.getInt(0);

        close();

        return i;
    }

    public int setLidoVerso(Biblia bi){

        int row;
        String query = "UPDATE "+TABELA_VERSES+" set lido = 1 where verses.[id] = "+bi.getIdVerse()+";";
        // openDataBase();

        String patch = DB_PATH + CheckBancoExiste.DB_NAME ;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(patch, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        // SQLiteDatabase db = this.getWritableDatabase();

        ContentValues vl = new ContentValues();
        vl.put("lido", "1");


        try {

            db.beginTransaction();

            row = db.update("verses", vl, "id = ?", new String[]{String.valueOf(bi.getIdVerse())});

            db.setTransactionSuccessful();


        } catch (Exception e) {

            throw  new Error("Update");

        } finally {
            db.endTransaction();
            db.close();
        }

        Log.e("ID VERSE", bi.getIdVerse());
        Log.e("Row update", Integer.toString(row));

        return row;

    }

    public int getQuantCompartilhar() {

        int i = 0;
        String query = "select count(msg) from compartilhar";

        openDataBase();

        cursor = myDataBase.rawQuery(query, null);

            if (cursor.moveToFirst())
                i = cursor.getInt(0);


        close();

        return i;
    }

    public void setVersLimparCompartilhar(){

        String query = "delete from compartilhar";

        String patch = DB_PATH + CheckBancoExiste.DB_NAME ;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(patch, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);

        try {

            db.beginTransaction();

            db.execSQL(query);

            db.setTransactionSuccessful();


        } catch (Exception e) {

            throw  new Error("Delete compartilhar");

        } finally {
            db.endTransaction();
            db.close();
        }


    }

    public void setVersCompartilhar(Biblia bi){


        String versiculo = bi.getVersesText() + " ("+bi.getBooksName()+ " "+bi.getVersesChapter()+":"+bi.getVersesNum()+") ";

        String query = "insert into compartilhar (msg) values ('"+versiculo+"')";

        String patch = DB_PATH + CheckBancoExiste.DB_NAME ;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(patch, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);



            try {

                db.beginTransaction();

                db.execSQL(query);

                db.setTransactionSuccessful();


            } catch (Exception e) {

                throw new Error("Insert versiculo");

            } finally {
                db.endTransaction();
                db.close();
            }


    }

    public StringBuffer getVersCompartilhar() {

       StringBuffer stringBuffer = new StringBuffer();

        String query = "select msg from compartilhar";

        openDataBase();

        cursor = myDataBase.rawQuery(query, null);


        if(cursor!=null && cursor.getCount()>0)
            if (cursor.moveToFirst()) {
                do {

                    stringBuffer.append(cursor.getString(0).concat(" \n"));


                } while (cursor.moveToNext());
            }


        close();

        return stringBuffer;
    }

    public Dicionario getDicionarioTexto(String id){

        String query = "select id,palavra,texto from [dicionario] where id = "+id+"";

        openDataBase();

        Dicionario dic = null ;

        cursor = myDataBase.rawQuery(query, null);

        if(cursor!=null && cursor.getCount()>0)
            if (cursor.moveToFirst()) {

                    dic = new Dicionario();
                    dic.setId(cursor.getInt(0));
                    dic.setPalavra(cursor.getString(1));
                    dic.setTexto(cursor.getString(2));

            }


        close();

        return dic;

    }

    public ArrayList<Dicionario> getDicionarioPalavra(){

        String query = "select id, palavra from [dicionario]";

        ArrayList<Dicionario> listDic = new ArrayList<Dicionario>();

        openDataBase();

        Dicionario dic ;

        cursor = myDataBase.rawQuery(query, null);

        if(cursor!=null && cursor.getCount()>0)
        if (cursor.moveToFirst()) {

            do {
                dic = new Dicionario();
                dic.setId(cursor.getInt(0));
                dic.setPalavra(cursor.getString(1));

                listDic.add(dic);
            }while(cursor.moveToNext());
        }


        close();

        return listDic;

    }

    public List<Biblia> pesquisarBiblia(String termo) {

        List<Biblia> books = new LinkedList<Biblia>();

        String query = "select testament.name,books.name,verses.chapter,verses.verse,verses.text,verses.lido,verses.id " +
                "from testament,verses,books where testament.id = " +
                "verses.testament and books.id = verses.book and verses.text like '%" + termo + "%';";


        openDataBase();

        cursor = myDataBase.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Biblia biblia;

        if(cursor!=null && cursor.getCount()>0)
           if (cursor.moveToFirst()) {
            do {
                //contains: case sensitive - filtra por caixa alto-baixa
                if(cursor.getString(4).contains(termo)) {
                    biblia = new Biblia();
                    biblia.setTestamentName(cursor.getString(0));
                    biblia.setBooksName(cursor.getString(1));
                    biblia.setVersesChapter(cursor.getString(2));
                    biblia.setVerseNum(cursor.getString(3));
                    biblia.setText(cursor.getString(4));
                    biblia.setLido(cursor.getInt(5));
                    biblia.setIdVerse(cursor.getString(6));

                    // Add book to books
                    books.add(biblia);
                }

            } while (cursor.moveToNext());
        }


        close();
        // return books
        return books;
    }

    public List<Biblia> pesquisarBibliaTestamento(String termo,String testamento) {

        List<Biblia> books = new LinkedList<Biblia>();

        String query = "select testament.name,books.name,verses.chapter,verses.verse,verses.text,verses.lido,verses.id " +
                "from testament,verses,books where testament.id = verses.testament "+
                "and books.id = verses.book and verses.text like '%"+termo+"%' "+
                "and [testament].[id] = "+testamento;


        openDataBase();

        cursor = myDataBase.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Biblia biblia;

        if(cursor!=null && cursor.getCount()>0)
            if (cursor.moveToFirst()) {
                do {

                    //contains: case sensitive - filtra por caixa alto-baixa
                    if(cursor.getString(4).contains(termo)) {
                        biblia = new Biblia();
                        biblia.setTestamentName(cursor.getString(0));
                        biblia.setBooksName(cursor.getString(1));
                        biblia.setVersesChapter(cursor.getString(2));
                        biblia.setVerseNum(cursor.getString(3));
                        biblia.setText(cursor.getString(4));
                        biblia.setLido(cursor.getInt(5));
                        biblia.setIdVerse(cursor.getString(6));

                        // Add book to books
                        books.add(biblia);
                    }


                } while (cursor.moveToNext());
            }


        close();
        // return books
        return books;
    }

    public List<Biblia> pesquisarBibliaLivro(String livro, String termo) {

        List<Biblia> books = new LinkedList<Biblia>();

        String query = "select testament.name,books.name,verses.chapter,verses.verse,verses.text,verses.lido,verses.id " +
                "from testament,verses,books where testament.id = verses.testament "+
                "and books.id = verses.book and verses.text like '%"+termo+"%' "+
                "and books.[name] = '"+livro+"'";


        openDataBase();

        cursor = myDataBase.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Biblia biblia;

        if(cursor!=null && cursor.getCount()>0)
            if (cursor.moveToFirst()) {
                do {

                    //contains: case sensitive - filtra por caixa alto-baixa
                  if(cursor.getString(4).contains(termo)) {
                      biblia = new Biblia();
                      biblia.setTestamentName(cursor.getString(0));
                      biblia.setBooksName(cursor.getString(1));
                      biblia.setVersesChapter(cursor.getString(2));
                      biblia.setVerseNum(cursor.getString(3));
                      biblia.setText(cursor.getString(4));
                      biblia.setLido(cursor.getInt(5));
                      biblia.setIdVerse(cursor.getString(6));

                      // Add book to books
                      books.add(biblia);

                  }

                } while (cursor.moveToNext());
            }


        close();
        // return books
        return books;
    }

    public int getQuantVersosLidosTotal(){

        int i = 0;

      String query = "select count(verses.[lido]) as total from verses  where verses.[lido] = '1';";

        openDataBase();
        cursor = myDataBase.rawQuery(query, null);

        if(cursor!=null && cursor.getCount()>0) {

            cursor.moveToFirst();

            i = cursor.getInt(0);

            close();


        }
        return i;
    }

    public int getQuantVersos(String livro){

        int i = 0;
        String query = "select count(verses.[text]) as total from verses, books " +
                "where  verses.[book] = books.[id] and books.[name] = '"+livro+"'";

        openDataBase();
        cursor = myDataBase.rawQuery(query, null);


        if(cursor!=null && cursor.getCount()>0)
        {
            cursor.moveToFirst();
            i = cursor.getInt(0);

            Log.e("Lidos",livro +"   "+ Integer.toString(i));

            return i;
        }

        return i;



    }

    public int getVersoLidoLivro(String livro){


        String query = "select count(verses.[lido]) as total from verses,books " +
                "where verses.[lido] = '1' and books.[id] = verses.[book] and books.[name] = '"+livro+"' " +
                "group by books.[name]";

        openDataBase();
        cursor = myDataBase.rawQuery(query, null);


        if(cursor!=null && cursor.getCount()>0)
        {
            cursor.moveToFirst();
            return cursor.getInt(0);

        }else
        {
            return 0;
        }


    }

    public synchronized void close() {
        super.close();

        if(myDataBase != null)
            myDataBase = null;

        if(cursor != null){
            cursor.close();
            cursor = null;}

    }

    private int versDoDiaId(int max) {

        Random rand = new Random();
        int min = 1;

        if(max < 1)
          max = 1;

        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public void versDoDiaText(TextView textViewAssuntoVers,TextView textViewVersDia, boolean salvarVersDia){

        VersDoDia v;

           v =  getVersDoDia();

            if(textViewAssuntoVers != null)
            textViewAssuntoVers.setText(v.getAssunto());

            if(textViewVersDia != null)
            textViewVersDia.setText(v.toString());

        if(salvarVersDia) {
            SharedPreferences settings = myContext.getSharedPreferences("versDia", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("assunto", v.getAssunto());
            editor.putString("versDia", v.toString());
            editor.putString("livroVersDia",v.getBooksName());
            editor.putString("capVersDia",v.getVersesChapter());
            editor.putString("verVersDia",v.getVersesNum());

            editor.commit();
        }



    }

    public VersDoDia getVersDoDia() {


        int id = versDoDiaId(getQuantVersDoDia());

        String query = "select books.[name],verses.[chapter],verses.[verse],verses.[text],selecionados.[assunto]" +
                " from verses,books,selecionados where [verses].[book] = books.[id]" +
                " and [selecionados].[livro] = books.[id]" +
                " and [selecionados].[cap] = verses.[chapter]" +
                " and [selecionados].[vers] = verses.[verse]" +
                " and [selecionados].[id] ="+id;

        VersDoDia v = new VersDoDia();

        openDataBase();


            cursor = myDataBase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            v.setBooksName(cursor.getString(0));
            v.setVersesChapter(Integer.toString(cursor.getInt(1)));
            v.setVerseNum(Integer.toString(cursor.getInt(2)));
            v.setText(cursor.getString(3));
            v.setAssunto(cursor.getString(4));
        }


        close();

        return v;

    }

    private int getQuantVersDoDia() {

        int i = 0;
        String query ="select count(id) from selecionados";

        openDataBase();

            cursor = myDataBase.rawQuery(query, null);

            if (cursor.moveToFirst())
                i = cursor.getInt(0);



        close();

        Log.e("Quantidade",Integer.toString(i));

        return i;
    }

    public void deleteNota(String id){

        String query = "delete from nota where nota.id ="+ id;

        openDataBase();

        myDataBase.execSQL(query);

        close();

    }

    public void salvarNota(String titulo,String texto,String data){

        String query = "insert into nota (id,titulo,texto,data_) values (null,'"+titulo+"','"+texto+"','"+data+"')";

        String patch = DB_PATH + CheckBancoExiste.DB_NAME ;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(patch, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);

        try {

            db.beginTransaction();

            db.execSQL(query);

            db.setTransactionSuccessful();

            Log.e("nota",texto);

        } catch (Exception e) {

            throw  new Error("Insert nota");

        } finally {
            db.endTransaction();
            db.close();
        }


    }

    public ArrayList<Anotacao> getNota() {

        ArrayList<Anotacao> notas = new ArrayList<Anotacao>();


        String query = "SELECT nota.[id],nota.[titulo],nota.[texto]," +
                "nota.[data_] FROM nota";

        openDataBase();


            cursor = myDataBase.rawQuery(query, null);

            Anotacao anotacao;

            if (cursor.moveToFirst()) {
                do {
                    anotacao = new Anotacao();
                    anotacao.setId(cursor.getInt(0));
                    anotacao.setTitulo(cursor.getString(1));
                    anotacao.setTexto(cursor.getString(2));
                    anotacao.setData(cursor.getString(3));

                    notas.add(anotacao);
                } while (cursor.moveToNext());
            }


        close();

        return notas;
    }

    public void deleteFavorito(String id){

        String query = "delete from favorito where idVerso = "+id;

        String patch = DB_PATH + CheckBancoExiste.DB_NAME ;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(patch, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);

        try {

            db.beginTransaction();

            db.execSQL(query);

            db.setTransactionSuccessful();


        } catch (Exception e) {

            throw  new Error("Delete compartilhar");

        } finally {
            db.endTransaction();
            db.close();
        }


    }

    public void setFavorito(String i){


        String q = "insert into favorito (idVerso) values ('"+i+"')";

        String k = "select count(*) from favorito where idVerso ="+i;

        openDataBase();

        cursor = myDataBase.rawQuery(k,null);

        int c = 0;

        if(cursor.moveToFirst())
           c = cursor.getInt(0);

        close();

       if(c == 0) {

           String patch = DB_PATH + CheckBancoExiste.DB_NAME ;
           SQLiteDatabase db = SQLiteDatabase.openDatabase(patch, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);

           try {

               db.beginTransaction();

               db.execSQL(q);

               db.setTransactionSuccessful();


           } catch (Exception e) {

               throw new Error("Insert favorito ID");

           } finally {
               db.endTransaction();
               db.close();
           }

       }


    }

    public List<Biblia> getFavorito() {

        List<Biblia> books = new LinkedList<Biblia>();

        String query = "select favorito.idVerso,books.name,verses.chapter,verses.verse,verses.text " +
                "from testament,verses,books,favorito where testament.id = verses.testament and books.id = verses.book " +
                "and [favorito].[idVerso] = verses.[id]";

        openDataBase();

        cursor = myDataBase.rawQuery(query, null);

        Biblia biblia;

        if(cursor!=null && cursor.getCount()>0)
            if (cursor.moveToFirst()) {
                do {

                        biblia = new Biblia();
                        biblia.setIdVerse(cursor.getString(0));
                        biblia.setBooksName(cursor.getString(1));
                        biblia.setVersesChapter(cursor.getString(2));
                        biblia.setVerseNum(cursor.getString(3));
                        biblia.setText(cursor.getString(4));

                    books.add(biblia);

                } while (cursor.moveToNext());
            }

        close();

        return books;
    }

    // tabelaName = favorito;
    //campos = "(id integer primary key,idVerso TINYINT(3) not null)";

    public void criarTabela(String tabelaName,String campos){

        String b = "CREATE TABLE IF NOT EXISTS "+tabelaName+campos;

        openDataBase();

        if(myDataBase.isOpen()) {
            myDataBase.execSQL(b);
            close();
        }

    }

    public int tabelaExiste(String tableName){


        String b = "SELECT count(*) FROM sqlite_master WHERE type='table' AND name='"+tableName+"'";

         openDataBase();

          if(myDataBase.isOpen()) {

            cursor = myDataBase.rawQuery(b, null);

            cursor.moveToFirst();

            int i = cursor.getInt(0);

            close();

            return  i;

        }

        return  0;

    }


    public class Dicionario {

        private int id;
        private String palavra;

        private String texto;

        public String getTexto() {
            return texto;
        }

        public void setTexto(String texto) {
            this.texto = texto;
        }



        public String getPalavra() {
            return palavra;
        }

        public void setPalavra(String palavra) {
            this.palavra = palavra;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }


    }

    public final class NonFatalError extends RuntimeException {

        public NonFatalError(String msg) {
            super(msg);
        }
    }

    /*
    // Updating single book
    public int updateBook(Book book) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = icon_new ContentValues();
        values.put("title", book.getTitle()); // get title
        values.put("author", book.getAuthor()); // get author

        // 3. updating row
        int i = db.update(TABLE_BOOKS, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                icon_new String[] { String.valueOf(book.getId()) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    // Deleting single book
    public void deleteBook(Book book) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_BOOKS,
                KEY_ID+" = ?",
                icon_new String[] { String.valueOf(book.getId()) });

        // 3. close
        db.close();

        Log.d("deleteBook", book.toString());

    }
    */

    public class VersDoDia extends Biblia {

        private String assunto = "Amor";

        public void setAssunto(String assunto){
            this.assunto  = assunto;

        }

        public String getAssunto(){
            return assunto;
        }

        @Override
        public String toString() {
            return getVersesText()+ " ("+ getBooksName()+" " + getVersesChapter()+":"+ getVersesNum()+")" ;
        }
    }


}