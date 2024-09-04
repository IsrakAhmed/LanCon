package team.lancon;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LanConDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LanCon.db";
    private static final int DATABASE_VERSION = 1;

    public LanConDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create Servers table
        String CREATE_SERVERS_TABLE = "CREATE TABLE servers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "server_name TEXT NOT NULL," +
                "server_ip TEXT NOT NULL UNIQUE," +
                "password TEXT," +
                "server_owner TEXT NOT NULL" +
                ");";
        db.execSQL(CREATE_SERVERS_TABLE);


        // Create Users table
        String CREATE_USERS_TABLE = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL," +
                "user_ip TEXT NOT NULL UNIQUE" +
                ");";
        db.execSQL(CREATE_USERS_TABLE);

        // Create Conversations table
        String CREATE_CONVERSATIONS_TABLE = "CREATE TABLE conversations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "from_user_id INTEGER NOT NULL," +
                "to_user_id INTEGER NOT NULL," +
                "message TEXT NOT NULL," +
                "message_type TEXT NOT NULL," +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY(from_user_id) REFERENCES users(id)," +
                "FOREIGN KEY(to_user_id) REFERENCES users(id)" +
                ");";
        db.execSQL(CREATE_CONVERSATIONS_TABLE);

        // Create any other necessary tables here
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS servers");
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS conversations");
        onCreate(db);
    }

    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM servers");
        db.execSQL("DELETE FROM users");
        db.execSQL("DELETE FROM conversations");
    }
}