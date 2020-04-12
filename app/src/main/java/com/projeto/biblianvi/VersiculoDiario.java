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
    private  Context context;


    @Override
    public void onReceive(Context context, Intent intent) {

       this.context = context;

        try {
            criarNotification();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private void versiculoDoDia() throws ParseException {


        bibliaHelp = new BibliaBancoDadosHelper(context);
        checkBancoExiste = new CheckBancoExiste(context);

        SharedPreferences settings ;

        try {
            if (checkBancoExiste.checkDataBase() && checkBancoExiste.checarIntegridadeDoBanco()) {
                bibliaHelp.versDoDiaText();
                settings = context.getSharedPreferences("versDiaPreference", Activity.MODE_PRIVATE);
                assunto = settings.getString("assunto", "Paz");
                livro = settings.getString("livroNome", "João");
                cap = settings.getString("capVersDia", "16");
                vers = settings.getString("verVersDia", "Tenho-vos dito isto, para que em mim tenhais paz; no mundo tereis aflições, mas tende bom ânimo, eu venci o mundo.");

            }else{
                assunto = "Houve um erro na solicitação. Por favor, comunique ao desenvolvedor";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    String assunto;
    String livro;
    String cap;
    String vers;
    private void criarNotification() throws ParseException {

        versiculoDoDia();

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
