package com.example.androidinventorymanagement.SqlDB;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DbManager extends SQLiteOpenHelper {

    public static final String dbname = "dbQuote";

    public DbManager(@Nullable Context context) {
        super(context, dbname, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String qry = "create table tbl_quote(id integer primary key autoincrement,code text,name text,gst text,qty float,mrp text,addedfrom text)";
        db.execSQL(qry);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String qry = "DROP TABLE IF EXISTS tbl_quote";
        db.execSQL(qry);
        onCreate(db);
    }

    public Boolean verifyData(String TABLE_NAME, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE name = '" + name + "'";
        Cursor res = db.rawQuery(query, null);
        if (res.moveToFirst()){
            res.close();
            return true;
        }
        res.close();
        return false;
    }

    public String createQuote(String code,String name,String gst,Float qty,String mrp,String addedfrom)
    {

        if (verifyData("tbl_quote", name))
        {
            return String.valueOf(0);
        }


        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("code",code);
        contentValues.put("name",name);
        contentValues.put("gst",gst);
        contentValues.put("qty",qty);
        contentValues.put("mrp",mrp);
        contentValues.put("addedfrom",addedfrom);
       float res =  sqLiteDatabase.insert("tbl_quote",null,contentValues);

       if (res==-1)
           return "failed";
       else
           return "Successfully Added";

    }

    public Cursor readalldata()
    {
        SQLiteDatabase database =this.getWritableDatabase();
        String qry = "select * from tbl_quote";
        Cursor cursor = database.rawQuery(qry,null);
        return cursor;
    }

    @SuppressLint("Range")
    public int readallOnlyDiscountValues()
    {

        SQLiteDatabase db = this.getWritableDatabase();
        //main
        Cursor cursor = db.rawQuery("SELECT Sum(mrp * qty) AS Total FROM " + "tbl_quote" + " WHERE addedfrom = '" + "scan" + "'", null);
        Log.e("cursor",String.valueOf(cursor.getColumnIndex("Total")));

        if (cursor.moveToNext()) {
            @SuppressLint("Range") int total = cursor.getInt(cursor.getColumnIndex("Total"));
            Log.e("total",String.valueOf(total));
            return total;
        }
        return 0;
    }

    public void UpdateQty(String qtyNew,String names)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE tbl_quote SET qty = ("+qtyNew+") WHERE NAME = '" + names + "'");

        db.close();

    }
    public void UpdateAll(String names, String gstm, String rate, String nameKey, String mproductQtyEdit)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE tbl_quote SET name = ("+"'"+names+"'"+") WHERE NAME = '" + nameKey + "'");
        db.execSQL("UPDATE tbl_quote SET gst = ("+gstm+") WHERE NAME = '" + nameKey + "'");
        db.execSQL("UPDATE tbl_quote SET mrp = ("+rate+") WHERE NAME = '" + nameKey + "'");
        db.execSQL("UPDATE tbl_quote SET qty = ("+mproductQtyEdit+") WHERE NAME = '" + nameKey + "'");


        db.close();

    }
    public Cursor getQty(String nameKey){
        SQLiteDatabase database =this.getWritableDatabase();
        String qry = "select * from tbl_quote WHERE NAME = '" + nameKey + "'"   ;
        Cursor cursor = database.rawQuery(qry,null);
        return cursor;
    }

    public void deleteAll()
    {
        SQLiteDatabase db =this.getWritableDatabase();
        db.execSQL("delete from tbl_quote");
        db.close();
    }
    public void deleteItem(String get_ID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + "tbl_quote" + " WHERE name = '" + get_ID + "'");
        db.close();

    }
}
