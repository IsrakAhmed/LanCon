package team.lancon;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import team.lancon.MessagesAdapter;
import team.lancon.Message;

public class ConversationActivity extends AppCompatActivity {

    private ConversationRepository conversationRepository;
    private UserRepository userRepository;
    private ServerRepository serverRepository;
    private TextView receiversNameTextView;
    private String serverIp, serverName, receiversUserName, receiversUserIp;
    private EditText messageEditText;
    private ImageButton sendMessageButton;
    private RecyclerView messagesRecyclerView;
    private MessagesAdapter messagesAdapter;
    private List<Message> messagesList;


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

        // Get the receiversUserName from the intent
        receiversUserName = getIntent().getStringExtra("receiversUserName");

        // Get the receiversUserIp from the intent
        receiversUserIp = getIntent().getStringExtra("receiversUserIp");

        receiversNameTextView.setText(receiversUserName);

        messageEditText = findViewById(R.id.messageEditText);
        sendMessageButton = findViewById(R.id.sendMessageButton);

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

        // Sample messages for testing
        messagesList.add(new Message("Hello, how are you?", false));  // Received message
        messagesList.add(new Message("I'm good, thank you!", true));  // Sent message
        messagesList.add(new Message("Great to hear!", false));  // Received message
        messagesList.add(new Message("What about you?", true));  // Sent message

        messagesAdapter = new MessagesAdapter(messagesList);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messagesRecyclerView.setAdapter(messagesAdapter);


    }
}
