package com.netdb.nthu.whalecharger.dataBase;


/**
 * Created by user on 2015/1/17.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MessageDBHandler extends SQLiteOpenHelper {

    Context context;

    public MessageDBHandler(Context context) {
        super(context, "messages.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MessageDAO.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldV, int newV) {
    }
}
