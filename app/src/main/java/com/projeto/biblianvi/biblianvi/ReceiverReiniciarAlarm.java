package com.projeto.biblianvi.biblianvi;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by Ezequiel on 25/05/2016.
 */

public class ReceiverReiniciarAlarm extends BroadcastReceiver {

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        agendarAlarmeVersiculo();
        Log.e("Broadcast","Iniciado");

    }


    private void agendarAlarmeVersiculo(){

        Intent it = new Intent(context, VersiculoDiario.class);
        PendingIntent p = PendingIntent.getBroadcast(context,121312131,it,0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        SharedPreferences settings = context.getSharedPreferences("alarme", Activity.MODE_PRIVATE);

        String h = settings.getString("hora", "9");
        String m = settings.getString("minuto", "0");

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(h));
        c.set(Calendar.MINUTE, Integer.parseInt(m));
        c.set(Calendar.SECOND, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, p);

    }

}
