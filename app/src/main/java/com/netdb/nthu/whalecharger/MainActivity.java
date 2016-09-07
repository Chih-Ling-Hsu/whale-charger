package com.netdb.nthu.whalecharger;



import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.content.res.Resources;

import com.netdb.nthu.whalecharger.dataBase.HistoryDAO;
import com.netdb.nthu.whalecharger.dataBase.MessageDAO;
import com.netdb.nthu.whalecharger.dataBase.ExpectedDAO;
import com.netdb.nthu.whalecharger.model.HistoryItem;
import com.netdb.nthu.whalecharger.model.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends Activity {
    private int money;
    private int number;
    private int whaleMode;
    private MessageDAO messageDAO = new MessageDAO();
    private ExpectedDAO expectedDAO = new ExpectedDAO();
    private Message message;
    private HistoryDAO itemDAO;
    private HistoryItem item;

    private ImageButton hCharger;
    private ImageButton budgetBtn;
    private ImageButton guideBtn;
    private ImageView whaleView;
    private TextView whaleDialog;
    private TextView budgetView;
    private TextView remainView;
    private TextView durationView;
    private ProgressBar progressBar;
    private SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
    private Calendar today = Calendar.getInstance();//current time
    private Handler myHandler = new Handler();
    AnimationDrawable anim;
    int a=-1,b=-1,c=-1;

    String[] dialogSet = {"I'm so lonely.", "I miss you.", "Hello, do you want to play with me?", "I like you. Do you like me too?", "Love you, my dear."};
    int[] animSet = {R.drawable.whale_interact_1, R.drawable.whale_interact_2, R.drawable.whale_interact_3, R.drawable.whale_interact_4, R.drawable.whale_interact_5};


    private Runnable closeWhaleDialog = new Runnable(){
        @Override
        public void run(){
            whaleDialog.setAlpha(0);
            anim.stop();
            Resources resf = getResources();
            anim = (AnimationDrawable)resf.getDrawable(R.drawable.whale_normal);
            whaleView.setImageDrawable(anim);
            anim.start();
        }
    };

    private OnClickListener hCListener = new OnClickListener(){
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, ChargeListActivity.class);
            startActivity(intent);
        }
    };

    private OnClickListener budgetListener = new OnClickListener(){
        @Override
        public void onClick(View v) {
            // Get the layout inflater
            LayoutInflater inflater = MainActivity.this.getLayoutInflater();
            View alert_view = inflater.inflate(R.layout.add_pop,null);

            // dialog.setView(inflater.inflate(R.layout.add_pop, null));
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setView(alert_view);

            final EditText Money = (EditText)alert_view.findViewById(R.id.setmoney);
            final EditText Number = (EditText)alert_view.findViewById(R.id.setnumber);
            final AlertDialog dialog = builder.setPositiveButton(getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //remind user to enter
                            try{
                                itemDAO = new HistoryDAO();
                                money = Integer.valueOf(Money.getText().toString().trim());
                                number =  Integer.valueOf(Number.getText().toString());

                                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                Calendar duetime = Calendar.getInstance();
                                Calendar current = Calendar.getInstance();
                                duetime.roll(Calendar.DAY_OF_YEAR,number-1);
                                String dueT = df.format(duetime.getTime());
                                String curT = df.format(current.getTime());
                                itemDAO.addHistoryItem(MainActivity.this, -1,curT,dueT,money,"Incompleted",0,number);
                                onResume();
                                Toast.makeText(MainActivity.this, "Your charging plan would start from today.", Toast.LENGTH_LONG).show();
                            }catch(Exception e){

                            }
                        }
                    }).setNegativeButton(getString(R.string.cancel),new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                }
            }).create();

            dialog.show();//show the dialog
        }
    };

    private OnClickListener whaleListener = new OnClickListener(){
        public void onClick(View v) {
            whaleDialog.setText(dialogSet[whaleMode]);
            whaleDialog.setAlpha(1);

            anim.stop();
            Resources resf = getResources();
            int animId = animSet[whaleMode];
            anim = (AnimationDrawable)resf.getDrawable(animId);
            whaleView.setImageDrawable(anim);
            anim.start();

            myHandler.postDelayed(closeWhaleDialog, 4000);//Message will be delivered in 1 second.
        }
    };

    private OnClickListener guideListener = new OnClickListener(){
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, GuideActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hCharger = (ImageButton) findViewById(R.id.btn_book);
        budgetBtn = (ImageButton) findViewById(R.id.btn_budget);
        guideBtn = (ImageButton) findViewById(R.id.btn_guide);
        whaleView = (ImageView) findViewById(R.id.imageView);
        whaleDialog = (TextView) findViewById(R.id.whaledialog);
        budgetView = (TextView)findViewById(R.id.budgetview);
        durationView = (TextView)findViewById(R.id.durationview);
        remainView = (TextView)findViewById(R.id.remainview);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        /*Set interactions*/
        hCharger.setOnClickListener(hCListener);
        budgetBtn.setOnClickListener(budgetListener);
        guideBtn.setOnClickListener(guideListener);
        whaleView.setOnClickListener(whaleListener);

        //Cancel all notifications
        NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();


        /*Set preferences*/
        SharedPreferences prefs = getSharedPreferences("WhaleSettings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        Calendar last_message = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        String lastLogin = prefs.getString("lastLogin", "none");
        whaleMode= prefs.getInt("whaleMode", 2);
        String date_now = today.get(Calendar.DAY_OF_MONTH)+"/"+today.get(Calendar.MONTH)+"/"+today.get(Calendar.YEAR);
        if (lastLogin.equals(date_now)==false) {
            if(lastLogin.equals("none")==true){
                whaleMode=1;
                //初次登入，設定鬧鐘
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 22);
                calendar.set(Calendar.MINUTE, 0);
                Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmMgr = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
            }
            else {
                int last_duration = 1;
                if (messageDAO.getCount(MainActivity.this)!=0) {
                    message = messageDAO.getLastMessage(MainActivity.this);
                    last_message.set(message.getYear(), message.getMonth()-1, message.getDay());
                    last_duration = today.get(Calendar.DAY_OF_YEAR) - last_message.get(Calendar.DAY_OF_YEAR) - 1;
                }
                if (last_duration != 0) {
                    whaleMode = whaleMode-last_duration;
                } else {
                    whaleMode = whaleMode + 1;
                }
                if (whaleMode>4){
                    whaleMode = 4;
                }
                else if (whaleMode<0){
                    whaleMode = 0;
                }
                editor.putInt("whaleMode", whaleMode);
                editor.commit();
            }

            editor.putString("lastLogin", date_now);
            editor.commit();

            //process future expenses
            expectedDAO.checkMessages(MainActivity.this);
        }

        /*generate views*/
        //set the animate
        Resources resf = getResources();
        anim = (AnimationDrawable)resf.getDrawable(R.drawable.whale_normal);
        whaleView.setImageDrawable(anim);
        anim.start();

        if(itemDAO.getCount(MainActivity.this)==0){
            budgetView.setText("No charging plan in process.");
            durationView.setText("Hi there, nice to meet you.");
            remainView.setText("");
            progressBar.setMax(100);
            progressBar.setProgress(0);
            progressBar.setSecondaryProgress(0);
        }
        else{
            /*Get content of latest charging plan*/
            item = itemDAO.getLastItem(MainActivity.this);
            int budget_money = item.getGoal();
            //int duration = item.getUnit();
            String date_end = item.getDateEnd();
            String date_origin = item.getDateOrigin();
            Calendar cal_end = Calendar.getInstance();
            Calendar cal_origin = Calendar.getInstance();
            try {
                cal_end.setTime(df2.parse(date_end));
                cal_origin.setTime(df2.parse(date_origin));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int remain_duration = cal_end.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR);
            if (remain_duration<=0 || cal_origin.get(Calendar.DAY_OF_YEAR) > today.get(Calendar.DAY_OF_YEAR)){
                budgetView.setText("No charging plan in process.");
                durationView.setText("Hi there, nice to meet you.");
                remainView.setText("");
                progressBar.setMax(100);
                progressBar.setProgress(0);
                progressBar.setSecondaryProgress(0);
            }
            else {
                /*Get present money*/
                int present_money = messageDAO.getPresentMoney(MainActivity.this, cal_origin, yesterday);

                /*Get today Money*/
                int today_money = messageDAO.getTodayMoney(MainActivity.this);

                /*Set text & bar*/
                budgetView.setText("$" + budget_money);
                durationView.setText(date_origin + "~" + date_end);
                int remain_money = budget_money - present_money - today_money;
                if(remain_money<=0){
                    remainView.setText("Already run out of money.");
                    progressBar.setMax(budget_money);
                    progressBar.setProgress(budget_money);
                    progressBar.setSecondaryProgress(budget_money);
                }
                else {
                    int remain_average = remain_money / remain_duration;
                    remainView.setText("$" + remain_money + " remain for " + remain_duration + " days  ($"+remain_average+" /day)");
                    progressBar.setMax(budget_money);
                    progressBar.setProgress(present_money+today_money);
                    progressBar.setSecondaryProgress(present_money);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Always call the superclass method first

        //Cancel all notifications
        NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();

       /*Set preferences*/
        SharedPreferences prefs = getSharedPreferences("WhaleSettings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        Calendar last_message = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        String lastLogin = prefs.getString("lastLogin", "none");
        whaleMode= prefs.getInt("whaleMode", 2);
        String date_now = today.get(Calendar.DAY_OF_MONTH)+"/"+today.get(Calendar.MONTH)+"/"+today.get(Calendar.YEAR);
        if (lastLogin.equals(date_now)==false) {
            if(lastLogin.equals("none")==true){
                whaleMode=1;
            }
            else {
                int last_duration = 1;
                if (messageDAO.getCount(MainActivity.this)!=0) {
                    message = messageDAO.getLastMessage(MainActivity.this);
                    last_message.set(message.getYear(), message.getMonth(), message.getDay());
                    last_duration = today.get(Calendar.DAY_OF_YEAR) - last_message.get(Calendar.DAY_OF_YEAR) - 1;
                }
                if (last_duration != 0) {
                    whaleMode = whaleMode - last_duration >= 0 ? whaleMode - last_duration : 0;
                    editor.putInt("whaleMode", whaleMode);
                    editor.commit();
                } else {
                    if (whaleMode < 4) whaleMode = whaleMode + 1;
                    editor.putInt("whaleMode", whaleMode);
                    editor.commit();
                }
            }

            editor.putString("lastLogin", date_now);
            editor.commit();

            //process future expenses
            expectedDAO.checkMessages(MainActivity.this);
        }

        /*generate views*/
        //set the animate
        Resources resf = getResources();
        anim = (AnimationDrawable)resf.getDrawable(R.drawable.whale_normal);
        whaleView.setImageDrawable(anim);
        anim.start();

        if(itemDAO.getCount(MainActivity.this)==0){
            budgetView.setText("No charging plan in process.");
            durationView.setText("Hi there, nice to meet you.");
            remainView.setText("");
            progressBar.setMax(100);
            progressBar.setProgress(0);
            progressBar.setSecondaryProgress(0);
        }
        else{
            /*Get content of latest charging plan*/
            item = itemDAO.getLastItem(MainActivity.this);
            int budget_money = item.getGoal();
            //int duration = item.getUnit();
            String date_end = item.getDateEnd();
            String date_origin = item.getDateOrigin();
            Calendar cal_end = Calendar.getInstance();
            Calendar cal_origin = Calendar.getInstance();
            try {
                cal_end.setTime(df2.parse(date_end));
                cal_origin.setTime(df2.parse(date_origin));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int remain_duration = cal_end.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR);
            if (remain_duration<0 || cal_origin.get(Calendar.DAY_OF_YEAR) > today.get(Calendar.DAY_OF_YEAR)){
                budgetView.setText("No charging plan in process.");
                durationView.setText("Hi there, nice to meet you.");
                remainView.setText("");
                progressBar.setMax(100);
                progressBar.setProgress(0);
                progressBar.setSecondaryProgress(0);
            }
            else {
                /*Get present money*/
                int present_money = messageDAO.getPresentMoney(MainActivity.this, cal_origin, yesterday);

                /*Get today Money*/
                int today_money = messageDAO.getTodayMoney(MainActivity.this);

                /*Set text & bar*/
                budgetView.setText("$" + budget_money);
                durationView.setText(date_origin + "~" + date_end);
                int remain_money = budget_money - present_money - today_money;
                if(remain_money<=0){
                    remainView.setText("Already run out of money.");
                    progressBar.setMax(budget_money);
                    progressBar.setProgress(budget_money);
                    progressBar.setSecondaryProgress(budget_money);
                }
                else {
                    int remain_average = remain_money / remain_duration;
                    remainView.setText("$" + remain_money + " remain for " + remain_duration + " days  ($"+remain_average+" /day)");
                    progressBar.setMax(budget_money);
                    progressBar.setProgress(present_money+today_money);
                    progressBar.setSecondaryProgress(present_money);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

}
