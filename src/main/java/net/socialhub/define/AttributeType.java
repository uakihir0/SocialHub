package net.socialhub.define;

import java.util.Arrays;
import java.util.List;

public enum AttributeType {

    // Commons
    Link, //
    Email, //
    Phone, //
    HashTag, //

    // Accounts
    TwitterAccount, //
    MastodonAccount, //
    ;

    public static List<AttributeType> all() {
        return Arrays.asList(AttributeType.values());
    }
}
