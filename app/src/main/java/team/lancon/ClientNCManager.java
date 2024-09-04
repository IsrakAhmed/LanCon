package team.lancon;

/*
    This is the Client NetworkConnection Manager
*/

public class ClientNCManager {

    private static ClientNCManager instance;
    private NetworkConnection networkConnection;

    private ClientNCManager() {}

    public static ClientNCManager getInstance() {
        if (instance == null) {
            instance = new ClientNCManager();
        }
        return instance;
    }

    public void setNetworkConnection(NetworkConnection networkConnection) {
        this.networkConnection = networkConnection;
    }

    public NetworkConnection getNetworkConnection() {
        return networkConnection;
    }

}
