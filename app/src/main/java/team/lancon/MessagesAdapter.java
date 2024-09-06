package team.lancon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public MessagesAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        // Check if the message is sent by the current user
        if (messages.get(position).isSentByCurrentUser()) {
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
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(message);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message);
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
        }
    }

    // ViewHolder for received messages
    class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView receivedMessageTextView;

        ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            receivedMessageTextView = itemView.findViewById(R.id.receivedMessageTextView);
        }

        void bind(Message message) {
            receivedMessageTextView.setText(message.getText());
        }
    }
}