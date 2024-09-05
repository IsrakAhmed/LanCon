package team.lancon;

/*
    This is the Server NetworkConnection Manager : Manages NetworkConnections with different clients.
*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerNCManager {

    private static ServerNCManager instance;

    /*  This clientConnections contains user's ip as key and their NetworkConnection
        with server as value.
    */
    private static List<HashMap<String, NetworkConnection>> clientConnections;

    private ServerNCManager() {}

    public static ServerNCManager getInstance() {
        if (instance == null) {
            instance = new ServerNCManager();
            clientConnections = new ArrayList<>();
        }
        return instance;
    }

    public List<HashMap<String, NetworkConnection>> getClientConnections() {
        return clientConnections;
    }
}
