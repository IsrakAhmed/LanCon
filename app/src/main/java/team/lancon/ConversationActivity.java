package team.lancon;

import android.content.Context;
import android.database.Cursor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import team.lancon.MessagesAdapter;
import team.lancon.Message;

public class ConversationActivity extends AppCompatActivity {

    private ConversationRepository conversationRepository;
    private UserRepository userRepository;
    private ServerRepository serverRepository;
    private TextView receiversNameTextView;
    private String serverIp, serverName, serverOwner, receiversUserName, receiversUserIp;
    private EditText messageEditText;
    private ImageButton sendMessageButton;
    private RecyclerView messagesRecyclerView;
    private MessagesAdapter messagesAdapter;
    private List<Message> messagesList;
    NetworkConnection networkConnection;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        conversationRepository = new ConversationRepository(this);
        userRepository = new UserRepository(this);
        serverRepository = new ServerRepository(this);

        receiversNameTextView = findViewById(R.id.receiversNameTextView);

        // Get the serverIp from the intent
        serverIp = getIntent().getStringExtra("serverIp");

        // Get the serverName from the intent
        serverName = getIntent().getStringExtra("serverName");

        // Get the serverOwner from the intent
        serverOwner = getIntent().getStringExtra("serverOwner");

        // Get the receiversUserName from the intent
        receiversUserName = getIntent().getStringExtra("receiversUserName");

        // Get the receiversUserIp from the intent
        receiversUserIp = getIntent().getStringExtra("receiversUserIp");

        receiversNameTextView.setText(receiversUserName);

        messageEditText = findViewById(R.id.messageEditText);
        sendMessageButton = findViewById(R.id.sendMessageButton);

        if(Objects.equals(serverOwner, "YES")) {
            List<HashMap<String, NetworkConnection>> clientConnections = ServerNCManager.getInstance().getClientConnections();

            String targetIP = receiversUserIp;

            for (HashMap<String, NetworkConnection> userMap : clientConnections) {
                if (userMap.containsKey(targetIP)) {
                    networkConnection = userMap.get(targetIP);
                }
            }
        }

        else{
            networkConnection = ClientNCManager.getInstance().getNetworkConnection();
        }

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable/Disable send button based on message input
                sendMessageButton.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messagesList = new ArrayList<>();

        reloadMessagesFromDatabase();

        MessageListManager.getInstance().setDataChangedListener(new MessageListManager.DataChangedListener() {
            @Override
            public void onDataChanged() {

                // Using runOnUiThread to reload the UI safely from the main thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Reload the messages from the database
                        reloadMessagesFromDatabase();
                    }
                });
            }
        });

        //MessageListManager.getInstance().setMessagesList(messagesList);
        //MessageListManager.getInstance().setConversationRepository(conversationRepository);



        // Sample messages for testing
        //messagesList.add(new Message("Hello, how are you?", false));  // Received message
        //messagesList.add(new Message("I'm good, thank you!", true));  // Sent message


        // Send the message when sendMessageButton is clicked
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });


    }


    public void reloadMessagesFromDatabase() {
        messagesList.clear();  // Clear the old list

        Cursor cursor = conversationRepository.getConversationsBetweenUsers(getMyIp(), receiversUserIp);

        // Move to the first record in the cursor
        if (cursor.moveToFirst()) {
            // Get column indexes
            int fromUserIpIndex = cursor.getColumnIndex("from_user_ip");
            int toUserIpIndex = cursor.getColumnIndex("to_user_ip");
            int messageIndex = cursor.getColumnIndex("message");
            int messageTypeIndex = cursor.getColumnIndex("message_type");
            int timestampIndex = cursor.getColumnIndex("timestamp");

            do {
                // Extract the values from the cursor
                String fromUserIp = cursor.getString(fromUserIpIndex);
                String messageContent = cursor.getString(messageIndex);

                // Determine if the message was sent by the current user
                boolean isSentByUser = fromUserIp.equals(getMyIp());

                // Create a new Message object and add it to the messages list
                Message message = new Message(messageContent, isSentByUser);
                messagesList.add(message);

                // Optionally, you can use messageType or timestamp if needed

            } while (cursor.moveToNext());  // Move to the next row in the cursor
        }

        // Close the cursor when done to release resources
        cursor.close();

        messagesAdapter = new MessagesAdapter(messagesList);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(messagesAdapter);

        MessageListManager.getInstance().setMessagesAdapter(messagesAdapter);

        // Notify the adapter about the new data
        messagesAdapter.notifyDataSetChanged();
    }


    private void sendMessage() {
        // Get the message text from the EditText
        String messageText = messageEditText.getText().toString().trim();

        // Check if the message is not empty
        if (!messageText.isEmpty()) {
            // Create a new Message object representing the sent message
            Message sentMessage = new Message(messageText, true);

            // Add the message to the list
            messagesList.add(sentMessage);

            // Notify the adapter that a new item has been inserted
            messagesAdapter.notifyItemInserted(messagesList.size() - 1);

            // Scroll the RecyclerView to the last message
            messagesRecyclerView.scrollToPosition(messagesList.size() - 1);

            // Clear the message input field
            messageEditText.setText("");

            try {
                Data dataObject = new Data();
                dataObject.message = messageText;
                dataObject.sendersUserIp = getMyIp();
                dataObject.receiversUserIp = receiversUserIp;

                new Thread(() -> {
                    networkConnection.write(dataObject);
                    conversationRepository.addConversation(getMyIp(), receiversUserIp, messageText, "Plain Text");
                }).start();

            } catch (Exception e) {
                Message problemMessage = new Message("Unable To Send Message", true);
                messagesList.add(problemMessage);
            }
        }
    }


    private String getMyIp(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();

        return formatIpAddress(ipAddress);
    }

    private String formatIpAddress(int ipAddress) {
        return String.format("%d.%d.%d.%d",
                (ipAddress & 0xff),
                (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff),
                (ipAddress >> 24 & 0xff));
    }

}
