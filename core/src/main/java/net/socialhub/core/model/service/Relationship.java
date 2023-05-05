package net.socialhub.core.model.service;

/**
 * Relationship between accounts
 * アカウント間の関係を取得
 */
public class Relationship {

    private boolean followed;

    private boolean following;

    private boolean blocking;

    private boolean muting;

    //region // Getter&Setter
    public boolean getFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public boolean getFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public boolean getBlocking() {
        return blocking;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    public boolean getMuting() {
        return muting;
    }

    public void setMuting(boolean muting) {
        this.muting = muting;
    }
    //endregion
}
