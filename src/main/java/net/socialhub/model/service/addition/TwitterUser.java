package net.socialhub.model.service.addition;

/**
 * Twitter における User 要素
 * Twitter specified user's attributes
 */
public class TwitterUser {

    /** User setting url */
    private String url;

    /** User setting location */
    private String location;

    /** Is verified account? */
    private Boolean isVerified;

    /** Is protected account? */
    private Boolean isProtected;

    /** Count of followings */
    private Long followingsCount;

    /** Count of followers */
    private Long followersCount;

    /** Count of Statuses */
    private Long statusesCount;

    /** Count of Favorites */
    private Long favoritesCount;

    //region // Getter&Setter
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

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

    public Long getFavoritesCount() {
        return favoritesCount;
    }

    public void setFavoritesCount(Long favoritesCount) {
        this.favoritesCount = favoritesCount;
    }

    public Boolean getVerified() {
        return isVerified;
    }

    public void setVerified(Boolean verified) {
        isVerified = verified;
    }

    public Boolean getProtected() {
        return isProtected;
    }

    public void setProtected(Boolean aProtected) {
        isProtected = aProtected;
    }

    //endregion
}
