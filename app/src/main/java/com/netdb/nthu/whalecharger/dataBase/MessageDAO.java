package com.netdb.nthu.whalecharger.dataBase;

/**
 * Created by user on 2015/1/17.
 */


/**
 * Created by user on 2015/1/17.
 */
        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;

        import com.netdb.nthu.whalecharger.model.Message;

        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.List;


public class MessageDAO {
    public static final int DAY = 1;
    public static final int MONTH = 2;
    public static final int YEAR = 3;

    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "_id";
    // 其它表格欄位名稱
    public static final String COLUMN_ITEM = "item";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_MONTH = "month";
    public static final String COLUMN_DAY = "day";
    // 表格名稱
    public static final String TABLE_NAME = "message";

    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY " + "AUTOINCREMENT DEFAULT 1 , "+
                    COLUMN_ITEM + " TEXT NOT NULL, " +
                    COLUMN_CATEGORY + " INTEGER NOT NULL, " +
                    COLUMN_PRICE + " INTEGER NOT NULL, " +
                    COLUMN_MONTH + " INTEGER NOT NULL, " +
                    COLUMN_DAY + " INTEGER NOT NULL, " +
                    COLUMN_YEAR + " INTEGER NOT NULL)";

    public synchronized static void addMessage(Context context, Message message) {
        addMessage(context, message.getId(), message.getItem(), message.getCategory(), message.getPrice(),
                message.getYear(),message.getMonth(),message.getDay());
    }

