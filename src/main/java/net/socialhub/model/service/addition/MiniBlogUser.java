package net.socialhub.model.service.addition;

import net.socialhub.model.common.AttributedString;
import net.socialhub.model.service.Service;
import net.socialhub.model.service.User;

/**
 * MiniBlog User Model
 * MiniBlog のユーザー要素
 */
public abstract class MiniBlogUser extends User {

    public MiniBlogUser(Service service) {
        super(service);
    }

    /** Count of followings */
    private Long followingsCount;

    /** Count of followers */
    private Long followersCount;

    /** Count of Statuses */
    private Long statusesCount;

    /** Is protected account? */
    private boolean isProtected = false;

    /** Profile url */
    private AttributedString profileUrl;

    /**
     * Url を取得
     */
    abstract public String getWebUrl();

    //region // Getter&Setter
    public Long getFollowingsCount() {
        return followingsCount;
    }

    public void setFollowingsCount(Long followingsCount) {
        this.followingsCount = followingsCount;
    }

    public Long getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Long followersCount) {
        this.followersCount = followersCount;
    }

    public Long getStatusesCount() {
        return statusesCount;
    }

    public void setStatusesCount(Long statusesCount) {
        this.statusesCount = statusesCount;
    }

    public boolean getProtected() {
        return isProtected;
    }

    public void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }

    public AttributedString getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(AttributedString profileUrl) {
        this.profileUrl = profileUrl;
    }
    //endregion
}
