package com.netdb.nthu.whalecharger;

/**
 * Created by user on 2015/1/17.
 */

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.netdb.nthu.whalecharger.model.Message;



import java.util.Calendar;
import com.netdb.nthu.whalecharger.dataBase.MessageDAO;

public class AlarmActivity extends Activity{
    private EditText edt_hr;
    private EditText edt_min;
    private Button okBtn;
    private ImageView backBtn;
    private Switch switch_on;
    private Switch switch_vib;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        SharedPreferences prefs = getSharedPreferences("AlarmSettings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        boolean alarmMode= prefs.getBoolean("alarmMode", true);
        boolean alarmVibrate= prefs.getBoolean("alarmVibrate", true);
        int alarmHour= prefs.getInt("alarmHour", 22);
        int alarmMin= prefs.getInt("alarmMin", 0);
        String alarmMinStr = alarmMin>=10? String.valueOf(alarmMin) : ("0"+String.valueOf(alarmMin));

        edt_hr = (EditText) findViewById(R.id.edit_hr);
        edt_min = (EditText) findViewById(R.id.edit_min);
        okBtn = (Button) findViewById(R.id.btn_ok);
        switch_on = (Switch) findViewById(R.id.alarmOn);
        switch_vib = (Switch) findViewById(R.id.alarmVibrate);
        backBtn = (ImageView) findViewById(R.id.backbtn);
        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edt_hr.setText(String.valueOf(alarmHour));
        edt_min.setText(alarmMinStr);
        switch_on.setChecked(alarmMode);
        switch_vib.setChecked(alarmVibrate);


        okBtn.setOnClickListener(new Button.OnClickListener(){
                                     @Override
                                     public void onClick(View v){
                                         if(!edt_hr.getText().toString().equals("")&&!edt_min.getText().toString().equals("")){
                                             SharedPreferences prefs = getSharedPreferences("AlarmSettings", MODE_PRIVATE);
                                             SharedPreferences.Editor editor = prefs.edit();
                                             editor.putBoolean("alarmMode", switch_on.isChecked());
                                             editor.putBoolean("alarmVibrate", switch_vib.isChecked());
                                             int hour = Integer.valueOf(edt_hr.getText().toString());
                                             int min = Integer.valueOf(edt_min.getText().toString());
                                             editor.putInt("alarmHour", hour);
                                             editor.putInt("alarmMin", min);
                                             editor.commit();

                                             AlarmManager alarmMgr = (AlarmManager) AlarmActivity.this.getSystemService(Context.ALARM_SERVICE);
                                             Intent intent = new Intent(AlarmActivity.this, AlarmReceiver.class);
                                             PendingIntent alarmIntent = PendingIntent.getBroadcast(AlarmActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                             if(switch_on.isChecked()) {
                                                 Calendar calendar = Calendar.getInstance();
                                                 calendar.set(Calendar.HOUR_OF_DAY, hour);
                                                 calendar.set(Calendar.MINUTE, min);
                                                 alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
                                             }
                                             else{
                                                  alarmMgr.cancel(alarmIntent);
                                             }

                                             finish();
                                         }
                                         else{
                                             Toast.makeText(AlarmActivity.this, "information incomplete！",Toast.LENGTH_SHORT).show();
                                         }
                                     }
                                 }
        );
    }


}
