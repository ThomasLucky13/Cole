package com.example.developerslife.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.developerslife.MemStruct;

import java.util.ArrayList;

public class DevelopersLifeDBManager {
    private Context context;
    private DevelopersLifeDBHelper developersLifeDBHelper;
    private SQLiteDatabase db;
    public DevelopersLifeDBManager(Context context)
    {
        this.context = context;
        developersLifeDBHelper = new DevelopersLifeDBHelper(context);
    }
    public void DeleteTables(String Table_name)
    {
        db.delete(Table_name, null, null);
    }

    public void openDb()
    {
        db = developersLifeDBHelper.getWritableDatabase();
    }

    public void insertMemStruct(String text, String gifURL, String author, String date)
    {
        ContentValues cv = new ContentValues();
        cv.put(MemStructDB.TEXT, text);
        cv.put(MemStructDB.GIF_URL, gifURL);
        cv.put(MemStructDB.AUTHOR, author);
        cv.put(MemStructDB.DATE, date);
        db.insert(MemStructDB.Name, null, cv);
    }
    public ArrayList<MemStruct> GetMemStruct()
    {
        ArrayList<MemStruct> res = new ArrayList<MemStruct>();
        int idIndex, textIndex, gifURLIndex, authorIndex, dateIndex;
        Cursor cur = db.query(MemStructDB.Name, null, null, null, null, null, null);
        if (cur.moveToFirst())
        {
            idIndex = cur.getColumnIndex(MemStructDB._ID);
            textIndex = cur.getColumnIndex(MemStructDB.TEXT);
            gifURLIndex = cur.getColumnIndex(MemStructDB.GIF_URL);
            authorIndex = cur.getColumnIndex(MemStructDB.AUTHOR);
            dateIndex = cur.getColumnIndex(MemStructDB.DATE);
            do {
                res.add(new MemStruct(cur.getString(textIndex), cur.getString(gifURLIndex),cur.getString(authorIndex),cur.getString(dateIndex)));
            } while (cur.moveToNext());

        } else
        {
            res.add(new MemStruct("У Вас пока нет любимых постов :(", "none", "", ""));
        }
        cur.close();
        return res;
    }

    public void closeDb()
    {
         developersLifeDBHelper.close();
    }
}
