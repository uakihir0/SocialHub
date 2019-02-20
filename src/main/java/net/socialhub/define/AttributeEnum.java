package net.socialhub.define;

import java.util.Arrays;
import java.util.List;

public enum AttributeEnum {

    // Commons
    Link, //
    Email, //
    Phone, //
    HashTag, //

    // Accounts
    TwitterAccount, //
    MastodonAccount, //
    ;

    public static List<AttributeEnum> all() {
        return Arrays.asList(AttributeEnum.values());
    }
}
