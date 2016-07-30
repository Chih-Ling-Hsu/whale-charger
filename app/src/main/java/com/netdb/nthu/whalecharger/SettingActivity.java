package com.netdb.nthu.whalecharger;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by user on 2016/7/25.
 */
public class SettingActivity extends Activity {

    private TextView expectbtn;
    private ImageView backBtn;
    private TextView searchbtn;
    private TextView alarmbtn;

    private View.OnClickListener expectListener = new View.OnClickListener(){
        public void onClick(View v) {
            Intent intent = new Intent(SettingActivity.this, ExpectedListActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
    };
    private View.OnClickListener searchListener = new View.OnClickListener(){
        public void onClick(View v) {
            Intent intent = new Intent(SettingActivity.this, SearchActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
    };
    private View.OnClickListener alarmListener = new View.OnClickListener(){
        public void onClick(View v) {
            Intent intent = new Intent(SettingActivity.this, AlarmActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Initialize views
        expectbtn = (TextView) findViewById(R.id.expectedexpenses);
        searchbtn = (TextView) findViewById(R.id.datasearch);
        alarmbtn = (TextView) findViewById(R.id.alarm);
        backBtn = (ImageView) findViewById(R.id.backbtn);
        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set interactions
        expectbtn.setOnClickListener(expectListener);
        searchbtn.setOnClickListener(searchListener);
        alarmbtn.setOnClickListener(alarmListener);
    }
}
