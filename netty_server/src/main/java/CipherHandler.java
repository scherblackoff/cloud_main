import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class CipherHandler {
    private static final SecretKeySpec key = new SecretKeySpec("Bar12345Bar12345".getBytes(), "AES");


    public static byte[] doEncrypt(String password) throws InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException,
            NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));
    }

    public static String doDecrypt(byte[] password) throws NoSuchPaddingException,
            NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException, IllegalArgumentException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] bytes = cipher.doFinal(password);
        String decryptPassword = new String(bytes);
        System.out.println(decryptPassword);
        return decryptPassword;
    }
}
