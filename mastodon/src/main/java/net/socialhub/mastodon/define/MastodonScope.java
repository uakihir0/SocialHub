package net.socialhub.mastodon.define;

/**
 * Mastodon Authorization Scopes
 * Mastodon 認証用スコープリスト
 */
public class MastodonScope {

    public final static String READ = "read";

    public final static String WRITE = "write";

    public final static String FOLLOW = "follow";

    public final static String PUSH = "push";

    /** Full Access Scopes */
    public final static String FULL_ACCESS = READ + " " + WRITE + " " + FOLLOW + " " + PUSH;
}
