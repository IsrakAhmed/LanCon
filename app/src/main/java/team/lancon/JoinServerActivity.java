package team.lancon;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class JoinServerActivity extends AppCompatActivity {

    private ListView serverListView;
    private ArrayList<String> serverList;
    private TextView headerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joinserver);

        serverListView = findViewById(R.id.serverListView);

        headerTextView = findViewById(R.id.headerTextView);

        // Mock data for active servers
        serverList = new ArrayList<>();
        serverList.add("Server 1 - 192.168.1.101");
        serverList.add("Server 2 - 192.168.1.102");
        serverList.add("Server 3 - 192.168.1.103");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.server_list_item, serverList);

        serverListView.setAdapter(adapter);

        serverListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedServer = serverList.get(position);
                showConnectDialog(selectedServer);
            }
        });
    }

    private void showConnectDialog(final String serverInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Connect to Server");
        builder.setMessage("Do you want to connect to " + serverInfo + "?");

        builder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(JoinServerActivity.this, "Okay, connecting...", Toast.LENGTH_SHORT).show();
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
}