package Client;

import java.io.*;
import java.nio.ByteBuffer;

public class ChatListener implements Runnable {

    private final InputStream in;
    private final PrintStream output;

    private boolean connected = true;

    public ChatListener(InputStream in, PrintStream output) {
        this.in = in;
        this.output = output;
    }

    public boolean isConnected() {
        return connected;
    }

    @Override
    public void run() {
        while(true) {
                try {
                    if (in.available() > 0) {
                        byte[] input = in.readNBytes(in.available());
                        byte[] sender = new byte[4];
                        for (int i = 4; i < 8; i++) {
                            sender[i - 4] = input[i];
                        }
                        int sender_uid = ByteBuffer.wrap(sender).getInt();

                        byte[] data = new byte[input.length - 8];
                        for (int j = 8; j < input.length; j++) {
                            data[j - 8] = input[j];
                        }
                        String string = new String(data);
                        output.println(sender_uid + ": " + string);
                    }
                } catch(Exception err){
                    System.out.println(err);
                }
        }
    }
}
