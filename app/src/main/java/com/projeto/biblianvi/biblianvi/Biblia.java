package com.projeto.biblianvi.biblianvi;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.Html;

/**
 * Created by Ezequiel on 08/07/2015.
 */
public class Biblia {

    private  SharedPreferences prefs = null;
    private Context context = null;
    private String termoBusca;

    private String testamentName,
                   booksName = "João",
                   versesChapter = "3",
                   versesNum = "16",
                   versesText = "Porque Deus amou o mundo de tal maneira que deu o seu Filho unigênito, " +
                           "para que todo aquele que nele crê não pereça, mas tenha a vida eterna";

    int lido;
    private String idVerse;

    public Biblia(){


    }

    public void setTestamentName(String n){

        testamentName = n;
    }

    public void setBooksName(String b){

        booksName = b;

    }

    public void setVersesChapter(String c){

        versesChapter = c;
    }

    public void setText(String t){

        versesText = t.replace(";","");

    }

    public void setVerseNum(String i){

        versesNum = i;
    }

    public void setLido(int lido){

        this.lido = lido;
    }

    public void setIdVerse(String i){

        idVerse = i;

    }

    public int getLido(){

        return  lido;

    }

    public String getIdVerse(){

        return idVerse;
    }

    public  String getBooksName(){

       if(booksName != null)
         return booksName;
        else
         return "0";
    }


    public String getVersesChapter(){
        if(versesChapter != null)
          return versesChapter;
        else
          return "0";
    }


    public String getVersesNum(){

        if(versesNum != null)
          return versesNum;
        else
          return "0";

    }

    @Override
    public String toString() {
        return "<b>"+versesNum+"</b>"+" "+ versesText;
    }

    public String getVersesText(){

        if(versesText != null)
          return versesText;
        else
          return "0";
    }

    public String toPesquisarString() {


        String texto = versesText.replace(termoBusca,"<font color=\"red\">"+termoBusca+"</font>");

        return "<p>"+booksName+" "+versesChapter+":"+versesNum+"</p>"+
              "<p>"+texto+"</p>";
    }

    public void setContext(Context context) {
        this.context = context;

        if(context != null) {

            prefs = context.getSharedPreferences("termo_busca", Activity.MODE_PRIVATE);
            termoBusca = prefs.getString("busca","a");
        }

    }
}
