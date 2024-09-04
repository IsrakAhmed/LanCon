package team.lancon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserRepository {
    private LanConDatabaseHelper dbHelper;

    public UserRepository(Context context) {
        dbHelper = new LanConDatabaseHelper(context);
    }

    // Add a new user
    public long addUser(String username, String userIp) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("user_ip", userIp);
        return db.insert("users", null, values);
    }

    // Get a user by username
    public Cursor getUserByUsername(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query("users", null, "username = ?", new String[]{username}, null, null, null);
    }

    // Get a user by IP address
    public Cursor getUserByIp(String userIp) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query("users", null, "user_ip = ?", new String[]{userIp}, null, null, null);
    }

    // Update user information
    public int updateUser(String username, String userIp) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_ip", userIp);
        return db.update("users", values, "username = ?", new String[]{username});
    }

    // Delete a user by IP address
    public int deleteUserByIp(String userIp) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("users", "user_ip = ?", new String[]{userIp});
    }

    // Get all users
    public Cursor getAllUsers() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query("users", null, null, null, null, null, null);
    }

    public LanConDatabaseHelper getDBHelper () {
        return dbHelper;
    }
}