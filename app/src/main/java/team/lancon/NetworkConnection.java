package team.lancon;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkConnection {
    Socket socket;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;

    public NetworkConnection(Socket socket) throws IOException {
        this.socket = socket;
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
    }

    public NetworkConnection(String ip, int port) throws IOException{
        socket = new Socket(ip, port);
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
    }

    public synchronized void write(Data dataObj){
        try {
            objectOutputStream.writeObject(dataObj);
        } catch (Exception e) {
            //System.out.println("Failed to write");
            //throw ex;
            Log.e("HomeActivity", e.toString());
        }
    }

    public /*synchronized*/ Data read(){
        Data dataObj;
        try {
            dataObj = (Data) objectInputStream.readObject();
        } catch (Exception e) {
            //System.out.println("Failed to read");
            Log.e("HomeActivity", e.toString());
            return null;
        }
        return dataObj;
    }

    public Socket getSocket() {
        return socket;
    }
}
