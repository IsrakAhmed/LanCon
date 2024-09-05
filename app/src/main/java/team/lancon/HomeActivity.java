package team.lancon;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private ListView conversationListView;
    private TextView noConversationsTextView;
    private Button convosButton;
    private Button activePipsButton;
    private String serverIp, serverName, userName, userIp, serverOwner, serverPassword;

    private ConversationRepository conversationRepository;
    private UserRepository userRepository;
    private ServerRepository serverRepository;
    ServerSocket serverSocket;
    NetworkConnection networkConnection;
    ServerNCManager serverNCManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        conversationListView = findViewById(R.id.conversationListView);
        noConversationsTextView = findViewById(R.id.noConversationsTextView);
        convosButton = findViewById(R.id.convosButton);
        activePipsButton = findViewById(R.id.activePipsButton);

        // Set Convos button as active by default
        setActiveButton(convosButton);
        setInactiveButton(activePipsButton);

        conversationRepository = new ConversationRepository(this);
        userRepository = new UserRepository(this);
        serverRepository = new ServerRepository(this);

        clearDatabase();

        serverPassword = null;


        // Get the serverIp from the intent
        serverIp = getIntent().getStringExtra("serverIp");

        // Get the serverName from the intent
        serverName = getIntent().getStringExtra("serverName");

        // Get the username from the intent
        userName = getIntent().getStringExtra("USERNAME");

        // Get the userIp from the intent
        userIp = getIntent().getStringExtra("userIp");

        // Get the serverOwner from the intent
        serverOwner = getIntent().getStringExtra("serverOwner");


        if(Objects.equals(serverOwner, "YES")){

            serverRepository.addServer(serverName, serverIp, serverPassword, serverOwner);

            userRepository.addUser(userName, userIp);

            serverSocket = ServerSocketManager.getInstance().getServerSocket();

            serverNCManager = ServerNCManager.getInstance();

            new Thread(() -> {
                // Map to store client information
                HashMap<String, Information> clientList = new HashMap<>();

                while (true) {

                    try {
                        Socket socket = serverSocket.accept();
                        //System.out.println("New client connected: " + socket.getInetAddress().getHostAddress());

                        NetworkConnection networkConnection = new NetworkConnection(socket);

                        new Thread(() -> {
                            startNewUser(socket, networkConnection);
                        }).start();

                        //new Thread(new ReaderWriterServer(userName, networkConnection, userRepository)).start();

                        //new Thread(new CreateConnection(clientList, networkConnection)).start();

                    } catch (Exception e){

                    }
                }

            }).start();
        }

        else{

            networkConnection = ClientNCManager.getInstance().getNetworkConnection();

            new Thread(new ReaderWriterServer(userName, networkConnection, userRepository)).start();

            /*new Thread(() -> {



            }).start();*/
        }


        showConversations();


        // Set OnClickListener for Buttons
        convosButton.setOnClickListener(v -> {
            showConversations();
            setActiveButton(convosButton);
            setInactiveButton(activePipsButton);
        });

        activePipsButton.setOnClickListener(v -> {
            // Run network operations on a separate thread
            new Thread(() -> {
                if (Objects.equals(serverOwner, "YES")) {
                    Cursor cursor = userRepository.getAllUsers();

                    List<HashMap<String, Object>> userList = new ArrayList<>();

                    if (cursor.moveToFirst()) {
                        do {
                            HashMap<String, Object> row = new HashMap<>();

                            for (int i = 0; i < cursor.getColumnCount(); i++) {
                                String columnName = cursor.getColumnName(i);
                                Object value = cursor.getString(i); // Get value based on the column type
                                row.put(columnName, value);
                            }

                            userList.add(row);

                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                    // Post the result to the UI thread
                    runOnUiThread(() -> showActivePips(userList));
                }

                else {
                    /*Log.e("HomeActivity", "Creating data object");
                    Data dataObject = new Data();
                    Log.e("HomeActivity", "writing message into data object");
                    dataObject.message = "GET_ALL-USERS";
                    Log.e("HomeActivity", "sending GET_ALL-USERS message send");
                    networkConnection.write(dataObject);

                    Log.e("HomeActivity", "GET_ALL-USERS message send");*/

                    try {

                        Log.e("HomeActivity", "Creating data object");
                        Data dataObject = new Data();
                        Log.e("HomeActivity", "writing message into data object");
                        dataObject.message = "GET_ALL-USERS";
                        Log.e("HomeActivity", "sending GET_ALL-USERS message send");
                        networkConnection.write(dataObject);

                        Log.e("HomeActivity", "GET_ALL-USERS message send");

                        Thread.sleep(1000);
                    } catch (Exception e) {}

                    List<HashMap<String, Object>> receivedUserList = ClientULManager.getInstance().getUserList();

                    Log.e("HomeActivity", "receivedUserList got from instance");
                    /*List<HashMap<String, Object>> receivedUserList = new ArrayList<>();

                    // Create a dummy user
                    HashMap<String, Object> dummyUser1 = new HashMap<>();
                    dummyUser1.put("username", "John Doe");
                    dummyUser1.put("user_ip", "192.168.1.2");

                    // Add the dummy user to the list
                    receivedUserList.add(dummyUser1);*/

                    // Post the result to the UI thread
                    runOnUiThread(() -> showActivePips(receivedUserList));

                }

                // Update UI to reflect the active button state
                runOnUiThread(() -> {
                    setActiveButton(activePipsButton);
                    setInactiveButton(convosButton);

                    conversationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            // Get the server name from the adapter at the clicked position
                            String selectedPip = (String) parent.getItemAtPosition(position);

                            String[] selectedPipInfo = selectedPip.split(" @ ");

                            String receiversUserName = selectedPipInfo[0];
                            String receiversUserIp = selectedPipInfo[1];

                            // Navigate to ConversationActivity and pass the serverIp, serverName, receiversUserName, receiversUserIp
                            Intent intent = new Intent(HomeActivity.this, ConversationActivity.class);

                            intent.putExtra("serverIp", serverIp);
                            intent.putExtra("serverName", serverName);
                            intent.putExtra("receiversUserName", receiversUserName);
                            intent.putExtra("receiversUserIp", receiversUserIp);

                            startActivity(intent);
                        }
                    });
                });
            }).start();
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        clearDatabase();
    }


    private void setActiveButton(Button button) {
        button.setTextColor(Color.WHITE); // Set text color to white for the active button
        button.setBackgroundColor(Color.GRAY); // Set background color to gray for the active button
    }

    private void setInactiveButton(Button button) {
        button.setTextColor(Color.BLACK); // Set text color to black for inactive buttons
        button.setBackgroundColor(Color.LTGRAY); // Set background color to light gray for inactive buttons
    }

    private void startNewUser(Socket socket, NetworkConnection networkConnection){
        String newUserIP = socket.getInetAddress().getHostAddress();

        boolean flag = true;

        while (flag){
            try {

                Data dataObject = new Data();
                dataObject.message = "GET_USERNAME";
                networkConnection.write(dataObject);

                /*Object obj = networkConnection.read();
                Data dataObj = (Data) obj;*/
                Data dataObj = networkConnection.read();
                String actualMessage = dataObj.message;

                String[] userInfo = actualMessage.split("::");

                String newUserName;

                if(Objects.equals(userInfo[0], "USERNAME")){
                    newUserName = userInfo[1];

                    userRepository.addUser(newUserName, newUserIP);

                    List<HashMap<String, NetworkConnection>> clientConnections = serverNCManager.getClientConnections();

                    // Create a new user
                    HashMap<String, NetworkConnection> newUserMap = new HashMap<>();
                    newUserMap.put(newUserIP, networkConnection);

                    // Add the new user to the list
                    clientConnections.add(newUserMap);

                    flag = false;
                }
            } catch (Exception e) {}
        }

        new Thread(new ReaderWriterServer(userName, networkConnection, userRepository)).start();
    }

    private void showActivePips(List<HashMap<String, Object>> userList){

        //Toast.makeText(HomeActivity.this, "Showing Active Pips", Toast.LENGTH_SHORT).show();

        if (userList.isEmpty()) {
            noConversationsTextView.setText("No Active Pips");
            noConversationsTextView.setVisibility(View.VISIBLE);
            conversationListView.setVisibility(View.GONE);
        } else {
            noConversationsTextView.setVisibility(View.GONE);
            conversationListView.setVisibility(View.VISIBLE);

            List <String> receivedUserList = new ArrayList<>();

            // Iterate through the userList and feed data into receivedUserList
            for (HashMap<String, Object> userMap : userList) {
                // Extract username and user_ip
                String username = (String) userMap.get("username");
                String userIp = (String) userMap.get("user_ip");

                // Combine them into a single string
                String userInfo = username + " @ " + userIp;

                // Add to the receivedUserList
                receivedUserList.add(userInfo);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    R.layout.list_item_conversation,
                    R.id.conversationItemTextView,
                    receivedUserList
            );

            conversationListView.setAdapter(adapter);
        }
    }

    private void showConversations(){
        // Dummy Data for Conversations
        List<String> conversations = getConversations();

        if (conversations.isEmpty()) {
            noConversationsTextView.setText("No Conversations");
            noConversationsTextView.setVisibility(View.VISIBLE);
            conversationListView.setVisibility(View.GONE);
        } else {
            noConversationsTextView.setVisibility(View.GONE);
            conversationListView.setVisibility(View.VISIBLE);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    R.layout.list_item_conversation,
                    R.id.conversationItemTextView,
                    conversations
            );

            conversationListView.setAdapter(adapter);
        }
    }

    private List<String> getConversations() {
        // Fetch the list of conversations from your data source
        // For now, returning dummy data
        List<String> conversations = new ArrayList<>();
        //conversations.add("Conversation 1");
        //conversations.add("Conversation 2");
        //conversations.add("Conversation 3");
        return conversations;
    }


    private void clearDatabase() {
        if (userRepository.getDBHelper() != null) {
            userRepository.getDBHelper().clearDatabase();
        }
    }
}