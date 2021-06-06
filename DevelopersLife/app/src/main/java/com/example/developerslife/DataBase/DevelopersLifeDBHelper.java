package com.example.developerslife.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DevelopersLifeDBHelper extends SQLiteOpenHelper {

    public DevelopersLifeDBHelper(Context context) {
        super(context, MemStructDB.DB_NAME, null, MemStructDB.DB_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MemStructDB.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(MemStructDB.DROP);
        onCreate(db);
    }
}
