package net.socialhub;

import net.socialhub.j2objc.security.HmacProvider;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.util.Base64;

public class SocialHubTest {

    @Before
    public void before() {
        Security.addProvider(new HmacProvider());
    }

    @Test
    public void testHmacSHA1() {

        Security.addProvider(new HmacProvider());

        String token = "Token";
        String message = "Message";

        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec spec = new SecretKeySpec(token.getBytes(), "HmacSHA1");
            mac.init(spec);

            byte[] bytes = mac.doFinal(message.getBytes());
            System.out.println(Base64.getEncoder().encodeToString(bytes));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
