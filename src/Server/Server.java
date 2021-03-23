package Server;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {

    public final int port;
    private static final List<ConnectedClient> connections = new LinkedList<>();
    private final int connectionTimeoutMinutes;
    private static final ExecutorService threadPool = new ThreadPoolExecutor(1, 400,
            60L, TimeUnit.SECONDS, new SynchronousQueue<>());
    private int clientCounter = 0;

    public Server(int port, int connectionTimeoutMinutes) {
        this.port = port;
        this.connectionTimeoutMinutes = connectionTimeoutMinutes;
    }

    public void run() {
        System.out.println("Server running: port=" + port);
        try (ServerSocket serverSocket = new ServerSocket(port)){
            acceptConnections(serverSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acceptConnections(ServerSocket serverSocket) {
        while (true) {
            try{
                Socket accept = serverSocket.accept();
                threadPool.execute(() -> newConnection(accept));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void newConnection(Socket accept) {
        try {
            accept.setSoTimeout(connectionTimeoutMinutes * 60 * 1000);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }
        ConnectedClient client = new ConnectedClient(accept, generateUID());
        connections.add(client);
        System.out.println("New connection from " + client.getClientSocket().getInetAddress().getHostAddress() + ":" +
                client.getClientSocket().getPort() + " -> user id: " + client.getUid());
        try {
            try {
                forwarding(client);
            } catch (SocketTimeoutException e) {
                System.out.println("Connection from " + client.getClientSocket().getInetAddress().getHostAddress() +
                        " timed out");
            } catch (SocketException e) {
                System.out.println("Connection from " + client.getClientSocket().getInetAddress().getHostAddress() +
                        " reset");
            } catch (Exception e) {
                if(accept.isConnected()) {
                    sendErrorMessage(client);
                }
                e.printStackTrace();
            } finally {
                client.getClientSocket().close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void forwarding(ConnectedClient client) throws IOException {
        InputStream in = client.getClientSocket().getInputStream();
        while(true) {
            if(in.available()>0) {
                byte[] input = in.readNBytes(in.available());
                byte[] b_uid = new byte[4];
                for(int i=0; i<4; i++) {
                    b_uid[i] = input[i];
                }
                ByteBuffer UID = ByteBuffer.wrap(b_uid);
                int recipientUID = UID.getInt();

                boolean messageSent = false;
                synchronized(connections) {
                    Iterator<ConnectedClient> it = connections.iterator();
                    while (it.hasNext()) {
                        ConnectedClient otherClient = it.next();
                        if (otherClient.getClientSocket().isClosed()) {
                            it.remove();
                            continue;
                        }
                        if (otherClient == client)
                            continue;
                        if (otherClient.getUid() == recipientUID) {
                            OutputStream out =
                                    otherClient.getClientSocket().getOutputStream();
                            out.write(input);
                            out.flush();
                            messageSent = true;
                        }
                        if (messageSent) {
                            break;
                        }
                    }
                }
                if(!messageSent) {
                    sendErrorMessage(client);
                }
            }
        }
    }

    private void sendErrorMessage(ConnectedClient client) {
        try{
            OutputStream out = client.getClientSocket().getOutputStream();
            int uid = client.getUid();
            byte[] b_uid = ByteBuffer.allocate(4).putInt(uid).array();
            byte[] error = ByteBuffer.allocate(16).put(b_uid).putInt(0).putInt(0).putInt(0).array();
            out.write(error);
            out.flush();
        } catch(IOException err) {
            System.out.println("Error while sending Error message: "+err);
        }
    }

    private int generateUID() {
        clientCounter++;
        return clientCounter;
    }
}
