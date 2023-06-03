package net.socialhub.service.bluesky.model;

import net.socialhub.core.model.Channel;
import net.socialhub.core.model.Service;
import net.socialhub.core.model.User;

/**
 * Bluesky Channel Model
 * Bluesky のチャンネル情報
 */
public class BlueskyChannel extends Channel {

    private String cid;
    private User owner;
    private String iconUrl;
    private Integer likeCount;

    public BlueskyChannel(Service service) {
        super(service);
    }

    // region
    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
    // endregion
}
