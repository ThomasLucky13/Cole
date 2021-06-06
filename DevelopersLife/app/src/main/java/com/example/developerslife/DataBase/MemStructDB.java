package com.example.developerslife.DataBase;

public class MemStructDB {
    public static final String Name = "MemStruct";
    public static final String _ID = "_id";
    public static final String TEXT = "text";
    public static final String GIF_URL = "gifURL";
    public static final String AUTHOR = "author";
    public static final String DATE = "date";

    public static final String DB_NAME = "MemStructDB.db";
    public static final int DB_Version = 1;
    public static final String CREATE = "CREATE TABLE IF NOT EXISTS " + Name + " (" +
            _ID + " INTEGER PRIMARY KEY," + TEXT + " TEXT," +
            GIF_URL + " TEXT," + AUTHOR + " TEXT," + DATE + " TEXT)";
    public static final String DROP = "DROP TABLE IF EXISTS " + Name;
}
