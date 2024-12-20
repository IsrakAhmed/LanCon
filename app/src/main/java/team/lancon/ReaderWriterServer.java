package team.lancon;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReaderWriterServer implements Runnable {

    String userName, userIp;
    NetworkConnection networkConnection;
    HashMap<String, Information> clientList;
    UserRepository userRepository;

    public ReaderWriterServer(String userName, String userIp, NetworkConnection networkConnection, UserRepository userRepository) {
        this.userName = userName;
        this.userIp = userIp;
        this.networkConnection = networkConnection;
        this.userRepository = userRepository;
    }

    @Override
    public void run() {

        while (true) {

            try {

                //Object obj = networkConnection.read();
                //Data dataObj = (Data) obj;

                Data dataObj = networkConnection.read();

                Log.e("HomeActivity", "data object read");

                if (dataObj != null) {

                    Log.e("HomeActivity", "File Type : " + dataObj.getFileType());

                    String actualMessage = dataObj.message;

                    if (Objects.equals(actualMessage, "GET_USERNAME")) {
                        Data dataObject = new Data();

                        dataObject.message = "USERNAME::" + userName;

                        networkConnection.write(dataObject);
                    }


                    if (Objects.equals(actualMessage, "GET_ALL-USERS")) {
                        Log.e("HomeActivity", "GET_ALL-USERS message received");
                        //userRepository.addUser("newUserName", "newUserIP");
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

                        Data dataObject = new Data();
                        dataObject.message = "USER_LIST";
                        Log.e("HomeActivity", "Setting user list");
                        dataObject.setUserList(userList); // Assuming you add a new field to the Data class to hold this list

                        Log.e("HomeActivity", "sending USER_LIST message");
                        networkConnection.write(dataObject);
                        Log.e("HomeActivity", "USER_LIST message send");
                    }

                    if (Objects.equals(actualMessage, "USER_LIST")) {
                        List<HashMap<String, Object>> receivedUserList = dataObj.getUserList();

                        Log.e("HomeActivity", "receivedUserList got from dataObj");

                        ClientULManager.getInstance().setUserList(receivedUserList);
                        Log.e("HomeActivity", "receivedUserList set to instance");

                        //Thread.sleep(1000);
                    }

                    else {
                        if(Objects.equals(dataObj.receiversUserIp, userIp)) {
                            //List<Message> messagesList = MessageListManager.getInstance().getMessagesList();

                            //messagesList.add(new Message(dataObj.message, false));  // Received message

                            if (Objects.equals(actualMessage, "Image")) {
                                MessageListManager.getInstance().getConversationRepository().addConversation(dataObj.sendersUserIp, dataObj.receiversUserIp, null,"Image", dataObj.getFileData());
                            }

                            else {
                                MessageListManager.getInstance().getConversationRepository().addConversation(dataObj.sendersUserIp, dataObj.receiversUserIp, dataObj.message,"Plain Text",null);
                            }


                            MessageListManager.getInstance().notifyDataChanged();
                        }

                        else {
                            List<HashMap<String, NetworkConnection>> clientConnections = ServerNCManager.getInstance().getClientConnections();

                            String targetIP = dataObj.receiversUserIp;

                            for (HashMap<String, NetworkConnection> userMap : clientConnections) {
                                if (userMap.containsKey(targetIP)) {
                                    userMap.get(targetIP).write(dataObj);
                                }
                            }
                        }
                    }

                } else {
                    Log.e("HomeActivity", "null data object is read");
                }

            }
            catch (Exception e){
                Log.e("HomeActivity", String.valueOf(e));
            }
        }

    }

}
