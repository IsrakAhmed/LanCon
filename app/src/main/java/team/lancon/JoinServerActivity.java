package team.lancon;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

import java.util.ArrayList;
import android.os.AsyncTask;

import android.os.Handler;
import android.os.Looper;

public class JoinServerActivity extends AppCompatActivity {

    private ListView serverListView;
    private HashMap <String, String> serverList;
    private TextView headerTextView;
    private static final int DISCOVERY_PORT = 8888; // The port for UDP broadcast
    private static final int SERVER_PORT = 12345; // The port for TCP connection
    private String serverIp;
    private String serverName;
    private String userName;
    private String userIp;
    private Handler handler;
    private Runnable runnable;
    private int dotCount = 0;
    private static final int MAX_DOTS = 4;
    private Button searchAgainButton;
    private boolean isSearching = true; // Track search state
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joinserver);

        searchAgainButton = findViewById(R.id.searchAgain);
        searchAgainButton.setVisibility(View.GONE);

        serverListView = findViewById(R.id.serverListView);

        headerTextView = findViewById(R.id.headerTextView);

        userRepository = new UserRepository(this);

        clearDatabase();

        // Get the username from the intent
        userName = getIntent().getStringExtra("USERNAME");

        userIp = getMyIp();

        //headerTextView.setText("Searching For Active Servers....");

        // Initialize the Handler before calling startDotAnimation()
        handler = new Handler(Looper.getMainLooper());

        // Start the dot animation
        startDotAnimation();

        new DiscoverServersTask().execute();
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
                headerTextView.setText("Searching For Active Servers" + dots.toString());

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

    private class DiscoverServersTask extends AsyncTask<Void, Void, HashMap<String, String>> {

        @Override
        protected HashMap<String, String> doInBackground(Void... voids) {
            return discoverServerIPs();
        }

        @Override
        protected void onPostExecute(HashMap<String, String> result) {

            serverList = result;

            //serverList.put("192.168.1.000", "Emni");

            List<String> serverDisplayList = new ArrayList<>();

            stopDotAnimation();

            for (HashMap.Entry<String, String> entry : serverList.entrySet()) {
                String displayText = entry.getValue() + " @ " + entry.getKey();
                serverDisplayList.add(displayText);
            }

            if (serverDisplayList.isEmpty()) {
                headerTextView.setText("No Active Server Found");
            } else {
                headerTextView.setText("Currently Active Servers");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(JoinServerActivity.this, R.layout.server_list_item, serverDisplayList);
                serverListView.setAdapter(adapter);
            }

            searchAgainButton.setVisibility(View.VISIBLE);

            searchAgainButton.setText("Search Again"); // Reset button text

            isSearching = false; // Reset search state

            serverListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // Get the server name from the adapter at the clicked position
                    String selectedServer = (String) parent.getItemAtPosition(position);

                    showConnectDialog(selectedServer);
                }
            });

            searchAgainButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSearching) {
                        stopSearch(); // Stop search if already searching

                    } else {
                        startSearch(); // Start search if not searching
                    }
                }
            });
        }
    }

    private void startSearch() {

        // Clear the list of servers by setting an empty adapter
        ArrayAdapter<String> emptyAdapter = new ArrayAdapter<>(JoinServerActivity.this, R.layout.server_list_item, new ArrayList<>());
        serverListView.setAdapter(emptyAdapter);

        // Start the dot animation
        startDotAnimation();

        new DiscoverServersTask().execute();

        searchAgainButton.setText("Stop Search"); // Update button text

        isSearching = true; // Update search state
    }

    private void stopSearch() {
        stopDotAnimation(); // Stop the dot animation

        handler.removeCallbacks(runnable); // Stop any ongoing search task

        searchAgainButton.setText("Search Again"); // Update button text

        isSearching = false; // Update search state
    }

    private void showConnectDialog(final String serverInfo) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Connect To Server");

        String[] infoServer = serverInfo.split(" @ ");

        builder.setMessage("Do You Want To Connect To " + infoServer[0] + " @ " + infoServer[1] + " ?");

        builder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(JoinServerActivity.this, "Okay, Connecting...", Toast.LENGTH_SHORT).show();
                // Implement connection logic here

                serverName = infoServer[0];
                serverIp = infoServer[1];

                connectServer(serverName, serverIp);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected HashMap<String, String> discoverServerIPs() {

        HashMap <String, String> servers = new HashMap<>();

        long startTime = System.currentTimeMillis();
        long timeoutPeriod = 15000; // 30 seconds timeout period
        long numberOfServers = 0;


        try (DatagramSocket socket = new DatagramSocket(DISCOVERY_PORT)) {
            socket.setSoTimeout(5000); // Timeout for receiving the broadcast

            byte[] buffer = new byte[256];

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            //Toast.makeText(JoinServerActivity.this, "Listening For Server Broadcasts...", Toast.LENGTH_SHORT).show();

            while (System.currentTimeMillis() - startTime < timeoutPeriod && isSearching) {
                try {
                    socket.receive(packet); // Receive the broadcast packet

                    String message = new String(packet.getData(), 0, packet.getLength()).trim();

                    String[] serverInfo = message.split("@"); // Split the message into server name and IP

                    String serverIP = serverInfo[0];
                    String serverName = serverInfo[1];

                    if (!servers.containsKey(serverIP)) {
                        servers.put(serverIP, serverName);

                        numberOfServers++;

                        startTime = System.currentTimeMillis(); // Reset the start time on receiving a packet to prevent timeout
                    }

                } catch (IOException e) {
                    //Toast.makeText(JoinServerActivity.this, "Something Is Wrong !!!", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (IOException e) {
            //Toast.makeText(JoinServerActivity.this, "Failed To Receive Server Broadcast.", Toast.LENGTH_SHORT).show();
        }

        stopDotAnimation();

        return servers;
    }

    private void connectServer(String serverName, String serverIp) {
        new ConnectTask().execute(serverIp, serverName);
    }

    private class ConnectTask extends AsyncTask<String, Void, Boolean> {
        private String serverName;
        private String serverIp;

        @Override
        protected Boolean doInBackground(String... params) {
            serverIp = params[0];
            serverName = params[1];

            try {
                NetworkConnection networkConnection = new NetworkConnection(serverIp, SERVER_PORT);

                ClientNCManager.getInstance().setNetworkConnection(networkConnection);

                return true; // Connection successful
            } catch (IOException e) {
                return false; // Connection failed
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                // Navigate to HomeActivity and pass the userName, serverIp, serverName
                Intent intent = new Intent(JoinServerActivity.this, HomeActivity.class);

                intent.putExtra("USERNAME", userName);
                intent.putExtra("userIp", userIp);
                intent.putExtra("serverIp", serverIp);
                intent.putExtra("serverName", serverName);
                intent.putExtra("serverOwner", "NO");

                startActivity(intent);
            } else {
                Toast.makeText(JoinServerActivity.this, "Unable To Connect To The Server.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void clearDatabase() {
        if (userRepository.getDBHelper() != null) {
            userRepository.getDBHelper().clearDatabase();
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