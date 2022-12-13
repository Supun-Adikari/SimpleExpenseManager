package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String dbName = "200012R";
    private static final int type = 1;
    private static DatabaseHandler instance = null;
    private DatabaseHandler(@Nullable Context context){
        super(context, dbName, null, type);
    }

    public static DatabaseHandler getInstance(@Nullable Context context){
        if(instance == null){
            instance = new DatabaseHandler(context);
        }
        return instance;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Persistent_AccountDAO.createQuery());
        sqLiteDatabase.execSQL(Persistent_TransactionDAO.createQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(Persistent_AccountDAO.dropQuery());
        sqLiteDatabase.execSQL(Persistent_TransactionDAO.dropQuery());
        onCreate(sqLiteDatabase);
    }
}
