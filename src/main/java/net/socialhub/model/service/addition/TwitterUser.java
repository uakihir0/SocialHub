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

    /** Count of followings */
    private Long followingsCount;

    /** Count of followers */
    private Long followersCount;

    /** Count of Tweets */
    private Long tweetsCount;

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

    public Long getTweetsCount() {
        return tweetsCount;
    }

    public void setTweetsCount(Long tweetsCount) {
        this.tweetsCount = tweetsCount;
    }

    public Long getFavoritesCount() {
        return favoritesCount;
    }

    public void setFavoritesCount(Long favoritesCount) {
        this.favoritesCount = favoritesCount;
    }
    //endregion
}
