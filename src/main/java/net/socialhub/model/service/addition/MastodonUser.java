package net.socialhub.model.service.addition;

/**
 * Mastodon における User 要素
 * Mastodon specified user's attributes
 */
public class MastodonUser {

    /** Count of followings */
    private Long followingsCount;

    /** Count of followers */
    private Long followersCount;

    /** Count of Statuses */
    private Long statusesCount;

    /** Is protected account? */
    private Boolean isProtected;

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

    public Boolean getProtected() {
        return isProtected;
    }

    public void setProtected(Boolean aProtected) {
        isProtected = aProtected;
    }
    //endregion
}

