package net.socialhub.model.service;

import net.socialhub.service.action.AccountAction;
import net.socialhub.service.action.CommentAction;
import net.socialhub.service.action.CommentActionImpl;
import net.socialhub.utils.MemoSupplier;

import java.util.Date;

/**
 * SNS コメント情報
 * SNS Comment Model
 */
public class Comment extends Identify {

    private String comment;

    private Date createAt;

    private MemoSupplier<User> user;

    public Comment(Service service) {
        super(service);
    }

    public CommentAction action() {
        AccountAction action = getService().getAccount().action();
        return new CommentActionImpl(action).comment(this);
    }

    //region // Getter&Setter
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user.get();
    }

    public void setUser(MemoSupplier<User> user) {
        this.user = user;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }
    //endregion
}
