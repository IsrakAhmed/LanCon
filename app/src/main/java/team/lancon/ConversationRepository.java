package team.lancon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ConversationRepository {
    private LanConDatabaseHelper dbHelper;

    public ConversationRepository(Context context) {
        dbHelper = new LanConDatabaseHelper(context);
    }

    // Add a new conversation
    public long addConversation(String fromUserIp, String toUserIp, String message, String messageType) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("from_user_ip", fromUserIp);
        values.put("to_user_ip", toUserIp);
        values.put("message", message);
        values.put("message_type", messageType);
        return db.insert("conversations", null, values);
    }

    // Get all conversations between two users
    public Cursor getConversationsBetweenUsers(String fromUserIp, String toUserIp) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query("conversations", null,
                "(from_user_ip = ? AND to_user_ip = ?) OR (from_user_ip = ? AND to_user_ip = ?)",
                new String[]{String.valueOf(fromUserIp), String.valueOf(toUserIp), String.valueOf(toUserIp), String.valueOf(fromUserIp)},
                null, null, "timestamp ASC");
    }

    // Delete a conversation by ID
    public int deleteConversation(int conversationId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("conversations", "id = ?", new String[]{String.valueOf(conversationId)});
    }
}