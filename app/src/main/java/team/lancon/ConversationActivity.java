package team.lancon;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.Manifest;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    private ImageButton sendMessageButton,cameraButton,galleryButton;
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
        cameraButton = findViewById(R.id.cameraButton);
        galleryButton = findViewById(R.id.galleryButton);

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


        // Capture the image and send it when cameraButton is clicked
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent,1);
                }
                else{
                    //request camera permission
                    requestPermissions(new String[]{Manifest.permission.CAMERA},100);
                }
            }
        });


        // Select image from gallery and send it
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent,2);
                }
                else{
                    // Request permission to access external storage
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Get the captured image as a Bitmap
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // Now you can display or process the image
            // Convert the Bitmap to a byte array or send it as needed
            sendImage(imageBitmap);
        }

        else if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            // Handle image selection from gallery
            Uri selectedImageUri = data.getData();
            try {
                Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                sendImage(selectedImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            int imageDataIndex = cursor.getColumnIndex("image_data");

            do {
                // Extract the values from the cursor
                String fromUserIp = cursor.getString(fromUserIpIndex);
                String messageContent = cursor.getString(messageIndex);

                String messageType = cursor.getString(messageTypeIndex);
                byte[] imageData = cursor.getBlob(imageDataIndex);  // Retrieve image data

                // Determine if the message was sent by the current user
                boolean isSentByUser = fromUserIp.equals(getMyIp());


                if ("Image".equals(messageType)) {
                    // Handle image messages
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                    Message imageMessage = new Message(imageBitmap, isSentByUser);
                    messagesList.add(imageMessage);

                    //Log.d("ConversationActivity", "First : " + String.valueOf(imageBitmap.getByteCount()));
                    //Log.d("ConversationActivity", "Second : " + String.valueOf(imageData.length));
                }

                else {
                    // Create a new Message object and add it to the messages list
                    Message message = new Message(messageContent, isSentByUser);
                    messagesList.add(message);
                }

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
                    conversationRepository.addConversation(getMyIp(), receiversUserIp, messageText, "Plain Text",null);
                }).start();

            } catch (Exception e) {
                Message problemMessage = new Message("Unable To Send Message", true);
                messagesList.add(problemMessage);
            }
        }
    }


    private void sendImage(Bitmap imageBitmap) {
        if (imageBitmap != null) {
            // Convert Bitmap to byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Create the image message and add it to the list immediately
            Message imageMessage = new Message(imageBitmap, true);  // Set it as sent by the user
            messagesList.add(imageMessage);
            messagesAdapter.notifyItemInserted(messagesList.size() - 1);  // Notify the adapter

            // Scroll to the bottom to show the new message
            messagesRecyclerView.scrollToPosition(messagesList.size() - 1);

            // Set the image details for file transfer in the Data object
            String fileName = "captured_image.png";  // You can modify the file name as needed
            long fileSize = imageBytes.length;
            String fileType = "image/png";  // Assuming PNG format, adjust as needed

            try {
                Data dataObject = new Data();
                dataObject.setFileData(fileName, fileSize, fileType, imageBytes);
                dataObject.sendersUserIp = getMyIp();
                dataObject.receiversUserIp = receiversUserIp;
                dataObject.message = "Image";

                // Send the image in a background thread
                new Thread(() -> {
                    networkConnection.write(dataObject);  // Send the Data object containing the image
                    conversationRepository.addConversation(getMyIp(), receiversUserIp, null, "Image", imageBytes);  // Log the conversation
                }).start();

            } catch (Exception e) {
                Message problemMessage = new Message("Unable To Send Image", true);
                messagesList.add(problemMessage);
                messagesAdapter.notifyItemInserted(messagesList.size() - 1);  // Notify the adapter in case of error
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
