package net.socialhub.model.service.addition;

import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Identify;
import net.socialhub.model.service.Reaction;
import net.socialhub.model.service.Service;

import java.util.List;

/**
 * MiniBlog Comment Model
 * MiniBlog のコメントモデル
 */
public abstract class MiniBlogComment extends Comment {

    /** Like count (Alias with Favorite) */
    private Long likeCount;

    /** User likes this comment */
    private Boolean isLiked;

    /** Share count (Alias with ReTweet) */
    private Long shareCount;

    /** User shared this comment */
    private Boolean isShared;

    /** Reply To ID */
    private Identify replyTo;

    public MiniBlogComment(Service service) {
        super(service);
    }

    /**
     * Url を取得
     */
    abstract public String getUrl();

    @Override
    public Comment getDisplayComment() {
        return (getText() == null && getSharedComment() != null) //
                ? this.getSharedComment() : this;
    }

    @Override
    public List<Reaction> getReactions() {
        List<Reaction> reactions = super.getReactions();

        Reaction like = new Reaction();
        like.setReacting(isLiked);
        like.setCount(likeCount);
        like.setName("like");
        reactions.add(like);

        Reaction share = new Reaction();
        share.setReacting(isShared);
        share.setCount(shareCount);
        share.setName("share");
        reactions.add(share);

        return reactions;
    }

    //region // Getter&Setter
    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public Long getShareCount() {
        return shareCount;
    }

    public void setShareCount(Long shareCount) {
        this.shareCount = shareCount;
    }

    public Identify getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(Identify replyTo) {
        this.replyTo = replyTo;
    }

    public Boolean getLiked() {
        return isLiked;
    }

    public void setLiked(Boolean liked) {
        isLiked = liked;
    }

    public Boolean getShared() {
        return isShared;
    }

    public void setShared(Boolean shared) {
        isShared = shared;
    }
    //endregion
}
