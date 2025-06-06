package com.example.speedyscript;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TypingResultDatabaseHelper extends SQLiteOpenHelper {

    public TypingResultDatabaseHelper(Context context) {
        super(context, "TypingResults.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Results (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "timeTaken REAL," +
                "wpm REAL," +
                "accuracy REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Results");
        onCreate(db);
    }
}
