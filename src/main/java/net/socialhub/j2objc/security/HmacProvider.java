package net.socialhub.j2objc.security;

import java.security.Provider;

public class HmacProvider extends Provider {

    private static final String PROVIDER_NAME = "SocialHubSecurity";

    private static final String PREFIX = "net.socialhub.j2objc.security.";

    public HmacProvider() {
        super(PROVIDER_NAME, 1.0, "SocialHub Hmac Security provider");

        put("Mac.HmacSHA1", PREFIX + "HmacCore$HmacSHA1");
        put("Mac.HmacSHA256", PREFIX + "HmacCore$HmacSHA256");

        put("Mac.HmacSHA1 SupportedKeyFormats", "RAW");
        put("Mac.HmacSHA256 SupportedKeyFormats", "RAW");
    }

    @SuppressWarnings("unused")
    // J2Objc loads classes dynamically
    private static final Class<?>[] unused = {
            HmacCore.class,
    };
}
