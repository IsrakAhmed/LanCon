package team.lancon;

import java.util.List;

public class MessageListManager {

    private static MessageListManager instance;
    private List<Message> messagesList;
    private MessagesAdapter messagesAdapter;
    private ConversationRepository conversationRepository;
    private DataChangedListener dataChangedListener;

    private MessageListManager() {}

    public static MessageListManager getInstance() {
        if (instance == null) {
            instance = new MessageListManager();
        }
        return instance;
    }

    public void setMessagesList(List<Message> messagesList) {
        this.messagesList = messagesList;
    }

    public List<Message> getMessagesList() {
        return messagesList;
    }

    public void setMessagesAdapter(MessagesAdapter messagesAdapter) {
        this.messagesAdapter = messagesAdapter;
    }

    public MessagesAdapter getMessagesAdapter() {
        return messagesAdapter;
    }

    public void setConversationRepository(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public ConversationRepository getConversationRepository() {
        return conversationRepository;
    }

    // Method to set the data changed listener
    public void setDataChangedListener(DataChangedListener listener) {
        this.dataChangedListener = listener;
    }

    // Notify listener when data changes
    public void notifyDataChanged() {
        if (dataChangedListener != null) {
            dataChangedListener.onDataChanged();
        }
    }

    // Listener interface
    public interface DataChangedListener {
        void onDataChanged();
    }
}
