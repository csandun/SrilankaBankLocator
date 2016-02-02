package com.mikepenz.materialdrawer.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chathuranga on 1/21/2016.
 */
public class DBHelper extends SQLiteOpenHelper{

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "amanabank.db";

    // marks table name
    public static final String TABLE_BRANCH= "branch";

    // marks Table Columns names
    public static final String KEY_BRANCH_CODE = "branchcode";
    public static final String KEY_BRANCH_NAME = "branchname";
    public static final String KEY_LATITUDE= "latitude";
    public static final String KEY_LONGITUDE= "longitude";
    public static final String KEY_ADDRESS= "address";
    public static final String KEY_TEL= "tel";
    public static final String KEY_FAX= "fax";

    //crating table quary
    private static final String CREATE_BRANCH_TABLE =   "CREATE TABLE " +TABLE_BRANCH+" ("+
            KEY_BRANCH_CODE+ " INTEGER PRIMARY KEY, " +
            KEY_BRANCH_NAME+" TEXT, "+
            KEY_LATITUDE+" REAL, "+
            KEY_LONGITUDE+" REAL, "+
            KEY_ADDRESS+" TEXT, "+
            KEY_TEL+" INTEGER, "+
            KEY_FAX+" INTEGER "+   ")";

    //all columns
    public static final String[] ALL_COLUMNS = {KEY_BRANCH_CODE,KEY_BRANCH_NAME,KEY_LATITUDE,KEY_LATITUDE,KEY_LONGITUDE,KEY_ADDRESS,KEY_TEL,KEY_FAX};

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BRANCH_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BRANCH);
        onCreate(db);
    }


    // _______________________ for branch table___________________________________________________//



    public void addBranch(Branch branch) {
        SQLiteDatabase db = this.getReadableDatabase();



        ContentValues values = new ContentValues();
        values.put(KEY_BRANCH_CODE,branch.getBranchCode());
        values.put(KEY_BRANCH_NAME,branch.getBranchName());
        values.put(KEY_LATITUDE,branch.getLatitude());
        values.put(KEY_LONGITUDE,branch.getLongitude());
        values.put(KEY_ADDRESS,branch.getAddress());
        values.put(KEY_TEL,branch.getTel());
        values.put(KEY_FAX,branch.getFax());


        // Inserting Row
        db.insert(TABLE_BRANCH, null, values);
        db.close(); // Closing database connection
    }

    public List<Branch> getAllBranches() {
        List<Branch> contactList = new ArrayList<Branch>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_BRANCH;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Branch branch = new Branch();
                branch.setBranchCode(cursor.getInt(0));
                branch.setBranchName(cursor.getString(1));
                branch.setLatitude(cursor.getDouble(2));
                branch.setLongitude(cursor.getDouble(3));
                branch.setAddress(cursor.getString(4));
                branch.setTel(cursor.getString(5));
                branch.setFax(cursor.getString(6));

                // Adding contact to list
                contactList.add(branch);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }


    public void truncate() {
        SQLiteDatabase db = this.getReadableDatabase();

        db.execSQL("DELETE FROM " + TABLE_BRANCH);
    }

    public Branch getBranch(int code){
        String selectQuery = "SELECT  * FROM " + TABLE_BRANCH +" WHERE "+KEY_BRANCH_CODE+" = "+code;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        Branch branch = new Branch();
        branch.setBranchCode(cursor.getInt(0));
        branch.setBranchName(cursor.getString(1));
        branch.setLatitude(cursor.getDouble(2));
        branch.setLongitude(cursor.getDouble(3));
        branch.setAddress(cursor.getString(4));
        branch.setAddress(cursor.getString(5));
        branch.setAddress(cursor.getString(6));

        return branch;
    }
}
