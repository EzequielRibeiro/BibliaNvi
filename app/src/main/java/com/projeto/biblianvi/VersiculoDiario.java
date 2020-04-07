package com.projeto.biblianvi;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ezequiel on 25/05/2016.
 */

public class VersiculoDiario extends BroadcastReceiver{

    private int notifyID = 0;
    private  BibliaBancoDadosHelper bibliaHelp;
    private CheckBancoExiste checkBancoExiste;
    private boolean existeBancoDados = false;
    private  Context context;


    @Override
    public void onReceive(Context context, Intent intent) {

       this.context = context;

        try {
            versiculoDoDia();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void versiculoDoDia() throws ParseException {


        bibliaHelp = new BibliaBancoDadosHelper(context);
        checkBancoExiste = new CheckBancoExiste(context);

        SharedPreferences settings = context.getSharedPreferences("data_preferences", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date1 = sdf.parse(settings.getString("dia","1")+
                "/"+settings.getString("mes","1")+
                "/"+settings.getString("ano","1999"));


        Calendar c2 = Calendar.getInstance();

        Date date2 = sdf.parse(Integer.toString(c2.get(Calendar.DAY_OF_MONTH))+
                "/"+Integer.toString(c2.get(Calendar.MONTH))+
                "/"+Integer.toString(c2.get(Calendar.YEAR)));


        try {
            if (checkBancoExiste.checkDataBase() && checkBancoExiste.checarIntegridadeDoBanco())
                existeBancoDados = true;
        } catch (IOException e) {
            e.printStackTrace();
        }


        if(existeBancoDados)
            if(date1.before(date2)){

                bibliaHelp.versDoDiaText(null,null,true);

                editor.putString("ano",Integer.toString(c2.get(Calendar.YEAR)));
                editor.putString("mes",Integer.toString(c2.get(Calendar.MONTH)));
                editor.putString("dia",Integer.toString(c2.get(Calendar.DAY_OF_MONTH)));

                editor.commit();

                settings =  context.getSharedPreferences("versDia", Activity.MODE_PRIVATE);
                assunto = settings.getString("assunto","Paz");
                livro =  settings.getString("livroVersDia"," ");
                cap =    settings.getString("capVersDia"," ");
                vers =   settings.getString("verVersDia"," ");

                criarNotification();


            }else{

                Log.e("AgendarVersiculos","Data anterior");

            }
    }

    String assunto;
    String livro;
    String cap;
    String vers;
    private void criarNotification(){


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setAutoCancel(true)
                        .setSound(alarmSound)
                        .setSmallIcon(R.mipmap.biblia_icon_notification)
                        .setContentTitle(context.getResources().getString(R.string.app_name))
                        .setContentText("Versículo do dia: "+assunto+" ("+livro+" "+cap+":"+vers+")");

        Intent resultIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(notifyID, mBuilder.build());



    }
}
