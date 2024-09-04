package team.lancon;

/*
    This is the Client userList Manager
*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClientULManager {
    private static ClientULManager instance;
    List<HashMap<String, Object>> userList;

    private ClientULManager() {}

    public static ClientULManager getInstance() {
        if (instance == null) {
            instance = new ClientULManager();
        }
        return instance;
    }

    public void setUserList(List<HashMap<String, Object>> userList) {
        this.userList = userList;
    }

    public List<HashMap<String, Object>> getUserList() {

        if(userList == null){
            userList = new ArrayList<>();
        }

        return userList;
    }
}
