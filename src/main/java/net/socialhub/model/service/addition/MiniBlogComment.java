package net.socialhub.model.service.addition;

import net.socialhub.model.request.CommentForm;
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
    private boolean isLiked = false;

    /** Share count (Alias with ReTweet) */
    private Long shareCount;

    /** User shared this comment */
    private boolean isShared = false;

    /** Reply To ID */
    private Identify replyTo;

    public MiniBlogComment(Service service) {
        super(service);
    }

    /**
     * Url を取得
     */
    abstract public String getWebUrl();

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

    @Override
    public void applyReaction(Reaction reaction) {
        if (reaction.getName().startsWith("like")) {
            if (reaction.getReacting() && !isLiked) {
                likeCount = (likeCount == null) ? 1 : likeCount + 1;
                isLiked = true;
            }
            if (!reaction.getReacting() && isLiked) {
                likeCount = (likeCount == null) ? 0 : likeCount - 1;
                isLiked = false;
            }
        }
        if (reaction.getName().startsWith("share")) {
            if (reaction.getReacting() && !isShared) {
                shareCount = (shareCount == null) ? 1 : shareCount + 1;
                isShared = true;
            }
            if (!reaction.getReacting() && isShared) {
                shareCount = (shareCount == null) ? 0 : shareCount - 1;
                isShared = false;
            }
        }
    }

    @Override
    public CommentForm getReplyForm() {
        if (getDirectMessage()) {
            CommentForm form = new CommentForm();
            form.targetId(getUser().getId());
            form.message(true);
            return form;
        }
        CommentForm form = new CommentForm();
        form.text(getUser().getAccountIdentify() + " ");
        form.targetId(getId());
        form.message(false);
        return form;
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

    public boolean getLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public boolean getShared() {
        return isShared;
    }

    public void setShared(boolean shared) {
        isShared = shared;
    }
    //endregion
}
