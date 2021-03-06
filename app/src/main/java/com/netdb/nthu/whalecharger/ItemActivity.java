package com.netdb.nthu.whalecharger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.netdb.nthu.whalecharger.dataBase.MessageDAO;
import com.netdb.nthu.whalecharger.model.Message;

import java.util.Calendar;

/**
 * Created by user on 2015/1/18.
 */
public class ItemActivity extends Activity {
    public static final int REQUEST_CODE = 2;
    public static final int DELETE = 0;
    public static final int UPDATE = -1;
    private EditText edt_item;
    private EditText edt_price;
    private Spinner spinner;
    private Button okBtn;
    private Button deleteBtn;
    private ImageView backBtn;
    private ArrayAdapter<String> listAdapter;
    private String selected;
    private int category;
    Context mContext;
    private String [] list = {"food","clothes","transportation","others"};
    private MessageDAO messageDAO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        Bundle bundle = this.getIntent().getExtras();
        long position = bundle.getLong("POSITION");
        //System.out.printf("-----------------i chose id=%d-----------------\n",position);
        final Message message = messageDAO.getMessageById(ItemActivity.this,position);

        edt_item = (EditText) findViewById(R.id.edit_item);
        edt_price = (EditText) findViewById(R.id.edit_price);
        spinner = (Spinner) findViewById(R.id.edt_category);
        listAdapter = new ArrayAdapter<String>(this, R.layout.myspinner, list);
        listAdapter.setDropDownViewResource(R.layout.myspinner);
        spinner.setAdapter(listAdapter);
        okBtn = (Button) findViewById(R.id.btn_ok);
        deleteBtn = (Button) findViewById(R.id.btn_delete);
        mContext = this.getApplicationContext();
        backBtn = (ImageView) findViewById(R.id.backbtn);
        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edt_item.setText(message.getItem());
        edt_price.setText(String.valueOf(message.getPrice()));
        spinner.setSelection(message.getCategory());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selected = parent.getItemAtPosition(pos).toString();
                for (int i = 0; i < 4; i++) {
                    if (selected.equals(list[i])) {
                        category = i;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                category = 4;
            }
        });
        okBtn.setOnClickListener(new Button.OnClickListener(){
                                     @Override
                                     public void onClick(View v){
                                         if(!edt_item.getText().toString().equals("")&&!edt_price.getText().toString().equals("")){
                                             messageDAO = new MessageDAO();
                                             messageDAO.addMessage(getApplicationContext(),message.getId(),edt_item.getText().toString(), category, Integer.valueOf(edt_price.getText().toString()),message.getYear(), message.getMonth(),message.getDay());

                                             Intent dataIntent = new Intent();
                                             Bundle bundle = new Bundle();
                                             bundle.putInt("MODE", UPDATE);
                                             dataIntent.putExtras(bundle);
                                             setResult(Activity.RESULT_OK, dataIntent);
                                             finish();
                                         }
                                         else{
                                             Toast.makeText(mContext, "information incomplete！", Toast.LENGTH_SHORT).show();
                                         }
                                     }
                                 }
        );
        deleteBtn.setOnClickListener(new Button.OnClickListener(){
                                         @Override
                                         public void onClick(View v){
                                             AlertDialog.Builder dialog = new AlertDialog.Builder(ItemActivity.this);
                                             dialog.setTitle("Notice");
                                             dialog.setMessage("Are you sure you want to remove this expense?");
                                             dialog.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                                                 public void onClick(DialogInterface dialog, int which) {
                                                     Intent dataIntent = new Intent();
                                                     Bundle bundle = new Bundle();
                                                     bundle.putInt("MODE", DELETE);
                                                     dataIntent.putExtras(bundle);
                                                     setResult(Activity.RESULT_OK, dataIntent);
                                                     finish();
                                                 }
                                             });
                                             dialog.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                                                 public void onClick(DialogInterface dialog, int which) {
                                                     dialog.dismiss();
                                                 }
                                             });

                                             dialog.create().show();
                                         }
                                     }
        );
    }
}
