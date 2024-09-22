package com.pinoy.side_gig.data.database;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, "db_pinoysidegig", null, 1);
    }
    /* TODO : Table and columns of dim_user_accounts */
    public static final String dim_user_accounts_table = "dim_user_accounts";
    public static final String dim_user_accounts_email = "email";
    public static final String dim_user_accounts_password = "password";
    public static final String dim_user_accounts_lastname = "lastname";
    public static final String dim_user_accounts_firstname = "firstname";
    public static final String dim_user_accounts_gender = "gender";
    public static final String dim_user_accounts_age = "age";
    public static final String dim_user_accounts_phone_num = "phone_num";
    public static final String dim_user_accounts_bday = "bday";
    public static final String dim_user_accounts_location = "location";
    public static final String dim_user_accounts_skills = "skills";
    public static final String dim_user_accounts_experience = "experience";
    public static final String dim_user_accounts_valid_id = "valid_id";
    public static final String dim_user_accounts_file_1 = "file_1";
    public static final String dim_user_accounts_display_photo = "display_photo";
    public static final String dim_user_accounts_user_type = "user_type";



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTableUserAccounts(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private void createTableUserAccounts(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + dim_user_accounts_table );
        db.execSQL("CREATE TABLE IF NOT EXISTS " + dim_user_accounts_table + " (" +
                "email	varchar	(255) PRIMARY KEY UNIQUE, " +
                "password	varchar	(255), " +
                "lastname	varchar	(255), " +
                "firstname	varchar	(255), " +
                "gender	varchar	(255), " +
                "age	int	(11), " +
                "phone_num	int	(11), " +
                "bday	date	(0), " +
                "location	varchar	(255), " +
                "skills	varchar	(255), " +
                "experience	varchar	(255), " +
                "valid_id	varchar	(255), " +
                "file_1	varchar	(255), " +
                "display_photo	varchar	(255), " +
                "user_type	varchar	(255) " +
                ")");
    }

    public boolean insertAccount(String email,String pass) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(dim_user_accounts_email, email);
        contentValues.put(dim_user_accounts_password, pass);

        long bookingtype_result = db.insert(dim_user_accounts_table, null, contentValues);

        if (bookingtype_result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getAccount() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + dim_user_accounts_table + "", null);
        return res;
    }

    public void Logout() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + dim_user_accounts_table + "");
    }
}
