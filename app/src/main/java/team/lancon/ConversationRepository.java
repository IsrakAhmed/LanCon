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
    public long addConversation(int fromUserId, int toUserId, String message, String messageType) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("from_user_id", fromUserId);
        values.put("to_user_id", toUserId);
        values.put("message", message);
        values.put("message_type", messageType);
        return db.insert("conversations", null, values);
    }

    // Get all conversations between two users
    public Cursor getConversationsBetweenUsers(int fromUserId, int toUserId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query("conversations", null,
                "(from_user_id = ? AND to_user_id = ?) OR (from_user_id = ? AND to_user_id = ?)",
                new String[]{String.valueOf(fromUserId), String.valueOf(toUserId), String.valueOf(toUserId), String.valueOf(fromUserId)},
                null, null, "timestamp ASC");
    }

    // Delete a conversation by ID
    public int deleteConversation(int conversationId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete("conversations", "id = ?", new String[]{String.valueOf(conversationId)});
    }
}