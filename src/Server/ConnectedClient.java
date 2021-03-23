package Server;

import java.net.Socket;

public class ConnectedClient {
    private Socket clientSocket;
    private int uid;

    public ConnectedClient(Socket clientSocket, int uid) {
        this.clientSocket = clientSocket;
        this.uid = uid;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public int getUid() {
        return uid;
    }
}
