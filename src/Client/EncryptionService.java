package Client;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class EncryptionService {

    private Cipher decryptor;
    private Cipher encryptor;
    private KeyPair ownKeyPair;

    public EncryptionService(KeyPair ownKeyPair) {
        this.ownKeyPair = ownKeyPair;
        try {
            decryptor = Cipher.getInstance("RSA");
            encryptor = Cipher.getInstance("RSA");

            decryptor.init(Cipher.DECRYPT_MODE, ownKeyPair.getPrivate());
        } catch(Exception err) {
            System.out.println(err);
        }
    }

    public byte[] decryptData(byte[] encryptedData) {
        byte[] decryptedData = null;
        try{
            decryptedData = decryptor.doFinal(encryptedData);
        } catch(Exception err) {
            System.out.println(err);
        }
        return decryptedData;
    }

    public byte[] encryptData(byte[] rawData, Key publicKey) {
        byte[] encryptedData = null;
        try {
            encryptor.init(Cipher.ENCRYPT_MODE, publicKey);
            encryptedData = encryptor.doFinal(rawData);
        } catch(Exception err) {
            System.out.println(err);
        }
        return encryptedData;
    }

    public static KeyPair generateKeyPair() {
        KeyPair keyPair = null;
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            keyPair = generator.generateKeyPair();
        } catch(Exception err) {
            System.out.println(err);
        }
        return keyPair;
    }

    public static EncryptionService getInstance() {
        KeyPair keyPair = generateKeyPair();
        return new EncryptionService(keyPair);
    }
}
