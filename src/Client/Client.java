package Client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Client {
    private Socket socket = null;
    private ChatListener listener = null;
    private final PrintStream outputPrintStream;
    private final String hostname;
    private final int port;

    public Client(String hostname, int port, PrintStream outputPrintStream) {
        this.hostname = hostname;
        this.port = port;
        this.outputPrintStream = outputPrintStream;
    }

    public void connect() throws IOException {
        try {
            socket = new Socket(InetAddress.getByName(hostname), port);
        } catch (IOException e) {
            throw new IOException("Could not connect to " + hostname + ":" + port + " : " + e.getMessage(), e);
        }

        outputPrintStream.println("Connected to " + socket.getInetAddress().getHostAddress() + " through port " + socket.getLocalPort());

        InputStream in;
        // TODO: Create BufferedReader on top of InputStreamReader on top of the input stream of the socket. Throw IOException if that fails
        try{
            in = socket.getInputStream();
        } catch(Exception err) {
            throw new IOException();
        }

        // TODO: Create new ChatListener with BufferedReader 'in' and 'outputPrintStream'. Store in member variable 'listener'
        listener = new ChatListener(in, outputPrintStream);
        // TODO: Create and start a new Thread with this ChatListener
        Thread lThread = new Thread(listener);
        lThread.start();
    }

    public void disconnect() {
        if(!isConnected())
            return;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        if(socket == null || listener == null)
            return false;
        return !socket.isClosed() && socket.isConnected() && listener.isConnected();
    }

    public void send(int recipientUID, int ownUID, String message) throws IOException{
        // TODO: Throw IllegalStateException when client is not connected
        if(!isConnected()) {
            throw new IllegalStateException();
        }
        try {
            OutputStream out = socket.getOutputStream();
            byte[] packet = ByteBuffer.allocate(8+message.length()).
                    putInt(recipientUID).putInt(ownUID).put(message.getBytes()).array();
            out.write(packet);
            // Don't forget to flush (auto-flush is an option)!
            out.flush();
        } catch (IOException e) {
            throw new IOException("Could not send message: " + e.getMessage(), e);
        }
    }
}
