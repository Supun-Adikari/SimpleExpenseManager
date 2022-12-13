package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class Persistent_AccountDAO implements AccountDAO {
    private Context context;
    private static String table_name = "account_table";

    private static String acc_no = "account_no";
    private static String bank_name = "bank_name";
    private static String holders_name = "acc_holders_name";
    private static String bal = "balance";
    private DatabaseHandler databaseHandler;
    public Persistent_AccountDAO(Context context){
        this.context=context;
        databaseHandler=DatabaseHandler.getInstance(context);

    }

    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase sqliteDB= databaseHandler.getReadableDatabase();
        String q = "Select "+acc_no+" FROM "+table_name+";";
        Cursor cursor = sqliteDB.rawQuery(q,null,null);
        ArrayList<String> lstRet= new ArrayList<String>();
        if(cursor.moveToFirst()){
            do{
                lstRet.add(cursor.getString(0));
            }while(cursor.moveToNext());
        }
        sqliteDB.close();
        cursor.close();
        return lstRet;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase sqliteDB= databaseHandler.getReadableDatabase();
        String q = "Select "+acc_no+" FROM "+table_name+";";
        Cursor cursor = sqliteDB.rawQuery(q,null,null);
        ArrayList<Account> lstRet= new ArrayList<Account>();
        if(cursor.moveToFirst()){
            do{
                Account newAccount= new Account(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getDouble(3));
                lstRet.add(newAccount);
            }while(cursor.moveToNext());
        }
        sqliteDB.close();
        cursor.close();
        return lstRet;
    }

    @Override
    public Account getAccount(String accNo) throws InvalidAccountException {
        SQLiteDatabase sqliteDB=databaseHandler.getReadableDatabase();
        String q ="SELECT * FROM "+table_name +" WHERE "+acc_no+"=?";
        try {
            Cursor cursor = sqliteDB.rawQuery(q, new String[]{accNo}, null);
            cursor.moveToFirst();
            Account newAcc=new Account(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getDouble(3));
            cursor.close();
            sqliteDB.close();
            return newAcc;
        }catch (Exception ex){
            throw new InvalidAccountException("INVALID ACCOUNT NUMBER "+accNo);
        }

    }

    @Override
    public void addAccount(Account acc) {
        SQLiteDatabase sqliteDB=databaseHandler.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(acc_no, acc.getAccountNo());
        cv.put(bank_name, acc.getBankName());
        cv.put(holders_name,acc.getAccountHolderName());
        cv.put(bal,acc.getBalance());
        sqliteDB.insert(table_name,null,cv);
        sqliteDB.close();
    }

    @Override
    public void removeAccount(String accNo) throws InvalidAccountException {
        SQLiteDatabase sqliteDB = databaseHandler.getWritableDatabase();
        int q= sqliteDB.delete(table_name,acc_no+"=?",new String[]{accNo});
        if (q==-1){
            throw new InvalidAccountException("Invalid Account Number"+accNo);
        }
        sqliteDB.close();
    }

    @Override
    public void updateBalance(String accNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        ContentValues cv = new ContentValues();
        Account acc = getAccount(accNo);
        if (expenseType==ExpenseType.EXPENSE) {
            cv.put(bal, acc.getBalance() - amount);
        }else if (expenseType==ExpenseType.INCOME){
            cv.put(bal, acc.getBalance() + amount);
        }
        SQLiteDatabase sqLiteDatabase=databaseHandler.getWritableDatabase();
        cv.put(holders_name,acc.getAccountHolderName());
        cv.put(holders_name,acc.getAccountHolderName());
        int q=sqLiteDatabase.update(table_name,cv,acc_no+"=?",new String[]{accNo});
        sqLiteDatabase.close();
        if (q==-1){
            throw  new InvalidAccountException("ACCOUNT INVALID "+accNo);
        }
    }

    public static String dropQuery(){
        String q="DROP TABLE IF EXISTS "+table_name+";";
        return q;
    }

    public static String createQuery(){
        String q="CREATE TABLE " + table_name +" (" +
                acc_no + " TEXT PRIMARY KEY, "+
                bank_name +" TEXT, "+
                holders_name+" TEXT, "+
                bal+" DECIMAL(20, 2));";
        return q;

    }


}
