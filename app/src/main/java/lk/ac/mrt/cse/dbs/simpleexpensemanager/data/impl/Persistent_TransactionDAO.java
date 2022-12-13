package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class Persistent_TransactionDAO implements TransactionDAO {

    private Context context;
    private static String table_name = "transaction_table";
    private static String id = "id";
    private static String date = "date";
    private static String expenseType= "expenseType";
    private static String acc_no = "accountNumber";
    private static  String amount = "amount";
    private DatabaseHandler databaseHandler;

    public Persistent_TransactionDAO(Context context){
        this.context = context;
        databaseHandler = DatabaseHandler.getInstance(context);
    }

    @Override
    public void logTransaction(Date date_, String accNo, ExpenseType expenseType_, double amount_) {
        SQLiteDatabase sqLiteDatabase=databaseHandler.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(date, date_.getTime());
        cv.put(expenseType, String.valueOf(expenseType_));
        cv.put(acc_no, accNo);
        cv.put(amount, amount_);
        sqLiteDatabase.insert(table_name,null, cv);
        sqLiteDatabase.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        String q="SELECT * FROM "+table_name+";";
        SQLiteDatabase sqLiteDatabase = databaseHandler.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(q,null,null);
        ArrayList<Transaction> lstRet= new ArrayList<Transaction>();
        if (cursor.moveToFirst()){
            do {
                Date date= new Date(cursor.getLong(1));
                ExpenseType expenseType= ExpenseType.valueOf(cursor.getString(2));
                Transaction newTransaction = new Transaction(date,cursor.getString(3),expenseType,cursor.getDouble(4));
                lstRet.add(newTransaction);
            }while(cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return lstRet;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactions = getAllTransactionLogs();
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }

        return transactions.subList(size - limit, size);
    }

    public static String dropQuery(){
        String q="DROP TABLE IF EXISTS "+table_name+";";
        return q;
    }

    public static String createQuery(){
        String q="CREATE TABLE " + table_name +" (" +
                id + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                date +" TEXT, "+
                expenseType +" TEXT, "+
                acc_no +" TEXT, "+
                amount +" DECIMAL(25, 2), "+
                "FOREIGN KEY(" + acc_no + ") REFERENCES account_table(account_no));";
        return q;

    }
}
