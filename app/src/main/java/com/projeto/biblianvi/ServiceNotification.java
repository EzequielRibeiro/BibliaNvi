package com.projeto.biblianvi;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * Created by Ezequiel on 24/04/2016.
 */
public class ServiceNotification extends Service {


    private int notifyID = 0;
    private  BibliaBancoDadosHelper bibliaHelp;
    private CheckBancoExiste checkBancoExiste;
    private boolean existeBancoDados = false;
    private boolean atualizarNotification = false;
    private static  int INTERVALONOTIFICATION = 5;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
       Log.d("ServiceNotification","created");


        new Thread(new Runnable() {
            @Override
            public void run() {


                try {

                    while(true) {

                        TimeUnit.HOURS.sleep(INTERVALONOTIFICATION);
                      //  Thread.sleep(INTERVALONOTIFICATION);

                        versiculoDoDia();

                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }).start();



    }


    private void versiculoDoDia() throws ParseException {


        bibliaHelp = new BibliaBancoDadosHelper(ServiceNotification.this);
        checkBancoExiste = new CheckBancoExiste(ServiceNotification.this);

        SharedPreferences settings = getSharedPreferences("data_preferences", Activity.MODE_PRIVATE);
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

                settings =  getSharedPreferences("versDia", Activity.MODE_PRIVATE);
                assunto = settings.getString("assunto","Paz");
                livro =  settings.getString("livroVersDia"," ");
                cap =    settings.getString("capVersDia"," ");
                vers =   settings.getString("verVersDia"," ");

                criarNotification();




            }
    }


    private void atualizarNotification(){

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        NotificationCompat.Builder  mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText("Novo versículo !")
                .setSmallIcon(R.mipmap.biblia_icon);

       int numMessages = 0;


        mNotifyBuilder.setContentText("Versículo do dia: "+assunto+" ("+livro+" "+cap+":"+vers+")")
                .setNumber(++numMessages);
        // Because the ID remains unchanged, the existing notification is
        // updated.
        mNotificationManager.notify(
                notifyID,
                mNotifyBuilder.build());


    }

    String assunto;
    String livro;
    String cap;
    String vers;
    private void criarNotification(){


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setAutoCancel(true)
                        .setSound(alarmSound)
                        .setSmallIcon(R.mipmap.biblia_icon_notification)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText("Versículo do dia: "+assunto+" ("+livro+" "+cap+":"+vers+")");

        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(notifyID, mBuilder.build());



    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ServiceNotification","destruído");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onCreate();
        Log.d("ServiceNotification","iniciado");
    }





}
