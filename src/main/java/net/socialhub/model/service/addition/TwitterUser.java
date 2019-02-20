package net.socialhub.model.service.addition;

import net.socialhub.model.service.Service;

/**
 * Twitter における User 要素
 * Twitter specified user's attributes
 */
public class TwitterUser extends MiniBlogUser {

    public TwitterUser(Service service) {
        super(service);
    }

    /** User setting url */
    private String url;

    /** User setting location */
    private String location;

    /** Is verified account? */
    private Boolean isVerified;

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
    //endregion
}
