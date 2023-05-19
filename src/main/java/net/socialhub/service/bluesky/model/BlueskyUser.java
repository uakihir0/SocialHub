package net.socialhub.service.bluesky.model;

import net.socialhub.core.model.Service;
import net.socialhub.service.microblog.model.MiniBlogUser;

/**
 * Bluesky User Model
 * Bluesky のユーザー情報
 */
public class BlueskyUser extends MiniBlogUser {

    /**
     * Is simple User object?
     * 簡易ユーザー情報か？
     * (一部の情報が抜けています)
     */
    private Boolean simple;

    /**
     * Follow Record Uri
     * フォローのレコードの URI
     * (フォローしている場合のみ記録される)
     */
    private String followRecordUri;

    private String followedRecordUri;

    private Boolean muted;

    private Boolean blockedBy;

    public BlueskyUser(Service service) {
        super(service);
    }

    @Override
    public String getWebUrl() {
        String handle = getAccountIdentify();
        return "https://bsky.app/profile/" + handle;
    }

    // region
    public Boolean getSimple() {
        return simple;
    }

    public void setSimple(Boolean simple) {
        this.simple = simple;
    }

    public String getFollowRecordUri() {
        return followRecordUri;
    }

    public void setFollowRecordUri(String followRecordUri) {
        this.followRecordUri = followRecordUri;
    }

    public String getFollowedRecordUri() {
        return followedRecordUri;
    }

    public void setFollowedRecordUri(String followedRecordUri) {
        this.followedRecordUri = followedRecordUri;
    }

    public Boolean getMuted() {
        return muted;
    }

    public void setMuted(Boolean muted) {
        this.muted = muted;
    }

    public Boolean getBlockedBy() {
        return blockedBy;
    }

    public void setBlockedBy(Boolean blockedBy) {
        this.blockedBy = blockedBy;
    }
    // endregion
}
