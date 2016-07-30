package com.netdb.nthu.whalecharger.dataBase;

/**
 * Created by user on 2015/1/17.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.netdb.nthu.whalecharger.model.HistoryItem;

import java.util.ArrayList;
import java.util.List;


public class HistoryDAO {

    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "_id";
    // 其它表格欄位名稱
    public static final String COLUMN_DATE_ORIGIN = "dateOrigin";
    public static final String COLUMN_DATE_END = "dateEnd";
    public static final String COLUMN_GOAL = "goal";
    public static final String COLUMN_RESULT = "result";
    public static final String COLUMN_STATE = "state";
    public static final String COLUMN_UNIT = "unit";
    // 表格名稱
    public static final String TABLE_NAME = "history";

    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY " + "AUTOINCREMENT DEFAULT 1 , "+
                    COLUMN_DATE_ORIGIN + " TEXT NOT NULL, " +
                    COLUMN_DATE_END + " TEXT NOT NULL, " +
                    COLUMN_GOAL + " INTEGER NOT NULL, " +
                    COLUMN_STATE + " INTEGER NOT NULL, " +
                    COLUMN_UNIT + " INTEGER NOT NULL, " +
                    COLUMN_RESULT + " TEXT NOT NULL)";

    public synchronized static void addHistoyItem(Context context, HistoryItem historyItem) {
        addHistoryItem(context, historyItem.getId(), historyItem.getDateOrigin(), historyItem.getDateEnd(), historyItem.getGoal(),
                historyItem.getResult(),historyItem.getState(),historyItem.getUnit());
    }

    public synchronized static void addHistoryItem(Context context,long id,String dateOrigin,String dateEnd,Integer goal,String result,Integer state,Integer unit) {

        HistoryDBHandler dbHelper = new HistoryDBHandler(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        if (id == -1) { // New Record
            cv.put(COLUMN_DATE_ORIGIN, dateOrigin);
            cv.put(COLUMN_DATE_END, dateEnd);
            cv.put(COLUMN_GOAL, goal);
            cv.put(COLUMN_STATE, state);
            cv.put(COLUMN_RESULT, result);
            cv.put(COLUMN_UNIT, unit);
            id= db.insert(TABLE_NAME, null, cv);
            //System.out.printf("----------%s %s %d %d %s %d-----------\n",dateOrigin,dateEnd,goal,state,result,unit);
        } else { // Existing Record
            cv.put(KEY_ID, id);
            cv.put(COLUMN_DATE_ORIGIN, dateOrigin);
            cv.put(COLUMN_DATE_END, dateEnd);
            cv.put(COLUMN_GOAL, goal);
            cv.put(COLUMN_STATE, state);
            cv.put(COLUMN_RESULT, result);
            cv.put(COLUMN_UNIT, unit);
            db.update(TABLE_NAME, cv, KEY_ID + "=" + id, null);
        }
        db.close();
    }

    public synchronized static void removeHistoryItem(Context context, int id) {

        HistoryDBHandler dbHelper = new HistoryDBHandler(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + "=" + id, null);
        db.close();
    }

    public synchronized static List<HistoryItem> getAllHistoryItems(Context context) {
        ArrayList<HistoryItem> historyItemList = new ArrayList<>();

        HistoryDBHandler dbHelper = new HistoryDBHandler(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);

        getRecordsKernel(c, historyItemList);
        c.close();
        db.close();
        return historyItemList;
    }

    public static int getCount(Context context) {
        HistoryDBHandler dbHelper = new HistoryDBHandler(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = 0;
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
        if(c.getCount()!=0) result=c.getCount();
        c.close();
        db.close();
        return result;
    }

    public static int getCompletedCount(Context context) {
        HistoryDBHandler dbHelper = new HistoryDBHandler(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = 0;
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
        c.moveToFirst();
        for(int i=0;i<c.getCount();i++){
            if(c.getString(c.getColumnIndex(COLUMN_RESULT)).equals("completed")){
                result++;
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return result;
    }

    public synchronized static HistoryItem getHistoryItemById(Context context, int id) {

        HistoryDBHandler dbHelper = new HistoryDBHandler(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query(TABLE_NAME, null, KEY_ID + "=" + id, null, null, null, null);
        if (c.getCount() == 1) {
            c.moveToFirst();
        }
        HistoryItem historyItem = db2record(c);

        c.close();
        db.close();
        return historyItem;
    }

    public synchronized static HistoryItem getLastItem(Context context) {

        HistoryDBHandler dbHelper = new HistoryDBHandler(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
        c.moveToLast();
        HistoryItem item = db2record(c);

        c.close();
        db.close();
        return item;
    }

    private static void getRecordsKernel(Cursor c, ArrayList<HistoryItem> historyItemList) {
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            HistoryItem baby = db2record(c);
            historyItemList.add(baby);
            c.moveToNext();
        }
    }

    private static HistoryItem db2record(Cursor c) {
        long id =  c.getInt(c.getColumnIndex(KEY_ID));
        String dateOrigin = c.getString(c.getColumnIndex(COLUMN_DATE_ORIGIN));
        String dateEnd = c.getString(c.getColumnIndex(COLUMN_DATE_END));
        Integer goal = c.getInt(c.getColumnIndex(COLUMN_GOAL));
        String result = c.getString(c.getColumnIndex(COLUMN_RESULT));
        Integer state = c.getInt(c.getColumnIndex(COLUMN_STATE));
        Integer unit = c.getInt(c.getColumnIndex(COLUMN_UNIT));
        HistoryItem historyItem = new HistoryItem(id,dateOrigin,dateEnd,goal,result,state,unit);
        return historyItem;
    }
}  