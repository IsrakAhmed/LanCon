package team.lancon;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ChatActivity extends AppCompatActivity {

    private TextView serverStatusTextView;
    private TextView clientStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        serverStatusTextView = findViewById(R.id.serverStatusTextView);
        clientStatusTextView = findViewById(R.id.clientStatusTextView);

        // Get the username from the intent
        String username = getIntent().getStringExtra("USERNAME");

        // Start server and client threads
        startServer();
        startClient();
    }

    private void startServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Simulate server start
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        serverStatusTextView.setText("Server Started");
                    }
                });
            }
        }).start();
    }

    private void startClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Simulate client start
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        clientStatusTextView.setText("Client Started");
                    }
                });
            }
        }).start();
    }
}