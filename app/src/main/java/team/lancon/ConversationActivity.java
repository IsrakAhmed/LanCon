package team.lancon;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ConversationActivity extends AppCompatActivity {

    private ConversationRepository conversationRepository;
    private UserRepository userRepository;
    private ServerRepository serverRepository;
    private TextView receiversNameTextView;
    private String serverIp, serverName, receiversUserName, receiversUserIp;


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

    }
}
