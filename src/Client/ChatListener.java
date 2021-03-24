package Client;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Map;

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
                        Map<String, byte[]> inputMap = ProtocolInterpreter.interprete(input);
                        int senderUID = ByteBuffer.wrap(inputMap.get("sender")).getInt();
                        String dataString = new String(inputMap.get("data"));
                        output.println(senderUID + ": " + dataString);
                    }
                } catch(Exception err){
                    System.out.println(err);
                }
        }
    }
}
