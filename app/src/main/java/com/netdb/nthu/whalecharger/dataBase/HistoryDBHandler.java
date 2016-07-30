package com.netdb.nthu.whalecharger.dataBase;

/**
 * Created by user on 2015/1/18.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HistoryDBHandler extends SQLiteOpenHelper {

    Context context;

    public HistoryDBHandler(Context context) {
        super(context, "history.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HistoryDAO.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldV, int newV) {
    }
}
