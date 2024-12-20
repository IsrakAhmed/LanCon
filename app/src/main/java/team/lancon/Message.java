package team.lancon;

import android.graphics.Bitmap;

public class Message {
    private String text;
    private boolean isSentByCurrentUser;
    private Bitmap imageBitmap;


    // Constructor for text message
    public Message(String text, boolean isSentByCurrentUser) {
        this.text = text;
        this.isSentByCurrentUser = isSentByCurrentUser;
        this.imageBitmap = null;
    }


    // Constructor for image message
    public Message(Bitmap imageBitmap, boolean isSentByCurrentUser) {
        this.imageBitmap = imageBitmap;
        this.isSentByCurrentUser = isSentByCurrentUser;
        this.text = null;  // No text in image message
    }

    public String getMessageType() {
        if(text != null && imageBitmap == null){
            return "Plain Text";
        }
        else{
            return "Image";
        }
    }

    public String getText() {
        return text;
    }

    public boolean isSentByCurrentUser() {
        return isSentByCurrentUser;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public boolean isImageMessage() {
        return imageBitmap != null;
    }
}