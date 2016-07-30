package com.netdb.nthu.whalecharger;

/**
 * Created by user on 2015/1/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.netdb.nthu.whalecharger.dataBase.MessageDAO;
import com.netdb.nthu.whalecharger.model.Message;
import com.netdb.nthu.whalecharger.service.MessageAdapter;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SearchActivity extends Activity {

    private ImageView searchBtn;
    private ListView mListView;
    private RadioButton itemRBtn;
    private RadioButton categoryRBtn;
    private TextView resultTxt;
    private EditText editSearch;
    private ImageView backBtn;
    private RadioGroup rgroup;
    private int mode = 0;
    private String [] list = {"food","clothes","transportation","others"};

    private List<Message> mMessageList = new ArrayList<>();
    private MessageAdapter mMessageAdapter;
    private MessageDAO messageDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize views
        mListView = (ListView) findViewById(R.id.list_message);
        itemRBtn = (RadioButton) findViewById(R.id.rBtn_item);
        categoryRBtn = (RadioButton) findViewById(R.id.rBtn_category);
        resultTxt = (TextView) findViewById(R.id.search_result);
        searchBtn = (ImageView) findViewById(R.id.btn_search);
        editSearch = (EditText) findViewById(R.id.text_search);
        rgroup = (RadioGroup) findViewById(R.id.rgroup);
        backBtn = (ImageView) findViewById(R.id.backbtn);
        backBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        itemRBtn.setChecked(true);

        // Set messages to the list view
        messageDAO = new MessageDAO();
        messageDAO.removeALLMessage(SearchActivity.this);


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = String.valueOf(editSearch.getText());
                mMessageList = messageDAO.fetchMessages(SearchActivity.this, searchText, mode);
                Collections.sort(mMessageList, new Comparator<Message>() {
                    public int compare(Message a, Message b) {
                        if (a.getId() >= b.getId()) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                });
                if (mMessageList.isEmpty()){
                    resultTxt.setText("None matched.");
                }
                else {
                    mMessageAdapter = new MessageAdapter(SearchActivity.this, mMessageList);
                    mListView.setAdapter(mMessageAdapter);
                }
            }
        });

        rgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                messageDAO = new MessageDAO();
                switch (checkedId) {
                    case R.id.rBtn_item:
                        mode = 0;
                        break;
                    case R.id.rBtn_category:
                        mode = 1;
                        break;
                }
            }
        });
    }

}