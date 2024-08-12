package team.lancon;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class JoinServerActivity extends AppCompatActivity {

    private ListView serverListView;
    private HashMap <String, String> serverList;
    private TextView headerTextView;
    private static final int DISCOVERY_PORT = 8888; // The port for UDP broadcast
    private static final int SERVER_PORT = 12345; // The port for TCP connection
    private static String serverIp = null;
    private static String serverName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joinserver);

        serverListView = findViewById(R.id.serverListView);

        headerTextView = findViewById(R.id.headerTextView);

        headerTextView.setText("Searching For Active Servers....");

        new DiscoverServersTask().execute();

        // list of active servers
        /*serverList = discoverServerIPs();

        //serverList.put("192.168.1.000", "Emni");

        // Create a list to hold the formatted server information
        List<String> serverDisplayList = new ArrayList<>();

        for (HashMap.Entry<String, String> entry : serverList.entrySet()) {
            String displayText = entry.getValue() + " - " + entry.getKey();
            serverDisplayList.add(displayText);
        }


        if (serverDisplayList.isEmpty()) {
            headerTextView.setText("No Active Server Found");
        }

        else {
            headerTextView.setText("Currently Active Servers");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.server_list_item, serverDisplayList);
            serverListView.setAdapter(adapter);
        }

        serverListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedServer = serverList.get(position);
                showConnectDialog(selectedServer);
            }
        });*/
    }

    private class DiscoverServersTask extends AsyncTask<Void, Void, HashMap<String, String>> {

        @Override
        protected HashMap<String, String> doInBackground(Void... voids) {
            return discoverServerIPs();
        }

        @Override
        protected void onPostExecute(HashMap<String, String> result) {

            serverList = result;

            serverList.put("192.168.1.000", "Emni");

            List<String> serverDisplayList = new ArrayList<>();

            for (HashMap.Entry<String, String> entry : serverList.entrySet()) {
                String displayText = entry.getValue() + " - " + entry.getKey();
                serverDisplayList.add(displayText);
            }

            if (serverDisplayList.isEmpty()) {
                headerTextView.setText("No Active Server Found");
            } else {
                headerTextView.setText("Currently Active Servers");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(JoinServerActivity.this, R.layout.server_list_item, serverDisplayList);
                serverListView.setAdapter(adapter);
            }

            serverListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // Get the server name from the adapter at the clicked position
                    String selectedServer = (String) parent.getItemAtPosition(position);

                    showConnectDialog(selectedServer);
                }
            });
        }
    }

    private void showConnectDialog(final String serverInfo) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Connect To Server");
        builder.setMessage("Do You Want To Connect To " + serverInfo + " ?");

        builder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(JoinServerActivity.this, "Okay, Connecting...", Toast.LENGTH_SHORT).show();
                // Implement connection logic here
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

            while (System.currentTimeMillis() - startTime < timeoutPeriod) {
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

        return servers;
    }

}