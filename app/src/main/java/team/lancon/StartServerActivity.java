package team.lancon;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class StartServerActivity extends AppCompatActivity {

    private static final int TCP_PORT = 12345;
    private static final int BROADCAST_PORT = 8888;
    private String SERVER_NAME;
    private String SERVER_IP;
    ServerSocket serverSocket;

    private String userName;
    private EditText serverNameEditText;
    private TextView headerTextView;
    private Button startServerButton;
    private Handler handler;
    private Runnable runnable;
    private int dotCount = 0;
    private static final int MAX_DOTS = 4;

    private ProgressBar progressBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startserver);

        // Get the username from the intent
        userName = getIntent().getStringExtra("USERNAME");

        // Initialize views
        serverNameEditText = findViewById(R.id.serverNameEditText);
        startServerButton = findViewById(R.id.startServerButton);
        progressBar = findViewById(R.id.progressBar);
        headerTextView = findViewById(R.id.headerTextView);

        serverNameEditText.setText(userName + "'s Server");

        // Set click listener for the Start Server button
        startServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String serverName = serverNameEditText.getText().toString().trim();

                if (serverName.isEmpty()) {
                    Toast.makeText(StartServerActivity.this, "Please Enter Server Name", Toast.LENGTH_SHORT).show();
                } else {

                    // Show the progress bar
                    progressBar.setVisibility(View.VISIBLE);

                    // Initialize the Handler before calling startDotAnimation()
                    handler = new Handler(Looper.getMainLooper());

                    // Start the dot animation
                    startDotAnimation();

                    // Proceed with starting the server
                    // Add your server starting logic here
                    Toast.makeText(StartServerActivity.this, "Server Starting With Name: " + serverName, Toast.LENGTH_SHORT).show();

                    new Thread(() -> {
                        new ServerMain(serverName);

                        // Simulate delay (This is to make sure you see the progress bar)
                        runOnUiThread(() -> {
                            new android.os.Handler().postDelayed(() -> {
                                progressBar.setVisibility(View.GONE);

                                stopDotAnimation();
                                headerTextView.setText("Server Started");

                                Toast.makeText(StartServerActivity.this, "Server Started Successfully", Toast.LENGTH_SHORT).show();
                            }, 5000); // 5 seconds delay for demonstration purposes

                            new android.os.Handler().postDelayed(() -> {

                                // Navigate to HomeActivity and pass the USERNAME, serverIp, serverName and serverOwner
                                Intent intent = new Intent(StartServerActivity.this, HomeActivity.class);

                                intent.putExtra("USERNAME", userName);
                                intent.putExtra("userIp", SERVER_IP);
                                intent.putExtra("serverIp", SERVER_IP);
                                intent.putExtra("serverName", SERVER_NAME);
                                intent.putExtra("serverOwner", "YES");

                                startActivity(intent);

                            }, 5000); // 5 seconds delay for demonstration purposes

                        });

                    }).start();

                }
            }
        });
    }

    private void startDotAnimation() {

        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        runnable = new Runnable() {
            @Override
            public void run() {
                StringBuilder dots = new StringBuilder();
                for (int i = 0; i < dotCount; i++) {
                    dots.append(".");
                }
                headerTextView.setText("Creating Server" + dots.toString());

                dotCount = (dotCount + 1) % (MAX_DOTS + 1);

                // Repeat the animation every 500ms (half a second)
                handler.postDelayed(this, 500);
            }
        };

        handler.post(runnable);
    }

    private void stopDotAnimation() {
        handler.removeCallbacks(runnable);
    }


    private class ServerMain {

        ServerMain(String serverName) {
            try {

                SERVER_NAME = serverName;

                // Start broadcasting the server IP
                new Thread(() -> broadcastServerIp()).start();

                // Start TCP server
                serverSocket = new ServerSocket(TCP_PORT);

                ServerSocketManager.getInstance().setServerSocket(serverSocket);

                //System.out.println("Server Started...");
                //System.out.println("Server IP: " + InetAddress.getLocalHost().getHostAddress());

            } catch (Exception e) {
                //System.err.println("Server error: " + e.getMessage());
                //e.printStackTrace();
            }
        }

        private void broadcastServerIp() {
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setBroadcast(true);

                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();
                String ipAddressString = formatIpAddress(ipAddress);

                SERVER_IP = ipAddressString;

                //String serverIp = InetAddress.getLocalHost().getHostAddress();

                String serverIp = ipAddressString;
                String message = serverIp + "@" + SERVER_NAME; // Include server name in the message
                byte[] buffer = message.getBytes();

                DatagramPacket packet = new DatagramPacket(
                        buffer,
                        buffer.length,
                        InetAddress.getByName("255.255.255.255"),
                        BROADCAST_PORT
                );

                while (true) {
                    socket.send(packet);
                    //System.out.println("Broadcasting server IP: " + serverIp);
                    Thread.sleep(5000); // Broadcast every 5 seconds
                }
            } catch (IOException | InterruptedException e) {
                //System.err.println("Error broadcasting server IP: " + e.getMessage());
                //e.printStackTrace();
            }
        }

        private String formatIpAddress(int ipAddress) {
            return String.format("%d.%d.%d.%d",
                    (ipAddress & 0xff),
                    (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff),
                    (ipAddress >> 24 & 0xff));
        }
    }

}