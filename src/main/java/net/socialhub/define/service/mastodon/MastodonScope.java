package net.socialhub.define.service.mastodon;

/**
 * Mastodon Authorization Scopes
 * Mastodon 認証用スコープリスト
 */
public class MastodonScope {

    public final static String READ = "read";

    public final static String WRITE = "write";

    public final static String FOLLOW = "follow";

    /** Full Access Scopes */
    public final static String FULL_ACCESS = READ + " " + WRITE + " " + FOLLOW;
}
