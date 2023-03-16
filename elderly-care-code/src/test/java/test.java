import org.cuit.app.constant.Constants;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class test {

    @Test
    public void test() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String password="1234";
        MessageDigest md = MessageDigest.getInstance("MD5");
        String newP = new String(md.digest(password.getBytes()), Constants.UTF8);
        System.out.println(newP);
    }
}
