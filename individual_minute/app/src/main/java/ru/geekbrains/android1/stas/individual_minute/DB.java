package ru.geekbrains.android1.stas.individual_minute;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Stars on 29.09.2016.
 */
class DB {

    static final String DB_NAME = "appIM";
    static final int DB_VERSION = 1;
    static final String DB_TABLE = "IM";

    static final String COLUMN_ID = "_id";
    static final String COLUMN_DATE = "date";
    static final String COLUMN_TIME = "time";

    private ContentValues cv = new ContentValues();

    private static final String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_DATE + " text, " +
                    COLUMN_TIME + " text  " +
                    ");";

    final Context mCtx;

    DBHelper mDBHelper;
    SQLiteDatabase mDB;

    DB(Context ctx) {
        mCtx = ctx;
    }

    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    void close() {
        if (mDBHelper != null) mDBHelper.close();
    }

//    Cursor getNameData(String name) {
//        String selection = COLUMN_DATE + "=?";
//        String[] selectionArgs = new String[]{name};
//        return mDB.query(DB_TABLE, null, selection, selectionArgs, null, null, null);
//    }

    MyData selectOne(long id) {
        Cursor mCursor = mDB.query(DB_TABLE, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, COLUMN_TIME);
        mCursor.moveToFirst();
        String date = mCursor.getString(1);
        String time = mCursor.getString(2);


        mCursor.close();
        return new MyData(id, date, time);
    }

    void addRec(String date, String time) {
        cv.put(COLUMN_DATE, date);
        cv.put(COLUMN_TIME, time);
        mDB.insert(DB_TABLE, null, cv);
    }

    void delRec(long id) {
        mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
    }

    Cursor getAllData() {
        return mDB.query(DB_TABLE, null, null, null, null, null, null);
    }

    int updRec(MyData md) {
        cv.put(COLUMN_DATE, md.getDate());
        cv.put(COLUMN_TIME, md.getTime());
        return mDB.update(DB_TABLE, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(md.getID())});
    }

    void clearDB() {
        mDB.delete(DB_TABLE, null, null);
    }

    public class DBHelper extends SQLiteOpenHelper {

        DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                 int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
