package team.lancon;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReaderWriterServer implements Runnable {

    String userName;
    NetworkConnection networkConnection;
    HashMap<String, Information> clientList;
    UserRepository userRepository;

    public ReaderWriterServer(String userName, NetworkConnection networkConnection, UserRepository userRepository) {
        this.userName = userName;
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
                } else {
                    Log.e("HomeActivity", "null data object is read");
                }


                //if (actualMessage.toLowerCase().contains("list")) {

                //System.out.println("List asked.." + actualMessage);

                /*String words[] = actualMessage.split("\\$");*/

                /*
                words[0] = Sender Name
                words[1] = Receiver Name
                words[2] = keyword
                words[3] = message/null
                */

                //System.out.println("Client List: \n" + clientList);

                /*Information information = clientList.get(words[0]);

                String msgToSend = new String("List of Clients...\n");

                for (Map.Entry<String, Information> entry : clientList.entrySet()) {

                    String key = entry.getKey();
                    //Information value = entry.getValue();
                    msgToSend = new String(msgToSend + key + "\n");
                    //System.out.println(key);

                }*/

                /*Object object = msgToSend;*/

                //System.out.println("sending.." + msgToSend);
                //System.out.println("words0: " + words[0]);

                /*information.networkConnection.write(msgToSend);*/

                //String messageToSend=username+" -> "+sendMsg;
                //Data data=new Data();
                //data.message=messageToSend;
                //}

                //if (actualMessage.toLowerCase().contains("ip")){

                //String words[] = actualMessage.split("\\$");

                /*
                words[0] = Sender Name
                words[1] = Receiver Name
                words[2] = keyword = ip
                words[3] = message/null
                */

                //System.out.println("Client List: \n" + clientList);

                /*Information information = clientList.get(words[0]);
                String msgToSend = new String("Your PORT: \n");
                msgToSend += information.networkConnection.getSocket().getLocalAddress().getHostAddress();
                Object object = msgToSend;*/

                //System.out.println("sending.." + msgToSend);
                //System.out.println("words0: " + words[0]);

                //information.networkConnection.write(msgToSend);
                //}
                //if (actualMessage.toLowerCase().contains("send")){

                //String words[] = actualMessage.split("\\$");

                /*
                words[0] = Sender Name
                words[1] = Receiver Name
                words[2] = keyword = send
                words[3] = message
                */

                /*Information information = clientList.get(words[1]);
                String msgToSend = words[0]+" says: " + words[3];
                Object object = msgToSend;*/

                //System.out.println("sending.." + msgToSend);
                //System.out.println("words0: " + words[0]);

                //information.networkConnection.write(msgToSend);
                //}
            }
            catch (Exception e){
                Log.e("HomeActivity", String.valueOf(e));
            }
        }

    }

}
