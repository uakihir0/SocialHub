package net.socialhub.apis;

import net.socialhub.TestProperty;
import net.socialhub.j2objc.security.HmacProvider;
import org.junit.Before;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.Security;

public class AbstractApiTest {

    @Before
    public void before() {
        Security.addProvider(new HmacProvider());
        TestProperty.before();
    }

    /**
     * File to ImageBytes
     */
    public static byte[] convertFile(InputStream stream) {
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream()) {

            int len = 0;
            byte[] buffer = new byte[1024];

            while ((len = stream.read(buffer)) != -1) {
                bout.write(buffer, 0, len);
            }
            return bout.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
