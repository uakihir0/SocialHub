package net.socialhub.model.service;

import net.socialhub.model.common.AttributedString;
import net.socialhub.service.action.AccountAction;
import net.socialhub.service.action.CommentAction;
import net.socialhub.service.action.CommentActionImpl;
import net.socialhub.utils.MemoSupplier;

import java.util.Date;
import java.util.List;

/**
 * SNS コメント情報
 * SNS Comment Model
 */
public class Comment extends Identify {

    /** Text of comment */
    private AttributedString comment;

    /** Date of comment created */
    private Date createAt;

    /** User who create this comment */
    private MemoSupplier<User> user;

    /** Files which attached with this comment */
    private MemoSupplier<List<Media>> medias;

    public Comment(Service service) {
        super(service);
    }

    public CommentAction action() {
        AccountAction action = getService().getAccount().action();
        return new CommentActionImpl(action).comment(this);
    }

    //region // Getter&Setter
    public AttributedString getComment() {
        return comment;
    }

    public void setComment(AttributedString comment) {
        this.comment = comment;
    }

    public User getUser() {
        if (user == null) {
            return null;
        }
        return user.get();
    }

    public void setUser(MemoSupplier<User> user) {
        this.user = user;
    }

    public List<Media> getMedias() {
        if (medias == null) {
            return null;
        }
        return medias.get();
    }

    public void setMedias(MemoSupplier<List<Media>> medias) {
        this.medias = medias;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    //endregion
}
