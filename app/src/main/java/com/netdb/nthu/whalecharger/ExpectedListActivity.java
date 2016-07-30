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

import com.netdb.nthu.whalecharger.dataBase.ExpectedDAO;
import com.netdb.nthu.whalecharger.dataBase.MessageDAO;
import com.netdb.nthu.whalecharger.model.Message;
import com.netdb.nthu.whalecharger.service.MessageAdapter;

import net.sourceforge.zbar.Symbol;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ExpectedListActivity extends Activity {
    private static final int TEXT_REQUEST = 1;
    private static final int ITEM_REQUEST = 2;
    private static final int ADD = 1;
    private static final int DELETE = 0;
    private static final int UPDATE = -1;
    public static final int ALL = 3;
    private static long update = 0;
    private static int pos;

    private ImageButton textBtn;
    private ListView mListView;
    private ImageView backBtn;

    private List<Message> mMessageList = new ArrayList<>();
    private MessageAdapter mMessageAdapter;
    private ExpectedDAO messageDAO = new ExpectedDAO();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expected_list);

        // Initialize views
        textBtn = (ImageButton) findViewById(R.id.btn_add);
        mListView = (ListView) findViewById(R.id.list_expected_message);
        backBtn = (ImageView) findViewById(R.id.backbtn);
        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set messages to the list view
        messageDAO.removeALLMessage(ExpectedListActivity.this);
        mMessageList = messageDAO.getAllMessages(ExpectedListActivity.this,ALL);
        mMessageAdapter = new MessageAdapter(this, mMessageList);
        mListView.setAdapter(mMessageAdapter);

        textBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ExpectedListActivity.this, PostExpectedMsgActivity.class);
                startActivityForResult(intent, PostExpectedMsgActivity.REQUEST_CODE);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent= new Intent(ExpectedListActivity.this, ExpectedItemActivity.class);
                pos=position;
                Message message = mMessageList.get(position);
                update=message.getId();
                Bundle bundle = new Bundle();
                bundle.putLong("POSITION", update);
                intent.putExtras(bundle);
                startActivityForResult(intent, ExpectedItemActivity.REQUEST_CODE);
            }
        });

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case TEXT_REQUEST:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "You add a future expense.", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(this, "You delete a future expense.", Toast.LENGTH_SHORT).show();
                            updateMessageList(DELETE);
                    }
                }
        }
    }

    private void updateMessageList(int mode){
        Message message;
        switch(mode){
            case DELETE:
                messageDAO.removeMessage(ExpectedListActivity.this, update);
                mMessageList.remove(pos);
                break;
            case UPDATE:
                message = messageDAO.getMessageById(ExpectedListActivity.this, update);
                mMessageList.set(pos,message);
                break;
            default:
                message = messageDAO.getLastMessage(ExpectedListActivity.this);
                mMessageList.add(0, message);
        }
        mListView.setAdapter(mMessageAdapter);
        mMessageAdapter.notifyDataSetChanged();
    }

}
