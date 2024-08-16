package team.lancon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SelectionActivity extends AppCompatActivity {

    private Button joinServerButton, startServerButton;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        joinServerButton = findViewById(R.id.joinServer);
        startServerButton = findViewById(R.id.startServer);

        // Get the username from the intent
        userName = getIntent().getStringExtra("USERNAME");

        joinServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(SelectionActivity.this, "Running Servers in your LAN, Select One", Toast.LENGTH_SHORT).show();

                // Navigate to JoinServerActivity and pass the userName
                Intent intent = new Intent(SelectionActivity.this, JoinServerActivity.class);
                intent.putExtra("USERNAME", userName);
                startActivity(intent);

            }
        });

        startServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(SelectionActivity.this, "Server Started", Toast.LENGTH_SHORT).show();

            }
        });
    }

    /*private void startServer() {
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
    }*/
}