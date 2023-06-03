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
    private boolean isSimple = false;

    /**
     * Follow Record Uri
     * フォローのレコードの URI
     * (フォローしている場合のみ記録される)
     */
    private String followRecordUri;

    private String followedRecordUri;

    private String blockingRecordUri;

    private Boolean muted;

    private Boolean blockedBy;

    public BlueskyUser(Service service) {
        super(service);
    }

    @Override
    public String getName() {
        // Bluesky では名前が空の場合も存在
        if (super.getName() == null) {
            return getScreenName();
        }
        return super.getName();
    }

    @Override
    public String getAccountIdentify() {
        return "@" + getScreenName();
    }

    @Override
    public String getWebUrl() {
        String handle = getAccountIdentify();
        return "https://bsky.app/profile/" + handle;
    }

    @Override
    public String getIconImageUrl() {
        // IconUrl が存在しない場合は空文字を返す
        String url = super.getIconImageUrl();
        return (url != null) ? url : "";
    }

    // region
    public boolean isSimple() {
        return isSimple;
    }

    public void setSimple(boolean simple) {
        isSimple = simple;
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

    public String getBlockingRecordUri() {
        return blockingRecordUri;
    }

    public void setBlockingRecordUri(String blockingRecordUri) {
        this.blockingRecordUri = blockingRecordUri;
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
