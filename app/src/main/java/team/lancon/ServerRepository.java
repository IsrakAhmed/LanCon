package team.lancon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ServerRepository {
    private LanConDatabaseHelper dbHelper;

    public ServerRepository(Context context) {
        dbHelper = new LanConDatabaseHelper(context);
    }

    // Add a new server
    public long addServer(String serverName, String serverIp, String password, String serverOwner) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("server_name", serverName);
        values.put("server_ip", serverIp);
        values.put("password", password);
        values.put("server_owner", serverOwner);
        return db.insert("servers", null, values);
    }

    // Get a server by IP address
    public Cursor getServerByIp(String serverIp) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query("servers", null, "server_ip = ?", new String[]{serverIp}, null, null, null);
    }

    // Update server information
    public int updateServer(String serverIp, String serverName, String password, String serverOwner) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("server_name", serverName);
        values.put("password", password);
        values.put("server_owner", serverOwner);
        return db.update("servers", values, "server_ip = ?", new String[]{serverIp});
    }

    // Delete a server by IP address
    public int deleteServer(String serverIp) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("servers", "server_ip = ?", new String[]{serverIp});
    }
}