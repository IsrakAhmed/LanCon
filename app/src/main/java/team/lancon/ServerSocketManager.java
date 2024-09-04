package team.lancon;

import java.net.ServerSocket;
public class ServerSocketManager {
    private static ServerSocketManager instance;
    private ServerSocket serverSocket;

    private ServerSocketManager() {}

    public static ServerSocketManager getInstance() {
        if (instance == null) {
            instance = new ServerSocketManager();
        }
        return instance;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }
}
