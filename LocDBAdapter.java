package ca.cmetcalfe.locationshare;

/**
 * Created by ibmsap on 5/12/17.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocDBAdapter {
    static final String KEY_ROWID = "_id";
    static final String KEY_NAME = "name";
    static final String KEY_LAT = "lat";
    static final String KEY_LONG = "lon";
    static final String TAG = "DBAdapter";
    static final String DATABASE_NAME = "MyDB2";
    static final String DATABASE_TABLE = "logloc";
    static final int DATABASE_VERSION = 4;
    static final String DATABASE_CREATE =
            "create table logloc (_id integer primary key autoincrement, "
                    + "name text not null, lat text not null,lon text not null);";
    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;
    public LocDBAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(DATABASE_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS contacts");
            onCreate(db);
        }
    }
    //---opens the database---
    public LocDBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }
    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }
    //---insert a contact into the database---
    public long insertContact(String name, String lat,String lon)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_LAT, lat);
        initialValues.put(KEY_LONG, lon);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }
    //---deletes a particular contact---
    public boolean deleteContact(long rowId)
    {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    public void deleteAll() {
        db.execSQL("delete from " + DATABASE_TABLE);
    }
    //---retrieves all the contacts---
    public Cursor getAllContacts()
    {
        return db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME,
                KEY_LAT,KEY_LONG}, null, null, null, null, null);
    }


    //---retrieves a particular contact---
    public Cursor getContact(String name) throws SQLException
    {
        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                KEY_NAME, KEY_LAT,KEY_LONG}, KEY_NAME + "= '" + name+"'", null,null, null, null, null);
        if (mCursor != null)
        {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a contact---
    public boolean updateContact(long rowId, String name, String lat,String lon)
    {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_LAT, lat);
        args.put(KEY_LAT, lon);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }


}
