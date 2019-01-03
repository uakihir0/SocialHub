package net.socialhub.apis;

import net.socialhub.TestProperty;
import net.socialhub.j2objc.security.HmacProvider;
import org.junit.Before;

import java.security.Security;

public class AbstractApiTest {

    @Before
    public void before() {
        Security.addProvider(new HmacProvider());
        TestProperty.before();
    }
}
