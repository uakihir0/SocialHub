package net.socialhub.model.service;

/**
 * Relationship between accounts
 * アカウント間の関係を取得
 */
public class Relationship {

    private Boolean followed;

    private Boolean following;

    private Boolean blocking;

    private Boolean muting;

    //region // Getter&Setter
    public Boolean getFollowed() {
        return followed;
    }

    public void setFollowed(Boolean followed) {
        this.followed = followed;
    }

    public Boolean getFollowing() {
        return following;
    }

    public void setFollowing(Boolean following) {
        this.following = following;
    }

    public Boolean getBlocking() {
        return blocking;
    }

    public void setBlocking(Boolean blocking) {
        this.blocking = blocking;
    }

    public Boolean getMuting() {
        return muting;
    }

    public void setMuting(Boolean muting) {
        this.muting = muting;
    }
    //endregion
}
