package team.lancon;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import team.lancon.R;
import team.lancon.Message;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messages;
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private static final int VIEW_TYPE_SENT_IMAGE = 3;
    private static final int VIEW_TYPE_RECEIVED_IMAGE = 4;

    public MessagesAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        // Check if the message or image is sent by the current user
        Message message = messages.get(position);
        if (message.isImageMessage()) {
            if (message.isSentByCurrentUser()) {
                return VIEW_TYPE_SENT_IMAGE;  // Sent image message
            } else {
                return VIEW_TYPE_RECEIVED_IMAGE;  // Received image message
            }
        } else if (message.isSentByCurrentUser()) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        }

        else if (viewType == VIEW_TYPE_RECEIVED) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }

        else if (viewType == VIEW_TYPE_SENT_IMAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentImageViewHolder(view);
        }

        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedImageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(message);
        }

        else if (holder instanceof ReceivedMessageViewHolder) {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }

        else if (holder instanceof SentImageViewHolder) {
            ((SentImageViewHolder) holder).bind(message);
        }

        else if (holder instanceof ReceivedImageViewHolder) {
            ((ReceivedImageViewHolder) holder).bind(message);
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // ViewHolder for sent messages
    class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView sentMessageTextView;

        SentMessageViewHolder(View itemView) {
            super(itemView);
            sentMessageTextView = itemView.findViewById(R.id.sentMessageTextView);
        }

        void bind(Message message) {
            sentMessageTextView.setText(message.getText());
            sentMessageTextView.setVisibility(View.VISIBLE);
        }
    }

    // ViewHolder for received messages
    class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView receivedMessageTextView;

        ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            receivedMessageTextView = itemView.findViewById(R.id.receivedMessageTextView);
            receivedMessageTextView.setVisibility(View.VISIBLE);
        }

        void bind(Message message) {
            receivedMessageTextView.setText(message.getText());
        }
    }


    // ViewHolder for sent image messages
    class SentImageViewHolder extends RecyclerView.ViewHolder {
        ImageView sentImageView;

        SentImageViewHolder(View itemView) {
            super(itemView);
            sentImageView = itemView.findViewById(R.id.sentImageView);
        }

        void bind(Message message) {
            // Set the image for sent image message
            Bitmap imageBitmap = message.getImageBitmap();
            if (imageBitmap != null) {
                sentImageView.setImageBitmap(imageBitmap);
                sentImageView.setVisibility(View.VISIBLE);
            }
        }
    }


    // ViewHolder for received image messages
    class ReceivedImageViewHolder extends RecyclerView.ViewHolder {
        ImageView receivedImageView;

        ReceivedImageViewHolder(View itemView) {
            super(itemView);
            receivedImageView = itemView.findViewById(R.id.receivedImageView);
        }

        void bind(Message message) {
            // Set the image for received image message
            Bitmap imageBitmap = message.getImageBitmap();
            if (imageBitmap != null) {
                receivedImageView.setImageBitmap(imageBitmap);
                receivedImageView.setVisibility(View.VISIBLE);
            }
        }
    }
}