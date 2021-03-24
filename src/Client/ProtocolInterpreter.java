package Client;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class ProtocolInterpreter{
    public static Map<String, byte[]> interprete(byte[] byteArray) throws InvalidProtocolException{
        if(byteArray==null || byteArray.length<8) {
            throw new InvalidProtocolException();
        }

        Map<String, byte[]> map = new HashMap<>();

        byte[] recipient = new byte[4];
        for (int i = 0; i < 4; i++) {
            recipient[i] = byteArray[i];
        }
        map.put("recipient", recipient);

        byte[] sender = new byte[4];
        for (int j = 4; j < 8; j++) {
            sender[j - 4] = byteArray[j];
        }
        map.put("sender", sender);

        byte[] data = new byte[byteArray.length - 8];
        for (int k = 8; k < byteArray.length; k++) {
            data[k - 8] = byteArray[k];
        }
        map.put("data", data);

        return map;
    }
}