    public synchronized static void addMessage(Context context, long id,String item,Integer category,Integer price,Integer year,Integer month,Integer day) {

        MessageDBHandler dbHelper = new MessageDBHandler(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        if (id == -1) { // New Record
            cv.put(COLUMN_ITEM, item);
            cv.put(COLUMN_CATEGORY, category);
            cv.put(COLUMN_PRICE, price);
            cv.put(COLUMN_YEAR, year);
            cv.put(COLUMN_MONTH, month);
            cv.put(COLUMN_DAY, day);
            id= db.insert(TABLE_NAME, null, cv);
            //System.out.printf("-----------------%d %s %d %d-----------------\n",id,item,category,price);
        } else { // Existing Record
            cv.put(KEY_ID, id);
            cv.put(COLUMN_ITEM, item);
            cv.put(COLUMN_CATEGORY, category);
            cv.put(COLUMN_PRICE, price);
            cv.put(COLUMN_YEAR, year);
            cv.put(COLUMN_MONTH, month);
            cv.put(COLUMN_DAY, day);
            db.update(TABLE_NAME, cv, KEY_ID + "=" + id, null);
        }
        db.close();
    }

    public synchronized static void removeMessage(Context context, long id) {

        MessageDBHandler dbHelper = new MessageDBHandler(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + "=" + id, null);
        db.close();
    }
    public synchronized static void removeALLMessage(Context context) {

        MessageDBHandler dbHelper = new MessageDBHandler(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Calendar calendar = Calendar.getInstance();
        db.delete(TABLE_NAME, COLUMN_YEAR + "=" + String.valueOf(calendar.get(Calendar.YEAR)-1), null);
        db.close();
    }

    public static int getCount(Context context) {
        MessageDBHandler dbHelper = new MessageDBHandler(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int result = 0;
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
        if(c.getCount()!=0) result=c.getCount();
        c.close();
        db.close();
        return result;
    }

    public synchronized static List<Message> fetchMessages(Context context,String searchText, int mode) {
        List<Message> messageList = new ArrayList<>();

        MessageDBHandler dbHelper = new MessageDBHandler(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String [] list = {"food","clothes","transportation","others"};

        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);

        c.moveToLast();
        for (int i = 0; i < c.getCount(); i++) {
            Message message = db2record(c);
            //System.out.printf("---------------%d %d %d(%d %d %d)------------------\n",message.getYear(),message.getMonth(),message.getDay(),calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH));
            if(mode==0 && message.getItem().contains(searchText)){
                messageList.add(message);
            }
            else if(mode==1 && list[message.getCategory()].contains(searchText)){
                messageList.add(message);
            }
            c.moveToPrevious();
        }
        c.close();
        db.close();
        return messageList;
    }

    public synchronized static List<Message> getAllMessages(Context context,int mode) {
        List<Message> messageList = new ArrayList<>();

        MessageDBHandler dbHelper = new MessageDBHandler(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Calendar calendar = Calendar.getInstance();

        // Cursor c = db.rawQuery("select * from " + BABY_TABLE + ";", null);
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);

        c.moveToLast();
        for (int i = 0; i < c.getCount(); i++) {
            Message message = db2record(c);
            //System.out.printf("---------------%d %d %d(%d %d %d)------------------\n",message.getYear(),message.getMonth(),message.getDay(),calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH));
            if(mode==MONTH){
                if (!message.getYear().equals(calendar.get(Calendar.YEAR))||!message.getMonth().equals(calendar.get(Calendar.MONTH)+1)) break;
            }
            else if(mode==DAY){
                if (!message.getYear().equals(calendar.get(Calendar.YEAR))||!message.getMonth().equals(calendar.get(Calendar.MONTH)+1)||!message.getDay().equals(calendar.get(Calendar.DAY_OF_MONTH))) break;
            }
            messageList.add(message);
            c.moveToPrevious();
        }
        c.close();
        db.close();
        return messageList;
    }
    public synchronized static Integer getPresentMoney(Context context,Calendar start,Calendar end) {

        MessageDBHandler dbHelper = new MessageDBHandler(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int sum=0, flag=0;

        // Cursor c = db.rawQuery("select * from " + BABY_TABLE + ";", null);
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);

        c.moveToLast();
        for (int i = 0; i < c.getCount(); i++) {
            Message message = db2record(c);
            int year = message.getYear();
            int month = message.getMonth()-1;
            int day = message.getDay();
            if(flag==0){
                if (end.get(Calendar.YEAR)>=year && end.get(Calendar.MONTH)>=month && end.get(Calendar.DAY_OF_MONTH)>=day){
                    flag=1;
                }
            }
            if (flag==1){
                if (start.get(Calendar.YEAR)>year || (start.get(Calendar.YEAR)==year && start.get(Calendar.MONTH)>month) || (start.get(Calendar.YEAR)==year && start.get(Calendar.MONTH)==month && start.get(Calendar.DAY_OF_MONTH)>day)){
                    break;
                }
                sum+=message.getPrice();
            }
            c.moveToPrevious();
        }
        c.close();
        db.close();
        return sum;
    }

    public synchronized static Integer getTodayMoney(Context context) {
        MessageDBHandler dbHelper = new MessageDBHandler(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int sum=0,end=0;
        Calendar calendar = Calendar.getInstance();

        // Cursor c = db.rawQuery("select * from " + BABY_TABLE + ";", null);
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);

        c.moveToLast();
        for (int i = 0; i < c.getCount(); i++) {
            Message message = db2record(c);
            if (!message.getYear().equals(calendar.get(Calendar.YEAR))||!message.getMonth().equals(calendar.get(Calendar.MONTH)+1)||!message.getDay().equals(calendar.get(Calendar.DAY_OF_MONTH))) break;
            sum = sum + c.getInt(c.getColumnIndex(COLUMN_PRICE));
            c.moveToPrevious();
        }
        c.close();
        db.close();
        return sum;
    }

    public synchronized static Message getMessageById(Context context, long id) {

        MessageDBHandler dbHelper = new MessageDBHandler(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query(TABLE_NAME, null, KEY_ID + "=" + id, null, null, null, null);
        if (c.getCount() == 1) {
            c.moveToFirst();
        }
        Message message = db2record(c);

        c.close();
        db.close();
        return message;
    }

    public synchronized static Message getLastMessage(Context context) {

        MessageDBHandler dbHelper = new MessageDBHandler(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);
        c.moveToLast();
        Message message = db2record(c);

        c.close();
        db.close();
        return message;
    }


    private static void getRecordsKernel(Cursor c, ArrayList<Message> messageList) {
        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            Message message = db2record(c);
            messageList.add(message);
            c.moveToNext();
        }
    }

    private static Message db2record(Cursor c) {
        long id =  c.getInt(c.getColumnIndex(KEY_ID));
        String item = c.getString(c.getColumnIndex(COLUMN_ITEM));
        int category = c.getInt(c.getColumnIndex(COLUMN_CATEGORY));
        int year = c.getInt(c.getColumnIndex(COLUMN_YEAR));
        int month = c.getInt(c.getColumnIndex(COLUMN_MONTH));
        int day = c.getInt(c.getColumnIndex(COLUMN_DAY));
        int price = c.getInt(c.getColumnIndex(COLUMN_PRICE));
        Message message = new Message(id,item,category, price, year,month,day);
        return message;
    }
}