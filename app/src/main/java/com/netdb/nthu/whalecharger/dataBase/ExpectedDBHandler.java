package com.netdb.nthu.whalecharger.dataBase;


/**
 * Created by user on 2015/1/17.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ExpectedDBHandler extends SQLiteOpenHelper {

    Context context;

    public ExpectedDBHandler(Context context) {
        super(context, "expectations.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ExpectedDAO.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldV, int newV) {
    }
}
