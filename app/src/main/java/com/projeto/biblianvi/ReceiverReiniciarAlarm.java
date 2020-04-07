package com.projeto.biblianvi;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by Ezequiel on 25/05/2016.
 */

public class ReceiverReiniciarAlarm extends BroadcastReceiver {

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

       if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        agendarAlarmeVersiculo(9);

    }


    private void agendarAlarmeVersiculo(int hora){

        Intent it = new Intent("EXECUTAR_ALARME");
        PendingIntent p = PendingIntent.getBroadcast(context,0,it,0);

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, hora);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),AlarmManager.INTERVAL_DAY,p);


    }

}
