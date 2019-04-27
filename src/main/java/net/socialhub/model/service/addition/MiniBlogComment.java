package net.socialhub.model.service.addition;

import net.socialhub.model.service.Comment;
import net.socialhub.model.service.Reaction;
import net.socialhub.model.service.Service;

import java.util.List;

/**
 * MiniBlog Comment Model
 * MiniBlog のコメントモデル
 */
public class MiniBlogComment extends Comment {

    /** Like count (Alias with Favorite) */
    private Long likeCount;

    /** Share count (Alias with ReTweet) */
    private Long shareCount;

    public MiniBlogComment(Service service) {
        super(service);
    }

    @Override
    public Comment getDisplayComment() {
        return (getText() == null && getSharedComment() != null) //
                ? this.getSharedComment() : this;
    }

    @Override
    public List<Reaction> getReactions() {
        List<Reaction> reactions = super.getReactions();

        Reaction like = new Reaction();
        like.setCount(likeCount);
        like.setName("like");
        reactions.add(like);

        Reaction share = new Reaction();
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
    //endregion
}
