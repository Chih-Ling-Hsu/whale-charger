package com.netdb.nthu.whalecharger;

/**
 * Created by user on 2015/1/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.dm.zbar.android.scanner.ZBarConstants;
import com.dm.zbar.android.scanner.ZBarScannerActivity;

import com.netdb.nthu.whalecharger.dataBase.MessageDAO;
import com.netdb.nthu.whalecharger.model.Message;
import com.netdb.nthu.whalecharger.service.MessageAdapter;

import net.sourceforge.zbar.Symbol;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ChargeListActivity extends Activity {
    private static final int ZBAR_SCANNER_REQUEST = 0;
    private static final int TEXT_REQUEST = 1;
    private static final int ITEM_REQUEST = 2;
    private static final int ADD = 1;
    private static final int DELETE = 0;
    private static final int UPDATE = -1;
    public static final int DAY = 1;
    public static final int MONTH = 2;
    public static final int YEAR = 3;
    private static long update = 0;
    private static int pos;
    private static long price;

    private ImageButton qrBtn;
    private ImageButton textBtn;
    private ImageButton settingBtn;
    private TextView sumTxt;
    private ListView mListView;
    private RadioButton dayRBtn;
    private RadioButton monthRBtn;
    private RadioButton yearRBtn;
    private RadioGroup rgroup;
    private ImageView backBtn;

    private List<Message> mMessageList = new ArrayList<>();
    private MessageAdapter mMessageAdapter;
    private MessageDAO messageDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_list);

        // Initialize views
        qrBtn = (ImageButton) findViewById(R.id.btn_qr);
        textBtn = (ImageButton) findViewById(R.id.btn_text);
        settingBtn = (ImageButton) findViewById(R.id.btn_setting);
        mListView = (ListView) findViewById(R.id.list_message);
        sumTxt = (TextView) findViewById(R.id.txt_sum);
        dayRBtn = (RadioButton) findViewById(R.id.rBtn_day);
        monthRBtn = (RadioButton) findViewById(R.id.rBtn_month);
        yearRBtn = (RadioButton) findViewById(R.id.rBtn_year);
        rgroup = (RadioGroup) findViewById(R.id.rgroup);
        dayRBtn.setChecked(true);
        backBtn = (ImageView) findViewById(R.id.backbtn);
        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set messages to the list view
        messageDAO = new MessageDAO();
        messageDAO.removeALLMessage(ChargeListActivity.this);
        mMessageList = messageDAO.getAllMessages(ChargeListActivity.this,DAY);
        price=0;
        for(int i=0;i<mMessageList.size();i++){
            price+=mMessageList.get(i).getPrice();
        }
        sumTxt.setText("Total Expenditure: $"+price);
        Collections.sort(mMessageList, new Comparator<Message>() {
            public int compare(Message a, Message b) {
                if (a.getId() >= b.getId()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        mMessageAdapter = new MessageAdapter(this, mMessageList);
        mListView.setAdapter(mMessageAdapter);


        qrBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChargeListActivity.this, ZBarScannerActivity.class);
                intent.putExtra(ZBarConstants.SCAN_MODES, new int[]{Symbol.QRCODE});
                startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
            }
        });

        textBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ChargeListActivity.this, PostMessageActivity.class);
                startActivityForResult(intent, PostMessageActivity.REQUEST_CODE);
            }
        });


        settingBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ChargeListActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        rgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                messageDAO = new MessageDAO();
                switch (checkedId) {
                    case R.id.rBtn_day:
                        mMessageList = messageDAO.getAllMessages(ChargeListActivity.this,DAY);
                        break;
                    case R.id.rBtn_month:
                        mMessageList = messageDAO.getAllMessages(ChargeListActivity.this,MONTH);
                        break;
                    case R.id.rBtn_year:
                        mMessageList = messageDAO.getAllMessages(ChargeListActivity.this,YEAR);
                        break;
                }
                price=0;
                for(int i=0;i<mMessageList.size();i++){
                    price+=mMessageList.get(i).getPrice();
                }
                sumTxt.setText("Total Expenditure: $" + price);
                Collections.sort(mMessageList, new Comparator<Message>() {
                    public int compare(Message a, Message b) {
                        if (a.getId() >= b.getId()) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                });
                mMessageAdapter.setList(mMessageList);
                mMessageAdapter.notifyDataSetChanged();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent= new Intent(ChargeListActivity.this, ItemActivity.class);
                pos=position;
                Message message = mMessageList.get(position);
                update=message.getId();
                Bundle bundle = new Bundle();
                bundle.putLong("POSITION", update);
                //System.out.printf("-----------------position=%d id=%d-----------------\n",pos,update);
                intent.putExtras(bundle);
                startActivityForResult(intent, ItemActivity.REQUEST_CODE);
            }
        });

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case ZBAR_SCANNER_REQUEST:
                //dael with QRCode message
                if (resultCode == RESULT_OK) {
                    String qrData = data.getStringExtra(ZBarConstants.SCAN_RESULT);
                    int amount = QRCodeparser(qrData);
                    if (amount == 0) {
                        Toast.makeText(this, "No information in this QR Code！", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(this, "You add "+String.valueOf(amount)+" new expense(s).", Toast.LENGTH_SHORT).show();
                        updateMessageList(amount);
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    //Toast.makeText(this, "failed to add a new charge record", Toast.LENGTH_SHORT).show();
                }
                break;
            case TEXT_REQUEST:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "You add a new expense.", Toast.LENGTH_SHORT).show();
                    updateMessageList(ADD);
                }
                break;
            case ITEM_REQUEST:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    Integer mode = bundle.getInt("MODE");
                    switch(mode){
                        case UPDATE:
                            updateMessageList(UPDATE);
                            break;
                        case DELETE:
                            Toast.makeText(this, "You delete a expense.", Toast.LENGTH_SHORT).show();
                            updateMessageList(DELETE);
                    }
                }
        }
    }

    private void updateMessageList(int mode){
        messageDAO = new MessageDAO();
        Message message;
        switch(mode){
            case DELETE:
                sumTxt.setText("Total Expenditure: $"+String.valueOf(price-=mMessageList.get(pos).getPrice()));
                messageDAO.removeMessage(ChargeListActivity.this, update);
                mMessageList.remove(pos);
                break;
            case UPDATE:
                message = messageDAO.getMessageById(ChargeListActivity.this, update);
                sumTxt.setText("Total Expenditure: $"+String.valueOf(price+=(message.getPrice()-mMessageList.get(pos).getPrice())));
                mMessageList.set(pos,message);
                break;
            default:
                message = messageDAO.getLastMessage(ChargeListActivity.this);
                mMessageList.add(0, message);
                price+=message.getPrice();
                long id = message.getId();
                for(long i=1;i<mode;i++){
                    message = messageDAO.getMessageById(ChargeListActivity.this,id-i);
                    mMessageList.add(0, message);
                    price+=message.getPrice();
                    break;
                }
                sumTxt.setText("Total Expenditure: $"+String.valueOf(price));
        }
        mListView.setAdapter(mMessageAdapter);
        mMessageAdapter.notifyDataSetChanged();
    }


    private int QRCodeparser(String data){
        Calendar calendar = Calendar.getInstance();
        List<Message> messages = new ArrayList<>();
        Message message;
        String[] parsed_1 = data.split(":+");
        int mode,haveData=0;

        if(parsed_1[0].contains("**"))   mode=1;
        else mode=0;

        switch(mode){
            case 0://QR code on the left
                //if encoded in base64, then give up
                if (parsed_1[4].equals("2")) return 0;
                for(int i=0;i<(parsed_1.length-5)/3;i++) {
                    message = new Message();
                    message.setItem(parsed_1[5+i*3]);
                    message.setPrice(Integer.valueOf(parsed_1[5+i*3+2].trim()));
                    messages.add(message);
                    haveData=1;
                }
                break;
            case 1://QR code on the right
                if(parsed_1.length<=2) return 0;
                int offset = 0;
                if(parsed_1[0].equals("**")) offset = 1;
                else {
                    StringBuilder str = new StringBuilder(parsed_1[0]);
                    CharSequence parsed_2 = str.subSequence(2, parsed_1[0].length());
                    parsed_1[0] = String.valueOf(parsed_2);
                }
                for(int i=0;i<(parsed_1.length-offset)/3;i++){
                    if(parsed_1[offset + 3*i].equals("")) continue;
                    message = new Message();
                    message.setItem(parsed_1[offset + 3*i]);
                    message.setPrice(Integer.valueOf(parsed_1[offset + 3*i + 2].trim()));
                    messages.add(message);
                    haveData=1;
                }
                break;
        }
        if(haveData==0) return 0;

        //save into database
        messageDAO = new MessageDAO();
        for(int i=0;i<messages.size();i++){
            messages.get(i).setDay(calendar.get(Calendar.DAY_OF_MONTH));
            messages.get(i).setMonth(calendar.get(Calendar.MONTH) + 1);
            messages.get(i).setYear(calendar.get(Calendar.YEAR));

            messageDAO.addMessage(ChargeListActivity.this, messages.get(i));
        }
        return messages.size();
    }

}
