package com.akns.siamaakns;

import android.content.Context;

import com.affan.sqlitedbhelper.SQLiteAdapter;

/**
 * Created by Affan Mohammad on 29/06/2016.
 */
public class MySQLite {
    private static MySQLite instance;
    private SQLiteAdapter sqLiteAdapter;
    private static final String DB_NAME = "siama.db";

    public MySQLite(Context context) {
        sqLiteAdapter = new SQLiteAdapter(context, DB_NAME).createDatabaseFile().open();
    }

    public static MySQLite getInstance(Context context) {
        if (instance == null) {
            instance = new MySQLite(context);
        }
        return instance;
    }

    public SQLiteAdapter getSqLiteAdapter() {
        return sqLiteAdapter;
    }
}
