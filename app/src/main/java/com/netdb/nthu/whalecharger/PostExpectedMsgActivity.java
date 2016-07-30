package com.netdb.nthu.whalecharger;

/**
 * Created by user on 2015/1/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.netdb.nthu.whalecharger.dataBase.ExpectedDAO;
import com.netdb.nthu.whalecharger.model.Message;



import java.util.Calendar;
import com.netdb.nthu.whalecharger.dataBase.MessageDAO;

public class PostExpectedMsgActivity extends Activity{
    public static final int REQUEST_CODE = 1;
    private EditText edt_item;
    private EditText edt_price;
    private EditText edt_day;
    private EditText edt_month;
    private EditText edt_year;
    private Spinner spinner;
    private Button okBtn;
    private ImageView backBtn;
    private ArrayAdapter<String> listAdapter;
    private String selected;
    private int category;
    Context mContext;
    private String [] list = {"food","clothes","transportation","others"};
    private ExpectedDAO messageDAO = new ExpectedDAO();
    //private TextView textView5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_expectedmsg);

        edt_item = (EditText) findViewById(R.id.edit_item);
        edt_price = (EditText) findViewById(R.id.edit_price);
        edt_day = (EditText) findViewById(R.id.edit_day);
        edt_month = (EditText) findViewById(R.id.edit_month);
        edt_year = (EditText) findViewById(R.id.edit_year);
        spinner = (Spinner) findViewById(R.id.edt_category);
        listAdapter = new ArrayAdapter<String>(this, R.layout.myspinner, list);
        listAdapter.setDropDownViewResource(R.layout.myspinner);
        spinner.setAdapter(listAdapter);
        okBtn = (Button) findViewById(R.id.btn_ok);
        mContext = this.getApplicationContext();
        //textView5 = (TextView) findViewById(R.id.textView5);
        backBtn = (ImageView) findViewById(R.id.backbtn);
        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selected = parent.getItemAtPosition(pos).toString();
                for(int i=0;i<4;i++){
                    if(selected.equals(list[i])){
                        category = i;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                category = 0;
            }
        });
        okBtn.setOnClickListener(new Button.OnClickListener(){
                                     @Override
                                     public void onClick(View v){
                                         //textView5.setText(edt_item.getText().toString()+"/"+edt_price.getText().toString()+"/"+category);
                                         if(!edt_item.getText().toString().equals("")&&!edt_price.getText().toString().equals("")&&!edt_day.getText().toString().equals("")&&!edt_month.getText().toString().equals("")&&!edt_year.getText().toString().equals("")){
                                             postMessage(edt_item.getText().toString(), Integer.valueOf(edt_price.getText().toString()), category, Integer.valueOf(edt_day.getText().toString()), Integer.valueOf(edt_month.getText().toString()), Integer.valueOf(edt_year.getText().toString()));
                                             Intent dataIntent = new Intent();
                                             setResult(Activity.RESULT_OK, dataIntent);
                                             finish();
                                         }
                                         else{
                                             Toast.makeText(mContext, "information incomplete！",Toast.LENGTH_SHORT).show();
                                         }
                                     }
                                 }
        );
    }

    private void postMessage(String item, Integer price,Integer category, Integer day, Integer month, Integer year) {
        //save into database
        messageDAO.addMessage(getApplicationContext(), -1,item,category,price,year,month,day);
    }

}
